"""
    QC Data Function Module
    Used to perform the tasks queued by qcls.py
    """

import sys
import ast
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

def AddMetVars(ds):
    """
        Add time series of meteorological variables based on fundamental
        relationships (Stull 1988)
        
        Usage qcts.AddMetVars(ds)
        ds: data structure
        
        Variables added:
            rhom: density of moist air, mf.densitymoistair(Ta,ps,Ah)
            Lv: latent heat of vapourisation, mf.Lv(Ta)
            q: specific humidity, mf.specifichumidity(mr)
                where mr (mixing ratio) = mf.mixingratio(ps,vp)
            Cpm: specific heat of moist air, mf.specificheatmoistair(q)
            VPD: vapour pressure deficit, VPD = esat - e
        """
    Ta,f = qcutils.GetSeriesasMA(ds,'Ta_HMP')
    ps,f = qcutils.GetSeriesasMA(ds,'ps')
    Ah,f = qcutils.GetSeriesasMA(ds,'Ah_HMP')
    if 'es_HMP' in ds.series.keys() and 'e_HMP' in ds.series.keys():
        esat,f = qcutils.GetSeriesasMA(ds,'es_HMP')
        e,f = qcutils.GetSeriesasMA(ds,'e_HMP')
    else:
        e = mf.vapourpressure(Ah,Ta)
        esat = mf.es(Ta)
    rhom = mf.densitymoistair(Ta,ps,Ah)
    Lv = mf.Lv(Ta)
    mr = mf.mixingratio(ps,e)
    q = mf.specifichumidity(mr)
    Cpm = mf.specificheatmoistair(q)
    VPD = esat - e
    qcutils.CreateSeries(ds,'rhom',rhom,['Ta_HMP','ps','Ah_HMP'],'Density of moist air','kg/m3')
    qcutils.CreateSeries(ds,'Lv',Lv,['Ta_HMP'],'Latent heat of vapourisation','J/kg')
    qcutils.CreateSeries(ds,'q',q,['ps','Ah_HMP','Ta_HMP'],'Specific humidity','kg/m3')
    qcutils.CreateSeries(ds,'Cpm',Cpm,['ps','Ah_HMP','Ta_HMP'],'Specific heat of moist air','J/kg-K')
    qcutils.CreateSeries(ds,'VPD',VPD,['es_HMP','e_HMP'],'Vapour pressure deficit','kPa')

def albedo(ds):
    """
        Filter albedo measurements to:
            high solar angle specified by periods between 10.00 and 14.00, inclusive
            and
            full sunlight in which Fsd > 290 W/m2
        Observations outside this window are masked and QA/QC flag is set to 7
        
        Usage qcts.albedo(ds)
        ds: data structure
        """
    if 'albedo' not in ds.series.keys():
        if 'Fsd' in ds.series.keys():
            Fsd,f = qcutils.GetSeriesasMA(ds,'Fsd')
            Fsu,f = qcutils.GetSeriesasMA(ds,'Fsu')
            albedo = Fsu / Fsd
            qcutils.CreateSeries(ds,'albedo',albedo,['Fsd','Fsu'],'solar albedo','unitless')
        else:
            log.error('  Fsd not in ds, albedo not calculated')
            return
    else:
        albedo,f = qcutils.GetSeriesasMA(ds,'albedo')
        if 'Fsd' in ds.series.keys():
            Fsd,f = qcutils.GetSeriesasMA(ds,'Fsd')
        else:
            Fsd,f = qcutils.GetSeriesasMA(ds,'Fn')
    
    index = numpy.ma.where((Fsd < 290) | (ds.series['Hdh']['Data'] < 10) | (ds.series['Hdh']['Data'] > 14))[0]
    albedo[index] = numpy.float64(-9999)
    ds.series['albedo']['Flag'][index] = 7
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
    log.info(' Applying linear correction to '+ThisOne)
    if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'Linear'):
        data = numpy.ma.masked_where(ds.series[ThisOne]['Data']==float(-9999),ds.series[ThisOne]['Data'])
        flag = ds.series[ThisOne]['Flag'].copy()
        ldt = ds.series['DateTime']['Data']
        LinearList = cf['Variables'][ThisOne]['Linear'].keys()
        for i in range(len(LinearList)):
            LinearItemList = eval(cf['Variables'][ThisOne]['Linear'][str(i)])
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
    log.info(' Applying linear drift correction to '+ThisOne)
    if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'Drift'):
        data = numpy.ma.masked_where(ds.series[ThisOne]['Data']==float(-9999),ds.series[ThisOne]['Data'])
        flag = ds.series[ThisOne]['Flag']
        ldt = ds.series['DateTime']['Data']
        DriftList = cf['Variables'][ThisOne]['Drift'].keys()
        for i in range(len(DriftList)):
            DriftItemList = eval(cf['Variables'][ThisOne]['Drift'][str(i)])
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
    log.info(' Applying linear drift correction to '+ThisOne)
    if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'LocalDrift'):
        data = numpy.ma.masked_where(ds.series[ThisOne]['Data']==float(-9999),ds.series[ThisOne]['Data'])
        flag = ds.series[ThisOne]['Flag']
        ldt = ds.series['DateTime']['Data']
        DriftList = cf['Variables'][ThisOne]['LocalDrift'].keys()
        for i in range(len(DriftList)):
            DriftItemList = eval(cf['Variables'][ThisOne]['LocalDrift'][str(i)])
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

def Average3SeriesByElements(ds,Av_out,Series_in):
    """
        Calculates the average of three time series.  Three time series are
        entered and a single time series representing the average at each
        observational period is returned.  This function is included to error in
        numpy.vstack that occurs when arrays of different dimension are stacked.
        
        Usage qcts.Average3SeriesByElements(ds,Av_out,Series_in)
        ds: data structure
        Av_out: output variable to ds.  Example: 'Fg_Av'
        Series_in: input variable series in ds.  Example: ['Fg_1','Fg_2','Fg_3']
        """
    log.info('Averaging series in ' + str(Series_in))
    nSeries = len(Series_in)
    TmpArr_data0 = ds.series[Series_in[0]]['Data'].copy()
    TmpArr_flag0 = ds.series[Series_in[0]]['Flag'].copy()
    SeriesNameString = Series_in[0]
    TmpArr_data1 = ds.series[Series_in[1]]['Data'].copy()
    TmpArr_flag1 = ds.series[Series_in[1]]['Flag'].copy()
    SeriesNameString = SeriesNameString+', '+Series_in[1]
    TmpArr_data2 = ds.series[Series_in[2]]['Data'].copy()
    TmpArr_flag2 = ds.series[Series_in[2]]['Flag'].copy()
    SeriesNameString = SeriesNameString+', '+Series_in[2]
    TmpArr_data = numpy.array([TmpArr_data0,TmpArr_data1,TmpArr_data2])
    TmpArr_flag = numpy.array([TmpArr_flag0,TmpArr_flag1,TmpArr_flag2])
    TmpArr_data = numpy.ma.masked_where(TmpArr_data==float(-9999),TmpArr_data)
    Av_data = numpy.ma.average(TmpArr_data,axis=0)
    Mx_flag = numpy.max(TmpArr_flag,axis=0)
    ds.series[Av_out] = {}
    ds.series[Av_out]['Data'] = numpy.ma.filled(Av_data,float(-9999))
    ds.series[Av_out]['Flag'] = Mx_flag
    ds.series[Av_out]['Attr'] = {}
    ds.series[Av_out]['Attr']['Description'] = 'Element-wise average of series '+SeriesNameString
    ds.series[Av_out]['Attr']['Units'] = ds.series[Series_in[0]]['Attr']['Units']

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
    log.info(' Averaging series in '+str(Series_in))
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
        SeriesUnitString = ds.series[Series_in[0]]['Attr']['Units']
    else:
        TmpArr_data = ds.series[Series_in[0]]['Data'].copy()
        TmpArr_flag = ds.series[Series_in[0]]['Flag'].copy()
        SeriesNameString = Series_in[0]
        SeriesUnitString = ds.series[Series_in[0]]['Attr']['Units']
        Series_in.remove(Series_in[0])
        for ThisOne in Series_in:
            SeriesNameString = SeriesNameString+', '+ThisOne
            TmpArr_data = numpy.vstack((TmpArr_data,ds.series[ThisOne]['Data'].copy()))
            TmpArr_flag = numpy.vstack((TmpArr_flag,ds.series[ThisOne]['Flag'].copy()))
        TmpArr_data = numpy.ma.masked_where(TmpArr_data==float(-9999),TmpArr_data)
        Av_data = numpy.ma.average(TmpArr_data,axis=0)
        Mx_flag = numpy.min(TmpArr_flag,axis=0)
    ds.series[Av_out] = {}
    ds.series[Av_out]['Data'] = numpy.ma.filled(Av_data,float(-9999))
    ds.series[Av_out]['Flag'] = Mx_flag
    ds.series[Av_out]['Attr'] = {}
    ds.series[Av_out]['Attr']['Description'] = 'Element-wise average of series '+SeriesNameString
    ds.series[Av_out]['Attr']['Units'] = SeriesUnitString

def CalculateAvailableEnergy(ds,Fa_out,Fn_in,Fg_in):
    """
        Calculate the average energy as Fn - G.
        
        Usage qcts.CalculateAvailableEnergy(ds,Fa_out,Fn_in,Fg_in)
        ds: data structure
        Fa_out: output available energy variable to ds.  Example: 'Fa'
        Fn_in: input net radiation in ds.  Example: 'Fn'
        Fg_in: input ground heat flux in ds.  Example: 'Fg'
        """
    log.info(' Calculating available energy from Fn and Fg')
    Fn,f = qcutils.GetSeriesasMA(ds,Fn_in)
    Fg,f = qcutils.GetSeriesasMA(ds,Fg_in)
    Fa = Fn - Fg
    qcutils.CreateSeries(ds,Fa_out,Fa,[Fn_in,Fg_in],'Available energy using '+Fn_in+','+Fg_in,'W/m2')

def CalculateFluxes(ds):
    """
        Calculate the fluxes from the rotated covariances.
        
        Usage qcts.CalculateFluxes(ds)
        ds: data structure
        
        Pre-requisite: CoordRotation2D
        
        Accepts meteorological constants or variables
        """
    log.info(' Calculating fluxes from covariances')
    if 'wT' in ds.series.keys():
        wT,f = qcutils.GetSeriesasMA(ds,'wT')
        Ta,f = qcutils.GetSeriesasMA(ds,'Ta_EC')
        ps,f = qcutils.GetSeriesasMA(ds,'ps')
        Ah,f = qcutils.GetSeriesasMA(ds,'Ah_EC')
        if 'rhom' in ds.series.keys():
            rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
            Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
        else:
            rhom = mf.densitymoistair(Ta,ps,Ah)
        Fh_nr = rhom * c.Cpd * wT
        qcutils.CreateSeries(ds,'Fh',Fh_nr,['wT'],'Sensible heat flux, rotated to natural wind coordinates','W/m2')
    else:
        log.error('  CalculateFluxes: wT not found in ds.series, Fh not calculated')
    if 'wA' in ds.series.keys():
        wA,f = qcutils.GetSeriesasMA(ds,'wA')
        if 'Lv' in ds.series.keys():
            Lv,f = qcutils,GetSeriesasMA(ds,'Lv')
            Fe_raw_nr = Lv * wA / float(1000)
        else:
            Fe_raw_nr = c.Lv * wA / float(1000)
        qcutils.CreateSeries(ds,'Fe_raw',Fe_raw_nr,['wA'],'Latent heat flux, rotated to natural wind coordinates','W/m2')
    else:
        log.error('  CalculateFluxes: wA not found in ds.series, Fe_raw not calculated')
    if 'wC' in ds.series.keys():
        wC,f = qcutils.GetSeriesasMA(ds,'wC')
        Fc_raw = wC
        qcutils.CreateSeries(ds,'Fc_raw',Fc_raw,['wC'],'CO2 flux, rotated to natural wind coordinates','mg/m2/s')
    else:
        log.error('  CalculateFluxes: wC not found in ds.series, Fc_raw not calculated')
    if 'uw' in ds.series.keys():
        if 'vw' in ds.series.keys():
            uw,f = qcutils.GetSeriesasMA(ds,'uw')
            vw,f = qcutils.GetSeriesasMA(ds,'vw')
            vs = uw*uw + vw*vw
            if 'wT' not in ds.series.keys():
                if 'rhom' not in ds.series.keys():
                        rhom = mf.densitymoistair(Ta,ps,Ah)
                else:
                    rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
            Fm = rhom * numpy.ma.sqrt(vs)
            us = numpy.ma.sqrt(numpy.ma.sqrt(vs))
            qcutils.CreateSeries(ds,'Fm',Fm,['uw','vw'],'Momentum flux, rotated to natural wind coordinates','kg/m/s2')
            qcutils.CreateSeries(ds,'ustar',us,['uw','vw'],'Friction velocity, rotated to natural wind coordinates','m/s')
        else:
            log.error('  CalculateFluxes: wy not found in ds.series, Fm and ustar not calculated')
    else:
        log.error('  CalculateFluxes: wx not found in ds.series, Fm and ustar not calculated')

def CalculateFluxes_Unrotated(ds):
    """
        Calculate the fluxes from the raw covariances.
        
        Usage qcts.CalculateFluxes_Unrotated(ds)
        ds: data structure
        
        Pre-conditions: MergeSeries (including TaFromTv)
        
        Accepts meteorological constants or variables
        """
    log.info(' Calculating fluxes from unrotated covariances')
    if 'UzT' in ds.series.keys():
        UzT,f = qcutils.GetSeriesasMA(ds,'UzT')
        Ta,f = qcutils.GetSeriesasMA(ds,'Ta_EC')
        ps,f = qcutils.GetSeriesasMA(ds,'ps')
        Ah,f = qcutils.GetSeriesasMA(ds,'Ah_EC')
        if 'rhom' in ds.series.keys():
            rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
        else:
            rhom = mf.densitymoistair(Ta,ps,Ah)
        Fh_nr = rhom * c.Cpd * UzT
        qcutils.CreateSeries(ds,'Fh_nr',Fh_nr,['UzT'],'Sensible heat flux, unrotated','W/m2')
    else:
        log.error('  CalculateFluxes_Unrotated: UzT not found in ds.series, Fh_nr not calculated')
    if 'UzA' in ds.series.keys():
        UzA,f = qcutils.GetSeriesasMA(ds,'UzA')
        if 'Lv' in ds.series.keys():
            Lv,f = qcutils,GetSeriesasMA(ds,'Lv')
            Fe_raw_nr = Lv * UzA / float(1000)
        else:
            Fe_raw_nr = c.Lv * UzA / float(1000)
        qcutils.CreateSeries(ds,'Fe_raw_nr',Fe_raw_nr,['UzA'],'Latent heat flux, unrotated','W/m2')
    else:
        log.error('  CalculateFluxes_Unrotated: UzA not found in ds.series, Fe_raw_nr not calculated')
    if 'UzC' in ds.series.keys():
        UzC,f = qcutils.GetSeriesasMA(ds,'UzC')
        Fc_raw_nr = UzC
        qcutils.CreateSeries(ds,'Fc_raw_nr',Fc_raw_nr,['UzC'],'CO2 flux, unrotated','mg/m2/s')
    else:
        log.error('  CalculateFluxes_Unrotated: UzC not found in ds.series, Fc_raw_nr not calculated')
    if 'UxUz' in ds.series.keys():
        if 'UyUz' in ds.series.keys():
            xz,f = qcutils.GetSeriesasMA(ds,'UxUz')
            yz,f = qcutils.GetSeriesasMA(ds,'UyUz')
            vs = xz*xz + yz*yz
            if 'Fh_nr' not in ds.series.keys():
                if 'rhom' not in ds.series.keys():
                        rhom = mf.densitymoistair(Ta,ps,Ah)
                else:
                    rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
            Fm_nr = rhom * numpy.ma.sqrt(vs)
            us_nr = numpy.ma.sqrt(numpy.ma.sqrt(vs))
            qcutils.CreateSeries(ds,'Fm_nr',Fm_nr,['UxUz','UyUz'],'Momentum flux, unrotated','kg/m/s2')
            qcutils.CreateSeries(ds,'ustar_nr',us_nr,['UxUz','UyUz'],'Friction velocity, unrotated','m/s')
        else:
            log.error('  CalculateFluxes_Unrotated: UyUz not found in ds.series, Fm and ustar not calculated')
    else:
        log.error('  CalculateFluxes_Unrotated: UxUz not found in ds.series, Fm and ustar not calculated')

def CalculateFluxesRM(ds):
    """
        Calculate the fluxes from the rotated and Massman corrected covariances.
        
        Usage qcts.CalculateFluxesRM(ds)
        ds: data structure
        
        Pre-requisites: CoordRotation2D, MassmanApprox, Massman
        
        Accepts meteorological constants or variables
        """
    log.info(' Calculating fluxes from covariances')
    if 'wTM' in ds.series.keys():
        wT,f = qcutils.GetSeriesasMA(ds,'wTM')
        Ta,f = qcutils.GetSeriesasMA(ds,'Ta_HMP')
        ps,f = qcutils.GetSeriesasMA(ds,'ps')
        Ah,f = qcutils.GetSeriesasMA(ds,'Ah_HMP')
        if 'rhom' in ds.series.keys():
            rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
        else:
            rhom = mf.densitymoistair(Ta,ps,Ah)
        Fh_rm = rhom * c.Cpd * wT
        qcutils.CreateSeries(ds,'Fh_rm',Fh_rm,['wTM'],'Sensible heat flux, rotated to natural wind coordinates, frequency response corrected, and converted from virtual','W/m2')
    else:
        log.error('  CalculateFluxes: wTc not found in ds.series, Fh not calculated')
    if 'wAM' in ds.series.keys():
        wA,f = qcutils.GetSeriesasMA(ds,'wAM')
        if 'Lv' in ds.series.keys():
            Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
            Fe_rm = Lv * wA / float(1000)
        else:
            Fe_rm = c.Lv * wA / float(1000)
        qcutils.CreateSeries(ds,'Fe_rm',Fe_rm,['wAM'],'Latent heat flux, rotated to natural wind coordinates and frequency response corrected','W/m2')
    else:
        log.error('  CalculateFluxes: wAM not found in ds.series, Fe_raw not calculated')
    if 'wCM' in ds.series.keys():
        wC,f = qcutils.GetSeriesasMA(ds,'wCM')
        Fc_rm = wC
        qcutils.CreateSeries(ds,'Fc_rm',Fc_rm,['wCM'],'CO2 flux, rotated to natural wind coordinates and frequency response corrected','mg/(m2 s)')
    else:
        log.error('  CalculateFluxes: wCM not found in ds.series, Fc_raw not calculated')
    if 'uwM' in ds.series.keys():
        if 'vwM' in ds.series.keys():
            uw,f = qcutils.GetSeriesasMA(ds,'uwM')
            vw,f = qcutils.GetSeriesasMA(ds,'vwM')
            vs = uw * uw + vw * vw
            if 'Fh_rm' not in ds.series.keys():
                if 'rhom' not in ds.series.keys():
                        rhom = mf.densitymoistair(Ta,ps,Ah)
                else:
                    rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
            Fm_rm = rhom * numpy.ma.sqrt(vs)
            qcutils.CreateSeries(ds,'Fm_rm',Fm_rm,['uw','vw'],'Momentum flux, rotated to natural wind coordinates and frequency response corrected','kg/(m s2)')
        else:
            log.error('  CalculateFluxes: vwM not found in ds.series, Fm not calculated')
    else:
        log.error('  CalculateFluxes: uwM not found in ds.series, Fm not calculated')

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
    qcutils.CreateSeries(ds,Fl_out,Fl,[Fl_in,Tbody_in],'Calculated longwave radiation using '+Fl_in+','+Tbody_in,'W/m2')

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
        qcutils.CreateSeries(ds,Fn_out,Fn,[Fsd_in,Fsu_in,Fld_in,Flu_in],'Calculated net radiation using '+Fsd_in+','+Fsu_in+','+Fld_in+','+Flu_in,'W/m2')
    else:
        nRecs = len(ds.series['xlDateTime']['Data'])
        ds.series[Fn_out] = {}
        ds.series[Fn_out]['Data'] = numpy.zeros(nRecs) + float(-9999)
        ds.series[Fn_out]['Flag'] = numpy.zeros(nRecs) + float(1)
        ds.series[Fn_out]['Attr'] = {}
        ds.series[Fn_out]['Attr']['Description'] = 'Calculated net radiation (one or more components missing)'
        ds.series[Fn_out]['Attr']['Units'] = 'W/m2'

def ComputeDailySums(cf,ds):
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
        
        List of sums:
            Rain, ET, Fe_MJ, Fh_MJ, Fg_MJ, Fld_MJ, Flu_MJ, Fnr_MJ, Fsd_MJ,
            Fsu_MJ, Fc_g, Fc_mmol
        List of sub-sums (sums split between positive and negative observations)
            Fe_MJ, Fh_MJ, Fg_MJ
        List of min/max:
            VPD, Ta_HMP, Vbat, Tpanel, Fc_mg, Fc_umol
        """
    monthabr = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
    M1st = int(cf['General']['firstMonth'])
    M2nd = int(cf['General']['secondMonth'])
    
    Fe,f = qcutils.GetSeriesasMA(ds,'Fe_gapfilled')
    Fh,f = qcutils.GetSeriesasMA(ds,'Fh_gapfilled')
    Fg,f = qcutils.GetSeriesasMA(ds,'Fg_Av')
    Fld,f = qcutils.GetSeriesasMA(ds,'Fld')
    Flu,f = qcutils.GetSeriesasMA(ds,'Flu')
    Fnr,f = qcutils.GetSeriesasMA(ds,'Fnr')
    Fsd,f = qcutils.GetSeriesasMA(ds,'Fsd')
    Fsu,f = qcutils.GetSeriesasMA(ds,'Fsu')
    Fc,f = qcutils.GetSeriesasMA(ds,'Fc_gapfilled')
    Rain,f = qcutils.GetSeriesasMA(ds,'Rain')
    VPD,f = qcutils.GetSeriesasMA(ds,'VPD')
    Ta_HMP,f = qcutils.GetSeriesasMA(ds,'Ta_HMP')
    Vbat,f = qcutils.GetSeriesasMA(ds,'Vbat')
    Tpanel,f = qcutils.GetSeriesasMA(ds,'Tpanel')
    Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
    
    ET = Fe * 60 * 30 * 1000 / (Lv * c.rho_water)  # mm/30min for summing
    Fc_umol = Fc * 1e6 / (1000 * 44)               # umol/m2-s for min/max
    Fc_mmol = Fc_umol * 1800 / 1000                # mmol/m2-30min for summing
    Fc_g = Fc * 1800 / 1000                        # g/m2-30min for summing
    
    InList = ['Fe_gapfilled','Fh_gapfilled','Fg_Av','Fld','Flu','Fnr','Fsd','Fsu']
    OutList = ['Fe_MJ','Fh_MJ','Fg_MJ','Fld_MJ','Flu_MJ','Fnr_MJ','Fsd_MJ','Fsu_MJ']
    for index in range(0,8):
        convert_energy(ds,InList[index],OutList[index])
    
    FeMJ,f = qcutils.GetSeriesasMA(ds,'Fe_MJ')
    FhMJ,f = qcutils.GetSeriesasMA(ds,'Fh_MJ')
    FgMJ,f = qcutils.GetSeriesasMA(ds,'Fg_MJ')
    FldMJ,f = qcutils.GetSeriesasMA(ds,'Fld_MJ')
    FluMJ,f = qcutils.GetSeriesasMA(ds,'Flu_MJ')
    FnrMJ,f = qcutils.GetSeriesasMA(ds,'Fnr_MJ')
    FsdMJ,f = qcutils.GetSeriesasMA(ds,'Fsd_MJ')
    FsuMJ,f = qcutils.GetSeriesasMA(ds,'Fsu_MJ')
    qcutils.CreateSeries(ds,'ET',ET,['Fe_gapfilled'],'Flux','mm')
    qcutils.CreateSeries(ds,'Fc_umol',Fc_umol,['Fc_gapfilled'],'Flux','umol/(m2 s)')
    qcutils.CreateSeries(ds,'Fc_mmol',Fc_mmol,['Fc_gapfilled'],'Flux','mmol/m2')
    qcutils.CreateSeries(ds,'Fc_mg',Fc,['Fc_gapfilled'],'Flux','mg/(m2 s)')
    qcutils.CreateSeries(ds,'Fc_g',Fc_g,['Fc_gapfilled'],'Flux','g/m2')

    xlFileName = cf['Files']['L4']['xlFilePath']+cf['Files']['L4']['xlFileName']

    xlFile = xlwt.Workbook()

    SumList = ['Rain','ET','Fe_MJ','Fh_MJ','Fg_MJ','Fld_MJ','Flu_MJ','Fnr_MJ','Fsd_MJ','Fsu_MJ','Fc_g','Fc_mmol']     # output series
    SubSumList = ['Fe_MJ','Fh_MJ','Fg_MJ']     # output series
    MinMaxList = ['VPD','Ta_HMP','Vbat','Tpanel','Fc_mg','Fc_umol']     # output series
    FullList = SumList + MinMaxList
    SWC0list = ['svwc_s_baresoil','svwc_s_mulga','svwc_s_understory','svwc_s_raingage']
    SWC10list = ['svwc_10cm_baresoil_a1','svwc_10cm_mulga_a1','svwc_10cm_understory_a1','svwc_10cm_baresoil_a2','svwc_10cm_a_mulga_a2','svwc_10cm_b_mulga_a2','svwc_10cm_a_understory_a2','svwc_10cm_b_understory_a2']
    SWC60list = ['svwc_60cm_baresoil_a1','svwc_60cm_mulga_a1','svwc_60cm_understory_a1','svwc_60cm_baresoil_a2','svwc_60cm_mulga_a2','svwc_60cm_understory_a2']
    SWC100list = ['svwc_100cm_baresoil_a1','svwc_100cm_mulga_a1','svwc_100cm_understory_a1','svwc_100cm_baresoil_a2','svwc_143cm_baresoil_a2','svwc_100cm_mulga_a2','svwc_100cm_understory_a2']
    
    for ThisOne in FullList:
        log.info(' Doing daily sums for '+ThisOne)
        xlSheet = xlFile.add_sheet(ThisOne)
        xlRow = 0
        xlSheet.write(xlRow,0,'Month')
        xlSheet.write(xlRow,1,'Day')
        xlSheet.write(xlRow,2,'n')
        if ThisOne in SumList:
            xlSheet.write(xlRow,3,ThisOne)
        if ThisOne in MinMaxList:
            xlSheet.write(xlRow,3,ThisOne+'_min')
            xlSheet.write(xlRow,4,ThisOne+'_max')
            if ThisOne == 'VPD' or ThisOne == 'Ta_HMP':
                xlSheet.write(xlRow,5,ThisOne+'_mean')
        if ThisOne in SubSumList:
            xlSheet.write(xlRow,4,'Pos n')
            xlSheet.write(xlRow,5,ThisOne+'_pos')
            xlSheet.write(xlRow,6,'Neg n')
            xlSheet.write(xlRow,7,ThisOne+'_neg')
        Units = ds.series[ThisOne]['Attr']['Units']
        xlRow = xlRow + 1
        xlSheet.write(xlRow,3,Units)
        if ThisOne in MinMaxList:
            xlSheet.write(xlRow,4,Units)
            if ThisOne == 'VPD' or ThisOne == 'Ta_HMP':
                xlSheet.write(xlRow,5,Units)
        if ThisOne in SubSumList:
            xlSheet.write(xlRow,5,Units)
            xlSheet.write(xlRow,7,Units)
        data = numpy.ma.masked_where(abs(ds.series[ThisOne]['Data']-float(-9999))<c.eps,ds.series[ThisOne]['Data'])
        for month in range(M1st,M2nd+1):
            if month == 1 or month == 3 or month == 5 or month == 7 or month == 8 or month == 10 or month == 12:
                dRan = 31
            if month == 2:
                dRan = 28
            if month == 4 or month == 6 or month == 9 or month == 11:
                dRan = 30
            
            for day in range(1,dRan+1):
                xlRow = xlRow + 1
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                if ThisOne in SumList:
                    Num,Sum = get_sums(data[di])
                    xlSheet.write(xlRow,0,monthabr[month-1])
                    xlSheet.write(xlRow,1,day)
                    xlSheet.write(xlRow,2,Num)
                    xlSheet.write(xlRow,3,Sum)
                if ThisOne in SubSumList:
                    PosNum,NegNum,SumPos,SumNeg = get_subsums(data[di])
                    xlSheet.write(xlRow,4,PosNum)
                    xlSheet.write(xlRow,5,SumPos)
                    xlSheet.write(xlRow,6,NegNum)
                    xlSheet.write(xlRow,7,SumNeg)
                if ThisOne in MinMaxList:
                    Num,Min,Max = get_minmax(data[di])
                    if ThisOne == 'VPD' or ThisOne == 'Ta_HMP':
                        Num2,Av = get_averages(data[di])
                    xlSheet.write(xlRow,0,monthabr[month-1])
                    xlSheet.write(xlRow,1,day)
                    xlSheet.write(xlRow,2,Num)
                    xlSheet.write(xlRow,3,Min)
                    xlSheet.write(xlRow,4,Max)
                    if ThisOne == 'VPD' or ThisOne == 'Ta_HMP':
                        xlSheet.write(xlRow,5,Av)
    
    xlSheet = xlFile.add_sheet('SWCsurface')
    xlRow = 0
    xlCol = 0
    xlSheet.write(xlRow,xlCol,'Month')
    xlCol = xlCol + 1
    xlSheet.write(xlRow,xlCol,'Day')
    
    for ThisOne in SWC0list:
        log.info(' Doing daily sums for '+ThisOne)
        xlRow = 0
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,'n')
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,ThisOne)
        Units = ds.series[ThisOne]['Attr']['Units']
        xlRow = xlRow + 1
        xlSheet.write(xlRow,xlCol,Units)
        data = numpy.ma.masked_where(abs(ds.series[ThisOne]['Data']-float(-9999))<c.eps,ds.series[ThisOne]['Data'])
        for month in range(M1st,M2nd+1):
            if month == 1 or month == 3 or month == 5 or month == 7 or month == 8 or month == 10 or month == 12:
                dRan = 31
            if month == 2:
                dRan = 28
            if month == 4 or month == 6 or month == 9 or month == 11:
                dRan = 30
            
            for day in range(1,dRan+1):
                xlRow = xlRow + 1
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                Num,Av = get_soilaverages(data[di])
                if xlCol == 3:
                    xlSheet.write(xlRow,0,monthabr[month-1])
                    xlSheet.write(xlRow,1,day)
                
                xlSheet.write(xlRow,xlCol-1,Num)
                xlSheet.write(xlRow,xlCol,Av)
    
    xlSheet = xlFile.add_sheet('SWC10cm')
    xlRow = 0
    xlCol = 0
    xlSheet.write(xlRow,xlCol,'Month')
    xlCol = xlCol + 1
    xlSheet.write(xlRow,xlCol,'Day')
    
    for ThisOne in SWC10list:
        log.info(' Doing daily sums for '+ThisOne)
        xlRow = 0
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,'n')
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,ThisOne)
        Units = ds.series[ThisOne]['Attr']['Units']
        xlRow = xlRow + 1
        xlSheet.write(xlRow,xlCol,Units)
        data = numpy.ma.masked_where(abs(ds.series[ThisOne]['Data']-float(-9999))<c.eps,ds.series[ThisOne]['Data'])
        for month in range(M1st,M2nd+1):
            if month == 1 or month == 3 or month == 5 or month == 7 or month == 8 or month == 10 or month == 12:
                dRan = 31
            if month == 2:
                dRan = 28
            if month == 4 or month == 6 or month == 9 or month == 11:
                dRan = 30
            
            for day in range(1,dRan+1):
                xlRow = xlRow + 1
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                Num,Av = get_soilaverages(data[di])
                if xlCol == 3:
                    xlSheet.write(xlRow,0,monthabr[month-1])
                    xlSheet.write(xlRow,1,day)
                
                xlSheet.write(xlRow,xlCol-1,Num)
                xlSheet.write(xlRow,xlCol,Av)
    
    xlSheet = xlFile.add_sheet('SWC60cm')
    xlRow = 0
    xlCol = 0
    xlSheet.write(xlRow,xlCol,'Month')
    xlCol = xlCol + 1
    xlSheet.write(xlRow,xlCol,'Day')
    
    for ThisOne in SWC60list:
        log.info(' Doing daily sums for '+ThisOne)
        xlRow = 0
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,'n')
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,ThisOne)
        Units = ds.series[ThisOne]['Attr']['Units']
        xlRow = xlRow + 1
        xlSheet.write(xlRow,xlCol,Units)
        data = numpy.ma.masked_where(abs(ds.series[ThisOne]['Data']-float(-9999))<c.eps,ds.series[ThisOne]['Data'])
        for month in range(M1st,M2nd+1):
            if month == 1 or month == 3 or month == 5 or month == 7 or month == 8 or month == 10 or month == 12:
                dRan = 31
            if month == 2:
                dRan = 28
            if month == 4 or month == 6 or month == 9 or month == 11:
                dRan = 30
            
            for day in range(1,dRan+1):
                xlRow = xlRow + 1
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                Num,Av = get_soilaverages(data[di])
                if xlCol == 3:
                    xlSheet.write(xlRow,0,monthabr[month-1])
                    xlSheet.write(xlRow,1,day)
                
                xlSheet.write(xlRow,xlCol-1,Num)
                xlSheet.write(xlRow,xlCol,Av)
    
    xlSheet = xlFile.add_sheet('SWC100cm')
    xlRow = 0
    xlCol = 0
    xlSheet.write(xlRow,xlCol,'Month')
    xlCol = xlCol + 1
    xlSheet.write(xlRow,xlCol,'Day')
    
    for ThisOne in SWC100list:
        log.info(' Doing daily sums for '+ThisOne)
        xlRow = 0
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,'n')
        xlCol = xlCol + 1
        xlSheet.write(xlRow,xlCol,ThisOne)
        Units = ds.series[ThisOne]['Attr']['Units']
        xlRow = xlRow + 1
        xlSheet.write(xlRow,xlCol,Units)
        data = numpy.ma.masked_where(abs(ds.series[ThisOne]['Data']-float(-9999))<c.eps,ds.series[ThisOne]['Data'])
        for month in range(M1st,M2nd+1):
            if month == 1 or month == 3 or month == 5 or month == 7 or month == 8 or month == 10 or month == 12:
                dRan = 31
            if month == 2:
                dRan = 28
            if month == 4 or month == 6 or month == 9 or month == 11:
                dRan = 30
            
            for day in range(1,dRan+1):
                xlRow = xlRow + 1
                di = numpy.where((ds.series['Month']['Data']==month) & (ds.series['Day']['Data']==day))[0]
                Num,Av = get_soilaverages(data[di])
                if xlCol == 3:
                    xlSheet.write(xlRow,0,monthabr[month-1])
                    xlSheet.write(xlRow,1,day)
                
                xlSheet.write(xlRow,xlCol-1,Num)
                xlSheet.write(xlRow,xlCol,Av)
    
    
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
    qcutils.CreateSeries(ds,OutVar,MJ,[InVar],'Flux','MJ/m2')

def CoordRotation2D(ds):
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
    qcutils.CreateSeries(ds,'eta',eta,['Ux','Uy','Uz'],'Horizontal rotation angle','deg')
    qcutils.CreateSeries(ds,'theta',theta,['Ux','Uy','Uz'],'Vertical rotation angle','deg')
    qcutils.CreateSeries(ds,'u',u,['Ux','Uy','Uz'],'Longitudinal component in natural wind coordinates','m/s')
    qcutils.CreateSeries(ds,'v',v,['Ux','Uy','Uz'],'Lateral component in natural wind coordinates','m/s')
    qcutils.CreateSeries(ds,'w',w,['Ux','Uy','Uz'],'Vertical component in natural wind coordinates','m/s')
    qcutils.CreateSeries(ds,'wT',wT,['Ux','Uy','Uz','UxT','UyT','UzT'],'Kinematic heat flux, rotated to natural wind coordinates','mC/s')
    qcutils.CreateSeries(ds,'wA',wA,['Ux','Uy','Uz','UxA','UyA','UzA'],'Kinematic vapour flux, rotated to natural wind coordinates','g/m2/s')
    qcutils.CreateSeries(ds,'wC',wC,['Ux','Uy','Uz','UxC','UyC','UzC'],'Kinematic CO2 flux, rotated to natural wind coordinates','mg/m2/s')
    qcutils.CreateSeries(ds,'uw',uw,['Ux','Uy','Uz','UxUz','UxUx','UxUy'],'Momentum flux X component, corrected to natural wind coordinates','m2/s2')
    qcutils.CreateSeries(ds,'vw',vw,['Ux','Uy','Uz','UyUz','UxUy','UyUy'],'Momentum flux Y component, corrected to natural wind coordinates','m2/s2')
    #for i in range(nRecs):
        #if ds.series['eta']['Flag'][i] > 0:
            #ds.series['eta']['Flag'][i] = 11
        #if ds.series['theta']['Flag'][i] > 0:
            #ds.series['theta']['Flag'][i] = 11
        #if ds.series['u']['Flag'][i] > 0:
            #ds.series['u']['Flag'][i] = 11
        #if ds.series['v']['Flag'][i] > 0:
            #ds.series['v']['Flag'][i] = 11
        #if ds.series['w']['Flag'][i] > 0:
            #ds.series['w']['Flag'][i] = 11
        #if ds.series['wT']['Flag'][i] > 0:
            #ds.series['wT']['Flag'][i] = 11
        #if ds.series['wA']['Flag'][i] > 0:
            #ds.series['wA']['Flag'][i] = 11
        #if ds.series['wC']['Flag'][i] > 0:
            #ds.series['wC']['Flag'][i] = 11
        #if ds.series['uw']['Flag'][i] > 0:
            #ds.series['uw']['Flag'][i] = 11
        #if ds.series['vw']['Flag'][i] > 0:
            #ds.series['vw']['Flag'][i] = 11

def CorrectFgForStorage(cf,ds,Fg_out,Fg_in,Ts_in):
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
    slist = ast.literal_eval(cf['Soil']['SwsSeries'])
    log.info('  CorrectForStorage: Sws_in is '+str(slist))
    if len(slist)==0:
        Sws = numpy.ones(nRecs)*Sws_default
    elif len(slist)==1:
        Sws,f = qcutils.GetSeriesasMA(ds,slist[0])
    else:
        MergeSeries(ds,'Sws',slist,[0,10])
        Sws,f = qcutils.GetSeriesasMA(ds,slist[0])
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
    qcutils.CreateSeries(ds,Fg_out,Fg_o,[Fg_in],'Soil heat flux corrected for storage','W/m2')
    qcutils.CreateSeries(ds,'S',S,[Fg_in],'Soil heat flux storage','W/m2')
    qcutils.CreateSeries(ds,'Cs',Cs,[Fg_in],'Specific heat capacity','J/m3/K')

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
    SWCempList = eval(cf['Soil']['empSWCin'])
    SWCoutList = eval(cf['Soil']['empSWCout'])
    SWCattr = eval(cf['Soil']['SWCattr'])
    if cf['General']['TDR']=='Yes':
        TDRempList = eval(cf['Soil']['empTDRin'])
        TDRoutList = eval(cf['Soil']['empTDRout'])
        TDRlinList = eval(cf['Soil']['linTDRin'])
        TDRattr = eval(cf['Soil']['TDRattr'])
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
        log.info(' Applying empirical correction to '+SWCempList[i])
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
        
        qcutils.CreateSeries(ds,outvar,Sws_out,[invar],attr,'cm3 water/cm3 soil')
    if cf['General']['TDR']=='Yes':
        for i in range(len(TDRempList)):
            log.info(' Applying empirical correction to '+TDRempList[i])
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
            
            qcutils.CreateSeries(ds,outvar,Sws_out,[invar],attr,'cm3 water/cm3 soil')
        for ThisOne in TDRlinList:
            flag_index = numpy.ma.where((ds.series[ThisOne]['Flag'] > 0))[0]
            j = ds.series[ThisOne]['Flag'].copy()
            ApplyLinear(cf,ds,ThisOne)
            ds.series[ThisOne]['Flag'][flag_index] = j[flag_index]

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
        ItemList = eval(cf['Variables'][Wd_in]['Correction'][str(i)])
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
        ds.globalattributes['Flag2'] = 'QA/QC: Range Check'
        ds.globalattributes['Flag3'] = 'QA/QC: CSAT Diagnostic'
        ds.globalattributes['Flag4'] = 'QA/QC: LI7500 Diagnostic'
        ds.globalattributes['Flag5'] = 'QA/QC: Diurnal SD Check'
        ds.globalattributes['Flag6'] = 'QA/QC: Excluded Dates'
        ds.globalattributes['Flag7'] = 'QA/QC: Solar albedo constraint'
        ds.globalattributes['Flag10'] = 'Corrections: Apply Linear'
        ds.globalattributes['Flag11'] = 'Corrections/Combinations: Coordinate Rotation (Ux, Uy, Uz, UxT, UyT, UzT, UxA, UyA, UzA, UxC, UyC, UzC, UxUz, UxUx, UxUy, UyUz, UxUy, UyUy)'
        ds.globalattributes['Flag12'] = 'Corrections/Combinations: Massman Frequency Attenuation Correction (Coord Rotation, Tv_CSAT, Ah_HMP, ps)'
        ds.globalattributes['Flag13'] = 'Corrections/Combinations: Virtual to Actual Fh (Coord Rotation, Massman, Ta_HMP)'
        ds.globalattributes['Flag14'] = 'Corrections/Combinations: WPL correction for flux effects on density measurements (Coord Rotation, Massman, Fhv to Fh, Cc_7500_Av)'
        ds.globalattributes['Flag16'] = 'Corrections/Combinations: Post-correction Range Check'
        ds.globalattributes['Flag17'] = 'Corrections/Combinations: Post-correction Diurnal SD Check'
        ds.globalattributes['Flag20'] = 'GapFilling (L3_Corrected): Gap coordination'
        ds.globalattributes['Flag21'] = 'GapFilling (L4_GapFilled): Gap Filled'
        ds.globalattributes['Flag22'] = 'GapFilling (L4_GapFilled): Gap not Filled'
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
            for i in range(len(FunctionList)):
                ds.series[ThisOne]['Data'] = eval(cf['Variables'][ThisOne]['Function'][str(i)])
                #ds.series[ThisOne]['Data'] = eval(cf['Variables'][ThisOne]['Function'][str(i)])
                nRecs = numpy.size(ds.series[ThisOne]['Data'])
                if 'Flag' not in ds.series[ThisOne].keys():
                    ds.series[ThisOne]['Flag'] = numpy.zeros(nRecs,int)

def do_linear(cf,ds):
    log.info(' Applying linear corrections given in control file')
    for ThisOne in cf['Variables'].keys():
        if 'Linear' in cf['Variables'][ThisOne].keys():
            if 'DateTime' not in ds.series.keys():
                qcio.get_datetime(ds)
            ApplyLinear(cf,ds,ThisOne)

def Fc_WPL(ds,Fc_wpl_out,Fc_raw_in,Fh_in,Fe_wpl_in,Ta_in,Ah_in,Cc_in,ps_in):
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
        if 'e_HMP' in ds.series.keys():
            e,f = qcutils.GetSeriesasMA(ds,'e_HMP')
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
        Lv,f = qcutils,GetSeriesasMA(ds,'Lv')
        co2_wpl_Fe = 1.61*(Cc/rhod)*(Fe_wpl/Lv)
        co2_wpl_Fh = (1+(1.61*sigma_wpl))*Cc/(Ta+273.15)*Fh/(rhom*Cpm)
    else:
        rhom = rhod+Ah                            # Density of moist air, kg/m3
        co2_wpl_Fe = 1.61*(Cc/rhod)*(Fe_wpl/c.Lv)
        co2_wpl_Fh = (1+(1.61*sigma_wpl))*Cc/(Ta+273.15)*Fh/(rhom*Cpm)
    Fc_wpl_data = Fc_raw+co2_wpl_Fe+co2_wpl_Fh
    mask = numpy.ma.getmask(Fc_wpl_data)
    index = numpy.where(mask.astype(int)==1)
    Fc_wpl_flag[index] = 12
    qcutils.CreateSeries(ds,Fc_wpl_out,Fc_wpl_data,[],'WPL corrected Fc','mg/m2/s')
    ds.series[Fc_wpl_out]['Flag'] = Fc_wpl_flag
    #qcutils.CreateSeries(ds,'rhod',rhod,[],'Density of air, dry','kg/m3')
    #qcutils.CreateSeries(ds,'rhom',rhom,[],'Density of air, moist','kg/m3')
    #qcutils.CreateSeries(ds,'sigma_wpl',sigma_wpl,[],'WPL sigma term','none')
    #qcutils.CreateSeries(ds,'Ah_wpl',Ah,[],'Ah in Fc_WPL','kg/m3')
    #qcutils.CreateSeries(ds,'Cc_wpl',Cc,[],'Cc in Fc_WPL','mg/m3')
    #qcutils.CreateSeries(ds,'Ta_wpl',Ta,[],'Ta in Fc_WPL','C')
    #qcutils.CreateSeries(ds,'co2_wpl_Fe',co2_wpl_Fe,[],'Fc WPL correction Fe term','mg/m2/s')
    #qcutils.CreateSeries(ds,'co2_wpl_Fh',co2_wpl_Fh,[],'Fc WPL correction Fh term','mg/m2/s')
    #ds.series['rhod']['Flag'] = Fc_wpl_flag
    #ds.series['rhom']['Flag'] = Fc_wpl_flag
    #ds.series['sigma_wpl']['Flag'] = Fc_wpl_flag
    #ds.series['Ah_wpl']['Flag'] = Fc_wpl_flag
    #ds.series['Cc_wpl']['Flag'] = Fc_wpl_flag
    #ds.series['Ta_wpl']['Flag'] = Fc_wpl_flag
    #ds.series['co2_wpl_Fe']['Flag'] = Fc_wpl_flag
    #ds.series['co2_wpl_Fh']['Flag'] = Fc_wpl_flag

def Fc_WPLcov(ds,Fc_wpl_out,wC,Fh,wA,Ta,Ah,Cc,ps):
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
    wC,f = qcutils.GetSeriesasMA(ds,wC)
    Fh,f = qcutils.GetSeriesasMA(ds,Fh)
    wA,f = qcutils.GetSeriesasMA(ds,wA)
    Ta,f = qcutils.GetSeriesasMA(ds,Ta)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah)
    Cc,f = qcutils.GetSeriesasMA(ds,Cc)
    ps,f = qcutils.GetSeriesasMA(ds,ps)
    Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
    nRecs = numpy.size(wC)
    TaK = Ta + 273.15
    rhod = mf.densitydryair(Ta,ps)            # Density of dry air, kg/m3
    Ah = Ah/float(1000)                       # Absolute humidity from g/m3 to kg/m3
    Cckg = Cc/float(1000000)                  # CO2 from mg/m3 to kg/m3
    sigma_wpl = Ah/rhod
    if 'Cpm' not in ds.series.keys():
        if 'e_HMP' in ds.series.keys():
            e,f = qcutils.GetSeriesasMA(ds,'e_HMP')
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
    qcutils.CreateSeries(ds,Fc_wpl_out,Fc_wpl_data,['wCM','Fh_rmv','wAwpl','Ta_HMP','Ah_HMP','Cc_7500_Av','ps'],'Fc, rotated to natural wind coordinates, frequency response corrected, and density flux corrected (wpl)','mg/m2/s')
    for i in range(nRecs):
        if (ds.series[Fc_wpl_out]['Flag'][i] > 0) & (ds.series[Fc_wpl_out]['Flag'][i] != 10):
            ds.series[Fc_wpl_out]['Flag'][i] = 14

def Fe_WPL(ds,Fe_wpl_out,Fe_raw_in,Fh_in,Ta_in,Ah_in,ps_in):
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
        if 'e_HMP' in ds.series.keys():
            e,f = qcutils.GetSeriesasMA(ds,'e_HMP')
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
        Lv,f = qcutils,GetSeriesasMA(ds,'Lv')
        h2o_wpl_Fe = 1.61*sigma_wpl*Fe_raw
        h2o_wpl_Fh = (1+(1.61*sigma_wpl))*Ah*Lv*(Fh/(rhom*Cpm))/(Ta+273.15)
    else:
        rhom = rhod+Ah                            # Density of moist air, kg/m3
        h2o_wpl_Fe = 1.61*sigma_wpl*Fe_raw
        h2o_wpl_Fh = (1+(1.61*sigma_wpl))*Ah*c.Lv*(Fh/(rhom*Cpm))/(Ta+273.15)
    Fe_wpl_data = Fe_raw+h2o_wpl_Fe+h2o_wpl_Fh
    mask = numpy.ma.getmask(Fe_wpl_data)
    index = numpy.where(mask.astype(int)==1)
    Fe_wpl_flag[index] = 12
    qcutils.CreateSeries(ds,Fe_wpl_out,Fe_wpl_data,[],'WPL corrected Fe','W/m2')
    ds.series[Fe_wpl_out]['Flag'] = Fe_wpl_flag

def Fe_WPLcov(ds,Fe_wpl_out,wA,Fh,Ta,Ah,ps):
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
    wA,f = qcutils.GetSeriesasMA(ds,wA)
    Fh,f = qcutils.GetSeriesasMA(ds,Fh)
    Ta,f = qcutils.GetSeriesasMA(ds,Ta)
    Ah,f = qcutils.GetSeriesasMA(ds,Ah)
    ps,f = qcutils.GetSeriesasMA(ds,ps)
    rhom,f = qcutils.GetSeriesasMA(ds,'rhom')
    Lv,f = qcutils.GetSeriesasMA(ds,'Lv')
    Cpm,f = qcutils.GetSeriesasMA(ds,'Cpm')
    nRecs = numpy.size(wA)
    TaK = Ta + 273.15
    rhod = mf.densitydryair(Ta,ps)            # Density of dry air, kg/m3
    Ah = Ah/float(1000)                       # Absolute humidity from g/m3 to kg/m3
    sigma_wpl = Ah/rhod
    if 'Cpm' not in ds.series.keys():
        if 'e_HMP' in ds.series.keys():
            e,f = qcutils.GetSeriesasMA(ds,'e_HMP')
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
    qcutils.CreateSeries(ds,Fe_wpl_out,Fe_wpl_data,['wAM','Fh_rmv','Ta_HMP','Ah_HMP','ps'],'Fe, rotated to natural wind coordinates, frequency response corrected, and density flux corrected (wpl)','W/m2')
    qcutils.CreateSeries(ds,'wAwpl',wAwpl,['wAM','Fh_rmv','Ta_HMP','Ah_HMP','ps'],'Cov(wA), rotated to natural wind coordinates, frequency response corrected, and density flux corrected (wpl)','g/(m2 s)')
    for i in range(nRecs):
        if ds.series[Fe_wpl_out]['Flag'][i] > 0:
            ds.series[Fe_wpl_out]['Flag'][i] = 14
        if ds.series['wAwpl']['Flag'][i] > 0:
            ds.series['wAwpl']['Flag'][i] = 14

def FhvtoFh(ds,Ta_in,Fh_in,Tv_in,Fe_in,ps_in,Ah_in,Fh_out,attr):
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
    
    qcutils.CreateSeries(ds,Fh_out,Fh_o,[Ta_in, Fh_in, Tv_in, Fe_in, ps_in, Ah_in], attr, 'W/m2')
    for i in range(nRecs):
        if ds.series[Fh_out]['Flag'][i] > 0:
            ds.series[Fh_out]['Flag'][i] = 13

def GapFillFromAlternate(cf,ds,series=''):
    """
        Gap fill using data from alternate sites specified in the control file
        """
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
                    TList = eval(cf['Variables'][ThisOne]['GapFillFromAlternate'][Alt]['Transform'])
                    # loop over the datetime ranges for the transform
                    for TListEntry in TList:
                        qcts.TransformAlternate(TListEntry,AltDateTime,AltSeriesData)
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
            ds.series[ThisOne]['Flag'][index] = 22

def GapFillFromRatios(cf,ds):
    nRecs = int(ds.globalattributes['NumRecs'])
    # get local versions of the series required as masked arrays
    # - we use masked arrays here to simplify subsequent calculations
    Fn,f = qcutils.GetSeriesasMA(ds,'Fn')         # net radiation
    Fg,f = qcutils.GetSeriesasMA(ds,'Fg')         # ground heat flux
    Fa = Fn - Fg                                  # available energy
    # get local copies of series required as non-masked arrays
    Fh = ds.series['Fh']['Data']
    Fe = ds.series['Fe_wpl']['Data']
    Fc = ds.series['Fc_wpl']['Data']
    for ThisOne in ['Fe_wpl','Fh','Fc_wpl']:
        alt_filename = cf['Variables'][ThisOne]['GapFillUsingRatios']['FileName']
        alt_xlbook = xlrd.open_workbook(alt_filename)
        xl_sheetname = cf['Variables'][ThisOne]['GapFillUsingRatios']['xlSheet']
        xlsheet = alt_xlbook.sheet_by_name(xl_sheetname)
        ratio = numpy.zeros((48,12))
        for xlCol in range(12):
            ratio[:,xlCol] = xlsheet.col_values(xlCol+1)[1:49]
        ratio1d = numpy.zeros(nRecs)
        for i in range(len(ds.series['Month']['Data'])):
            h = numpy.int(2*ds.series['Hdh']['Data'][i])
            m = numpy.int(ds.series['Month']['Data'][i])
            ratio1d[i] = ratio[h,m-1]
        if ThisOne=='Fe_wpl':
            log.info(' Gap filling Fe_wpl using EF')
            Fe_gf = ratio1d * Fa                  # latent heat flux from evaporative fraction
            Fe_gf = numpy.ma.filled(Fe_gf,float(-9999))
            index = numpy.where((abs(Fe-float(-9999))<c.eps)&(abs(Fe_gf-float(-9999))>c.eps))
            ds.series['Fe_wpl']['Data'][index] = Fe_gf[index]
            ds.series['Fe_wpl']['Flag'][index] = 23
            qcutils.CreateSeries(ds,'EF',ratio1d,['Fn'],'Evaporative fraction','none')
        if ThisOne=='Fh':
            log.info(' Gap filling Fh using BR')
            Fh_gf = ratio1d * Fe_gf               # sensible heat flux from Bowen ratio
            Fh_gf = numpy.ma.filled(Fh_gf,float(-9999))
            index = numpy.where((abs(Fh-float(-9999))<c.eps)&(abs(Fh_gf-float(-9999))>c.eps))
            ds.series['Fh']['Data'][index] = Fh_gf[index]
            ds.series['Fh']['Flag'][index] = 23
            qcutils.CreateSeries(ds,'BR',ratio1d,['Fn'],'Bowen ratio','none')
        if ThisOne =='Fc_wpl':
            log.info(' Gap filling Fc_wpl using WUE')
            Fc_gf = ratio1d * Fe_gf               # CO2 flux from ecosystem water use efficiency
            Fc_gf = numpy.ma.filled(Fc_gf,float(-9999))
            index = numpy.where((abs(Fc-float(-9999))<c.eps)&(abs(Fc_gf-float(-9999))>c.eps))
            ds.series['Fc_wpl']['Data'][index] = Fc_gf[index]
            ds.series['Fc_wpl']['Flag'][index] = 23
            qcutils.CreateSeries(ds,'WUE',ratio1d,['Fn'],'Water use efficiency','none')

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
    if Num == 48:
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
    if Num == 48:
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
    if Num == 48:
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
    nRecs = len(ds.series['xlDateTime']['Data'])
    for ThisOne in ['Year','Month','Day','Hour','Minute','Second']:
        ds.series[ThisOne] = {}
        ds.series[ThisOne]['Data'] = numpy.array([-9999]*nRecs,numpy.int32)
    ds.series['Hdh'] = {}
    ds.series['Hdh']['Data'] = numpy.array([-9999]*nRecs,numpy.float32)
    for i in range(nRecs):
        if cf['General']['Platform'] == 'Mac':
            DateTuple = xlrd.xldate_as_tuple(ds.series['xlDateTime']['Data'][i],1)
        else:
            DateTuple = xlrd.xldate_as_tuple(ds.series['xlDateTime']['Data'][i],0)
        ds.series['Year']['Data'][i] = int(DateTuple[0])
        ds.series['Month']['Data'][i] = int(DateTuple[1])
        ds.series['Day']['Data'][i] = int(DateTuple[2])
        ds.series['Hour']['Data'][i] = int(DateTuple[3])
        ds.series['Minute']['Data'][i] = int(DateTuple[4])
        ds.series['Second']['Data'][i] = int(DateTuple[5])
        ds.series['Hdh']['Data'][i] = float(DateTuple[3])+float(DateTuple[4])/60.

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
            ds.series[ThisOne]['Flag'][iom_new] = 21

def LPFilter_SavGol(cf,ds,ThisOne):
    log.info(' Applying low pass Savtisky-Golay filter to '+ThisOne)
    if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'SavGol'):
        data,flag = qcutils.GetSeries(ds,ThisOne)
        ldt = ds.series['DateTime']['Data']
        for ThisOp in cf['Variables'][ThisOne]['SavGol'].keys():
            OpList = eval(cf['Variables'][ThisOne]['SavGol'][ThisOp])
            try:
                si = ldt.index(datetime.datetime.strptime(OpList[0],'%Y-%m-%d %H:%M'))
            except ValueError:
                si = 0
            try:
                ei = ldt.index(datetime.datetime.strptime(OpList[1],'%Y-%m-%d %H:%M')) + 1
            except ValueError:
                ei = -1
            WindowSize = float(OpList[2])
            PolyOrder = float(OpList[3])
            data[si:ei] = savitzky_golay(data[si:ei],WindowSize,PolyOrder,0)
            flag[si:ei] = 13
            ds.series[ThisOne]['Data'] = numpy.ma.filled(data,float(-9999))
            ds.series[ThisOne]['Flag'] = flag

def Massman(cf,ds):
    """
        Massman:  use L recalculated from corrected ustar and wT to correct 
        for flux loss from spectral attenuation
        
        Usage qcts.Massman(cf,ds)
        cf: control file
        ds: data structure
        
        Parameters loaded from control file:
            zmd: z-d
            angle: CSAT-IRGA separation angle
            CSATarm: CSAT sensor distance from CSAT mount
            IRGAarm: IRGA sensor distance from CSAT mount
        
        Correct covariances for flux loss from spectral attenuation using 
        analytical expression in Eqn 4.3, Massman & Clement 2004.  z is referenced
        to the z0 as z - d.  Time constants are as defined in Massman 2000:
            alpha = 1;  0 < z / L <= 2, stable conditions
            alpha = 0.925;  z / L <= 0, neutral or unstable conditions
            b = 2 * pi * fx * tau_b
            p = 2 * pi * fx * tau_e
            fx = nx * (u / z)
            for scalars:
                nx = 0.085;   z / L <= 0
                nx = 2.0 - 1.915 / (1 + 0.5 * (z / L));  z / L > 0
            for momentum:
                nx = 0.079;  z / L <= 0
                nx = 0.079 * (1 + 7.9 * (z / L)) ^ 0.75;  z / L > 0
            tau_b = Tb / 2.8;  equivalent time constant associated with averaging
                               Tb is measurement interval in seconds
            tau_e = sqrt(sum(tau_i ^ 2));  equivalent time constant associated with
                                           sonic line averaging, scalar sensor line
                                           averaging, and sensor separation
            tau_i:
                sonic line averaging (momentum flux):
                    lw / (2.8 * u)  horizontal
                    lw / (5.7 * u)  vertical
                sonic line averaging (scalar flux):
                    lw / (8.4 * u)
                scalar sensor line averaging:
                    l_irga / (4.0 * u)
                lateral separation:
                    l_lat / (1.1 * u)
                longitudinal separation:
                    l_long / (1.05 * u)
                lwVert = 0.1 m
                lwHor = 0.058 m
                lwTv = 0.1155 m
                lIRGA = 0.125 m
        """
    
    log.info(' Correcting for flux loss from spectral attenuation')
    zmd = float(cf['Massman']['zmd'])   # z-d for site
    angle = float(cf['Massman']['angle'])     # CSAT3-IRGA separation angle
    CSATarm = float(cf['Massman']['CSATarm'])  # CSAT3 mounting distance
    IRGAarm = float(cf['Massman']['IRGAarm'])  # IRGA mountain distance

    # prepare parameters, add to dataset, and form masked matrices
    uwa,f = qcutils.GetSeriesasMA(ds,'uwa')
    vwa,f = qcutils.GetSeriesasMA(ds,'vwa')
    ustara = numpy.ma.sqrt(numpy.ma.sqrt(uwa ** 2 + vwa ** 2))
    nRecs = len(uwa)
    
    qcutils.CreateSeries(ds,'ustara',ustara,['uwa','vwa'],'Friction coefficient Massman approximately corrected','m/s')
    for i in range(nRecs):
        if ds.series['ustara']['Flag'][i] > 0:
            ds.series['ustara']['Flag'][i] = 12
    
    Tv,f = qcutils.GetSeriesasMA(ds,'Tv_CSAT')
    ustara,f = qcutils.GetSeriesasMA(ds,'ustara')
    wTa,f = qcutils.GetSeriesasMA(ds,'wTa')
    La = mf.molenv(Tv, ustara, wTa)
    qcutils.CreateSeries(ds,'La',La,['Tv_CSAT','Ah_HMP','ps','wTa'], 'Massman approximately corrected Obukhov Length', 'm')
    for i in range(nRecs):
        if ds.series['La']['Flag'][i] > 0:
            ds.series['La']['Flag'][i] = 12
    
    u,f = qcutils.GetSeriesasMA(ds,'u')
    uw,f = qcutils.GetSeriesasMA(ds,'uw')
    vw,f = qcutils.GetSeriesasMA(ds,'vw')
    wT,f = qcutils.GetSeriesasMA(ds,'wT')
    wC,f = qcutils.GetSeriesasMA(ds,'wC')
    wA,f = qcutils.GetSeriesasMA(ds,'wA')
    zoL = zmd / La
    
    mask = numpy.ma.getmask(u)
    index = numpy.where(mask.astype(int)==0)
    
    lLat = numpy.ma.sin(numpy.deg2rad(angle)) * IRGAarm
    lLong = CSATarm - (numpy.ma.cos(numpy.deg2rad(angle)) * IRGAarm)
    
    alpha = numpy.ma.zeros(nRecs)
    nxMom = numpy.ma.zeros(nRecs)
    nxScalar = numpy.ma.zeros(nRecs)
    fxMom = numpy.ma.zeros(nRecs)
    fxScalar = numpy.ma.zeros(nRecs)
    
    for i in range(nRecs):
        if zoL[i] > 0:
            nxMom[i] = 0.079 * (1 + 7.9 * zoL[i]) ** 0.75
            nxScalar[i] = 2.0 - 1.915 / (1 + 0.5 * zoL[i])
            alpha[i] = 1
        else:
            nxMom[i] = 0.079
            nxScalar[i] = 0.085
            alpha[i] = 0.925
        fxMom[i] = nxMom[i] * (u[i] / zmd)
        fxScalar[i] = nxScalar[i] * (u[i] / zmd)
    
    # compute Massman functions
    tao_eMom = ((c.lwVert / (5.7 * u)) ** 2) + ((c.lwHor / (2.8 * u)) ** 2)
    tao_ewT = ((c.lwVert / (8.4 * u)) ** 2) + ((c.lTv / (4.0 * u)) ** 2)
    tao_ewIRGA = ((c.lwVert / (8.4 * u)) ** 2) + ((c.lIRGA / (4.0 * u)) ** 2) \
                 + ((lLat / (1.1 * u)) ** 2) + ((lLong / (1.05 * u)) ** 2)
    tao_b = c.Tb / 2.8
    
    bMom = 2 * c.Pi * fxMom * tao_b
    bScalar = 2 * c.Pi * fxScalar * tao_b
    pMom = 2 * c.Pi * fxMom * tao_eMom
    pwT = 2 * c.Pi * fxScalar * tao_ewT
    pwIRGA = 2 * c.Pi * fxScalar * tao_ewIRGA
    
    rMom = numpy.ma.zeros(nRecs)
    rwT = numpy.ma.zeros(nRecs)
    rwIRGA = numpy.ma.zeros(nRecs)

    rMom[index] = ((bMom[index] ** alpha[index]) / (bMom[index] ** \
           alpha[index] + 1)) * ((bMom[index] ** alpha[index]) \
           / (bMom[index] ** alpha[index] + pMom[index] ** alpha[index])) \
           * (1 / (pMom[index] ** alpha[index] + 1))
    rwT[index] = ((bScalar[index] ** alpha[index]) / (bScalar[index] ** \
           alpha[index] + 1)) * ((bScalar[index] ** alpha[index]) \
           / (bScalar[index] ** alpha[index] + pwT[index] ** alpha[index])) * \
           (1 / (pwT[index] ** alpha[index] + 1))
    rwIRGA[index] = ((bScalar[index] ** alpha[index]) / (bScalar[index] ** \
           alpha[index] + 1)) * ((bScalar[index] ** \
           alpha[index]) / (bScalar[index] ** alpha[index] + pwIRGA[index] ** \
           alpha[index])) * (1 / (pwIRGA[index] ** \
           alpha[index] + 1))
    
    # determine true fluxes
    uwM = uw / rMom
    vwM = vw / rMom
    wTM = wT / rwT
    wCM = wC / rwIRGA
    wAM = wA / rwIRGA
    ustarM = numpy.ma.sqrt(numpy.ma.sqrt(uwM ** 2 + vwM ** 2))
    
    qcutils.CreateSeries(ds,'uwM',uwM,['uw','L'],'Massman true Cov(uw)','m2/s2')
    qcutils.CreateSeries(ds,'vwM',vwM,['vw','L'],'Massman true Cov(vw)','m2/s2')
    qcutils.CreateSeries(ds,'wTM',wTM,['wT','L'],'Massman true Cov(wT)', 'mC/s')
    qcutils.CreateSeries(ds,'wAM',wAM,['wA','L'],'Massman true Cov(wA)', 'g/m2/s')
    qcutils.CreateSeries(ds,'wCM',wCM,['wC','L'],'Massman true Cov(wC)', 'mg/m2/s')
    qcutils.CreateSeries(ds,'ustarM',ustarM,['uwM','vwM'],'Massman true ustar','m/s')
    for i in range(nRecs):
        if ds.series['uwM']['Flag'][i] > 0:
            ds.series['uwM']['Flag'][i] = 12
        if ds.series['vwM']['Flag'][i] > 0:
            ds.series['vwM']['Flag'][i] = 12
        if ds.series['wTM']['Flag'][i] > 0:
            ds.series['wTM']['Flag'][i] = 12
        if ds.series['wAM']['Flag'][i] > 0:
            ds.series['wAM']['Flag'][i] = 12
        if ds.series['wCM']['Flag'][i] > 0:
            ds.series['wCM']['Flag'][i] = 12
        if ds.series['ustarM']['Flag'][i] > 0:
            ds.series['ustarM']['Flag'][i] = 12
    
    LM = mf.molenv(Tv, ustarM, wTM)
    qcutils.CreateSeries(ds,'LM',LM,['Tv_CSAT','Ah_HMP','ps','wTM'], 'Massman true Obukhov Length', 'm')
    for i in range(nRecs):
        if ds.series['LM']['Flag'][i] > 0:
            ds.series['LM']['Flag'][i] = 12

def MassmanApprox(cf,ds):
    """
        MassmanApprox:  use uncorrected L to approximate L_massman
        
        Usage qcts.MassmanApprox(cf,ds)
        cf: control file
        ds: data structure
        
        Parameters loaded from control file:
            zmd: z-d
        
        Correct covariances for flux loss from spectral attenuation using 
        analytical expression in Eqn 4.3, Massman & Clement 2004.  z is referenced
        to the z0 as z - d.  Time constants are as defined in Massman 2000:
            alpha = 1;  0 < z / L <= 2, stable conditions
            alpha = 0.925;  z / L <= 0, neutral or unstable conditions
            b = 2 * pi * fx * tau_b
            p = 2 * pi * fx * tau_e
            fx = nx * (u / z)
            for scalars:
                nx = 0.085;   z / L <= 0
                nx = 2.0 - 1.915 / (1 + 0.5 * (z / L));  z / L > 0
            for momentum:
                nx = 0.079;  z / L <= 0
                nx = 0.079 * (1 + 7.9 * (z / L)) ^ 0.75;  z / L > 0
            tau_b = Tb / 2.8;  equivalent time constant associated with averaging
                               Tb is measurement interval in seconds
            tau_e = sqrt(sum(tau_i ^ 2));  equivalent time constant associated with
                                           sonic line averaging, scalar sensor line
                                           averaging, and sensor separation
            tau_i:
                sonic line averaging (momentum flux):
                    lw / (2.8 * u)  horizontal
                    lw / (5.7 * u)  vertical
                sonic line averaging (scalar flux):
                    lw / (8.4 * u)
                lwVert = 0.1 m
                lwHor = 0.058 m
                lTv = 0.1155 m
        """
    
    log.info(' Approximating L to correct for flux loss from spectral attenuation')
    zmd = float(cf['Massman']['zmd'])   # z-d for site

    # prepare parameters, save in dataset, and form masked matrices
    if 'ustar' not in ds.series.keys():
        uw,f = qcutils.GetSeriesasMA(ds,'uw')
        vw,f = qcutils.GetSeriesasMA(ds,'vw')
        ustar = numpy.ma.sqrt(numpy.ma.sqrt(uw ** 2 + vw ** 2))
        qcutils.CreateSeries(ds,'ustar',ustar,['uw','vw'],'Friction coefficient corrected in natural wind coordinates','m/s')
    
    if 'L' not in ds.series.keys():
        Tv,f = qcutils.GetSeriesasMA(ds,'Tv_CSAT')
        ustar,f = qcutils.GetSeriesasMA(ds,'ustar')
        wT,f = qcutils.GetSeriesasMA(ds,'wT')
        L = mf.molenv(Tv, ustar, wT)
        qcutils.CreateSeries(ds,'L',L,['Tv_CSAT','Ah_HMP','ps','wT'], 'Uncorrected Obukhov Length', 'm')
    
    u,f = qcutils.GetSeriesasMA(ds,'u')
    wT,f = qcutils.GetSeriesasMA(ds,'wT')
    zoL = zmd / L
    nRecs = len(zoL)

    mask = numpy.ma.getmask(u)
    index = numpy.ma.where(mask.astype(int)==0)
    
    alpha = numpy.ma.zeros(nRecs)
    nxMom = numpy.ma.zeros(nRecs)
    nxScalar = numpy.ma.zeros(nRecs)
    fxMom = numpy.ma.zeros(nRecs)
    fxScalar = numpy.ma.zeros(nRecs)
    for i in range(nRecs):
        if zoL[i] > 0:
            nxMom[i] = 0.079 * (1 + 7.9 * zoL[i]) ** 0.75
            nxScalar[i] = 2.0 - 1.915 / (1 + 0.5 * zoL[i])
            alpha[i] = 1
        else:
            nxMom[i] = 0.079
            nxScalar[i] = 0.085
            alpha[i] = 0.925
        fxMom[i] = nxMom[i] * (u[i] / zmd)
        fxScalar[i] = nxScalar[i] * (u[i] / zmd)
    
    # compute spectral filters
    tao_eMom = ((c.lwVert / (5.7 * u)) ** 2) + ((c.lwHor / (2.8 * u)) ** 2)
    tao_ewT = ((c.lwVert / (8.4 * u)) ** 2) + ((c.lTv / (4.0 * u)) ** 2)
    tao_b = c.Tb / 2.8
    
    bMom = 2 * c.Pi * fxMom * tao_b
    bScalar = 2 * c.Pi * fxScalar * tao_b
    pMom = 2 * c.Pi * fxMom * tao_eMom
    pwT = 2 * c.Pi * fxScalar * tao_ewT
    
    rMom = numpy.ma.zeros(nRecs)
    rwT = numpy.ma.zeros(nRecs)
    
    rMom[index] = ((bMom[index] ** alpha[index]) / (bMom[index] ** \
           alpha[index] + 1)) * ((bMom[index] ** alpha[index]) \
           / (bMom[index] ** alpha[index] + pMom[index] ** alpha[index])) \
           * (1 / (pMom[index] ** alpha[index] + 1))
    rwT[index] = ((bScalar[index] ** alpha[index]) / (bScalar[index] ** \
           alpha[index] + 1)) * ((bScalar[index] ** alpha[index]) \
           / (bScalar[index] ** alpha[index] + pwT[index] ** alpha[index])) * \
           (1 / (pwT[index] ** alpha[index] + 1))
    
    # determine approximately-true Massman fluxes
    uwa = uw / rMom
    vwa = vw / rMom
    wTa = wT / rwT
    
    qcutils.CreateSeries(ds,'uwa',uwa,['uw','L'],'Approximate Massman uw covariance', 'm2/s2')
    qcutils.CreateSeries(ds,'vwa',vwa,['vw','L'],'Approximate Massman vw covariance', 'm2/s2')
    qcutils.CreateSeries(ds,'wTa',wTa,['wT','L'],'Approximate Massman wT covariance', 'mC/s')
    for i in range(nRecs):
        if ds.series['uwa']['Flag'][i] > 0:
            ds.series['uwa']['Flag'][i] = 12
        if ds.series['vwa']['Flag'][i] > 0:
            ds.series['vwa']['Flag'][i] = 12
        if ds.series['wTa']['Flag'][i] > 0:
            ds.series['wTa']['Flag'][i] = 12

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
            log.error('  MergeSeries: primary input series'+Source[0]+'not found')
            return
        data = ds.series[Source[0]]['Data'].copy()
        flag = ds.series[Source[0]]['Flag'].copy()
        SeriesNameString = Source[0]
        SeriesUnitString = ds.series[Source[0]]['Attr']['Units']
    else:
        if Source[0] not in ds.series.keys():
            log.error('  MergeSeries: primary input series'+Source[0]+'not found')
            return
        data = ds.series[Source[0]]['Data'].copy()
        flag = ds.series[Source[0]]['Flag'].copy()
        SeriesNameString = Source[0]
        SeriesUnitString = ds.series[Source[0]]['Attr']['Units']
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
                log.error('  MergeSeries: secondary input series'+ThisOne+'not found')
    if Destination not in ds.series.keys():                 # create new series if destination does not exist
        ds.series[Destination] = {}
        ds.series[Destination]['Attr'] = {}
    ds.series[Destination]['Data'] = data.copy()
    ds.series[Destination]['Flag'] = flag.copy()
    ds.series[Destination]['Attr']['Description'] = 'Merged from '+SeriesNameString
    ds.series[Destination]['Attr']['Units'] = SeriesUnitString

def PT100(ds,T_out,R_in,m):
    log.info(' Calculating temperature from PT100 resistance')
    R,f = qcutils.GetSeriesasMA(ds,R_in)
    R = m*R
    T = (-c.PT100_alpha+numpy.sqrt(c.PT100_alpha**2-4*c.PT100_beta*(-R/100+1)))/(2*c.PT100_beta)
    qcutils.CreateSeries(ds,T_out,T,[R_in],'Calculated PT100 temperature using '+str(R_in),'degC')

def ReplaceOnDiff(cf,ds,series=''):
    # Gap fill using data from alternate sites specified in the control file
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
                            TList = eval(cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt]['Transform'])
                            for TListEntry in TList:
                                qcts.TransformAlternate(TListEntry,AltDateTime,AltSeriesData)
                        if 'Range' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                            RList = eval(cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt]['Range'])
                            for RListEntry in RList:
                                qcts.ReplaceWhenDiffExceedsRange(ds.series['DateTime']['Data'],ds.series[ThisOne],
                                                                 ds.series[ThisOne],ds_alt[n].series[alt_varname],
                                                                 RListEntry)
                    elif 'AltVarName' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                        alt_varname = ThisOne
                        if 'Range' in cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt].keys():
                            RList = eval(cf['Variables'][ThisOne]['ReplaceOnDiff'][Alt]['Range'])
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
    Destination['Attr']['Units'] = Primary['Attr']['Units']

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
    si = qcutils.GetDateIndex(DateTime,RList[0],0)
    ei = qcutils.GetDateIndex(DateTime,RList[1],0)
    Range = RList[2]
    Upper = float(Range[0])
    Lower = float(Range[1])
    index = numpy.ma.where((abs(d_data)<Lower)|(abs(d_data)>Upper))
    p_data[index] = s_data[index]
    p_flag[index] = 24
    Destination['Data'] = numpy.ma.filled(p_data,float(-9999))
    Destination['Flag'] = p_flag.copy()
    Destination['Attr']['Description'] = 'Replaced original with alternate when difference exceeded threshold'
    Destination['Attr']['Units'] = Primary['Attr']['Units']

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
    tmp[index] = numpy.square(Series[index])
    return tmp

def SquareRoot(Series):
    tmp = numpy.array([-9999]*numpy.size(Series),Series.dtype)
    index = numpy.where(Series!=float(-9999))[0]
    tmp[index] = numpy.sqrt(Series[index])
    return tmp

def TaFromTv(ds,Ta_out,Tv_in,Ah_in,ps_in):
    # Calculate the air temperature from the virtual temperature, the
    # absolute humidity and the pressure.
    # NOTE: the virtual temperature is used in place of the air temperature
    #       to calculate the vapour pressure from the absolute humidity, the
    #       approximation involved here is of the order of 1%.
    log.info(' Calculating Ta from Tv')
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
    Ta_flag[index] = 11
    qcutils.CreateSeries(ds,Ta_out,Ta_data,[],'Ta calculated from Tv using '+Tv_in,'degC')
    ds.series[Ta_out]['Flag'] = Ta_flag
    
def TransformAlternate(TList,DateTime,Series):
    # Apply polynomial transform to data series being used as replacement data for gap filling
    #print time.strftime('%X')+' Applying polynomial transform to '+ThisOne
    si = qcutils.GetDateIndex(DateTime,TList[0],0)
    ei = qcutils.GetDateIndex(DateTime,TList[1],-1)
    Series = numpy.ma.masked_where(abs(Series-float(-9999))<c.eps,Series)
    Series[si:ei] = qcutils.polyval(TList[2],Series[si:ei])
    Series = numpy.ma.filled(Series,float(-9999))

def UstarFromFh(ds,us_out,T_in, Ah_in, p_in, Fh_in, u_in, z, z0):
    # Calculate ustar from sensible heat flux, wind speed and
    # roughness length using Wegstein's iterative method.
    #  T is the air temperature, C
    #  p is the atmospheric pressure, kPa
    #  H is the sensible heat flux, W/m^2
    #  u is the wind speed, m/s
    #  z is the measurement height minus the displacement height, m
    #  z0 is the momentum roughness length, m
    log.info(' Calculating ustar from (Fh,Ta,Ah,p,u)')
    T,T_flag = qcutils.GetSeries(ds,T_in)
    Ah,Ah_flag = qcutils.GetSeries(ds,Ah_in)
    p,p_flag = qcutils.GetSeries(ds,p_in)
    Fh,Fh_flag = qcutils.GetSeries(ds,Fh_in)
    u,u_flag = qcutils.GetSeries(ds,u_in)
    nRecs = numpy.size(Fh)
    us = numpy.zeros(nRecs,dtype=numpy.float64) + numpy.float64(-9999)
    us_flag = numpy.zeros(nRecs,dtype=numpy.int)
    #print time.strftime('%X')+' Entering loop over Wegstein'
    for i in range(nRecs):
        if((abs(T[i]-float(-9999))>c.eps)&(abs(Ah[i]-float(-9999))>c.eps)&
           (abs(p[i]-float(-9999))>c.eps)&(abs(Fh[i]-float(-9999))>c.eps)&
           (abs(u[i]-float(-9999))>c.eps)):
            #print ds.series['DateTime']['Data'][i],T[i]
            us[i] = qcutils.Wegstein(T[i], Ah[i], p[i], Fh[i], u[i], z, z0)
            us_flag[i] = 14
        else:
            us[i] = numpy.float64(-9999)
            us_flag[i] = 15
    #print time.strftime('%X')+' Using CreateSeries to create u* series'
    #qcutils.CreateSeries(ds,us_out,us,[Fh_in,T_in,Ah_in,p_in,u_in],'ustar from (Fh,Ta,Ah,p,u)','m/s')
    ds.series[us_out] = {}
    ds.series[us_out]['Data'] = us
    ds.series[us_out]['Flag'] = us_flag
    ds.series[us_out]['Attr'] = {}
    ds.series[us_out]['Attr']['Description'] = 'u* calculated from (Fh,Ta,Ah,P,u)'
    ds.series[us_out]['Attr']['Units'] = 'm/s'