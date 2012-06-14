"""
    QC Data Function Module
    Used to perform the tasks queued by qcls.py
    """

import sys
import ast
from calendar import isleap
import constants as c
import datetime
from matplotlib.dates import date2num
import meteorologicalfunctions as mf
import numpy
import qcio
import qcts
import qcutils
from scipy import interpolate
import time
import xlrd
from matplotlib.mlab import griddata
import xlwt
import logging

log = logging.getLogger('qc.ts')

def albedo(cf,ds):
    """
        Filter albedo measurements to:
            high solar angle specified by periods between 10.00 and 14.00, inclusive
            and
            full sunlight in which Fsd > 290 W/m2
        
        Usage qcts.albedo(ds)
        ds: data structure
        """
    log.info(' Applying albedo constraints')
    if 'albedo' not in ds.series.keys():
        if 'Fsd' in ds.series.keys() and 'Fsu' in ds.series.keys():
            Fsd,f = qcutils.GetSeriesasMA(ds,'Fsd')
            Fsu,f = qcutils.GetSeriesasMA(ds,'Fsu')
            albedo = Fsu / Fsd
            qcutils.CreateSeries(ds,'albedo',albedo,FList=['Fsd','Fsu'],Descr='solar albedo',Units='unitless',Standard='solar_albedo')
        else:
            log.warning('  Fsd or Fsu not in ds, albedo not calculated')
            return
    else:
        albedo,f = qcutils.GetSeriesasMA(ds,'albedo')
        if 'Fsd' in ds.series.keys():
            Fsd,f = qcutils.GetSeriesasMA(ds,'Fsd')
        else:
            Fsd,f = qcutils.GetSeriesasMA(ds,'Fn')
    
    if qcutils.cfkeycheck(cf,ThisOne='albedo',key='Threshold'):
        Fsdbase = float(cf['Variables']['albedo']['Threshold']['Fsd'])
        ds.series['albedo']['Attr']['FsdCutoff'] = Fsdbase
    else:
        Fsdbase = 290.
    index = numpy.ma.where((Fsd < Fsdbase) | (ds.series['Hdh']['Data'] < 10) | (ds.series['Hdh']['Data'] > 14))[0]
    index1 = numpy.ma.where(Fsd < Fsdbase)[0]
    index2 = numpy.ma.where((ds.series['Hdh']['Data'] < 10) | (ds.series['Hdh']['Data'] > 14))[0]
    albedo[index] = numpy.float64(-9999)
    ds.series['albedo']['Flag'][index1] = 51     # bad Fsd flag only if bad time flag not set
    ds.series['albedo']['Flag'][index2] = 52     # bad time flag
    ds.series['albedo']['Data']=numpy.ma.filled(albedo,float(-9999))

def ApplyLinear(cf,ds,ThisOne):
    """
        Applies a linear correction to variable passed from qcls. Time period
        to apply the correction, slope and offset are specified in the control
        file.
        
        Usage qcts.ApplyLinear(cf,ds,x)
        cf: control file
        ds: data structure
        x: input/output variable in ds.  Example: 'Cc_7500_Av'
        """
    log.info('  Applying linear correction to '+ThisOne)
    if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'Linear'):
        data = numpy.ma.masked_where(ds.series[ThisOne]['Data']==float(-9999),ds.series[ThisOne]['Data'])
        flag = ds.series[ThisOne]['Flag'].copy()
        ldt = ds.series['DateTime']['Data']
        LinearList = cf['Variables'][ThisOne]['Linear'].keys()
        for i in range(len(LinearList)):
            LinearItemList = ast.literal_eval(cf['Variables'][ThisOne]['Linear'][str(i)])
            try:
                si = ldt.index(datetime.datetime.strptime(LinearItemList[0],'%Y-%m-%d %H:%M'))
            except ValueError:
                si = 0
            try:
                ei = ldt.index(datetime.datetime.strptime(LinearItemList[1],'%Y-%m-%d %H:%M')) + 1
            except ValueError:
                ei = -1
            Slope = float(LinearItemList[2])
            Offset = float(LinearItemList[3])
            data[si:ei] = Slope * data[si:ei] + Offset
            index = numpy.where(flag[si:ei]==0)[0]
            flag[si:ei][index] = 10
            ds.series[ThisOne]['Data'] = numpy.ma.filled(data,float(-9999))
            ds.series[ThisOne]['Flag'] = flag

def ApplyLinearDrift(cf,ds,ThisOne):
    """
        Applies a linear correction to variable passed from qcls. The slope is
        interpolated for each 30-min period between the starting value at time 0
        and the ending value at time 1.  Slope0, Slope1 and Offset are defined
        in the control file.  This function applies to a dataset in which the
        start and end times in the control file are matched by the time period
        in the dataset.
        
        Usage qcts.ApplyLinearDrift(cf,ds,x)
        cf: control file
        ds: data structure
        x: input/output variable in ds.  Example: 'Cc_7500_Av'
        """
    log.info('  Applying linear drift correction to '+ThisOne)
    if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'Drift'):
        data = numpy.ma.masked_where(ds.series[ThisOne]['Data']==float(-9999),ds.series[ThisOne]['Data'])
        flag = ds.series[ThisOne]['Flag']
        ldt = ds.series['DateTime']['Data']
        DriftList = cf['Variables'][ThisOne]['Drift'].keys()
        for i in range(len(DriftList)):
            DriftItemList = ast.literal_eval(cf['Variables'][ThisOne]['Drift'][str(i)])
            try:
                si = ldt.index(datetime.datetime.strptime(DriftItemList[0],'%Y-%m-%d %H:%M'))
            except ValueError:
                si = 0
            try:
                ei = ldt.index(datetime.datetime.strptime(DriftItemList[1],'%Y-%m-%d %H:%M')) + 1
            except ValueError:
                ei = -1
            Slope = numpy.zeros(len(data))
            Slope0 = float(DriftItemList[2])
            Slope1 = float(DriftItemList[3])
            Offset = float(DriftItemList[4])
            nRecs = len(Slope[si:ei])
            for i in range(nRecs):
                ssi = si + i
                Slope[ssi] = ((((Slope1 - Slope0) / nRecs) * i) + Slope0)
            data[si:ei] = Slope[si:ei] * data[si:ei] + Offset
            flag[si:ei] = 10
            ds.series[ThisOne]['Data'] = numpy.ma.filled(data,float(-9999))
            ds.series[ThisOne]['Flag'] = flag

def ApplyLinearDriftLocal(cf,ds,ThisOne):
    """
        Applies a linear correction to variable passed from qcls. The slope is
        interpolated since the starting value at time 0 using a known 30-min
        increment.  Slope0, SlopeIncrement and Offset are defined in the control
        file.  This function applies to a dataset in which the start time in the
        control file is matched by dataset start time, but in which the end time
        in the control file extends beyond the dataset end.
        
        Usage qcts.ApplyLinearDriftLocal(cf,ds,x)
        cf: control file
        ds: data structure
        x: input/output variable in ds.  Example: 'Cc_7500_Av'
        """
    log.info('  Applying linear drift correction to '+ThisOne)
    if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'LocalDrift'):
        data = numpy.ma.masked_where(ds.series[ThisOne]['Data']==float(-9999),ds.series[ThisOne]['Data'])
        flag = ds.series[ThisOne]['Flag']
        ldt = ds.series['DateTime']['Data']
        DriftList = cf['Variables'][ThisOne]['LocalDrift'].keys()
        for i in range(len(DriftList)):
            DriftItemList = ast.literal_eval(cf['Variables'][ThisOne]['LocalDrift'][str(i)])
            try:
                si = ldt.index(datetime.datetime.strptime(DriftItemList[0],'%Y-%m-%d %H:%M'))
            except ValueError:
                si = 0
            try:
                ei = ldt.index(datetime.datetime.strptime(DriftItemList[1],'%Y-%m-%d %H:%M')) + 1
            except ValueError:
                ei = -1
            Slope = numpy.zeros(len(data))
            Slope0 = float(DriftItemList[2])
            SlopeIncrement = float(DriftItemList[3])
            Offset = float(DriftItemList[4])
            nRecs = len(Slope[si:ei])
            for i in range(nRecs):
                ssi = si + i
                Slope[ssi] = (SlopeIncrement * i) + Slope0
            data[si:ei] = Slope[si:ei] * data[si:ei] + Offset
            flag[si:ei] = 10
            ds.series[ThisOne]['Data'] = numpy.ma.filled(data,float(-9999))
            ds.series[ThisOne]['Flag'] = flag

def AverageSeriesByElements(ds,Av_out,Series_in):
    """
        Calculates the average of multiple time series.  Multiple time series
        are entered and a single time series representing the average at each
        observational period is returned.
        
        Usage qcts.AverageSeriesByElements(ds,Av_out,Series_in)
        ds: data structure
        Av_out: output variable to ds.  Example: 'Fg_Av'
        Series_in: input variable series in ds.  Example: ['Fg_1','Fg_2']
        """
    log.info(' Averaging series in '+str(Series_in)+' into '+str(Av_out))
    nSeries = len(Series_in)
    if nSeries==0:
        log.error('  AverageSeriesByElements: no input series specified')
        return
    if nSeries==1:
        TmpArr_data = ds.series[Series_in[0]]['Data'].copy()
        TmpArr_flag = ds.series[Series_in[0]]['Flag'].copy()
        Av_data = numpy.ma.masked_where(TmpArr_data==float(-9999),TmpArr_data)
        Mx_flag = TmpArr_flag
        SeriesNameString = Series_in[0]
        SeriesUnitString = ds.series[Series_in[0]]['Attr']['units']
    else:
        TmpArr_data = ds.series[Series_in[0]]['Data'].copy()
        TmpArr_flag = ds.series[Series_in[0]]['Flag'].copy()
        SeriesNameString = Series_in[0]
        Series_in.remove(Series_in[0])
        for ThisOne in Series_in:
            SeriesNameString = SeriesNameString+', '+ThisOne
            TmpArr_data = numpy.vstack((TmpArr_data,ds.series[ThisOne]['Data'].copy()))
            TmpArr_flag = numpy.vstack((TmpArr_flag,ds.series[ThisOne]['Flag'].copy()))
        TmpArr_data = numpy.ma.masked_where(TmpArr_data==float(-9999),TmpArr_data)
        Av_data = numpy.ma.average(TmpArr_data,axis=0)
        Mx_flag = numpy.min(TmpArr_flag,axis=0)
    DStr = 'Element-wise average of series '+SeriesNameString
    UStr = ds.series[Series_in[0]]['Attr']['units']
    SStr = ds.series[Series_in[0]]['Attr']['standard_name']
    qcutils.CreateSeries(ds,Av_out,Av_data,FList=Series_in,Descr=DStr,Units=UStr,Standard=SStr)

def CalculateAvailableEnergy(cf,ds,Fa_out='Fa',Fn_in='Fn',Fg_in='Fg'):
    """
        Calculate the average energy as Fn - G.
        
        Usage qcts.CalculateAvailableEnergy(ds,Fa_out,Fn_in,Fg_in)
        ds: data structure
        Fa_out: output available energy variable to ds.  Example: 'Fa'
        Fn_in: input net radiation in ds.  Example: 'Fn'
        Fg_in: input ground heat flux in ds.  Example: 'Fg'
        """
    log.info(' Calculating available energy from Fn and Fg')
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='Avail'):
        arg = ast.literal_eval(cf['FunctionArgs']['Avail'])
        Fa_out = arg[0]
        Fn_in = arg[1]
        Fg_in = arg[2]
    Fn,f = qcutils.GetSeriesasMA(ds,Fn_in)
    Fg,f = qcutils.GetSeriesasMA(ds,Fg_in)
    Fa = Fn - Fg
    qcutils.CreateSeries(ds,Fa_out,Fa,FList=[Fn_in,Fg_in],
                         Descr='Available energy using '+Fn_in+','+Fg_in,
                         Units='W/m2')

def CalculateFluxes(cf,ds,l3functions,Ta_name='Ta',ps_name='ps',Ah_name='Ah',wT_in='wT',wA_in='wA',wC_in='wC',uw_in='uw',vw_in='vw',Fh_out='Fh',Fe_out='Fe',Fc_out='Fc',Fm_out='Fm',ustar_out='ustar'):
    """
        Calculate the fluxes from the rotated covariances.
        
        Usage qcts.CalculateFluxes(ds)
        ds: data structure
        
        Pre-requisite: CoordRotation2D
        
        Accepts meteorological constants or variables
        """
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='CF'):
        args = ast.literal_eval(cf['FunctionArgs']['CF'])
        Ta_name = args[0]
        Ah_name = args[1]
        ps_name = args[2]
        wT_in = args[3]
        wA_in = args[4]
        wC_in = args[5]
        uw_in = args[6]
        vw_in = args[7]
        Fh_out = args[8]
        Fe_out = args[9]
        Fc_out = args[10]
        Fm_out = args[11]
        ustar_out = args[12]
    long_name = ''
    if 'Massman' in l3functions:
        long_name = ' and frequency response corrected'
    Ta,f = qcutils.GetSeriesasMA(ds,Ta_name)
    ps,f = qcutils.GetSeriesasMA(ds,ps_name)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_name)
    if 'rhom' in ds.series.keys():
        rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
    else:
        rhom = mf.densitymoistair(Ta,ps,Ah)
        
    log.info(' Calculating fluxes from covariances')
    if wT_in in ds.series.keys():
        wT,f = qcutils.GetSeriesasMA(ds,wT_in)
        Fh = rhom * c.Cpd * wT
        qcutils.CreateSeries(ds,Fh_out,Fh,FList=[wT_in],Descr='Sensible heat flux, rotated to natural wind coordinates'+long_name,Units='W/m2',Standard='surface_upward_sensible_heat_flux')
    else:
        log.error('  CalculateFluxes: wT not found in ds.series, Fh not calculated')
    if wA_in in ds.series.keys():
        wA,f = qcutils.GetSeriesasMA(ds,wA_in)
        if 'Lv' in ds.series.keys():
            Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
            Fe = Lv * wA / float(1000)
        else:
            Fe = c.Lv * wA / float(1000)
        qcutils.CreateSeries(ds,Fe_out,Fe,FList=[wA_in],Descr='Latent heat flux, rotated to natural wind coordinates'+long_name,Units='W/m2',Standard='surface_upward_latent_heat_flux')
    else:
        log.error('  CalculateFluxes: '+wA_in+' not found in ds.series, Fe not calculated')
    if wC_in in ds.series.keys():
        wC,f = qcutils.GetSeriesasMA(ds,wC_in)
        Fc = wC
        qcutils.CreateSeries(ds,Fc_out,Fc,FList=[wC_in],Descr='CO2 flux, rotated to natural wind coordinates'+long_name,Units='mg/m2/s')
    else:
        log.error('  CalculateFluxes: '+wC_in+' not found in ds.series, Fc_raw not calculated')
    if uw_in in ds.series.keys():
        if vw_in in ds.series.keys():
            uw,f = qcutils.GetSeriesasMA(ds,uw_in)
            vw,f = qcutils.GetSeriesasMA(ds,vw_in)
            vs = uw*uw + vw*vw
            Fm = rhom * numpy.ma.sqrt(vs)
            us = numpy.ma.sqrt(numpy.ma.sqrt(vs))
            qcutils.CreateSeries(ds,Fm_out,Fm,FList=[uw_in,vw_in],Descr='Momentum flux, rotated to natural wind coordinates'+long_name,Units='kg/m/s2')
            qcutils.CreateSeries(ds,ustar_out,us,FList=[uw_in,vw_in],Descr='Friction velocity, rotated to natural wind coordinates'+long_name,Units='m/s')
        else:
            log.error('  CalculateFluxes: wy not found in ds.series, Fm and ustar not calculated')
    else:
        log.error('  CalculateFluxes: wx not found in ds.series, Fm and ustar not calculated')

def CalculateLongwave(ds,Fl_out,Fl_in,Tbody_in):
    """
        Calculate the longwave radiation given the raw thermopile output and the
        sensor body temperature.
        
        Usage qcts.CalculateLongwave(ds,Fl_out,Fl_in,Tbody_in)
        ds: data structure
        Fl_out: output longwave variable to ds.  Example: 'Flu'
        Fl_in: input longwave in ds.  Example: 'Flu_raw'
        Tbody_in: input sensor body temperature in ds.  Example: 'Tbody'
        """
    log.info(' Calculating longwave radiation')
    Fl_raw,f = qcutils.GetSeriesasMA(ds,Fl_in)
    Tbody,f = qcutils.GetSeriesasMA(ds,Tbody_in)
    Fl = Fl_raw + c.sb*(Tbody + 273.15)**4
    qcutils.CreateSeries(ds,Fl_out,Fl,FList=[Fl_in,Tbody_in],
                         Descr='Calculated longwave radiation using '+Fl_in+','+Tbody_in,
                         Units='W/m2')

def CalculateMeteorologicalVariables(cf,ds,Ta_name='Ta',ps_name='ps',Ah_name='Ah'):
    """
        Add time series of meteorological variables based on fundamental
        relationships (Stull 1988)

        Usage qcts.CalculateMeteorologicalVariables(ds,Ta_name,ps_name,Ah_name)
        ds: data structure
        Ta_name: data series name for air temperature
        ps_name: data series name for pressure
        Ah_name: data series name for absolute humidity

        Variables added:
            rhom: density of moist air, mf.densitymoistair(Ta,ps,Ah)
            Lv: latent heat of vapourisation, mf.Lv(Ta)
            q: specific humidity, mf.specifichumidity(mr)
                where mr (mixing ratio) = mf.mixingratio(ps,vp)
            Cpm: specific heat of moist air, mf.specificheatmoistair(q)
            VPD: vapour pressure deficit, VPD = esat - e
        """
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='MetVars'):
        vars = ast.literal_eval(cf['FunctionArgs']['MetVars'])
        Ta_name = vars[0]
        ps_name = vars[1]
        Ah_name = vars[2]
    Ta,f = qcutils.GetSeriesasMA(ds,Ta_name)
    ps,f = qcutils.GetSeriesasMA(ds,ps_name)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_name)
    
    if 'e' in ds.series.keys():
        e,f = qcutils.GetSeriesasMA(ds,'e')
    else:
        e = mf.vapourpressure(Ah,Ta)
    qcutils.CreateSeries(ds,'e',e,FList=[Ta_name,Ah_name],Descr='Vapour pressure',Units='kPa',Standard='water_vapor_partial_pressure_in_air')
    if 'esat' in ds.series.keys():
        esat,f = qcutils.GetSeriesasMA(ds,'esat')
    else:
        esat = mf.es(Ta)
        qcutils.CreateSeries(ds,'esat',esat,FList=[Ta_name],Descr='saturation vapour pressure (HMP)',Units='kPa')
    if 'rhod' not in ds.series.keys():
        rhod = mf.densitydryair(Ta,ps)
        qcutils.CreateSeries(ds,'rhod',rhod,FList=[Ta_name,ps_name],Descr='Density of dry air',Units='kg/m3')
    if 'rhom' not in ds.series.keys():
        rhom = mf.densitymoistair(Ta,ps,Ah)
        qcutils.CreateSeries(ds,'rhom',rhom,FList=[Ta_name,ps_name,Ah_name],Descr='Density of moist air',Units='kg/m3',Standard='air_density')
    Lv = mf.Lv(Ta)
    mr = mf.mixingratio(ps,e)
    mrsat = mf.mixingratio(ps,esat)
    q = mf.specifichumidity(mr)
    qsat = mf.specifichumidity(mrsat)
    Cpm = mf.specificheatmoistair(q)
    VPD = esat - e
    SHD = qsat - q
    qcutils.CreateSeries(ds,'Lv',Lv,FList=[Ta_name],Descr='Latent heat of vapourisation',Units='J/kg')
    qcutils.CreateSeries(ds,'Cpm',Cpm,FList=[Ta_name,ps_name,Ah_name],Descr='Specific heat of moist air',Units='J/kg-K')
    qcutils.CreateSeries(ds,'q',q,FList=[Ta_name,ps_name,Ah_name],Descr='Specific humidity',Units='kg/kg',Standard='specific_humidity')
    qcutils.CreateSeries(ds,'VPD',VPD,FList=[Ta_name,Ah_name],Descr='Vapour pressure deficit',Units='kPa',Standard='water_vapor_saturation_deficit_in_air')
    qcutils.CreateSeries(ds,'SHD',SHD,FList=[Ta_name,Ah_name],Descr='Specific humidity deficit',Units='kg/kg')

def CalculateNetRadiation(ds,Fn_out,Fsd_in,Fsu_in,Fld_in,Flu_in):
    """
        Calculate the net radiation from the 4 components of the surface
        radiation budget.
        
        Usage qcts.CalculateNetRadiation(ds,Fn_out,Fsd_in,Fsu_in,Fld_in,Flu_in)
        ds: data structure
        Fn_out: output net radiation variable to ds.  Example: 'Fn_KZ'
        Fsd_in: input downwelling solar radiation in ds.  Example: 'Fsd'
        Fsu_in: input upwelling solar radiation in ds.  Example: 'Fsu'
        Fld_in: input downwelling longwave radiation in ds.  Example: 'Fld'
        Flu_in: input upwelling longwave radiation in ds.  Example: 'Flu'
        """
    log.info(' Calculating net radiation from 4 components')
    if Fsd_in in ds.series.keys() and Fsu_in in ds.series.keys() and Fld_in in ds.series.keys() and Flu_in in ds.series.keys():
        Fsd,f = qcutils.GetSeriesasMA(ds,Fsd_in)
        Fsu,f = qcutils.GetSeriesasMA(ds,Fsu_in)
        Fld,f = qcutils.GetSeriesasMA(ds,Fld_in)
        Flu,f = qcutils.GetSeriesasMA(ds,Flu_in)
        Fn = (Fsd - Fsu) + (Fld - Flu)
        qcutils.CreateSeries(ds,Fn_out,Fn,FList=[Fsd_in,Fsu_in,Fld_in,Flu_in],
                             Descr='Calculated net radiation using '+Fsd_in+','+Fsu_in+','+Fld_in+','+Flu_in,
                             Units='W/m2')
    else:
        nRecs = len(ds.series['xlDateTime']['Data'])
        ds.series[Fn_out] = {}
        ds.series[Fn_out]['Data'] = numpy.zeros(nRecs) + float(-9999)
        ds.series[Fn_out]['Flag'] = numpy.zeros(nRecs) + float(1)
        ds.series[Fn_out]['Attr'] = {}
        ds.series[Fn_out]['Attr']['Description'] = 'Calculated net radiation (one or more components missing)'
        ds.series[Fn_out]['Attr']['units'] = 'W/m2'

def ComputeDailySums(cf,ds,SumList,SubSumList,MinMaxList,MeanList,SoilList):
    """
        Computes daily sums, mininima and maxima on a collection variables in
        the L4 dataset containing gap filled fluxes.  Sums are computed only
        when the number of daily 30-min observations is equal to 48 (i.e., no
        missing data) to avoid biasing.  Output to an excel file that specified
        in the control file.
        
        Usage qcts.ComputeDailySums(cf,ds)
        cf: control file
        ds: data structure
        
        Parameters loaded from control file:
            M1st: dataset start month
            M2nd: dataset end month
            SumList: list of variables to be summed
            SubSumList: list of variables to sum positive and negative observations separately
            MinMaxList: list of variables to compute daily min & max
            SoilList: list of soil moisture measurements groups
            SW0, SW10, etc: list of soil moisture sensors at a common level (e.g., surface, 10cm, etc)
        
        Default List of sums:
            Rain, ET, Fe_MJ, Fh_MJ, Fg_MJ, Fld_MJ, Flu_MJ, Fnr_MJ, Fsd_MJ,
            Fsu_MJ, Fc_g, Fc_mmol
        Default List of sub-sums (sums split between positive and negative observations)
            Fe_MJ, Fh_MJ, Fg_MJ
        Default List of min/max:
            Ta_HMP, Vbat, Tpanel, Fc_mg, Fc_umol
        Default List of soil moisture measurements:
        """
    OutList = []
    SumOutList = []
    SubOutList = []
    MinMaxOutList = []
    MeanOutList = []
    SoilOutList = []
    
    for ThisOne in SubSumList:
        if ThisOne not in SumList:
            SumList.append(ThisOne)
    
    for ThisOne in SumList:
        if ThisOne == 'ET':
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='ETin'):
                Invar = ast.literal_eval(cf['Sums']['ETin'])
            else:
                Invar = ['Fe']
            Fe,f = qcutils.GetSeriesasMA(ds,Invar[0])
            if 'Lv' in ds.series.keys():
                Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
            else:
                Lv = c.Lv
            ET = Fe * 60 * 30 * 1000 / (Lv * c.rho_water)  # mm/30min for summing
            qcutils.CreateSeries(ds,'ET',ET,FList=Invar,Descr='Evapotranspiration Flux',Units='mm')
            SumOutList.append('ET')
            OutList.append('ET')
            if ThisOne in SubSumList:
                SubOutList.append('ET')
        elif ThisOne == 'Energy':
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='Energyin'):
                EnergyIn = ast.literal_eval(cf['Sums']['Energyin'])
            else:
                EnergyIn = ['Fe', 'Fh', 'Fg']
            Fe,f = qcutils.GetSeriesasMA(ds,EnergyIn[0])
            Fh,f = qcutils.GetSeriesasMA(ds,EnergyIn[1])
            Fg,f = qcutils.GetSeriesasMA(ds,EnergyIn[2])
            EnergyOut = ['Fe_MJ','Fh_MJ','Fg_MJ']
            for index in range(0,3):
                convert_energy(ds,EnergyIn[index],EnergyOut[index])
                OutList.append(EnergyOut[index])
                SumOutList.append(EnergyOut[index])
                if ThisOne in SubSumList:
                    SubOutList.append(EnergyOut[index])
        elif ThisOne == 'Radiation':
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='Radin'):
                RadiationIn = ast.literal_eval(cf['Sums']['Radin'])
            else:
                RadiationIn = ['Fld','Flu','Fn','Fsd','Fsu']
            Fld,f = qcutils.GetSeriesasMA(ds,RadiationIn[0])
            Flu,f = qcutils.GetSeriesasMA(ds,RadiationIn[1])
            Fnr,f = qcutils.GetSeriesasMA(ds,RadiationIn[2])
            Fsd,f = qcutils.GetSeriesasMA(ds,RadiationIn[3])
            Fsu,f = qcutils.GetSeriesasMA(ds,RadiationIn[4])
            RadiationOut = ['Fld_MJ','Flu_MJ','Fnr_MJ','Fsd_MJ','Fsu_MJ']
            for index in range(0,5):
                convert_energy(ds,RadiationIn[index],RadiationOut[index])
                OutList.append(RadiationOut[index])
                SumOutList.append(RadiationOut[index])
                if ThisOne in SubSumList:
                    log.error('  Subsum: Negative radiation flux not defined')
        elif ThisOne == 'Carbon':
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='Cin'):
                CIn = ast.literal_eval(cf['Sums']['Cin'])
            else:
                CIn = ['Fc']
            
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='GPPin'):
                GPPIn = ast.literal_eval(cf['Sums']['GPPin'])
                GPP,f = qcutils.GetSeriesasMA(ds,GPPIn[0])
                Re,f = qcutils.GetSeriesasMA(ds,GPPIn[1])
                GPP_mmol = GPP * 1800 / 1000
                Re_mmol = Re * 1800 / 1000
                qcutils.CreateSeries(ds,'GPP_mmol',GPP_mmol,FList=[GPPIn[0]],Descr='Cumulative 30-min GPP',Units='mmol/m2',Standard='gross_primary_productivity_of_carbon')
                qcutils.CreateSeries(ds,'Re_mmol',Re_mmol,FList=[GPPIn[1]],Descr='Cumulative 30-min Re',Units='mmol/m2')
                GPPOut = ['GPP_mmol','Re_mmol']
                for listindex in range(0,2):
                    OutList.append(GPPOut[listindex])
                    SumOutList.append(GPPOut[listindex])
            
            Fc,f = qcutils.GetSeriesasMA(ds,CIn[0])
            Fc_umol = Fc * 1e6 / (1000 * 44)               # umol/m2-s for min/max
            Fc_mmol = Fc_umol * 1800 / 1000                # mmol/m2-30min for summing
            Fc_g = Fc * 1800 / 1000                        # g/m2-30min for summing
            qcutils.CreateSeries(ds,'Fc_mmol',Fc_mmol,FList=CIn,Descr='Cumulative 30-min Flux',Units='mmol/m2',Standard='surface_upward_mole_flux_of_carbon_dioxide')
            qcutils.CreateSeries(ds,'Fc_g',Fc_g,FList=CIn,Descr='Cumulative 30-min Flux',Units='g/m2')
            COut = ['Fc_g','Fc_mmol']
            for listindex in range(0,2):
                OutList.append(COut[listindex])
                SumOutList.append(COut[listindex])
                if ThisOne in SubSumList:
                    SubOutList.append(COut[listindex])
        elif ThisOne == 'PM':
            if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='PMin'):
                get_stomatalresistance(cf,ds)
                Gst_mmol,f = qcutils.GetSeriesasMA(ds,'Gst')   # mmol/m2-s
                Gst_mol =  Gst_mmol * 1800 / 1000                 # mol/m2-30min for summing
                qcutils.CreateSeries(ds,'Gst_mol',Gst_mol,FList=['Gst'],Descr='Cumulative 30-min Bulk Stomatal Conductance',Units='mol/m2')
                PMout = 'Gst_mol'
                if PMout not in OutList:
                    OutList.append(PMout)
                if ThisOne in SubSumList:
                    log.error('  Subsum: Negative bulk stomatal conductance not defined')
                SumOutList.append(PMout)
            else:
                info.error('  Penman-Monteith Daily sums: input Source not defined')
        else:
            OutList.append(ThisOne)
            SumOutList.append(ThisOne)
    
    for ThisOne in MinMaxList:
        if ThisOne == 'Carbon':
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='Cin'):
                CIn = ast.literal_eval(cf['Sums']['Cin'])
            else:
                CIn = ['Fc']
            Fc,f = qcutils.GetSeriesasMA(ds,CIn[0])
            Fc_umol = Fc * 1e6 / (1000 * 44)               # umol/m2-s for min/max
            qcutils.CreateSeries(ds,'Fc_umol',Fc_umol,FList=CIn,Descr='Average Flux',Units='umol/(m2 s)',Standard='surface_upward_mole_flux_of_carbon_dioxide')
            qcutils.CreateSeries(ds,'Fc_mg',Fc,FList=CIn,Descr='Average Flux',Units='mg/(m2 s)')
            COut = ['Fc_mg','Fc_umol']
            for listindex in range(0,2):
                OutList.append(COut[listindex])
                MinMaxOutList.append(COut[listindex])
        elif ThisOne == 'PM':
            if ThisOne not in SumList:
                if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='PMin'):
                        get_stomatalresistance(cf,ds,'L4')
                else:
                    info.error('  Penman-Monteith Daily min/max: input Source not defined')
            else:
                PMout = ['rst','Gst']
                for listindex in range(0,2):
                    if PMout[listindex] not in OutList:
                        OutList.append(PMout[listindex])
                    MinMaxOutList.append(PMout[listindex])
        else:
            if ThisOne not in OutList:
                OutList.append(ThisOne)
            MinMaxOutList.append(ThisOne)
    
    for ThisOne in MeanList:
        if ThisOne == 'Energy' or ThisOne == 'Carbon' or ThisOne == 'Radiation':
            log.error(' Mean error: '+ThisOne+' to be placed in SumList')
        elif ThisOne == 'PM':
            if ThisOne not in MinMaxList:
                if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='PMin'):
                    get_stomatalresistance(cf,ds,'L4')
                    PMout = ['rst','Gst']
                    for listindex in range(0,2):
                        if PMout[listindex] not in OutList:
                            OutList.append(PMout[listindex])
                        MeanOutList.append(PMout[listindex])
                else:
                    info.error('  Penman-Monteith Daily mean: input Source not defined')
            else:
                PMout = ['rst','Gst']
                for listindex in range(0,2):
                    if PMout[listindex] not in OutList:
                        OutList.append(PMout[listindex])
                    MeanOutList.append(PMout[listindex])
        else:
            MeanOutList.append(ThisOne)
            if ThisOne not in OutList:
                OutList.append(ThisOne)


    if len(SoilList) > 0:
        for ThisOne in SoilList:
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne=ThisOne):
                vars = ast.literal_eval(cf['Sums'][ThisOne])
                for index in range(0,len(vars)):
                    SoilOutList.append(vars[index])
                OutList.append(ThisOne)
    
    xlFileName = cf['Files']['L4']['xlSumFilePath']+cf['Files']['L4']['xlSumFileName']
    xlFile = xlwt.Workbook()
    
    for ThisOne in OutList:
        xlSheet = xlFile.add_sheet(ThisOne)
        xlCol = 0
        if ThisOne in SumOutList:
            if ThisOne in SubOutList:
                write_sums(cf,ds,ThisOne,xlCol,xlSheet,DoSum='True',DoSubSum='True')
            else:
                write_sums(cf,ds,ThisOne,xlCol,xlSheet,DoSum='True')
        
        if ThisOne in MinMaxOutList:
            if ThisOne in MeanOutList:
                write_sums(cf,ds,ThisOne,xlCol,xlSheet,DoMinMax='True',DoMean='True')
            else:
                write_sums(cf,ds,ThisOne,xlCol,xlSheet,DoMinMax='True')
        
        if ThisOne in MeanOutList:
            if ThisOne not in MinMaxOutList:
                write_sums(cf,ds,ThisOne,xlCol,xlSheet,DoMean='True')
        
        if ThisOne in SoilList:
            soilvars = ast.literal_eval(cf['Sums'][ThisOne])
            for n in soilvars:
                if n == soilvars[0]:
                    xC,xS = write_sums(cf,ds,n,xlCol,xlSheet,DoSoil='True')
                else:
                    xC,xS = write_sums(cf,ds,n,xlCol,xS,DoSoil='True')
                xlCol = xC + 1
        
    log.info(' Saving Excel file '+xlFileName)
    xlFile.save(xlFileName)

    log.info(' Daily sums: All done')

def convert_energy(ds,InVar,OutVar):
    """
        Integrate energy flux over 30-min time period.
        Converts flux in W/m2 to MJ/(m2 30-min)
        
        Usage qcts.convert_energy(ds,InVar,OutVar)
        ds: data structure
        InVar: name of input variable.  Example: 'Fe_gapfilled'
        OutVar: name of output variable.  Example: 'Fe_MJ'
        """
    Wm2,f = qcutils.GetSeriesasMA(ds,InVar)
    MJ = Wm2 * 1800 / 1e6
    qcutils.CreateSeries(ds,OutVar,MJ,FList=[InVar],Descr=ds.series[InVar]['Attr']['long_name'],Units='MJ/m2',Standard=ds.series[InVar]['Attr']['standard_name'])

def CoordRotation2D(cf,ds):
    """
        2D coordinate rotation to force v = w = 0.  Based on Lee et al, Chapter
        3 of Handbook of Micrometeorology.  This routine does not do the third
        rotation to force v'w' = 0.
        
        Usage qcts.CoordRotation2D(ds)
        ds: data structure
        """
    log.info(' Applying 2D coordinate rotation to wind components and covariances')
    # get the raw wind velocity components
    Ux,f = qcutils.GetSeriesasMA(ds,'Ux')          # longitudinal component in CSAT coordinate system
    Uy,f = qcutils.GetSeriesasMA(ds,'Uy')          # lateral component in CSAT coordinate system
    Uz,f = qcutils.GetSeriesasMA(ds,'Uz')          # vertical component in CSAT coordinate system
    # get the raw covariances
    UxUz,f = qcutils.GetSeriesasMA(ds,'UxUz')      # covariance(Ux,Uz)
    UyUz,f = qcutils.GetSeriesasMA(ds,'UyUz')      # covariance(Uy,Uz)
    UxUy,f = qcutils.GetSeriesasMA(ds,'UxUy')      # covariance(Ux,Uy)
    UyUy,f = qcutils.GetSeriesasMA(ds,'UyUy')      # variance(Uy)
    UxUx,f = qcutils.GetSeriesasMA(ds,'UxUx')      # variance(Ux)
    UzC,f = qcutils.GetSeriesasMA(ds,'UzC')        # covariance(Uz,C)
    UzA,f = qcutils.GetSeriesasMA(ds,'UzA')        # covariance(Uz,A)
    UzT,f = qcutils.GetSeriesasMA(ds,'UzT')        # covariance(Uz,T)
    UxC,f = qcutils.GetSeriesasMA(ds,'UxC')        # covariance(Ux,C)
    UyC,f = qcutils.GetSeriesasMA(ds,'UyC')        # covariance(Uy,C)
    UxA,f = qcutils.GetSeriesasMA(ds,'UxA')        # covariance(Ux,A)
    UyA,f = qcutils.GetSeriesasMA(ds,'UyA')        # covariance(Ux,A)
    UxT,f = qcutils.GetSeriesasMA(ds,'UxT')        # covariance(Ux,T)
    UyT,f = qcutils.GetSeriesasMA(ds,'UyT')        # covariance(Uy,T)
    nRecs = len(Ux)
    # get the 2D and 3D wind speeds
    ws2d = numpy.ma.sqrt(Ux**2 + Uy**2)
    ws3d = numpy.ma.sqrt(Ux**2 + Uy**2 + Uz**2)
    # get the sine and cosine of the angles through which to rotate
    #  - first we rotate about the Uz axis by eta to get v = 0
    #  - then we rotate about the v axis by theta to get w = 0
    ce = Ux/ws2d          # cos(eta)
    se = Uy/ws2d          # sin(eta)
    ct = ws2d/ws3d        # cos(theta)
    st = Uz/ws3d          # sin(theta)
    # get the rotation angles
    theta = numpy.rad2deg(numpy.arctan2(st,ct))
    eta = numpy.rad2deg(numpy.arctan2(se,ce))
    # do the wind velocity components first
    u = Ux*ct*ce + Uy*ct*se + Uz*st           # longitudinal component in natural wind coordinates
    v = Uy*ce - Ux*se                         # lateral component in natural wind coordinates
    w = Uz*ct - Ux*st*ce - Uy*st*se           # vertical component in natural wind coordinates
    # now do the covariances
    wT = UzT*ct - UxT*st*ce - UyT*st*se       # covariance(w,T) in natural wind coordinate system
    wA = UzA*ct - UxA*st*ce - UyA*st*se       # covariance(w,A) in natural wind coordinate system
    wC = UzC*ct - UxC*st*ce - UyC*st*se       # covariance(w,C) in natural wind coordinate system
    uw = UxUz*ct - UxUx*st*ce - UxUy*st*se    # covariance(w,x) in natural wind coordinate system
    vw = UyUz*ct - UxUy*st*ce - UyUy*st*se    # covariance(w,y) in natural wind coordinate system
    # store the rotated quantities in the nc object
    qcutils.CreateSeries(ds,'eta',eta,FList=['Ux','Uy','Uz'],Descr='Horizontal rotation angle',Units='deg')
    qcutils.CreateSeries(ds,'theta',theta,FList=['Ux','Uy','Uz'],Descr='Vertical rotation angle',Units='deg')
    qcutils.CreateSeries(ds,'u',u,FList=['Ux','Uy','Uz'],Descr='Longitudinal component of wind-speed in natural wind coordinates',Units='m/s')
    qcutils.CreateSeries(ds,'v',v,FList=['Ux','Uy','Uz'],Descr='Lateral component of wind-speed in natural wind coordinates',Units='m/s')
    qcutils.CreateSeries(ds,'w',w,FList=['Ux','Uy','Uz'],Descr='Vertical component of wind-speed in natural wind coordinates',Units='m/s')
    qcutils.CreateSeries(ds,'wT',wT,FList=['Ux','Uy','Uz','UxT','UyT','UzT'],
                         Descr='Kinematic heat flux, rotated to natural wind coordinates',Units='mC/s')
    qcutils.CreateSeries(ds,'wA',wA,FList=['Ux','Uy','Uz','UxA','UyA','UzA'],
                         Descr='Kinematic vapour flux, rotated to natural wind coordinates',Units='g/m2/s')
    qcutils.CreateSeries(ds,'wC',wC,FList=['Ux','Uy','Uz','UxC','UyC','UzC'],
                         Descr='Kinematic CO2 flux, rotated to natural wind coordinates',Units='mg/m2/s')
    qcutils.CreateSeries(ds,'uw',uw,FList=['Ux','Uy','Uz','UxUz','UxUx','UxUy'],
                         Descr='Momentum flux X component, corrected to natural wind coordinates',Units='m2/s2')
    qcutils.CreateSeries(ds,'vw',vw,FList=['Ux','Uy','Uz','UyUz','UxUy','UyUy'],
                         Descr='Momentum flux Y component, corrected to natural wind coordinates',Units='m2/s2')
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='RotateFlag') and cf['General']['RotateFlag'] == 'True':
        keys = ['eta','theta','u','v','w','wT','wA','wC','uw','vw']
        for ThisOne in keys:
            testseries,f = qcutils.GetSeriesasMA(ds,ThisOne)
            mask = numpy.ma.getmask(testseries)
            index = numpy.where(mask.astype(int)==1)
            ds.series[ThisOne]['Flag'][index] = 11

def CorrectFcForStorage(cf,ds,Fc_out,Fc_in,CO2_in):
    """
    Correct CO2 flux for storage in the air column beneath the CO2 instrument.  This
    routine assumes the air column between the sensor and the surface is well mixed.
    
    Usage qcts.CorrectFcForStorage(cf,ds,Fc_out,Fc_in,CO2_in)
    cf: control file object    
    ds: data structure
    Fc_out: series label of the corrected CO2 flux
    Fc_in: series label of the uncorrected CO2 flux
    CO2_in: series label of the CO2 concentration
    
    Parameters loaded from control file:
        zm: measurement height, m
        dt: timestep, seconds
    """
    log.info(' Correcting Fc for storage')
    if 'General' in cf:
        if 'zms' in cf['General']:
            zms = float(cf['General']['zms'])
            Cc,Cc_flag = qcutils.GetSeriesasMA(ds,CO2_in,si=0,ei=-1)
            rhod,rhod_flag = qcutils.GetSeriesasMA(ds,'rhod',si=0,ei=-1)
            Fc,Fc_flag = qcutils.GetSeriesasMA(ds,Fc_in,si=0,ei=-1)
            dc = numpy.ma.ediff1d(Cc,to_begin=0)                      # CO2 concentration difference from timestep to timestep
            dt=86400*numpy.ediff1d(ds.series['xlDateTime']['Data'],to_begin=float(30)/1440)    # time step in seconds from the Excel datetime values
            Fc_storage = zms*rhod*dc/dt                               # calculate the CO2 flux based on storage below the measurement height
            descr = 'Fc infered from CO2 storage using single point CO2 measurement'
            units = qcutils.GetUnitsFromds(ds,Fc_in)
            qcutils.CreateSeries(ds,'Fc_storage',Fc_storage,FList=[Fc_in,CO2_in],Descr=descr,Units=units)
            Fc = Fc + Fc_storage
            descr = 'Fc_wpl corrected for storage using single point CO2 measurement'
            qcutils.CreateSeries(ds,Fc_out,Fc,FList=[Fc_in,CO2_in],Descr=descr,Units=units)
        else:
            print 'CorrectFcForStorage: zms expected but not found in General section of control file'
    else:
        print 'CorrectFcForStorage: section General expected but not found in control file'

def CorrectFg(cf,ds):
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='CFgArgs'):
        List = cf['FunctionArgs']['CFgArgs'].keys()
        for i in range(len(List)):
            CFgArgs = ast.literal_eval(cf['FunctionArgs']['CFgArgs'][str(i)])
            CorrectFgForStorage(cf,ds,Fg_out=CFgArgs[0],Fg_in=CFgArgs[1],Ts_in=CFgArgs[2],SWC_in=CFgArgs[3])
        return
    
    CorrectFgForStorage(cf,ds)

def CorrectFgForStorage(cf,ds,Fg_out='Fg',Fg_in='Fg',Ts_in='Ts',SWC_in='Sws'):
    """
        Correct ground heat flux for storage in the layer above the heat flux plate
        
        Usage qcts.CorrectFgForStorage(cf,ds,Fg_out,Fg_in,Ts_in,Sws_in)
        ds: data structure
        Fg_out: output soil heat flux variable to ds.  Example: 'Fg'
        Fg_in: input soil heat flux in ds.  Example: 'Fg_Av'
        Ts_in: input soil temperature in ds.  Example: 'Ts'
        
        Parameters loaded from control file:
            FgDepth: Depth of soil heat flux plates, m
            BulkDensity: soil bulk density, kg/m3
            OrganicContent: soil organic content, fraction
            SwsDefault: default value of soil moisture content used when no sensors present
        """
    log.info(' Correcting soil heat flux for storage')
    d = max(0.0,min(0.5,float(cf['Soil']['FgDepth'])))
    bd = max(1200.0,min(2500.0,float(cf['Soil']['BulkDensity'])))
    oc = max(0.0,min(1.0,float(cf['Soil']['OrganicContent'])))
    mc = 1.0 - oc
    Fg,f = qcutils.GetSeriesasMA(ds,Fg_in)        # raw soil heat flux
    nRecs = len(Fg)                               # number of records in series
    Ts,f = qcutils.GetSeriesasMA(ds,Ts_in)        # soil temperature
    #Sws,f = qcutils.GetSeriesasMA(ds,Sws_in)      # volumetric soil moisture
    Sws_default = min(1.0,max(0.0,float(cf['Soil']['SwsDefault'])))
    if len(SWC_in) == 0:
        slist = []
        if qcutils.cfkeycheck(cf,Base='Soil',ThisOne='SwsSeries'):
            slist = ast.literal_eval(cf['Soil']['SwsSeries'])
        if len(slist)==0:
            Sws = numpy.ones(nRecs)*Sws_default
        elif len(slist)==1:
            Sws,f = qcutils.GetSeriesasMA(ds,slist[0])
        else:
            MergeSeries(ds,'Sws',slist,[0,10])
            Sws,f = qcutils.GetSeriesasMA(ds,'Sws')
    else:
        slist = SWC_in
        Sws,f = qcutils.GetSeriesasMA(ds,SWC_in)
    log.info('  CorrectForStorage: Sws_in is '+str(slist))
    iom = numpy.where(numpy.mod(f,10)!=0)[0]
    if len(iom)!=0:
        Sws[iom] = Sws_default
    dTs = numpy.ma.zeros(nRecs)
    dTs[1:] = numpy.diff(Ts)
    dt = numpy.ma.zeros(nRecs)
    dt[1:] = numpy.diff(date2num(ds.series['DateTime']['Data']))*float(86400)
    dt[0] = dt[1]
    Cs = mc*bd*c.Cd + oc*bd*c.Co + Sws*c.rho_water*c.Cw
    S = Cs*(dTs/dt)*d
    Fg_o = Fg + S
    qcutils.CreateSeries(ds,Fg_out,Fg_o,FList=[Fg_in],Descr='Soil heat flux corrected for storage',Units='W/m2',Standard='downward_heat_flux_in_soil')
    qcutils.CreateSeries(ds,'S',S,FList=[Fg_in],Descr='Soil heat flux storage',Units='W/m2')
    qcutils.CreateSeries(ds,'Cs',Cs,FList=[Fg_in],Descr='Specific heat capacity',Units='J/m3/K')

def CorrectSWC(cf,ds):
    """
        Correct soil moisture data using calibration curve developed from
        collected soil samples.  To avoid unrealistic or unphysical extreme
        values upon extrapolation, exponential and logarithmic using ln
        functions are applied to small and large values, respectively.
        Threshold values where one model replaces the other is determined where
        the functions cross.  The logarithmic curve is constrained at with a
        point at which the soil measurement = field porosity and the sensor
        measurement is maximised under saturation at field capacity.
        
        Usage qcts.CorrectSWC(cf,ds)
        cf: control file
        ds: data structure
        
        Parameters loaded from control file:
            SWCempList: list of raw CS616 variables
            SWCoutList: list of corrected CS616 variables
            SWCattr:  list of meta-data attributes for corrected CS616 variables
            SWC_a0: parameter in logarithmic model, actual = a1 * ln(sensor) + a0
            SWC_a1: parameter in logarithmic model, actual = a1 * ln(sensor) + a0
            SWC_b0: parameter in exponential model, actual = b0 * exp(b1 * sensor)
            SWC_b1: parameter in exponential model, actual = b0 * exp(b1 * sensor)
            SWC_t: threshold parameter for switching from exponential to logarithmic model
            TDRempList: list of raw CS610 variables
            TDRoutList: list of corrected CS610 variables
            TDRattr:  list of meta-data attributes for corrected CS610 variables
            TDRlinList: list of deep TDR probes requiring post-hoc linear correction to match empirical samples
            TDR_a0: parameter in logarithmic model, actual = a1 * ln(sensor) + a0
            TDR_a1: parameter in logarithmic model, actual = a1 * ln(sensor) + a0
            TDR_b0: parameter in exponential model, actual = b0 * exp(b1 * sensor)
            TDR_b1: parameter in exponential model, actual = b0 * exp(b1 * sensor)
            TDR_t: threshold parameter for switching from exponential to logarithmic model
        """
    SWCempList = ast.literal_eval(cf['Soil']['empSWCin'])
    SWCoutList = ast.literal_eval(cf['Soil']['empSWCout'])
    SWCattr = ast.literal_eval(cf['Soil']['SWCattr'])
    if cf['Soil']['TDR']=='Yes':
        TDRempList = ast.literal_eval(cf['Soil']['empTDRin'])
        TDRoutList = ast.literal_eval(cf['Soil']['empTDRout'])
        TDRlinList = ast.literal_eval(cf['Soil']['linTDRin'])
        TDRattr = ast.literal_eval(cf['Soil']['TDRattr'])
        TDR_a0 = float(cf['Soil']['TDR_a0'])
        TDR_a1 = float(cf['Soil']['TDR_a1'])
        TDR_b0 = float(cf['Soil']['TDR_b0'])
        TDR_b1 = float(cf['Soil']['TDR_b1'])
        TDR_t = float(cf['Soil']['TDR_t'])
    SWC_a0 = float(cf['Soil']['SWC_a0'])
    SWC_a1 = float(cf['Soil']['SWC_a1'])
    SWC_b0 = float(cf['Soil']['SWC_b0'])
    SWC_b1 = float(cf['Soil']['SWC_b1'])
    SWC_t = float(cf['Soil']['SWC_t'])
    
    for i in range(len(SWCempList)):
        log.info('  Applying empirical correction to '+SWCempList[i])
        invar = SWCempList[i]
        outvar = SWCoutList[i]
        attr = SWCattr[i]
        Sws,f = qcutils.GetSeriesasMA(ds,invar)
        
        nRecs = len(Sws)
        
        Sws_out = numpy.ma.empty(nRecs,float)
        Sws_out.fill(-9999)
        Sws_out.mask = numpy.ma.empty(nRecs,bool)
        Sws_out.mask.fill(True)
        
        index_high = numpy.ma.where((Sws.mask == False) & (Sws > SWC_t))[0]
        index_low = numpy.ma.where((Sws.mask == False) & (Sws < SWC_t))[0]
        
        Sws_out[index_low] = SWC_b0 * numpy.exp(SWC_b1 * Sws[index_low])
        Sws_out[index_high] = (SWC_a1 * numpy.log(Sws[index_high])) + SWC_a0
        
        qcutils.CreateSeries(ds,outvar,Sws_out,FList=[invar],Descr=attr,Units='cm3 water/cm3 soil',Standard='soil_moisture_content')
    if cf['Soil']['TDR']=='Yes':
        for i in range(len(TDRempList)):
            log.info('  Applying empirical correction to '+TDRempList[i])
            invar = TDRempList[i]
            outvar = TDRoutList[i]
            attr = TDRattr[i]
            Sws,f = qcutils.GetSeriesasMA(ds,invar)
            
            nRecs = len(Sws)
            
            Sws_out = numpy.ma.empty(nRecs,float)
            Sws_out.fill(-9999)
            Sws_out.mask = numpy.ma.empty(nRecs,bool)
            Sws_out.mask.fill(True)
            
            index_high = numpy.ma.where((Sws.mask == False) & (Sws > TDR_t))[0]
            index_low = numpy.ma.where((Sws.mask == False) & (Sws < TDR_t))[0]
            
            Sws_out[index_low] = TDR_b0 * numpy.exp(TDR_b1 * Sws[index_low])
            Sws_out[index_high] = (TDR_a1 * numpy.log(Sws[index_high])) + TDR_a0
            
            qcutils.CreateSeries(ds,outvar,Sws_out,FList=[invar],Descr=attr,Units='cm3 water/cm3 soil',Standard='soil_moisture_content')

def CorrectWindDirection(cf,ds,Wd_in):
    """
        Correct wind direction for mis-aligned sensor direction.
        
        Usage qcts.CorrectWindDirection(cf,ds,Wd_in)
        cf: control file
        ds: data structure
        Wd_in: input/output wind direction variable in ds.  Example: 'Wd_CSAT'
        """
    log.info(' Correcting wind direction')
    Wd,f = qcutils.GetSeriesasMA(ds,Wd_in)
    ldt = ds.series['DateTime']['Data']
    KeyList = cf['Variables'][Wd_in]['Correction'].keys()
    for i in range(len(KeyList)):
        ItemList = ast.literal_eval(cf['Variables'][Wd_in]['Correction'][str(i)])
        try:
            si = ldt.index(datetime.datetime.strptime(ItemList[0],'%Y-%m-%d %H:%M'))
        except ValueError:
            si = 0
        try:
            ei = ldt.index(datetime.datetime.strptime(ItemList[1],'%Y-%m-%d %H:%M')) + 1
        except ValueError:
            ei = -1
        Correction = float(ItemList[2])
        Wd[si:ei] = Wd[si:ei] + Correction
    Wd = numpy.mod(Wd,float(360))
    ds.series[Wd_in]['Data'] = numpy.ma.filled(Wd,float(-9999))

def do_attributes(cf,ds):
    """
        Import attriubes in xl2nc control file to netCDF dataset.  Included
        global and variable attributes.  Also attach flag definitions to global
        meta-data for reference.
        
        Usage qcts.do_attributes(cf,ds)
        cf: control file
        ds: data structure
        """
    log.info(' Getting the attributes given in control file')
    if 'Global' in cf.keys():
        for gattr in cf['Global'].keys():
            ds.globalattributes[gattr] = cf['Global'][gattr]
        ds.globalattributes['Flag0'] = 'Good data'
        ds.globalattributes['Flag1'] = 'QA/QC: -9999 in level 1 dataset'
        ds.globalattributes['Flag2'] = 'QA/QC: L2 Range Check'
        ds.globalattributes['Flag3'] = 'QA/QC: CSAT Diagnostic'
        ds.globalattributes['Flag4'] = 'QA/QC: LI7500 Diagnostic'
        ds.globalattributes['Flag5'] = 'QA/QC: L2 Diurnal SD Check'
        ds.globalattributes['Flag6'] = 'QA/QC: Excluded Dates'
        ds.globalattributes['Flag7'] = 'QA/QC: Excluded Hours'
        ds.globalattributes['Flag10'] = 'Corrections: Apply Linear'
        ds.globalattributes['Flag11'] = 'Corrections/Combinations: Coordinate Rotation (Ux, Uy, Uz, UxT, UyT, UzT, UxA, UyA, UzA, UxC, UyC, UzC, UxUz, UxUx, UxUy, UyUz, UxUy, UyUy)'
        ds.globalattributes['Flag12'] = 'Corrections/Combinations: Massman Frequency Attenuation Correction (Coord Rotation, Tv_CSAT, Ah_HMP, ps)'
        ds.globalattributes['Flag13'] = 'Corrections/Combinations: Virtual to Actual Fh (Coord Rotation, Massman, Ta_HMP)'
        ds.globalattributes['Flag14'] = 'Corrections/Combinations: WPL correction for flux effects on density measurements (Coord Rotation, Massman, Fhv to Fh, Cc_7500_Av)'
        ds.globalattributes['Flag15'] = 'Corrections/Combinations: Ta from Tv'
        ds.globalattributes['Flag16'] = 'Corrections/Combinations: L3 Range Check'
        ds.globalattributes['Flag17'] = 'Corrections/Combinations: L3 Diurnal SD Check'
        ds.globalattributes['Flag18'] = 'Corrections/Combinations: u* filter'
        ds.globalattributes['Flag19'] = 'Corrections/Combinations: Gap coordination'
        ds.globalattributes['Flag30'] = 'GapFilling: Flux Gap Filled by ANN (SOLO)'
        ds.globalattributes['Flag31'] = 'GapFilling: Flux Gap not Filled by ANN'
        ds.globalattributes['Flag32'] = 'GapFilling: Met Gap Filled from Climatology'
        ds.globalattributes['Flag33'] = 'GapFilling: Gap Filled from Ratios'
        ds.globalattributes['Flag34'] = 'GapFilling: Gap Filled by Interpolation'
        ds.globalattributes['Flag35'] = 'GapFilling: Gap Filled by Replacement'
        ds.globalattributes['Flag36'] = 'GapFilling: u* from Fh'
        ds.globalattributes['Flag37'] = 'GapFilling: u* not from Fh'
        ds.globalattributes['Flag38'] = 'GapFilling: L4 Range Check'
        ds.globalattributes['Flag39'] = 'GapFilling: L4 Diurnal SD Check'
        ds.globalattributes['Flag51'] = 'albedo: bad Fsd < threshold (290 W/m2 default) only if bad time flag (31) not set'
        ds.globalattributes['Flag52'] = 'albedo: bad time flag (not midday 10.00 to 14.00)'
        ds.globalattributes['Flag61'] = 'Penman-Monteith: bad rst (rst < 0) only if bad Uavg (35), bad Fe (33) and bad Fsd (34) flags not set'
        ds.globalattributes['Flag62'] = 'Penman-Monteith: bad Fe < threshold (0 W/m2 default) only if bad Fsd (34) flag not set'
        ds.globalattributes['Flag63'] = 'Penman-Monteith: bad Fsd < threshold (10 W/m2 default)'
        ds.globalattributes['Flag64'] = 'Penman-Monteith: Uavg == 0 (undefined aerodynamic resistance under calm conditions) only if bad Fe (33) and bad Fsd (34) flags not set'
        ds.globalattributes['Flag70'] = 'Partitioning Night: Re computed from exponential temperature response curves'
        ds.globalattributes['Flag80'] = 'Partitioning Day: GPP/Re computed from light-response curves, GPP = Re - Fc'
        ds.globalattributes['Flag81'] = 'Partitioning Day: GPP night mask'
        ds.globalattributes['Flag82'] = 'Partitioning Day: Fc > Re, GPP = 0, Re = Fc'
    for ThisOne in ds.series.keys():
        if ThisOne in cf['Variables']:
            if 'Attr' in cf['Variables'][ThisOne].keys():
                ds.series[ThisOne]['Attr'] = {}
                for attr in cf['Variables'][ThisOne]['Attr'].keys():
                    ds.series[ThisOne]['Attr'][attr] = cf['Variables'][ThisOne]['Attr'][attr]

def do_functions(cf,ds):
    log.info(' Resolving functions given in control file')
    for ThisOne in cf['Variables'].keys():
        if 'Function' in cf['Variables'][ThisOne].keys():
            ds.series[ThisOne] = {}
            FunctionList = cf['Variables'][ThisOne]['Function'].keys()
            if len(FunctionList) == 1:
                i = 0
                if 'Square' in cf['Variables'][ThisOne]['Function'][str(i)].keys() and 'Parent' in cf['Variables'][ThisOne]['Function'][str(i)]['Square'].keys():
                    Parent = cf['Variables'][ThisOne]['Function'][str(i)]['Square']['Parent']
                    ds.series[ThisOne]['Data'] = qcts.Square(ds.series[Parent]['Data'])
                    nRecs = numpy.size(ds.series[ThisOne]['Data'])
                    if 'Flag' not in ds.series[ThisOne].keys():
                        ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)
                        if 'Flag' in ds.series[Parent]:
                            ds.series[ThisOne]['Flag'] = ds.series[Parent]['Flag']
                        else:
                            ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)
                elif 'SquareRoot' in cf['Variables'][ThisOne]['Function'][str(i)].keys() and 'Parent' in cf['Variables'][ThisOne]['Function'][str(i)]['SquareRoot'].keys():
                    Parent = cf['Variables'][ThisOne]['Function'][str(i)]['SquareRoot']['Parent']
                    ds.series[ThisOne]['Data'] = qcts.SquareRoot(ds.series[Parent]['Data'])
                    nRecs = numpy.size(ds.series[ThisOne]['Data'])
                    if 'Flag' not in ds.series[ThisOne].keys():
                        ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)
                        if 'Flag' in ds.series[Parent]:
                            ds.series[ThisOne]['Flag'] = ds.series[Parent]['Flag']
                        else:
                            ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)
                else:
                    log.error ('Function missing or unknown for variable'+ThisOne)
                    return
            else:
                for i in range(len(FunctionList)):
                    if 'Square' in cf['Variables'][ThisOne]['Function'][str(i)].keys() and 'Parent' in cf['Variables'][ThisOne]['Function'][str(i)]['Square'].keys():
                        Parent = cf['Variables'][ThisOne]['Function'][str(i)]['Square']['Parent']
                        ds.series[ThisOne]['Data'] = qcts.Square(ds.series[Parent]['Data'])
                        nRecs = numpy.size(ds.series[ThisOne]['Data'])
                        if 'Flag' not in ds.series[ThisOne].keys():
                            ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)
                            if 'Flag' in ds.series[Parent]:
                                ds.series[ThisOne]['Flag'] = ds.series[Parent]['Flag']
                            else:
                                ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)
                    elif 'SquareRoot' in cf['Variables'][ThisOne]['Function'][str(i)].keys() and 'Parent' in cf['Variables'][ThisOne]['Function'][str(i)]['SquareRoot'].keys():
                        Parent = cf['Variables'][ThisOne]['Function'][str(i)]['SquareRoot']['Parent']
                        ds.series[ThisOne]['Data'] = qcts.SquareRoot(ds.series[Parent]['Data'])
                        nRecs = numpy.size(ds.series[ThisOne]['Data'])
                        if 'Flag' not in ds.series[ThisOne].keys():
                            ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)
                            if 'Flag' in ds.series[Parent]:
                                ds.series[ThisOne]['Flag'] = ds.series[Parent]['Flag']
                            else:
                                ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)
                    else:
                        log.error ('Function missing or unknown for variable'+ThisOne)
                        return

def do_l4qc(cf,ds3,level):
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList'):
        l4functions = ast.literal_eval(cf['General']['FunctionList'])
        WorkList = ast.literal_eval(cf['General']['FunctionList'])
        if 'SOLO' in WorkList:
            WorkList.remove('SOLO')
            ds4 = qcio.nc_read_series(cf,level)
            do_solo(cf,ds4)
            ds4.globalattributes['Functions'] = ['SOLO ANN GapFilling 10-day window']
            # add relevant meteorological values to L3 data
            log.info(' Adding standard met variables to database')
            CalculateMeteorologicalVariables(cf,ds4)
            ds4.globalattributes['Functions'].append('CalculateMetVars')
            if 'CalculateMetVars' in WorkList:
                WorkList.remove('CalculateMetVars')
        if 'Met' in WorkList:
            WorkList.remove('Met')
            if 'SOLO' not in l4functions:
                ds4 = copy.deepcopy(ds3)
            do_metgaps(cf,ds4)
            ds4.globalattributes['Functions'] = ['InterpolateOverMissing', 'GapFillFromAlternate', 'GapFillFromClimatology', 'GapFillFromRatios', 'ReplaceOnDiff', 'UstarFromFh', 'ReplaceWhereMissing', 'do_qcchecks']
        if 'Sums' in WorkList:
            if 'SOLO' not in l4functions and 'Met' not in l4functions:
                ds4 = copy.deepcopy(ds3)
                ds4.globalattributes['Functions'] = l4functions
            else:
                for i in range(len(WorkList)):
                    ds4.globalattributes['Functions'].append(WorkList[i])
            do_sums(cf,ds4,l4functions,WorkList)
        if 'Met' not in l4functions and 'SOLO' not in l4functions and 'Sums' not in l4functions:
            log.error('Met, SOLO or Sums not located in FunctionList')
            ds4 = copy.deepcopy(ds3)
            ds4.globalattributes['Level'] = 'L3'
            ds4.globalattributes['Functions'] = ['No functions applied']
            return ds4
    else:
        log.error('FunctionList not found in control file')
        ds4 = copy.deepcopy(ds3)
        ds4.globalattributes['Level'] = 'L3'
        ds4.globalattributes['Functions'] = ['No functions applied']
        return ds4
    ds4.globalattributes['Level'] = level
    return ds4

def do_metgaps(cf,ds4):
    # get z-d (measurement height minus displacement height) and z0 from the control file
    if qcutils.cfkeycheck(cf,Base='Params',ThisOne='zmd') and qcutils.cfkeycheck(cf,Base='Params',ThisOne='z0'):
        zmd = float(cf['Params']['zmd'])   # z-d for site
        z0 = float(cf['Params']['z0'])     # z0 for site
            
        # make a copy of the L4 data, data from the alternate sites will be merged with this copy
        # linear interpolation to fill missing values over gaps of 1 hour
        qcts.InterpolateOverMissing(cf,ds4,maxlen=2)
        # gap fill meteorological and radiation data from the alternate site(s)
        log.info(' Gap filling using data from alternate sites')
        qcts.GapFillFromAlternate(cf,ds4)
        # gap fill meteorological, radiation and soil data using climatology
        log.info(' Gap filling using site climatology')
        qcts.GapFillFromClimatology(cf,ds4)
        # gap fill using evaporative fraction (Fe/Fa), Bowen ratio (Fh/Fe) and ecosystem water use efficiency (Fc/Fe)
        log.info(' Gap filling Fe, Fh and Fc using ratios')
        qcts.GapFillFromRatios(cf,ds4)
        # calculate u* from Fh and corrected wind speed
        us_in,us_out = qcts.UstarFromFh(cf,ds4, zmd, z0)
        qcts.ReplaceWhereMissing(ds4.series[us_in],ds4.series[us_in],ds4.series[us_out],0)
        # re-apply the quality control checks (range, diurnal and rules)
        log.info(' Doing QC checks on L4 data')
        qcck.do_qcchecks(cf,ds4)
        # interpolate over any ramaining gaps up to 3 hours in length
        qcts.InterpolateOverMissing(cf,ds4,maxlen=6)
        # fill any remaining gaps climatology
        qcts.GapFillFromClimatology(cf,ds4)

def do_solo(cf,ds4,Fc_in='Fc',Fe_in='Fe',Fh_in='Fh',Fc_out='Fc',Fe_out='Fe',Fh_out='Fh'):
    ''' duplicate gapfilled fluxes for graphing comparison'''
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='SOLOvars'):
        invars = ast.literal_eval(cf['FunctionArgs']['SOLOvars'])
        Fc_in = invars[0]
        Fe_in = invars[1]
        Fh_in = invars[2]
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='SOLOplot'):
        outvars = ast.literal_eval(cf['FunctionArgs']['SOLOplot'])
        Fc_out = outvars[0]
        Fe_out = outvars[1]
        Fh_out = outvars[2]
    if Fe_in in ds4.series.keys():
        Fe,flag = qcutils.GetSeriesasMA(ds4,Fe_in)
        qcutils.CreateSeries(ds4,Fe_out,Fe,Flag=flag,Descr='ANN gapfilled Latent Heat Flux',Units='W/m2',Standard='surface_upward_latent_heat_flux')
    if Fc_in in ds4.series.keys():
        Fc,flag = qcutils.GetSeriesasMA(ds4,Fc_in)
        qcutils.CreateSeries(ds4,Fc_out,Fc,Flag=flag,Descr='ANN gapfilled Carbon Flux',Units='mg/m2/s')
    if Fh_in in ds4.series.keys():
        Fh,flag = qcutils.GetSeriesasMA(ds4,Fh_in)
        qcutils.CreateSeries(ds4,Fh_out,Fh,Flag=flag,Descr='ANN gapfilled Sensible Heat Flux',Units='W/m2',Standard='surface_upward_sensible_heat_flux')

def do_sums(cf,ds,l4functions,WorkList):
    if 'MergeSeriesWS' in l4functions:
        srclist = qcutils.GetMergeList(cf,'Ws',default=['Ws_WS_01','Ws_CSAT'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds,'Ws',srclist,[0,10])
            
            # compute daily statistics
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='SumList'):
                SumList = ast.literal_eval(cf['Sums']['SumList'])
            else:
                SumList = ['Rain','ET','Energy','Radiation','Carbon']
            
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='SubSumList'):
                SubSumList = ast.literal_eval(cf['Sums']['SubSumList'])
            else:
                SubSumList = []
            
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='MinMaxList'):
                MinMaxList = ast.literal_eval(cf['Sums']['MinMaxList'])
            else:
                MinMaxList = ['Ta_EC','Vbat','Tpanel','Carbon']
            
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='MeanList'):
                MeanList = ast.literal_eval(cf['Sums']['MeanList'])
            else:
                MeanList = ['Ta_EC','Tpanel']
            
            if qcutils.cfkeycheck(cf,Base='Sums',ThisOne='SoilList'):
                SoilList = ast.literal_eval(cf['Sums']['SoilList'])
            else:
                SoilList = []
        
        StatsList = SumList + MinMaxList + MeanList + SoilList
        if len(StatsList) > 0:
            qcts.ComputeDailySums(cf,ds,SumList,SubSumList,MinMaxList,MeanList,SoilList)

def do_WPL(cf,ds,cov=''):
    if cov == 'True':
        Fe_WPLcov(cf,ds)
        Fc_WPLcov(cf,ds)
        return
    
    Fe_WPL(cf,ds)
    Fc_WPL(cf,ds)

def Fc_WPL(cf,ds,Fc_wpl_out='Fc',Fc_raw_in='Fc',Fh_in='Fh',Fe_wpl_in='Fe',Ta_in='Ta',Ah_in='Ah',Cc_in='Cc_7500_Av',ps_in='ps'):
    """
        Apply Webb, Pearman and Leuning correction to carbon flux.  This
        correction is necessary to account for flux effects on density
        measurements.  Original formulation: Campbell Scientific
        
        Usage qcts.Fc_WPL(ds,Fc_wpl_out,Fc_raw_in,Fh_in,Fe_wpl_in,Ta_in,Ah_in,Cc_in,ps_in)
        ds: data structure
        Fc_wpl_out: output corrected carbon flux variable to ds.  Example: 'Fc_wpl'
        Fc_raw_in: input carbon flux in ds.  Example: 'Fc_raw'
        Fh_in: input sensible heat flux in ds.  Example: 'Fh_rv'
        Fe_wpl_in: input corrected latent heat flux in ds.  Example: 'Fe_wpl'
        Ta_in: input air temperature in ds.  Example: 'Ta_EC'
        Ah_in: input absolute humidity in ds.  Example: 'Ah_EC'
        Cc_in: input co2 density in ds.  Example: 'Cc_7500_Av'
        ps_in: input atmospheric pressure in ds.  Example: 'ps'
        
        Used for fluxes that are raw or rotated.
        
        Pre-requisite: CalculateFluxes, CalculateFluxes_Unrotated or CalculateFluxesRM
        Pre-requisite: FhvtoFh
        Pre-requisite: Fe_WPL
        
        Accepts meteorological constants or variables
        """
    log.info(' Applying WPL correction to Fc')
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='CWPL'):
        Cargs = ast.literal_eval(cf['FunctionArgs']['CWPL'])
        Fc_wpl_out = Cargs[0]
        Fc_raw_in = Cargs[1]
        Fh_in = Cargs[2]
        Fe_wpl_in = Cargs[3]
        Ta_in = Cargs[4]
        Ah_in = Cargs[5]
        Cc_in = Cargs[6]
        ps_in = Cargs[7]
    Fc_raw,f = qcutils.GetSeriesasMA(ds,Fc_raw_in)
    Fh,f = qcutils.GetSeriesasMA(ds,Fh_in)
    Fe_wpl,f = qcutils.GetSeriesasMA(ds,Fe_wpl_in)
    Ta,f = qcutils.GetSeriesasMA(ds,Ta_in)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_in)
    Cc,f = qcutils.GetSeriesasMA(ds,Cc_in)
    ps,f = qcutils.GetSeriesasMA(ds,ps_in)
    nRecs = numpy.size(Fh)
    Fc_wpl_flag = numpy.zeros(nRecs,int)
    rhod = mf.densitydryair(Ta,ps)            # Density of dry air, kg/m3
    Ah = Ah/float(1000)                       # Absolute humidity from g/m3 to kg/m3
    sigma_wpl = Ah/rhod
    if 'Cpm' not in ds.series.keys():
        if 'e' in ds.series.keys():
            e,f = qcutils.GetSeriesasMA(ds,'e')
            mr = mf.mixingratio(ps,e)
            q = mf.specifichumidity(mr)
            Cpm = mf.specificheatmoistair(q)
        else:
            e = mf.vapourpressure(Ah,Ta)
            mr = mf.mixingratio(ps,e)
            q = mf.specifichumidity(mr)
            Cpm = mf.specificheatmoistair(q)
    else:
        Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    if 'rhom' in ds.series.keys() and 'Lv' in ds.series.keys():
        rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
        Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
        co2_wpl_Fe = 1.61*(Cc/rhod)*(Fe_wpl/Lv)
        co2_wpl_Fh = (1+(1.61*sigma_wpl))*Cc/(Ta+273.15)*Fh/(rhom*Cpm)
    else:
        rhom = rhod+Ah                            # Density of moist air, kg/m3
        co2_wpl_Fe = 1.61*(Cc/rhod)*(Fe_wpl/c.Lv)
        co2_wpl_Fh = (1+(1.61*sigma_wpl))*Cc/(Ta+273.15)*Fh/(rhom*Cpm)
    Fc_wpl_data = Fc_raw+co2_wpl_Fe+co2_wpl_Fh
    mask = numpy.ma.getmask(Fc_wpl_data)
    index = numpy.where(mask.astype(int)==1)
    Fc_wpl_flag[index] = 14
    qcutils.CreateSeries(ds,Fc_wpl_out,Fc_wpl_data,Flag=Fc_wpl_flag,
                         Descr='WPL corrected Fc',Units='mg/m2/s')

def Fc_WPLcov(cf,ds,Fc_wpl_out='Fc',wC_in='wC',Fh_in='Fh',wA_in='wA',Ta_in='Ta',Ah_in='Ah',Cc_in='Cc_7500_Av',ps_in='ps'):
    """
        Apply Webb, Pearman and Leuning correction to carbon flux using the
        original formulation (WPL80).  This correction is necessary to account
        for flux effects on density measurements.  This method uses the
        originally-published formulation using covariances rather than fluxes.
        The difference in the corrected fluxes using the two routines is minor
        and related to scaling the met variables.
        
        Usage qcts.Fc_WPLcov(ds,Fc_wpl_out,wC,Fh,wA,Ta,Ah,Cc,ps)
        ds: data structure
        Fc_wpl_out: output corrected carbon flux to ds.  Example: 'Fc_wpl'
        wC: input covariance(wC) in ds.  Example: 'wCM'
        Fh: input sensible heat flux in ds.  Example: 'Fh_rmv'
        wA: input covariance(wA) in ds.  Example: 'wAwpl'
        Ta: input air temperature in ds.  Example: 'Ta_HMP'
        Ah: input absolute humidity in ds.  Example: 'Ah_HMP'
        Cc: input co2 density in ds.  Example: 'Cc_7500_Av'
        ps: input atmospheric pressure in ds.  Example: 'ps'
        
        Pre-requisite: FhvtoFh
        Pre-requisite: Fe_WPLcov
        
        Accepts meteorological constants or variables
        """
    log.info(' Applying WPL correction to Fc')
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='CWPL'):
        Cargs = ast.literal_eval(cf['FunctionArgs']['CWPL'])
        Fc_wpl_out = Cargs[0]
        wC_in = Cargs[1]
        Fh_in = Cargs[2]
        Fe_wpl_in = Cargs[3]
        Ta_in = Cargs[4]
        Ah_in = Cargs[5]
        Cc_in = Cargs[6]
        ps_in = Cargs[7]
    wC,f = qcutils.GetSeriesasMA(ds,wC_in)
    Fh,f = qcutils.GetSeriesasMA(ds,Fh_in)
    wA,f = qcutils.GetSeriesasMA(ds,wA_in)
    Ta,f = qcutils.GetSeriesasMA(ds,Ta_in)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_in)
    Cc,f = qcutils.GetSeriesasMA(ds,Cc_in)
    ps,f = qcutils.GetSeriesasMA(ds,ps_in)
    Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
    nRecs = numpy.size(wC)
    TaK = Ta + 273.15
    rhod = mf.densitydryair(Ta,ps)            # Density of dry air, kg/m3
    Ah = Ah/float(1000)                       # Absolute humidity from g/m3 to kg/m3
    Cckg = Cc/float(1000000)                  # CO2 from mg/m3 to kg/m3
    sigma_wpl = Ah/rhod
    if 'Cpm' not in ds.series.keys():
        if 'e' in ds.series.keys():
            e,f = qcutils.GetSeriesasMA(ds,'e')
            mr = mf.mixingratio(ps,e)
            q = mf.specifichumidity(mr)
            Cpm = mf.specificheatmoistair(q)
        else:
            e = mf.vapourpressure(Ah,Ta)
            mr = mf.mixingratio(ps,e)
            q = mf.specifichumidity(mr)
            Cpm = mf.specificheatmoistair(q)
    else:
        Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    if 'rhom' in ds.series.keys():
        rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
        wT = Fh / (rhom * Cpm)
    else:
        rhom = rhod+Ah                            # Density of moist air, kg/m3
        wT = Fh / (rhom * Cpm)
    Fc_wpl_data = wC + (1.61 * (Cckg / rhod) * wA) \
                     + ((1 + (1.61 * sigma_wpl)) * (Cc / TaK) * wT)
    qcutils.CreateSeries(ds,Fc_wpl_out,Fc_wpl_data,FList=[wC_in,Fh_in,wA_in,Ta_in,Ah_in,Cc_in,ps_in],
                         Descr='Fc, rotated to natural wind coordinates, frequency response corrected, and density flux corrected (wpl)',
                         Units='mg/m2/s')
    testseries = qcutils.GetSeriesasMA(ds,Fc_wpl_out)
    mask = numpy.ma.getmask(testseries)
    index = numpy.where(mask.astype(int)==1)
    ds.series[Fc_wpl_out]['Flag'][index] = 14

def Fe_WPL(cf,ds,Fe_wpl_out='Fe',Fe_raw_in='Fe',Fh_in='Fh',Ta_in='Ta',Ah_in='Ah',ps_in='ps'):
    """
        Apply Webb, Pearman and Leuning correction to vapour flux.  This
        correction is necessary to account for flux effects on density
        measurements.  Original formulation: Campbell Scientific
        
        Usage qcts.Fe_WPL(ds,Fe_wpl_out,Fe_raw_in,Fh_in,Ta_in,Ah_in,ps_in)
        ds: data structure
        Fe_wpl_out: output corrected water vapour flux variable to ds.  Example: 'Fe_wpl'
        Fe_raw_in: input water vapour flux in ds.  Example: 'Fe_raw'
        Fh_in: input sensible heat flux in ds.  Example: 'Fh_rv'
        Ta_in: input air temperature in ds.  Example: 'Ta_EC'
        Ah_in: input absolute humidity in ds.  Example: 'Ah_EC'
        ps_in: input atmospheric pressure in ds.  Example: 'ps'
        
        Used for fluxes that are raw or rotated.
        
        Pre-requisite: CalculateFluxes, CalculateFluxes_Unrotated or CalculateFluxesRM
        Pre-requisite: FhvtoFh
        
        Accepts meteorological constants or variables
        """
    log.info(' Applying WPL correction to Fe')
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='EWPL'):
        Eargs = ast.literal_eval(cf['FunctionArgs']['EWPL'])
        Fe_wpl_out = Eargs[0]
        Fe_raw_in = Eargs[1]
        Fh_in = Eargs[2]
        Ta_in = Eargs[3]
        Ah_in = Eargs[4]
        ps_in = Eargs[5]
    Fe_raw,f = qcutils.GetSeriesasMA(ds,Fe_raw_in)
    Fh,f = qcutils.GetSeriesasMA(ds,Fh_in)
    Ta,f = qcutils.GetSeriesasMA(ds,Ta_in)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_in)
    ps,f = qcutils.GetSeriesasMA(ds,ps_in)
    nRecs = numpy.size(Fh)
    Fe_wpl_flag = numpy.zeros(nRecs,int)
    rhod = mf.densitydryair(Ta,ps)            # Density of dry air, kg/m3
    Ah = Ah/float(1000)                       # Absolute humidity from g/m3 to kg/m3
    sigma_wpl = Ah/rhod
    if 'Cpm' not in ds.series.keys():
        if 'e' in ds.series.keys():
            e,f = qcutils.GetSeriesasMA(ds,'e')
            mr = mf.mixingratio(ps,e)
            q = mf.specifichumidity(mr)
            Cpm = mf.specificheatmoistair(q)
        else:
            e = mf.vapourpressure(Ah,Ta)
            mr = mf.mixingratio(ps,e)
            q = mf.specifichumidity(mr)
            Cpm = mf.specificheatmoistair(q)
    else:
        Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    if 'rhom' in ds.series.keys() and 'Lv' in ds.series.keys():
        rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
        Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
        h2o_wpl_Fe = 1.61*sigma_wpl*Fe_raw
        h2o_wpl_Fh = (1+(1.61*sigma_wpl))*Ah*Lv*(Fh/(rhom*Cpm))/(Ta+273.15)
    else:
        rhom = rhod+Ah                            # Density of moist air, kg/m3
        h2o_wpl_Fe = 1.61*sigma_wpl*Fe_raw
        h2o_wpl_Fh = (1+(1.61*sigma_wpl))*Ah*c.Lv*(Fh/(rhom*Cpm))/(Ta+273.15)
    Fe_wpl_data = Fe_raw+h2o_wpl_Fe+h2o_wpl_Fh
    mask = numpy.ma.getmask(Fe_wpl_data)
    index = numpy.where(mask.astype(int)==1)
    Fe_wpl_flag[index] = 14
    qcutils.CreateSeries(ds,Fe_wpl_out,Fe_wpl_data,Flag=Fe_wpl_flag,
                         Descr='WPL corrected Fe',Units='W/m2',Standard='surface_upward_latent_heat_flux')
    #ds.series[Fe_wpl_out]['Flag'] = Fe_wpl_flag

def Fe_WPLcov(cf,ds,Fe_wpl_out='Fe',wA_in='wA',Fh_in='Fh',Ta_in='Ta',Ah_in='Ah',ps_in='ps',wA_out='wA'):
    """
        Apply Webb, Pearman and Leuning correction to vapour flux using the
        original formulation (WPL80).  This correction is necessary to account
        for flux effects on density measurements.  This method uses the
        originally-published formulation using covariances rather than fluxes.
        The difference in the corrected fluxes using the two routines is minor
        and related to scaling the met variables.
        
        Usage qcts.Fe_WPLcov(ds,Fe_wpl_out,wA,Fh,Ta,Ah,ps)
        ds: data structure
        Fe_wpl_out: output corrected water vapour flux to ds.  Example: 'Fe_wpl'
        wA: input covariance(wA) in ds.  Example: 'wAM'
        Fh: input sensible heat flux in ds.  Example: 'Fh_rmv'
        Ta: input air temperature in ds.  Example: 'Ta_HMP'
        Ah: input absolute humidity in ds.  Example: 'Ah_HMP'
        ps: input atmospheric pressure in ds.  Example: 'ps'
        
        Pre-requisite: FhvtoFh
        Pre-requisite: Fe_WPLcov
        
        Accepts meteorological constants or variables
        """
    log.info(' Applying WPL correction to Fe')
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='EWPL'):
        Eargs = ast.literal_eval(cf['FunctionArgs']['EWPL'])
        Fe_wpl_out = Eargs[0]
        Fe_raw_in = Eargs[1]
        Fh_in = Eargs[2]
        Ta_in = Eargs[3]
        Ah_in = Eargs[4]
        ps_in = Eargs[5]
        wA_out = Eargs[6]
    wA,f = qcutils.GetSeriesasMA(ds,wA_in)
    Fh,f = qcutils.GetSeriesasMA(ds,Fh_in)
    Ta,f = qcutils.GetSeriesasMA(ds,Ta_in)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_in)
    ps,f = qcutils.GetSeriesasMA(ds,ps_in)
    rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
    Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
    Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    nRecs = numpy.size(wA)
    TaK = Ta + 273.15
    rhod = mf.densitydryair(Ta,ps)            # Density of dry air, kg/m3
    Ah = Ah/float(1000)                       # Absolute humidity from g/m3 to kg/m3
    sigma_wpl = Ah/rhod
    if 'Cpm' not in ds.series.keys():
        if 'e' in ds.series.keys():
            e,f = qcutils.GetSeriesasMA(ds,'e')
            mr = mf.mixingratio(ps,e)
            q = mf.specifichumidity(mr)
            Cpm = mf.specificheatmoistair(q)
        else:
            e = mf.vapourpressure(Ah,Ta)
            mr = mf.mixingratio(ps,e)
            q = mf.specifichumidity(mr)
            Cpm = mf.specificheatmoistair(q)
    else:
        Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    if 'rhom' in ds.series.keys() and 'Lv' in ds.series.keys():
        rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
        Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
        wT = Fh / (rhom * Cpm)
        Fe_wpl_data = (Lv / 1000) * (1 + (1.61 * sigma_wpl)) * (wA + ((Ah / TaK) * wT))
        wAwpl = Fe_wpl_data * 1000 / Lv
    else:
        rhom = rhod+Ah                            # Density of moist air, kg/m3
        wT = Fh / (rhom * Cpm)
        Fe_wpl_data = (c.Lv / 1000) * (1 + (1.61 * sigma_wpl)) * (wA + ((Ah / TaK) * wT))
        wAwpl = Fe_wpl_data * 1000 / c.Lv
    qcutils.CreateSeries(ds,Fe_wpl_out,Fe_wpl_data,FList=[wA_in,Fh_in,Ta_in,Ah_in,ps_in],
                         Descr='Fe, rotated to natural wind coordinates, frequency response corrected, and density flux corrected (wpl)',
                         Units='W/m2',Standard='surface_upward_latent_heat_flux')
    qcutils.CreateSeries(ds,wA_out,wAwpl,FList=[wA_in,Fh_in,Ta_in,Ah_in,ps_in],
                         Descr='Cov(wA), rotated to natural wind coordinates, frequency response corrected, and density flux corrected (wpl)',
                         Units='g/(m2 s)')
    keys = [Fe_wpl_out,wA_out]
    for ThisOne in keys:
        testseries,f = qcutils.GetSeriesasMA(ds,ThisOne)
        mask = numpy.ma.getmask(testseries)
        index = numpy.where(mask.astype(int)==1)
        ds.series[ThisOne]['Flag'][index] = 14

def FhvtoFh(cf,ds,Ta_in='Ta',Fh_in='Fh',Tv_in='Tv_CSAT',Fe_in='Fe',ps_in='ps',Ah_in='Ah',Fh_out='Fh',attr='Fh rotated and converted from virtual heat flux'):
    """
        Corrects sensible heat flux calculated on the covariance between w'
        and theta', deviations in vertical windspeed and sonically-derived
        virtual temperature.  Uses the formulation developed by Ed Swiatek,
        Campbell Scientific and located in the open path eddy covariance manual.
        
        Usage qcts.FhvtoFh(ds,Ta_in,Fh_in,Tv_in,Fe_in,ps_in,Ah_in,Fh_out,attr)
        ds: data structure
        Ta_in: input air temperature in ds.  Example: 'Ta_HMP'
        Fh_in: input sensible heat flux in ds.  Example: 'Fh'
        Tv_in: input sonic virtual temperature in ds.  Example: 'Tv_CSAT'
        Fe_in: input water vapour flux in ds.  Example: 'Fe_raw'
        ps_in: input atmospheric pressure in ds.  Example: 'ps'
        Ah_in: input absolute pressure in ds.  Example: 'Ah_EC'
        Fh_out: output corrected sensible heat flux to ds.  Example: 'Fh_rv'
        attr: attribute field for variable meta-data in ds.  Example: 'Fh rotated and converted from virtual heat flux'
        
        Typically used following:
            CoordRotation, MassmanApprox, Massman, CalculateFluxesRM (recommended)
            or
            CoordRotation, CalculateFluxes
            or
            CalculateFluxes_Unrotated
        
        Accepts meteorological constants or variables
        """
    log.info(' Converting virtual Fh to Fh')
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='FhvtoFhArgs'):
        args = ast.literal_eval(cf['FunctionArgs']['FhvtoFhArgs'])
        Ta_in = args[0]
        Fh_in = args[1]
        Tv_in = args[2]
        Fe_in = args[3]
        ps_in = args[4]
        Ah_in = args[5]
        Fh_out = args[6]
        attr = args[7]
    Ta,f = qcutils.GetSeriesasMA(ds,Ta_in)
    Fh,f = qcutils.GetSeriesasMA(ds,Fh_in)
    Tv,f = qcutils.GetSeriesasMA(ds,Tv_in)
    Fe,f = qcutils.GetSeriesasMA(ds,Fe_in)
    ps,f = qcutils.GetSeriesasMA(ds,ps_in)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_in)
    nRecs = len(Fh)
    psPa = ps * 1000
    TaK = Ta + c.C2K
    TvK = Tv + c.C2K
    if 'rhom' in ds.series.keys() and 'Cpm' in ds.series.keys() and 'Lv' in ds.series.keys():
        rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
        Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
        Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
        Fh_o = (TaK / TvK) * (Fh - (rhom * Cpm * \
                                ((0.51 * c.Rd * (TaK ** 2)) / psPa) * (Fe / Lv)))
    else:
        rhom = mf.densitymoistair(Ta,ps,Ah)
        vp = mf.vapourpressure(Ah,Ta)
        mr = mf.mixingratio(ps,vp)
        q = mf.specifichumidity(mr)
        Cpm = mf.specificheatmoistair(q)
        Fh_o = (TaK / TvK) * (Fh - (rhom * Cpm * \
                                ((0.51 * c.Rd * (TaK ** 2)) / psPa) * (Fe / c.Lv)))
    
    qcutils.CreateSeries(ds,Fh_out,Fh_o,FList=[Ta_in, Fh_in, Tv_in, Fe_in, ps_in, Ah_in], 
                         Descr=attr, Units='W/m2',Standard='surface_upward_sensible_heat_flux')
    testseries = qcutils.GetSeriesasMA(ds,Fh_out)
    mask = numpy.ma.getmask(testseries)
    index = numpy.where(mask.astype(int)==1)
    ds.series[Fh_out]['Flag'][index] = 13

def FilterFcByUstar(cf, ds, Fc_out, Fc_in, ustar_in):
    """
    Filter the CO2 flux for low ustar periods.  The filtering is done by checking the
    friction velocity for each time period.  If ustar is less than or equal to the
    threshold specified in the control file then the CO2 flux is set to missing.  If
    the ustar is greater than the threshold, no action is taken.  Filtering is not
    done "in place", a new series is created with the label given in the argument Fc_out.
    The QC flag is set to 23 to indicate the Fc value is missing due to low ustar values.
    
    Usage: qcts.FilterFcByUstar(cf, ds, Fc_out, Fc_in, ustar_in)
    cf: control file object    
    ds: data structure
    Fc_out: series label of the corrected CO2 flux
    Fc_in: series label of the uncorrected CO2 flux
    ustar_in: series label of the friction velocity
    
    Parameters loaded from control file:
        ustar_threshold: friction velocity threshold, m/s

    """
    log.info(' Filtering CO2 flux based on ustar')
    if 'General' in cf:
        if 'ustar_threshold' in cf['General']:
            us_threshold = float(cf['General']['ustar_threshold'])
            Fc,Fc_flag = qcutils.GetSeriesasMA(ds,Fc_in,si=0,ei=-1)
            us,us_flag = qcutils.GetSeriesasMA(ds,ustar_in,si=0,ei=-1)
            Fc = numpy.ma.masked_where(us<=us_threshold,Fc)
            index = numpy.where(us<=us_threshold)[0]
            Fc_flag[index] = 18
            descr = Fc_in+' filtered for low ustar conditions, ustar threshold was '+str(us_threshold)
            units = qcutils.GetUnitsFromds(ds, Fc_in)
            qcutils.CreateSeries(ds,Fc_out,Fc,Flag=Fc_flag,Descr=descr,Units=units)
        else:
            print 'FilterFcByUstar: ustar_threshold expected but not found in General section of control file'
    else:
        print 'FilterFcByUstar: section General expected but not found in control file'

def GapFillFromAlternate(cf,ds,series=''):
    """
        Gap fill using data from alternate sites specified in the control file
        """
    ts = ds.globalattributes['time_step']
    # Gap fill using data from alternate sites specified in the control file
    ds_alt = {}               # create a dictionary for the data from alternate sites
    open_ncfiles = []         # create an empty list of open netCDF files
    if len(series)==0:        # if no series list passed in then ...
        series = cf['Variables'].keys() # ... create one using all variables listed in control file
    # loop over variables listed in the control file
    for ThisOne in series:
        # check that GapFillFromAlternate is specified for this series
        if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'GapFillFromAlternate'):
            # loop over the entries in the GapFillFromAlternate section
            for Alt in cf['Variables'][ThisOne]['GapFillFromAlternate'].keys():
                log.info(' Gap filling '+ThisOne+' by replacing with alternate site data')
                # get the file name for the alternate site
                alt_filename = cf['Variables'][ThisOne]['GapFillFromAlternate'][Alt]['FileName']
                # get the variable name for the alternate site data if specified, otherwise use the same name
                if 'AltVarName' in cf['Variables'][ThisOne]['GapFillFromAlternate'][Alt].keys():
                    alt_varname = cf['Variables'][ThisOne]['GapFillFromAlternate'][Alt]['AltVarName']
                else:
                    alt_varname = ThisOne
                # check to see if the alternate site file is already open
                if alt_filename not in open_ncfiles:
                    # open and read the file if it is not already open
                    n = len(open_ncfiles)
                    open_ncfiles.append(alt_filename)
                    ds_alt[n] = qcio.nc_read_series_file(alt_filename)
                else:
                    # get the file index number if it is already open
                    n = open_ncfiles.index(alt_filename)
                # check to see if alternate site data needs transform
                if 'Transform' in cf['Variables'][ThisOne]['GapFillFromAlternate'][Alt].keys():
                    # get the datetime series for the alternate site
                    AltDateTime = ds_alt[n].series['DateTime']['Data']
                    # get the data for the alternate site
                    AltSeriesData = ds_alt[n].series[alt_varname]['Data']
                    # evaluate the list of start dates, end dates and transform coefficients
                    TList = ast.literal_eval(cf['Variables'][ThisOne]['GapFillFromAlternate'][Alt]['Transform'])
                    # loop over the datetime ranges for the transform
                    for TListEntry in TList:
                        qcts.TransformAlternate(TListEntry,AltDateTime,AltSeriesData,ts=ts)
                qcts.ReplaceWhereMissing(ds.series[ThisOne],ds.series[ThisOne],ds_alt[n].series[alt_varname],100)

def GapFillFromClimatology(cf,ds,series=''):
    alt_xlbook = {}
    open_xlfiles = []
    if len(series)==0:        # if no series list passed in then ...
        series = cf['Variables'].keys() # ... create one using all variables listed in control file
    for ThisOne in series:
        if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'GapFillFromClimatology'):
            log.info(' Gap filling '+ThisOne+' using climatology')
            Values = numpy.zeros([48,12])
            alt_filename = cf['Variables'][ThisOne]['GapFillFromClimatology']['FileName']
            if alt_filename not in open_xlfiles:
                n = len(open_xlfiles)
                alt_xlbook[n] = xlrd.open_workbook(alt_filename)
                open_xlfiles.append(alt_filename)
            else:
                n = open_xlfiles.index(alt_filename)
            ThisSheet = alt_xlbook[n].sheet_by_name(ThisOne)
            val1d = numpy.zeros_like(ds.series[ThisOne]['Data'])
            for month in range(1,13):
                xlCol = (month-1)*5 + 2
                Values[:,month-1] = ThisSheet.col_values(xlCol)[2:50]
            for i in range(len(ds.series[ThisOne]['Data'])):
                h = numpy.int(2*ds.series['Hdh']['Data'][i])
                m = numpy.int(ds.series['Month']['Data'][i])
                val1d[i] = Values[h,m-1]
            index = numpy.where(abs(ds.series[ThisOne]['Data']-float(-9999))<c.eps)[0]
            ds.series[ThisOne]['Data'][index] = val1d[index]
            ds.series[ThisOne]['Flag'][index] = 32

def GapFillFromRatios(cf,ds):
    nRecs = int(ds.globalattributes['NumRecs'])
    ndays = 365
    Year = ds.series['Year']['Data'][0]
    if isleap(Year): ndays = 366
    print ' GapFillFromRatios: ndays=',ndays
    # get local versions of the series required as masked arrays
    # - we use masked arrays here to simplify subsequent calculations
    Fn,f = qcutils.GetSeriesasMA(ds,'Fn')         # net radiation
    Fg,f = qcutils.GetSeriesasMA(ds,'Fg')         # ground heat flux
    Fa = Fn - Fg                                  # available energy
    # get local copies of series required as non-masked arrays
    Fh = ds.series['Fh']['Data']
    Fe = ds.series['Fe']['Data']
    Fc = ds.series['Fc']['Data']
    for ThisOne in ['Fe','Fh','Fc']:
        alt_filename = cf['Variables'][ThisOne]['GapFillUsingRatios']['FileName']
        alt_xlbook = xlrd.open_workbook(alt_filename)
        xl_sheetname = cf['Variables'][ThisOne]['GapFillUsingRatios']['xlSheet']
        xlsheet = alt_xlbook.sheet_by_name(xl_sheetname)
        ratio = numpy.zeros((48,12))
        for xlCol in range(12):
            ratio[:,xlCol] = xlsheet.col_values(xlCol+1)[1:49]

        r3d = numpy.tile(ratio,(3,3))
        nx = numpy.shape(r3d)[1]
        nxi = ndays*3
        ny = numpy.shape(r3d)[0]
        nyi = numpy.shape(r3d)[0]
        x = numpy.linspace(1,nx,nx)
        y = numpy.linspace(1,ny,ny)
        xi = numpy.linspace(1,nx,nxi)
        yi = numpy.linspace(1,ny,nyi)

        nk = interpolate.RectBivariateSpline(y,x,r3d,kx=2,ky=2)
        r3di = nk(yi,xi)
        ri = r3di[nyi/3:2*nyi/3,nxi/3:2*nxi/3]
        ratio1d = numpy.ravel(ri,order='F')

        #ratio1d = numpy.zeros(nRecs)
        #for i in range(len(ds.series['Month']['Data'])):
            #h = numpy.int(2*ds.series['Hdh']['Data'][i])
            #m = numpy.int(ds.series['Month']['Data'][i])
            #ratio1d[i] = ratio[h,m-1]

        if ThisOne=='Fe':
            log.info(' Gap filling Fe using EF')
            Fe_gf = ratio1d * Fa                  # latent heat flux from evaporative fraction
            Fe_gf = numpy.ma.filled(Fe_gf,float(-9999))
            index = numpy.where((abs(Fe-float(-9999))<c.eps)&(abs(Fe_gf-float(-9999))>c.eps))
            ds.series['Fe']['Data'][index] = Fe_gf[index]
            ds.series['Fe']['Flag'][index] = 33
            qcutils.CreateSeries(ds,'EF',ratio1d,FList=['Fn'],Descr='Evaporative fraction',Units='none')
            qcutils.CreateSeries(ds,'Fe_gf',Fe_gf,FList=['Fe'],Descr='Fe gap filled using EF',Units='W/m2',Standard='surface_upward_latent_heat_flux')
        if ThisOne=='Fh':
            log.info(' Gap filling Fh using BR')
            Fh_gf = ratio1d * Fe_gf               # sensible heat flux from Bowen ratio
            Fh_gf = numpy.ma.filled(Fh_gf,float(-9999))
            index = numpy.where((abs(Fh-float(-9999))<c.eps)&(abs(Fh_gf-float(-9999))>c.eps))
            ds.series['Fh']['Data'][index] = Fh_gf[index]
            ds.series['Fh']['Flag'][index] = 33
            qcutils.CreateSeries(ds,'BR',ratio1d,FList=['Fn'],Descr='Bowen ratio',Units='none')
        if ThisOne =='Fc':
            log.info(' Gap filling Fc using WUE')
            Fc_gf = ratio1d * Fe_gf               # CO2 flux from ecosystem water use efficiency
            Fc_gf = numpy.ma.filled(Fc_gf,float(-9999))
            index = numpy.where((abs(Fc-float(-9999))<c.eps)&(abs(Fc_gf-float(-9999))>c.eps))
            ds.series['Fc']['Data'][index] = Fc_gf[index]
            ds.series['Fc']['Flag'][index] = 33
            qcutils.CreateSeries(ds,'WUE',ratio1d,FList=['Fn'],Descr='Water use efficiency',Units='none')

def get_averages(Data):
    """
        Get daily averages on days when no 30-min observations are missing.
        Days with missing observations return a value of -9999
        Values returned are sample size (Num) and average (Av)
        
        Usage qcts.get_averages(Data)
        Data: 1-day dataset
        """
    li = numpy.ma.where(abs(Data-float(-9999))>c.eps)
    Num = numpy.size(li)
    if Num == 0:
        Av = -9999
    elif Num == 48:
        Av = numpy.ma.mean(Data[li])
    else:
        x = 0
        index = numpy.ma.where(Data.mask == True)[0]
        if len(index) == 1:
            x = 1
        elif len(index) > 1:
            for i in range(len(Data)):
                if Data.mask[i] == True:
                    x = x + 1
        
        if x == 0:
            Av = numpy.ma.mean(Data[li])
        else:
            Av = -9999
    return Num, Av

def get_minmax(Data):
    """
        Get daily minima and maxima on days when no 30-min observations are missing.
        Days with missing observations return a value of -9999
        Values returned are sample size (Num), minimum (Min) and maximum (Max)
        
        Usage qcts.get_minmax(Data)
        Data: 1-day dataset
        """
    li = numpy.ma.where(abs(Data-float(-9999))>c.eps)
    Num = numpy.size(li)
    if Num == 0:
        Min = -9999
        Max = -9999
    elif Num == 48:
        Min = numpy.ma.min(Data[li])
        Max = numpy.ma.max(Data[li])
    else:
        x = 0
        index = numpy.ma.where(Data.mask == True)[0]
        if len(index) == 1:
            x = 1
        elif len(index) > 1:
            for i in range(len(Data)):
                if Data.mask[i] == True:
                    x = x + 1
        
        if x == 0:
            Min = numpy.ma.min(Data[li])
            Max = numpy.ma.max(Data[li])
        else:
            Min = -9999
            Max = -9999
    return Num, Min, Max

def get_nightsums(Data):
    """
        Get nightly sums and averages on nights when no 30-min observations are missing.
        Nights with missing observations return a value of -9999
        Values returned are sample size (Num), sums (Sum) and average (Av)
        
        Usage qcts.get_nightsums(Data)
        Data: 1-day dataset
        """
    li = numpy.ma.where(Data.mask == False)[0]
    Num = numpy.size(li)
    if Num == 0:
        Sum = -9999
        Av = -9999
    else:
        x = 0
        for i in range(len(Data)):
            if Data.mask[i] == True:
                x = x + 1
        
        if x == 0:
            Sum = numpy.ma.sum(Data[li])
            Av = numpy.ma.mean(Data[li])
        else:
            Sum = -9999
            Av = -9999
    
    return Num, Sum, Av

def get_stomatalresistance(cf,ds):
    Level = ds.globalattributes['Level']
    log.info(' Computing Penman-Monteith bulk stomatal resistance at level '+Level)
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='PMin'):
        PMin = ast.literal_eval(cf['FunctionArgs']['PMin'])
    else:
        PMin = ['Fe_wpl', 'Ta_EC', 'Ah_EC', 'ps', 'Ws_EC', 'Fnr', 'Fsd', 'Fg']
    
    if 'Lv' not in ds.series.keys():
        AddMetVars(ds)
    Fe,f = qcutils.GetSeriesasMA(ds,PMin[0])
    Ta,f = qcutils.GetSeriesasMA(ds,PMin[1])
    Ah,f = qcutils.GetSeriesasMA(ds,PMin[2])
    ps,f = qcutils.GetSeriesasMA(ds,PMin[3])
    Uavg,f = qcutils.GetSeriesasMA(ds,PMin[4])
    Fnr,f = qcutils.GetSeriesasMA(ds,PMin[5])
    Fsd,f = qcutils.GetSeriesasMA(ds,PMin[6])
    Fg,f = qcutils.GetSeriesasMA(ds,PMin[7])
    VPD,f = qcutils.GetSeriesasMA(ds,'VPD')
    Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
    q,f = qcutils.GetSeriesasMA(ds,'q')
    Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    
    esat = mf.es(Ta)
    qsat = mf.qsat(esat,ps)
    gamma = mf.gamma(ps,Cpm,Lv)
    delta = mf.delta(Ta)
    Ce = mf.bulktransfercoefficient(Fe,Lv,Uavg,q,qsat)
    uindex = numpy.ma.where(Uavg == 0)[0]
    Uavg[uindex] = 0.000000000000001
    rav = mf.aerodynamicresistance(Uavg,Ce)
    rav[uindex] = numpy.float64(-9999)
    rst = ((((((delta * (Fnr - Fg) / (Lv / 1000)) + (c.rho_water * Cpm * (VPD / ((Lv / 1000) * rav)))) / (Fe / (Lv / 1000))) - delta) / gamma) - 1) * rav
    rst[uindex] = numpy.float64(-9999)
    Gst = (1 / rst) * (Ah * 1000) / 18
    Gst[uindex] = numpy.float64(-9999)
    qcutils.CreateSeries(ds,'Ce',Ce,FList=PMin,Descr='Bulk transfer coefficient, Brutseart/Stull formulation of bulk transfer coefficient, '+Level,Units='s/m')
    qcutils.CreateSeries(ds,'rav',rav,FList=PMin,Descr='Aerodynamic resistance from bulk transfer coefficient, Brutseart/Stull formulation of bulk transfer coefficient, '+Level,Units='s/m')
    qcutils.CreateSeries(ds,'rst_raw',rst,FList=PMin,Descr='Unfiltered Bulk stomatal resistance from Penman-Monteith inversion, Brutseart/Stull formulation of bulk transfer coefficient, '+Level,Units='s/m')
    qcutils.CreateSeries(ds,'Gst_raw',Gst,FList=PMin,Descr='Unfiltered Bulk stomatal conductance from Penman-Monteith inversion, Brutseart/Stull formulation of bulk transfer coefficient, '+Level,Units='mmolH2O/(m2ground s)')
    
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='PMcritFsd'):
        critFsd = float(cf['FunctionArgs']['PMcritFsd'])
    else:
        critFsd = 10.
    
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='PMcritFe'):
        critFe = float(cf['FunctionArgs']['PMcritFe'])
    else:
        critFe = 0.
    
    index = numpy.ma.where((Fsd < critFsd) | (Fe < critFe) | (rst < 0))[0]
    index1 = numpy.ma.where(rst < 0)[0]
    index2 = numpy.ma.where(Fe < critFe)[0]
    index3 = numpy.ma.where(Fsd < critFsd)[0]
    rst[index] = numpy.float64(-9999)
    Gst[index] = numpy.float64(-9999)
    
    qcutils.CreateSeries(ds,'rst',rst,FList=PMin,Descr='Bulk stomatal resistance from Penman-Monteith inversion, Brutseart/Stull formulation of bulk transfer coefficient, '+Level,Units='s/m')
    qcutils.CreateSeries(ds,'Gst',Gst,FList=PMin,Descr='Bulk stomatal conductance from Penman-Monteith inversion, Brutseart/Stull formulation of bulk transfer coefficient, '+Level,Units='mmolH2O/(m2ground s)')
    
    Label = ['rst','Gst']
    for listindex in range(0,2):
        ds.series[Label[listindex]]['Attr']['InputSeries'] = PMin
        ds.series[Label[listindex]]['Attr']['FsdCutoff'] = critFsd
        ds.series[Label[listindex]]['Attr']['FeCutoff'] = critFe
    
    ds.series['rst']['Flag'][index1] = 61
    ds.series['Gst']['Flag'][index1] = 61
    ds.series['rst']['Flag'][uindex] = 64
    ds.series['Gst']['Flag'][uindex] = 64
    ds.series['rav']['Flag'][uindex] = 64
    ds.series['rst']['Flag'][index2] = 62
    ds.series['Gst']['Flag'][index2] = 62
    ds.series['rst']['Flag'][index3] = 63
    ds.series['Gst']['Flag'][index3] = 63

def get_soilaverages(Data):
    """
        Get daily averages of soil water content on days when 15 or fewer 30-min observations are missing.
        Days with 16 or more missing observations return a value of -9999
        Values returned are sample size (Num) and average (Av)
        
        Usage qcts.get_soilaverages(Data)
        Data: 1-day dataset
        """
    li = numpy.ma.where(abs(Data-float(-9999))>c.eps)
    Num = numpy.size(li)
    if Num > 33:
        Av = numpy.ma.mean(Data[li])
    else:
        Av = -9999
    return Num, Av

def get_subsums(Data):
    """
        Get separate daily sums of positive and negative fluxes when no 30-min observations are missing.
        Days with missing observations return a value of -9999
        Values returned are positive and negative sample sizes (PosNum and NegNum) and sums (SumPos and SumNeg)
        
        Usage qcts.get_subsums(Data)
        Data: 1-day dataset
        """
    li = numpy.ma.where(abs(Data-float(-9999))>c.eps)
    Num = numpy.size(li)
    if Num == 48:
        pi = numpy.ma.where(Data[li]>0)
        ni = numpy.ma.where(Data[li]<0)
        PosNum = numpy.size(pi)
        NegNum = numpy.size(ni)
        if PosNum > 0:
            SumPos = numpy.ma.sum(Data[pi])
        else:
            SumPos = 0
        if NegNum > 0:
            SumNeg = numpy.ma.sum(Data[ni])
        else:
            SumNeg = 0
    else:
        pi = numpy.ma.where(Data[li]>0)
        ni = numpy.ma.where(Data[li]<0)
        PosNum = numpy.size(pi)
        NegNum = numpy.size(ni)
        SumPos = -9999
        SumNeg = -9999
    return PosNum, NegNum, SumPos, SumNeg

def get_sums(Data):
    """
        Get daily sums when no 30-min observations are missing.
        Days with missing observations return a value of -9999
        Values returned are sample size (Num) and sum (Sum)
        
        Usage qcts.get_sums(Data)
        Data: 1-day dataset
        """
    li = numpy.ma.where(abs(Data-float(-9999))>c.eps)
    Num = numpy.size(li)
    if Num == 0:
        Sum = -9999
    elif Num == 48:
        Sum = numpy.ma.sum(Data[li])
    else:
        x = 0
        index = numpy.ma.where(Data.mask == True)[0]
        if len(index) == 1:
            x = 1
        elif len(index) > 1:
            for i in range(len(Data)):
                if Data.mask[i] == True:
                    x = x + 1
        
        if x == 0:
            Sum = numpy.ma.sum(Data[li])
        else:
            Sum = -9999
    return Num, Sum

def get_qcflag(ds):
    """
        Set up flags during ingest of L1 data.
        Identifies missing observations as -9999 and sets flag value 1
        
        Usage qcts.get_qcflag(ds)
        ds: data structure
        """
    log.info(' Setting up the QC flags')
    nRecs = len(ds.series['xlDateTime']['Data'])
    for ThisOne in ds.series.keys():
        if ThisOne not in ['xlDateTime','Year','Month','Day','Hour','Minute','Second','Hdh']:
            ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,dtype=int)
            index = numpy.where(ds.series[ThisOne]['Data']==float(-9999))
            ds.series[ThisOne]['Flag'][index] = 1

def get_yearmonthdayhourminutesecond(cf,ds):
    """
        Gets year, month, day, hour, and if available seconds, from
        excel-formatted Timestamp
        
        Usage qcts.get_yearmonthdayhourminutesecond(cf,ds)
        cf: control file
        ds: data structure
        """
    log.info(' Getting date and time variables')
    # set the date mode for PC or MAC versions of Excel dates
    datemode = 0
    if cf['General']['Platform'] == 'Mac': datemode = 1
    nRecs = len(ds.series['xlDateTime']['Data'])
    Year = numpy.array([-9999]*nRecs,numpy.int32)
    Month = numpy.array([-9999]*nRecs,numpy.int32)
    Day = numpy.array([-9999]*nRecs,numpy.int32)
    Hour = numpy.array([-9999]*nRecs,numpy.int32)
    Minute = numpy.array([-9999]*nRecs,numpy.int32)
    Second = numpy.array([-9999]*nRecs,numpy.int32)
    Hdh = numpy.array([-9999]*nRecs,numpy.float64)
    Ddd = numpy.array([-9999]*nRecs,numpy.float64)
    flag = numpy.zeros(nRecs)
    for i in range(nRecs):
        DateTuple = xlrd.xldate_as_tuple(ds.series['xlDateTime']['Data'][i],datemode)
        Year[i] = int(DateTuple[0])
        Month[i] = int(DateTuple[1])
        Day[i] = int(DateTuple[2])
        Hour[i] = int(DateTuple[3])
        Minute[i] = int(DateTuple[4])
        Second[i] = int(DateTuple[5])
        Hdh[i] = float(DateTuple[3])+float(DateTuple[4])/60.
        Ddd[i] = ds.series['xlDateTime']['Data'][i] - xlrd.xldate.xldate_from_date_tuple((Year[i],1,1),datemode)
    qcutils.CreateSeries(ds,'Year',Year,Flag=flag,Descr='Year',Units='none')
    qcutils.CreateSeries(ds,'Month',Month,Flag=flag,Descr='Month',Units='none')
    qcutils.CreateSeries(ds,'Day',Day,Flag=flag,Descr='Day',Units='none')
    qcutils.CreateSeries(ds,'Hour',Hour,Flag=flag,Descr='Hour',Units='none')
    qcutils.CreateSeries(ds,'Minute',Minute,Flag=flag,Descr='Minute',Units='none')
    qcutils.CreateSeries(ds,'Second',Second,Flag=flag,Descr='Second',Units='none')
    qcutils.CreateSeries(ds,'Hdh',Hdh,Flag=flag,Descr='Decimal hour of the day',Units='none')
    qcutils.CreateSeries(ds,'Ddd',Ddd,Flag=flag,Descr='Decimal day of the year',Units='none')

def InvertSign(ds,ThisOne):
    log.info(' Inverting sign of '+ThisOne)
    index = numpy.where(abs(ds.series[ThisOne]['Data']-float(-9999))>c.eps)[0]
    ds.series[ThisOne]['Data'][index] = float(-1)*ds.series[ThisOne]['Data'][index]

def InterpolateOverMissing(cf,ds,series='',maxlen=1000):
    if len(series)==0:
        series = cf['Variables'].keys() # ... create one using all variables listed in control file
    #print time.strftime('%X')+' Interpolating over missing values in series '+S_in
    DateNum = date2num(ds.series['DateTime']['Data'])
    for ThisOne in series:
        iog = numpy.where(ds.series[ThisOne]['Data']!=float(-9999))[0]            # index of good values
        f = interpolate.interp1d(DateNum[iog],ds.series[ThisOne]['Data'][iog])    # linear interpolation function
        iom = numpy.where((ds.series[ThisOne]['Data']==float(-9999))&             # index of missing values
                          (DateNum>=DateNum[iog[0]])&                          # that occur between the first
                          (DateNum<=DateNum[iog[-1]]))[0]                      # and last dates used to define f
        # Now we step through the indices of the missing values and discard
        # contiguous blocks longer than maxlen.
        # !!! The following code is klunky and could be re-written to be
        # !!! neater and faster.
        # First, define 2 temporary arrays used and initialise 2 counters.
        tmp1 = numpy.zeros(len(iom),int)
        tmp2 = numpy.zeros(len(iom),int)
        k=0
        n=0
        # step through the array of idices for missing values
        for i in range(len(iom)-1):
            dn = iom[i+1]-iom[i]        # change in index number from one element of iom to the next
            if dn==1:                   # if the change is 1 then we are still in a contiguous block
                tmp1[n] = iom[i]        # save the index into a temporary array
                n = n + 1               # increment the contiguous block length counter
            elif dn>1:                  # if the change is greater than 1 then we have come to the end of a contiguous block
                if n<maxlen:            # if the contiguous block length is less then maxlen
                    tmp1[n]=iom[i]      # save the last index of the contiguous block
                    tmp2[k:k+n+1] = tmp1[0:n+1]   # concatenate the indices for this block to any previous block with length less than maxlen
                    k=k+n+1             # update the pointer to the concatenating array
                n=0                     # reset the contiguous block length counter
        if k>0:                         # do the interpolation only if 1 gap is less than maxlen
            tmp2[k] = iom[-1]               # just accept the last missing value index regardless
            iom_new = tmp2[:k+1]            # the array of missing data indices with contiguous block lengths less than maxlen
            ds.series[ThisOne]['Data'][iom_new] = f(DateNum[iom_new]).astype(numpy.float32)        # fill missing values with linear interpolations
            ds.series[ThisOne]['Flag'][iom_new] = 34

def MassmanStandard(cf,ds,Ta_in='Ta',Ah_in='Ah',ps_in='ps',ustar_in='ustar',ustar_out='ustar',L_in='L',L_out ='L',uw_out='uw',vw_out='vw',wT_out='wT',wA_out='wA',wC_out='wC'):
    """
       Massman corrections.
       The steps involved are as follows:
        1) calculate ustar and L using rotated but otherwise uncorrected covariances
       """
    if 'Massman' not in cf:
        log.info(' Massman section not found in control file, no corrections applied')
        return
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='MassmanVars'):
        MArgs = ast.literal_eval(cf['FunctionArgs']['MassmanVars'])
        Ta_in = MArgs[0]
        Ah_in = MArgs[1]
        ps_in = MArgs[2]
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='MassmanOuts'):
        MOut = ast.literal_eval(cf['FunctionArgs']['MassmanOuts'])
        ustar_in = MOut[0]
        ustar_out = MOut[1]
        L_in = MOut[2]
        L_out = MOut[3]
        uw_out = MOut[4]
        vw_out = MOut[5]
        wT_out = MOut[6]
        wA_out = MOut[7]
        wC_out = MOut[8]
    log.info(' Correcting for flux loss from spectral attenuation')
    zmd = float(cf['Massman']['zmd'])             # z-d for site
    angle = float(cf['Massman']['angle'])         # CSAT3-IRGA separation angle
    CSATarm = float(cf['Massman']['CSATarm'])     # CSAT3 mounting distance
    IRGAarm = float(cf['Massman']['IRGAarm'])     # IRGA mounting distance
    lLat = numpy.ma.sin(numpy.deg2rad(angle)) * IRGAarm
    lLong = CSATarm - (numpy.ma.cos(numpy.deg2rad(angle)) * IRGAarm)
    # *** Massman_1stpass starts here ***
    #  The code for the first and second passes is very similar.  It would be useful to make them the
    #  same and put into a loop to reduce the nu,ber of lines in this function.
    # calculate ustar and Monin-Obukhov length from rotated but otherwise uncorrected covariances
    Ta,f = qcutils.GetSeriesasMA(ds,Ta_in)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_in)
    ps,f = qcutils.GetSeriesasMA(ds,ps_in)
    nRecs = numpy.size(Ta)
    u,f = qcutils.GetSeriesasMA(ds,'u')
    uw,f = qcutils.GetSeriesasMA(ds,'uw')
    vw,f = qcutils.GetSeriesasMA(ds,'vw')
    wT,f = qcutils.GetSeriesasMA(ds,'wT')
    wC,f = qcutils.GetSeriesasMA(ds,'wC')
    wA,f = qcutils.GetSeriesasMA(ds,'wA')
    if ustar_in not in ds.series.keys():
        ustarm = numpy.ma.sqrt(numpy.ma.sqrt(uw ** 2 + vw ** 2))
    else:
        ustarm,f = qcutils.GetSeriesasMA(ds,ustar_in)
    if L_in not in ds.series.keys():
        Lm = mf.molen(Ta, Ah, ps, ustarm, wT)
    else:
        Lm,f = qcutils.GetSeriesasMA(ds,Lm_in)
    # now calculate z on L
    zoLm = zmd / Lm
    # start calculating the correction coefficients for approximate corrections
    #  create nxMom, nxScalar and alpha series with their unstable values by default
    nxMom, nxScalar, alpha = qcutils.nxMom_nxScalar_alpha(zoLm)
    # now calculate the fxMom and fxScalar coefficients
    fxMom = nxMom * u / zmd
    fxScalar = nxScalar * u / zmd
    # compute spectral filters
    tao_eMom = ((c.lwVert / (5.7 * u)) ** 2) + ((c.lwHor / (2.8 * u)) ** 2)
    tao_ewT = ((c.lwVert / (8.4 * u)) ** 2) + ((c.lTv / (4.0 * u)) ** 2)
    tao_ewIRGA = ((c.lwVert / (8.4 * u)) ** 2) + ((c.lIRGA / (4.0 * u)) ** 2) \
                 + ((lLat / (1.1 * u)) ** 2) + ((lLong / (1.05 * u)) ** 2)
    tao_b = c.Tb / 2.8
    # calculate coefficients
    bMom = qcutils.bp(fxMom,tao_b)
    bScalar = qcutils.bp(fxScalar,tao_b)
    pMom = qcutils.bp(fxMom,tao_eMom)
    pwT = qcutils.bp(fxScalar,tao_ewT)
    # calculate corrections for momentum and scalars
    rMom = qcutils.r(bMom, pMom, alpha)        # I suspect that rMom and rwT are the same functions
    rwT = qcutils.r(bScalar, pwT, alpha)
    # determine approximately-true Massman fluxes
    uwm = uw / rMom
    vwm = vw / rMom
    wTm = wT / rwT
    # *** Massman_1stpass ends here ***
    # *** Massman_2ndpass starts here ***
    # we have calculated the first pass corrected momentum and temperature covariances, now we use
    # these to calculate the final corrections
    #  first, get the 2nd pass corrected friction velocity and Monin-Obukhov length
    ustarm = numpy.ma.sqrt(numpy.ma.sqrt(uwm ** 2 + vwm ** 2))
    Lm = mf.molen(Ta, Ah, ps, ustarm, wTm)
    zoLm = zmd / Lm
    nxMom, nxScalar, alpha = qcutils.nxMom_nxScalar_alpha(zoLm)
    fxMom = nxMom * (u / zmd)
    fxScalar = nxScalar * (u / zmd)
    # calculate coefficients
    bMom = qcutils.bp(fxMom,tao_b)
    bScalar = qcutils.bp(fxScalar,tao_b)
    pMom = qcutils.bp(fxMom,tao_eMom)
    pwT = qcutils.bp(fxScalar,tao_ewT)
    pwIRGA = qcutils.bp(fxScalar,tao_ewIRGA)
    # calculate corrections for momentum and scalars
    rMom = qcutils.r(bMom, pMom, alpha)
    rwT = qcutils.r(bScalar, pwT, alpha)
    rwIRGA = qcutils.r(bScalar, pwIRGA, alpha)
    # determine true fluxes
    uwM = uw / rMom
    vwM = vw / rMom
    wTM = wT / rwT
    wCM = wC / rwIRGA
    wAM = wA / rwIRGA
    ustarM = numpy.ma.sqrt(numpy.ma.sqrt(uwM ** 2 + vwM ** 2))
    LM = mf.molen(Ta, Ah, ps, ustarM, wTM)
    # write the 2nd pass Massman corrected covariances to the data structure
    qcutils.CreateSeries(ds,ustar_out,ustarM,FList=['uw','vw'],Descr='Massman true ustar',Units='m/s')
    qcutils.CreateSeries(ds,L_out,LM,FList=[Ta_in,Ah_in,ps_in,'wT'],Descr='Massman true Obukhov Length',Units='m')
    qcutils.CreateSeries(ds,uw_out,uwM,FList=['uw',L_out],Descr='Massman true Cov(uw)',Units='m2/s2')
    qcutils.CreateSeries(ds,vw_out,vwM,FList=['vw',L_out],Descr='Massman true Cov(vw)',Units='m2/s2')
    qcutils.CreateSeries(ds,wT_out,wTM,FList=['wT',L_out],Descr='Massman true Cov(wT)',Units='mC/s')
    qcutils.CreateSeries(ds,wA_out,wAM,FList=['wA',L_out],Descr='Massman true Cov(wA)',Units='g/m2/s')
    qcutils.CreateSeries(ds,wC_out,wCM,FList=['wC',L_out],Descr='Massman true Cov(wC)',Units='mg/m2/s')
    # *** Massman_2ndpass ends here ***
    
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='MassmanFlag') and cf['General']['MassmanFlag'] == 'True':
        keys = [ustar_out,L_out,uw_out,vw_out,wT_out,wA_out,wC_out]
        for ThisOne in keys:
            testseries,f = qcutils.GetSeriesasMA(ds,ThisOne)
            mask = numpy.ma.getmask(testseries)
            index = numpy.where(mask.astype(int)==1)
            ds.series[ThisOne]['Flag'][index] = 12

def MergeSeries(ds,Destination,Source,QCFlag_OK):
    """
        Merge two series of data to produce one series containing the best data from both.
        Calling syntax is: MergeSeries(ds,Destination,Source,QCFlag_OK)
         where ds is the data structure containing all series
               Destination (str) is the label of the destination series
               Source (list) is the label of the series to be merged in order
               QCFlag_OK (list) is a list of QC flag values for which the data is considered acceptable
        If the QC flag for Primary is in QCFlag_OK, the value from Primary is placed in destination.
        If the QC flag for Primary is not in QCFlag_OK but the QC flag for Secondary is, the value
        from Secondary is placed in Destination.
        """
    log.info(' Merging series '+str(Source)+' into '+Destination)
    nSeries = len(Source)
    if nSeries==0:
        log.error('  MergeSeries: no input series specified')
        return
    if nSeries==1:
        if Source[0] not in ds.series.keys():
            log.error('  MergeSeries: primary input series '+Source[0]+' not found')
            return
        data = ds.series[Source[0]]['Data'].copy()
        flag = ds.series[Source[0]]['Flag'].copy()
        SeriesNameString = Source[0]
        SeriesUnitString = ds.series[Source[0]]['Attr']['units']
    else:
        if Source[0] not in ds.series.keys():
            log.error('  MergeSeries: primary input series '+Source[0]+' not found')
            return
        data = ds.series[Source[0]]['Data'].copy()
        flag = ds.series[Source[0]]['Flag'].copy()
        SeriesNameString = Source[0]
        SeriesUnitString = ds.series[Source[0]]['Attr']['units']
        Source.remove(Source[0])
        for ThisOne in Source:
            if ThisOne in ds.series.keys():
                SeriesNameString = SeriesNameString+', '+ThisOne
                indx1 = numpy.zeros(numpy.size(data),dtype=numpy.int)
                indx2 = numpy.zeros(numpy.size(data),dtype=numpy.int)
                for okflag in QCFlag_OK:
                    index = numpy.where((flag==okflag))[0]                             # index of acceptable primary values
                    indx1[index] = 1                                                   # set primary index to 1 when primary good
                    index = numpy.where((ds.series[ThisOne]['Flag']==okflag))[0]       # same process for secondary
                    indx2[index] = 1
                index = numpy.where((indx1!=1)&(indx2==1))[0]           # index where primary bad but secondary good
                data[index] = ds.series[ThisOne]['Data'][index]         # replace bad primary with good secondary
                flag[index] = ds.series[ThisOne]['Flag'][index]
            else:
                log.error('  MergeSeries: secondary input series '+ThisOne+' not found')
    if Destination not in ds.series.keys():                 # create new series if destination does not exist
        qcutils.CreateSeries(ds,Destination,data,Flag=flag,Descr='Merged from '+SeriesNameString,Units=SeriesUnitString,Standard=ds.series[Source[0]]['Attr']['standard_name'])
    else:
        ds.series[Destination]['Data'] = data.copy()
        ds.series[Destination]['Flag'] = flag.copy()
        ds.series[Destination]['Attr']['Description'] = 'Merged from '+SeriesNameString
        ds.series[Destination]['Attr']['units'] = SeriesUnitString

def PT100(ds,T_out,R_in,m):
    log.info(' Calculating temperature from PT100 resistance')
    R,f = qcutils.GetSeriesasMA(ds,R_in)
    R = m*R
    T = (-c.PT100_alpha+numpy.sqrt(c.PT100_alpha**2-4*c.PT100_beta*(-R/100+1)))/(2*c.PT100_beta)
    qcutils.CreateSeries(ds,T_out,T,FList=[R_in],
                         Descr='Calculated PT100 temperature using '+str(R_in),Units='degC')

def ReplaceOnDiff(cf,ds,series=''):
    # Gap fill using data from alternate sites specified in the control file
    ts = ds.globalattributes['time_step']
    if len(series)!=0:
        ds_alt = {}                     # create a dictionary for the data from alternate sites
        open_ncfiles = []               # create an empty list of open netCDF files
        for ThisOne in series:          # loop over variables in the series list
            # has ReplaceOnDiff been specified for this series?
            if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'ReplaceOnDiff'):
                # loop over all entries in the ReplaceOnDiff section
                for Alt in cf['Variables'][ThisOne]['ReplaceOnDiff'].keys():
                    if 'FileName' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                        alt_filename = cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt]['FileName']
                        if 'AltVarName' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                            alt_varname = cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt]['AltVarName']
                        else:
                            alt_varname = ThisOne
                        if alt_filename not in open_ncfiles:
                            n = len(open_ncfiles)
                            open_ncfiles.append(alt_filename)
                            ds_alt[n] = qcio.nc_read_series_file(alt_filename)
                        else:
                            n = open_ncfiles.index(alt_filename)
                        if 'Transform' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                            AltDateTime = ds_alt[n].series['DateTime']['Data']
                            AltSeriesData = ds_alt[n].series[alt_varname]['Data']
                            TList = ast.literal_eval(cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt]['Transform'])
                            for TListEntry in TList:
                                qcts.TransformAlternate(TListEntry,AltDateTime,AltSeriesData,ts=ts)
                        if 'Range' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                            RList = ast.literal_eval(cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt]['Range'])
                            for RListEntry in RList:
                                qcts.ReplaceWhenDiffExceedsRange(ds.series['DateTime']['Data'],ds.series[ThisOne],
                                                                 ds.series[ThisOne],ds_alt[n].series[alt_varname],
                                                                 RListEntry)
                    elif 'AltVarName' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                        alt_varname = ThisOne
                        if 'Range' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                            RList = ast.literal_eval(cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt]['Range'])
                            for RListEntry in RList:
                                qcts.ReplaceWhenDiffExceedsRange(ds.series['DateTime']['Data'],ds.series[ThisOne],
                                                                 ds.series[ThisOne],ds.series[alt_varname],
                                                                 RListEntry)
                    else:
                        log.error('ReplaceOnDiff: Neither AltFileName nor AltVarName given in control file')
    else:
        log.error('ReplaceOnDiff: No input series specified')

def ReplaceWhereMissing(Destination,Primary,Secondary,FlagOffset=0):
    #print time.strftime('%X')+' Merging series '+Primary+' and '+Secondary+' into '+Destination
    p_data = Primary['Data'].copy()
    p_flag = Primary['Flag'].copy()
    s_data = Secondary['Data'].copy()
    s_flag = Secondary['Flag'].copy()
    if numpy.size(p_data)>numpy.size(s_data):
        p_data = p_data[0:numpy.size(s_data)]
    if numpy.size(s_data)>numpy.size(p_data):
        s_data = s_data[0:numpy.size(p_data)]
    index = numpy.where((abs(p_data-float(-9999))<c.eps)&
                        (abs(s_data-float(-9999))>c.eps))[0]
    p_data[index] = s_data[index]
    p_flag[index] = s_flag[index] + FlagOffset
    Destination['Data'] = Primary['Data'].copy()
    Destination['Flag'] = Primary['Flag'].copy()
    Destination['Data'][0:len(p_data)] = p_data
    Destination['Flag'][0:len(p_flag)] = p_flag
    Destination['Attr']['Description'] = 'Merged from original and alternate'
    Destination['Attr']['units'] = Primary['Attr']['units']

def ReplaceWhenDiffExceedsRange(DateTime,Destination,Primary,Secondary,RList):
    #print time.strftime('%X')+' Replacing '+Primary+' with '+Secondary+' when difference exceeds threshold'
    # get the primary data series
    p_data = numpy.ma.array(Primary['Data'])
    p_flag = Primary['Flag'].copy()
    # get the secondary data series
    s_data = numpy.ma.array(Secondary['Data'])
    s_flag = Secondary['Flag'].copy()
    # truncate the longest series if the sizes do not match
    if numpy.size(p_data)!=numpy.size(s_data):
        log.warning(' ReplaceWhenDiffExceedsRange: Series lengths differ, longest will be truncated')
        if numpy.size(p_data)>numpy.size(s_data):
            p_data = p_data[0:numpy.size(s_data)]
        if numpy.size(s_data)>numpy.size(p_data):
            s_data = s_data[0:numpy.size(p_data)]
    # get the difference between the two data series
    d_data = p_data-s_data
    # normalise the difference if requested
    if RList[3]=='s':
        d_data = (p_data-s_data)/s_data
    elif RList[3]=='p':
        d_data = (p_data-s_data)/p_data
    #si = qcutils.GetDateIndex(DateTime,RList[0],0)
    #ei = qcutils.GetDateIndex(DateTime,RList[1],0)
    Range = RList[2]
    Upper = float(Range[0])
    Lower = float(Range[1])
    index = numpy.ma.where((abs(d_data)<Lower)|(abs(d_data)>Upper))
    p_data[index] = s_data[index]
    p_flag[index] = 35
    Destination['Data'] = numpy.ma.filled(p_data,float(-9999))
    Destination['Flag'] = p_flag.copy()
    Destination['Attr']['Description'] = 'Replaced original with alternate when difference exceeded threshold'
    Destination['Attr']['units'] = Primary['Attr']['units']

def savitzky_golay(y, window_size, order, deriv=0):
    ''' Apply Savitsky-Golay low-pass filter to data.'''
    try:
        window_size = numpy.abs(numpy.int(window_size))
        order = numpy.abs(numpy.int(order))
    except ValueError, msg:
        raise ValueError("window_size and order have to be of type int")
    if window_size % 2 != 1 or window_size < 1:
        raise TypeError("window_size size must be a positive odd number")
    if window_size < order + 2:
        raise TypeError("window_size is too small for the polynomials order")
    order_range = range(order+1)
    half_window = (window_size -1) // 2
    # precompute coefficients
    b = numpy.mat([[k**i for i in order_range] for k in range(-half_window, half_window+1)])
    m = numpy.linalg.pinv(b).A[deriv]
    # pad the signal at the extremes with
    # values taken from the signal itself
    firstvals = y[0] - numpy.abs( y[1:half_window+1][::-1] - y[0] )
    lastvals = y[-1] + numpy.abs(y[-half_window-1:-1][::-1] - y[-1])
    y = numpy.concatenate((firstvals, y, lastvals))
    return numpy.convolve( m, y, mode='valid')

def Square(Series):
    tmp = numpy.array([-9999]*numpy.size(Series),Series.dtype)
    index = numpy.where(Series!=float(-9999))[0]
    tmp[index] = Series[index] ** 2
    return tmp

def SquareRoot(Series):
    tmp = numpy.array([-9999]*numpy.size(Series),Series.dtype)
    index = numpy.where(Series!=float(-9999))[0]
    tmp[index] = Series[index] ** .5
    return tmp

def TaFromTv(cf,ds,Ta_out='Ta_CSAT',Tv_in='Tv_CSAT',Ah_in='Ah',ps_in='ps'):
    # Calculate the air temperature from the virtual temperature, the
    # absolute humidity and the pressure.
    # NOTE: the virtual temperature is used in place of the air temperature
    #       to calculate the vapour pressure from the absolute humidity, the
    #       approximation involved here is of the order of 1%.
    log.info(' Calculating Ta from Tv')
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='Ta2Tv'):
        args = ast.literal_eval(cf['FunctionArgs']['Ta2Tv'])
        Ta_out = args[0]
        Tv_in = args[1]
        Ah_in = args[2]
        ps_in = args[3]
        
    Tv,f = qcutils.GetSeriesasMA(ds,Tv_in)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah_in)
    ps,f = qcutils.GetSeriesasMA(ds,ps_in)
    nRecs = numpy.size(Tv)
    Ta_flag = numpy.zeros(nRecs,int)
    vp = mf.vapourpressure(Ah,Tv)
    mr = mf.mixingratio(ps,vp)
    q = mf.specifichumidity(mr)
    Ta_data = mf.tafromtv(Tv,q)
    mask = numpy.ma.getmask(Ta_data)
    index = numpy.where(mask.astype(int)==1)
    Ta_flag[index] = 15
    qcutils.CreateSeries(ds,Ta_out,Ta_data,Flag=Ta_flag,
                         Descr='Ta calculated from Tv using '+Tv_in,Units='degC',Standard='air_temperature')
    
def TransformAlternate(TList,DateTime,Series,ts=30):
    # Apply polynomial transform to data series being used as replacement data for gap filling
    #print time.strftime('%X')+' Applying polynomial transform to '+ThisOne
    si = qcutils.GetDateIndex(DateTime,TList[0],ts=ts,default=0,match='exact')
    ei = qcutils.GetDateIndex(DateTime,TList[1],ts=ts,default=-1,match='exact')
    Series = numpy.ma.masked_where(abs(Series-float(-9999))<c.eps,Series)
    Series[si:ei] = qcutils.polyval(TList[2],Series[si:ei])
    Series = numpy.ma.filled(Series,float(-9999))

def UstarFromFh(cf,ds,z,z0,us_out='uscalc',T_in='Ta', Ah_in='Ah', p_in='ps', Fh_in='Fh', u_in='Ws_CSAT', us_in='ustar'):
    # Calculate ustar from sensible heat flux, wind speed and
    # roughness length using Wegstein's iterative method.
    #  T is the air temperature, C
    #  p is the atmospheric pressure, kPa
    #  H is the sensible heat flux, W/m^2
    #  u is the wind speed, m/s
    #  z is the measurement height minus the displacement height, m
    #  z0 is the momentum roughness length, m
    log.info(' Calculating ustar from (Fh,Ta,Ah,p,u)')
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='ustarFh'):
        args = ast.literal_eval(cf['FunctionArgs']['ustarFh'])
        us_out = args[0]
        T_in = args[1]
        Ah_in = args[2]
        p_in = args[3]
        Fh_in = args[4]
        u_in = args[5]
        us_in = args[6]
    T,T_flag = qcutils.GetSeries(ds,T_in)
    Ah,Ah_flag = qcutils.GetSeries(ds,Ah_in)
    p,p_flag = qcutils.GetSeries(ds,p_in)
    Fh,Fh_flag = qcutils.GetSeries(ds,Fh_in)
    u,u_flag = qcutils.GetSeries(ds,u_in)
    nRecs = numpy.size(Fh)
    us = numpy.zeros(nRecs,dtype=numpy.float64) + numpy.float64(-9999)
    us_flag = numpy.zeros(nRecs,dtype=numpy.int)
    for i in range(nRecs):
        if((abs(T[i]-float(-9999))>c.eps)&(abs(Ah[i]-float(-9999))>c.eps)&
           (abs(p[i]-float(-9999))>c.eps)&(abs(Fh[i]-float(-9999))>c.eps)&
           (abs(u[i]-float(-9999))>c.eps)):
            #print ds.series['DateTime']['Data'][i],T[i]
            us[i] = qcutils.Wegstein(T[i], Ah[i], p[i], Fh[i], u[i], z, z0)
            us_flag[i] = 36
        else:
            us[i] = numpy.float64(-9999)
            us_flag[i] = 37
    qcutils.CreateSeries(ds,us_out,us,Flag=us_flag,Descr='ustar from (Fh,Ta,Ah,p,u)',Units='m/s')
    return us_in, us_out

def write_sums(cf,ds,ThisOne,xlCol,xlSheet,DoSum='False',DoMinMax='False',DoMean='False',DoSubSum='False',DoSoil='False'):
    monthabr = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
    if qcutils.cfkeycheck(cf,Base='Params',ThisOne='firstMonth'):
        M1st = int(cf['Params']['firstMonth'])
    else:
        M1st = 1
    if qcutils.cfkeycheck(cf,Base='Params',ThisOne='secondMonth'):
        M2nd = int(cf['Params']['secondMonth'])
    else:
        M2nd = 12
    log.info(' Doing daily sums for '+ThisOne)
    Units = ds.series[ThisOne]['Attr']['units']
    
    xlRow = 1
    if xlCol == 0:
        xlSheet.write(xlRow,xlCol,'Month')
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,'Day')
        xlCol = xlCol + 1
    xlSheet.write(xlRow,xlCol,'n')
    xlCol = xlCol + 1
    if DoMinMax == 'True':
        xlSheet.write(xlRow,xlCol,ThisOne+'_min')
        xlSheet.write(xlRow-1,xlCol,Units)
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,ThisOne+'_max')
        if DoMean == 'True':
            xlSheet.write(xlRow-1,xlCol,Units)
            xlCol = xlCol + 1
            xlSheet.write(xlRow,xlCol,ThisOne+'_mean')
    elif DoMinMax == 'False' and DoMean == 'True':
        xlSheet.write(xlRow,xlCol,ThisOne+'_mean')
    elif DoMinMax == 'False' and DoMean == 'False':
        xlSheet.write(xlRow,xlCol,ThisOne)
        
    xlSheet.write(xlRow-1,xlCol,Units)

    if DoSubSum == 'True':
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,'Pos n')
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,ThisOne+'_pos')
        xlSheet.write(xlRow-1,xlCol,Units)
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,'Neg n')
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,ThisOne+'_neg')
        xlSheet.write(xlRow-1,xlCol,Units)
    
    data = numpy.ma.masked_where(abs(ds.series[ThisOne]['Data']-float(-9999))<c.eps,ds.series[ThisOne]['Data'])
    for month in range(M1st,M2nd+1):
        if month == 1 or month == 3 or month == 5 or month == 7 or month == 8 or month == 10 or month == 12:
            dRan = 31
        if month == 2:
            if ds.series['Year']['Data'][0] % 4 == 0:
                dRan = 29
            else:
                dRan = 28
        if month == 4 or month == 6 or month == 9 or month == 11:
            dRan = 30
            
        for day in range(1,dRan+1):
            xlRow = xlRow + 1
            if ThisOne == 'rst' or ThisOne == 'Gst' or ThisOne == 'Gst_mol':
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day) & (ds.series[ThisOne]['Flag'] < 61))[0]
                ti = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                nRecs = len(ti)
                check = numpy.ma.empty(nRecs,str)
                for i in range(nRecs):
                    index = ti[i]
                    check[i] = ds.series['Day']['Data'][index]
                if len(check) < 48:
                    di = []
            elif ThisOne == 'GPP' or ThisOne == 'GPP_mmol':
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day) & ((ds.series[ThisOne]['Flag'] != 21) & (ds.series[ThisOne]['Flag'] != 81)))[0]
                ti = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                nRecs = len(ti)
                check = numpy.ma.empty(nRecs,str)
                for i in range(nRecs):
                    index = ti[i]
                    check[i] = ds.series['Day']['Data'][index]
                if len(check) < 48:
                    di = []
            elif ThisOne == 'Re_mmol':
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day) & ((ds.series[ThisOne]['Flag'] == 0) | (ds.series[ThisOne]['Flag'] > 69)))[0]
                ti = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                nRecs = len(ti)
                check = numpy.ma.empty(nRecs,str)
                for i in range(nRecs):
                    index = ti[i]
                    check[i] = ds.series['Day']['Data'][index]
                if len(check) < 48:
                    di = []
            else:
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                nRecs = len(di)
                check = numpy.ma.empty(nRecs,str)
                for i in range(nRecs):
                    index = di[i]
                    check[i] = ds.series['Day']['Data'][index]
                if len(check) < 48:
                    di = []
            
            if DoSoil == 'True':
                Num,Av = get_soilaverages(data[di])
                if xlCol == 3:
                    xlCol = 2
                    xlSheet.write(xlRow,xlCol-2,monthabr[month-1])
                    xlSheet.write(xlRow,xlCol-1,day)
                else:
                    xlCol = xlCol - 1
            else:
                if DoSum == 'True':
                    Num,Sum = get_sums(data[di])
                if DoMinMax == 'True':
                    Num,Min,Max = get_minmax(data[di])
                if DoMean == 'True':
                    if DoMinMax == 'True':
                        Num2,Av = get_averages(data[di])
                    else:
                        Num,Av = get_averages(data[di])
                if DoSubSum == 'True':
                    PosNum,NegNum,SumPos,SumNeg = get_subsums(data[di])
                xlCol = 2
                xlSheet.write(xlRow,xlCol-2,monthabr[month-1])
                xlSheet.write(xlRow,xlCol-1,day)
            
            xlSheet.write(xlRow,xlCol,Num)
            xlCol = xlCol + 1
            if DoSoil == 'True':
                xlSheet.write(xlRow,xlCol,Av)
            elif DoMinMax == 'True':
                xlSheet.write(xlRow,xlCol,Min)
                xlCol = xlCol + 1
                xlSheet.write(xlRow,xlCol,Max)
                if DoMean == 'True':
                    xlCol = xlCol + 1
                    xlSheet.write(xlRow,xlCol,Av)
            elif DoMinMax == 'False' and DoMean == 'True':
                xlSheet.write(xlRow,xlCol,Av)
            elif DoSum == 'True':
                xlSheet.write(xlRow,xlCol,Sum)
                if DoSubSum == 'True':
                    xlCol = xlCol + 1
                    xlSheet.write(xlRow,xlCol,PosNum)
                    xlCol = xlCol + 1
                    xlSheet.write(xlRow,xlCol,SumPos)
                    xlCol = xlCol + 1
                    xlSheet.write(xlRow,xlCol,NegNum)
                    xlCol = xlCol + 1
                    xlSheet.write(xlRow,xlCol,SumNeg)
    
    if DoSoil == 'True': 
        return xlCol,xlSheet
    else:
        return
