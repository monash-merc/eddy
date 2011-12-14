"""
    OzFlux QC v1.2 23 Aug 2011;

    Version History:
    <<v1.0: 21 July 2011, code diversion reconciliation, PIsaac & JCleverly>>
    <<v1.0b 25 July 2011, with log capability, JCleverly>>
    <<v1.1b 26 July 2011, FhvtoFh output generalised and added to all sites qcl3, qcts functions modified to accept met constants or variables, JCleverly>>
    <<v1.2 23 Aug 2011, daily_sums functions moved to qcts module, JCleverly>>
"""

import sys
import logging
import ast
import constants as c
import copy
import numpy
import qccf
import qcck
import qcio
import qcts
import qcutils
import time
import xlrd
import meteorologicalfunctions as mf

log = logging.getLogger('qc.ls')

def l2qc(cf,ds1):
    """
        Perform initial QA/QC on flux data
        Generates L2 from L1 data
        * check parameters specified in control file
        
        Functions performed:
            qcck.do_rangecheck*
            qcck.do_CSATcheck
            qcck.do_7500check
            qcck.do_diurnalcheck*
            qcck.do_excludedates*
            qcck.do_excludehours*
            qcts.albedo
        """
    # make a copy of the L1 data
    ds2 = copy.deepcopy(ds1)
    ds2.globalattributes['Level'] = 'L2'
    ds2.globalattributes['EPDversion'] = sys.version
    ds2.globalattributes['QCVersion'] = __doc__
    ds2.globalattributes['Functions'] = 'RangeCheck, CSATcheck, 7500check, diurnalcheck, excludedates, excludehours, albedo'
    # do the range check
    for ThisOne in ds2.series.keys():
        qcck.do_rangecheck(cf,ds2,ThisOne)
    log.info(' Finished the L2 range check')
    # do the CSAT diagnostic check
    qcck.do_CSATcheck(cf,ds2)
    # do the LI-7500 diagnostic check
    qcck.do_7500check(cf,ds2)
    # do the diurnal check
    for ThisOne in ds2.series.keys():
        qcck.do_diurnalcheck(cf,ds2,ThisOne)
    log.info(' Finished the L2 diurnal average check')
    # exclude user specified date ranges
    for ThisOne in ds2.series.keys():
        qcck.do_excludedates(cf,ds2,ThisOne)
    log.info(' Finished the L2 exclude dates')
    # exclude user specified hour ranges
    for ThisOne in ds2.series.keys():
        qcck.do_excludehours(cf,ds2,ThisOne)
    log.info(' Finished the L2 exclude hours')
    # constrain albedo estimates to full sun angles
    qcts.albedo(ds2)
    log.info(' Finished the albedo constraints')
    # apply linear corrections to the data
    qcck.do_linear(cf,ds2)
    return ds2

def l3qc_AdelaideRiver(cf,ds2):
    """
        Corrections applied for Adelaide River site
        Generates L3 from L2 data
        
        Functions performed:
            qcts.ApplyLinear (Ah_7500_Av, Cc_7500_Av, UzA, UxA, UyA)
            qcts.MergeSeries (Ah_EC, Ta_EC, Fn)
            qcts.TaFromTv
            qcts.CoordRotation2D
            qcts.CalculateFluxes
            qcts.FhvtoFh
            qcts.Fe_WPL
            qcts.Fc_WPL
            qcts.CalculateNetRadiation
            qcts.InterpolateOverMissing (Sws_5cm)
            qcts.AverageSeriesByElements (Fg)
            qcts.CorrectFgForStorage
            qcts.CalculateAvailableEnergy
            qcck.do_qcchecks
        """
    # make a copy of the L2 data
    ds3 = copy.deepcopy(ds2)
    ds3.globalattributes['Level'] = 'L3'
    ds3.globalattributes['EPDversion'] = sys.version
    ds3.globalattributes['QCVersion'] = __doc__
    ds3.globalattributes['Functions'] = 'ApplyLinear, MergeSeries, TaFromTv, CoordRotation2D, CalculateFluxes, Fe_WPL, Fc_WPL, CalculateNetRadiation, InterpolateOverMissing, AverageSeriesByElements, CorrectFgForStorage, CalculateAvailableEnergy, do_qcchecks'
    # apply linear corrections to the LI-7500 data
    qcts.ApplyLinear(cf,ds3,'Ah_7500_Av')
    qcts.ApplyLinear(cf,ds3,'Cc_7500_Av')
    qcts.ApplyLinear(cf,ds3,'UzA')
    qcts.ApplyLinear(cf,ds3,'UxA')
    qcts.ApplyLinear(cf,ds3,'UyA')
    # merge the HMP and corrected 7500 data
    SrcList = ast.liter_eval(cf['Variables']['Ah_EC']['MergeSeries']['Source'])
    qcts.MergeSeries(ds3,'Ah_EC',SrcList,[0,10])
    # get the air temperature from the CSAT virtual temperature
    qcts.TaFromTv(ds3,'Ta_CSAT','Tv_CSAT','Ah_EC','ps')
    # merge the air temperature from the HMP with that derived from the CSAT
    SrcList = ast.liter_eval(cf['Variables']['Ta_EC']['MergeSeries']['Source'])
    qcts.MergeSeries(ds3,'Ta_EC',SrcList,[0,10])
    # do the 2D coordinate rotation
    qcts.CoordRotation2D(ds3)
    # calculate the fluxes from covariances
    qcts.CalculateFluxes(ds3)
    # approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    attr = 'Fh rotated and converted from virtual heat flux'
    qcts.FhvtoFh(ds3,'Ta_EC','Fh','Tv_CSAT','Fe_raw','ps','Ah_EC','Fh_rv',attr)
    # correct the H2O flux
    qcts.Fe_WPL(ds3,'Fe_wpl','Fe_raw','Fh_rv','Ta_EC','Ah_EC','ps')
    # correct the CO2 flux
    qcts.Fc_WPL(ds3,'Fc_wpl','Fc_raw','Fh_rv','Fe_wpl','Ta_EC','Ah_EC','Cc_7500_Av','ps')
    # calculate the net radiation from the Kipp and Zonen CNR1
    qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
    # combine the net radiation from the NRlite and the Kipp and Zonen CNR1
    qcts.MergeSeries(ds3,'Fn','Fn_KZ','Fn_NR',[0,10])
    # interpolate over missing soil moisture values before using them to calculate soil heat capacity
    qcts.InterpolateOverMissing(ds3,series=['Sws_5cm'])
    # average the soil heat flux data
    qcts.AverageSeriesByElements(ds3,'Fg_Av',['Fg_1','Fg_2'])
    # correct the measured soil heat flux for storage in the soil layer above the sensor
    qcts.CorrectFgForStorage(cf,ds3,'Fg','Fg_Av','Ts','Sws')
    # calculate the available energy
    qcts.CalculateAvailableEnergy(ds3,'Fa','Fn','Fg')
    # re-apply the quality control checks (range, diurnal and rules)
    qcck.do_qcchecks(cf,ds3)
    return ds3

def l3qc_AliceSpringsMulga(cf,ds2):
    """
        Corrections applied for Alice Springs Mulga site
        Generates L3 from L2 data
        
        Functions performed:
            qcts.AddMetVars
            qcts.ApplyLinear (Cc_7500_Av, C_ppm, Ah_7500_Av, H_ppt, TDRlinList)
            qcts.ApplyLinearDrift (Cc_7500_Av, C_ppm)
            qcts.ApplyLinearDriftLocal (Cc_7500_Av, C_ppm)
            qcts.CorrectSWC (SWCList, TDRList)
            qcts.CoordRotation2D
            qcts.MassmanApprox
            qcts.Massman
            qcts.CalculateFluxesRM
            qcts.FhvtoFh
            qcts.Fe_WPLcov
            qcts.Fc_WPLcov
            qcts.CorrectFgForStorage (Fgc_bs, Fgc_ms, Fgc_mu)
            qcts.Average3SeriesByElements (Fg_av, Sws_Av, Ts_Av)
            qcck.do_qcchecks
            qcck.gaps
        """
    # make a copy of the L2 data
    ds3 = copy.deepcopy(ds2)
    ds3.globalattributes['Level'] = 'L3'
    ds3.globalattributes['EPDversion'] = sys.version
    ds3.globalattributes['QCVersion'] = __doc__
    ds3.globalattributes['Functions'] = 'AddMetVars, ApplyLinear, ApplyLinearDrift, ApplyLinearDriftLocal, CorrectSWC, CoordRotation, MassmanApprox, Massman, CalculateFluxesRM, FhvtoFh, Fe_WPLcov, Fc_WPLcov, CorrectFgForStorage, Average3SeriesByElements, do_qcchecks, gaps'
    # add relevant meteorological values to L3 data
    qcts.AddMetVars(ds3)
    # apply linear corrections to the LI-7500 carbon data
    qcts.ApplyLinear(cf,ds3,'Cc_7500_Av')
    qcts.ApplyLinear(cf,ds3,'C_ppm')
    qcts.ApplyLinear(cf,ds3,'Ah_7500_Av')
    qcts.ApplyLinear(cf,ds3,'H_ppt')
    qcts.ApplyLinearDrift(cf,ds3,'Cc_7500_Av')
    qcts.ApplyLinearDrift(cf,ds3,'C_ppm')
    qcts.ApplyLinearDriftLocal(cf,ds3,'Cc_7500_Av')
    qcts.ApplyLinearDriftLocal(cf,ds3,'C_ppm')
    # correct measured soil water content using empirical relationship to collected samples
    qcts.CorrectSWC(cf,ds3)
    # do the 2D coordinate rotation
    qcts.CoordRotation2D(ds3)
    # do the Massman frequency attenuation correction to approximate L
    qcts.MassmanApprox(cf,ds3)
    # do the Massman frequency attenuation correction
    qcts.Massman(cf,ds3)
    # calculate the fluxes from covariances
    qcts.CalculateFluxesRM(ds3)
    # approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    attr = 'Fh rotated, massman corrected, and converted from virtual heat flux'
    qcts.FhvtoFh(ds3,'Ta_HMP','Fh_rm','Tv_CSAT','Fe_rm','ps','Ah_HMP','Fh_rmv',attr)
    # correct the H2O flux
    qcts.Fe_WPLcov(ds3,'Fe_wpl','wAM','Fh_rmv','Ta_HMP','Ah_HMP','ps')
    # correct the CO2 flux
    qcts.Fc_WPLcov(ds3,'Fc_wpl','wCM','Fh_rmv','wAwpl','Ta_HMP','Ah_HMP','Cc_7500_Av','ps')
    # correct the measured soil heat flux for storage in the soil layer above the sensor
    qcts.CorrectFgForStorage(cf,ds3,'Fgc_bs','Fg_bs','Ts_bs','Sws_bs')
    qcts.CorrectFgForStorage(cf,ds3,'Fgc_ms','Fg_ms','Ts_ms','Sws_ms')
    qcts.CorrectFgForStorage(cf,ds3,'Fgc_mu','Fg_mu','Ts_mu','Sws_mu')
    # average soil measurements
    qcts.Average3SeriesByElements(ds3,'Fg_Av',['Fgc_bs','Fgc_ms','Fgc_mu'])
    qcts.Average3SeriesByElements(ds3,'Sws_Av',['svwc_s_baresoil','svwc_s_mulga','svwc_s_understory'])
    qcts.Average3SeriesByElements(ds3,'Ts_Av',['Ts_bs','Ts_ms','Ts_mu'])
    # re-apply the quality control checks (range, diurnal and rules)
    qcck.do_qcchecks(cf,ds3)
    # coordinate gaps in the three main fluxes
    qcck.gaps(cf,ds3)
    return ds3

#def l3qc_DalyPasture(cf,ds2):
    #"""
        #Corrections applied for Daly Pasture site
        #Generates L3 from L2 data
        
        #Functions performed:
            #qcts.ApplyLinear (Ah_7500_Av, Cc_7500_Av, UzA, UxA, UyA)
            #qcts.MergeSeries (Ah_EC, Ta_EC, Fn)
            #qcts.TaFromTv
            #qcts.CoordRotation2D
            #qcts.CalculateFluxes
            #qcts.FhvtoFh
            #qcts.Fe_WPL
            #qcts.Fc_WPL
            #qcts.CalculateNetRadiation
            #qcts.InterpolateOverMissing (Sws_5cm)
            #qcts.AverageSeriesByElements (Fg)
            #qcts.CorrectFgForStorage
            #qcts.CalculateAvailableEnergy
            #qcck.do_qcchecks
        #"""
    ## make a copy of the L2 data
    #ds3 = copy.deepcopy(ds2)
    #ds3.globalattributes['Level'] = 'L3'
    #ds3.globalattributes['EPDversion'] = sys.version
    #ds3.globalattributes['QCVersion'] = __doc__
    #ds3.globalattributes['Functions'] = 'ApplyLinear, MergeSeries, TaFromTv, CoordRotation2D, CalculateFluxes, Fe_WPL, Fc_WPL, CalculateNetRadiation, InterpolateOverMissing, CorrectFgForStorage, CalculateAvailableEnergy, do_qcchecks'
    ## apply linear corrections to the LI-7500 Ah and Fe data
    #qcts.ApplyLinear(cf,ds3,'Ah_7500_Av')
    #qcts.ApplyLinear(cf,ds3,'Cc_7500_Av')
    #qcts.ApplyLinear(cf,ds3,'UzA')
    #qcts.ApplyLinear(cf,ds3,'UxA')
    #qcts.ApplyLinear(cf,ds3,'UyA')
    ## merge the HMP and corrected 7500 data
    #SrcList = ast.liter_eval(cf['Variables']['Ah_EC']['MergeSeries']['Source'])
    #qcts.MergeSeries(ds3,'Ah_EC',SrcList,[0,10])
    ## get the air temperature from the CSAT virtual temperature
    #qcts.TaFromTv(ds3,'Ta_CSAT','Tv_CSAT','Ah_EC','ps')
    ## merge the air temperature from the HMP with that derived from the CSAT
    #SrcList = ast.liter_eval(cf['Variables']['Ta_EC']['MergeSeries']['Source'])
    #qcts.MergeSeries(ds3,'Ta_EC',SrcList,[0,10])
    ## do the 2D coordinate rotation
    #qcts.CoordRotation2D(ds3)
    ## calculate the fluxes from covariances
    #qcts.CalculateFluxes(ds3)
    ## approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    #attr = 'Fh rotated and converted from virtual heat flux'
    #qcts.FhvtoFh(ds3,'Ta_EC','Fh','Tv_CSAT','Fe_raw','ps','Ah_EC','Fh_rv',attr)
    ## correct the H2O flux
    #qcts.Fe_WPL(ds3,'Fe_wpl','Fe_raw','Fh_rv','Ta_EC','Ah_EC','ps')
    ## correct the CO2 flux
    #qcts.Fc_WPL(ds3,'Fc_wpl','Fc_raw','Fh_rv','Fe_wpl','Ta_EC','Ah_EC','Cc_7500_Av','ps')
    ## calculate the net radiation from the Kipp and Zonen CNR1
    #qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
    ## combine the net radiation from the Kipp and Zonen CNR1 and the NRlite
    #qcts.MergeSeries(ds3,'Fn','Fn_KZ','Fn_NR',[0,10])
    ## interpolate over missing soil moisture values before using them to calculate soil heat capacity
    #qcts.InterpolateOverMissing(ds3,series=['Sws_5cm'])
    ## correct the measured soil heat flux for storage in the soil layer above the sensor
    #qcts.CorrectFgForStorage(cf,ds3,'Fg','Fg_1','Ts','Sws')
    ## calculate the available energy
    #qcts.CalculateAvailableEnergy(ds3,'Fa','Fn','Fg')
    ## re-apply the quality control checks (range, diurnal and rules)
    #qcck.do_qcchecks(cf,ds3)
    #return ds3

def l3qc_DalyRegrowth(cf,ds2):
    """
        Corrections applied for Daly Regrowth site
        Generates L3 from L2 data
        
        Functions performed:
            qcts.ApplyLinear (Ah_7500_Av, Cc_7500_Av, UzA, UxA, UyA, Fc_wpl)
            qcts.MergeSeries (Ah_HMP_5m, Ah_EC, Ta_HMP_5m, Ta_EC, Fn)
            qcts.TaFromTv
            qcts.CoordRotation2D
            qcts.CalculateFluxes
            qcts.FhvtoFh
            qcts.Fe_WPL
            qcts.Fc_WPL
            qcts.CalculateNetRadiation
            qcts.InterpolateOverMissing (Sws_5cm)
            qcts.AverageSeriesByElements (Fg)
            qcts.CorrectFgForStorage
            qcts.CalculateAvailableEnergy
            qcck.do_qcchecks
        """
    # make a copy of the L2 data
    ds3 = copy.deepcopy(ds2)
    ds3.globalattributes['Level'] = 'L3'
    ds3.globalattributes['EPDversion'] = sys.version
    ds3.globalattributes['QCVersion'] = __doc__
    ds3.globalattributes['Functions'] = 'ApplyLinear, MergeSeries, TaFromTv, CoordRotation2D, CalculateFluxes, Fe_WPL, Fc_WPL, CalculateNetRadiation, InterpolateOverMissing, CorrectFgForStorage, CalculateAvailableEnergy, do_qcchecks'
    # apply linear corrections to the LI-7500 Ah and Fe data
    qcts.ApplyLinear(cf,ds3,'Ah_7500_Av')
    qcts.ApplyLinear(cf,ds3,'Cc_7500_Av')
    qcts.ApplyLinear(cf,ds3,'UzA')
    qcts.ApplyLinear(cf,ds3,'UxA')
    qcts.ApplyLinear(cf,ds3,'UyA')
    # merge the 2m and 5m HMP Ah data (5m missing until 21/4, 2m present from 15/3)
    qcts.MergeSeries(ds3,'Ah_HMP_5m','Ah_HMP_5m','Ah_HMP_2m',[0,10])
    # merge the HMP and corrected 7500 data
    qcts.MergeSeries(ds3,'Ah_EC','Ah_HMP_5m','Ah_7500_Av',[0,10])
    # get the air temperature from the CSAT virtual temperature
    qcts.TaFromTv(ds3,'Ta_CSAT','Tv_CSAT','Ah_EC','ps')
    # merge the 2m and 5m HMP Ta data (5m missing until 21/4, 2m present from 15/3)
    qcts.MergeSeries(ds3,'Ta_HMP_5m','Ta_HMP_5m','Ta_HMP_2m',[0,10])
    # merge the air temperature from the HMP with that derived from the CSAT
    qcts.MergeSeries(ds3,'Ta_EC','Ta_HMP_5m','Ta_CSAT',[0,10])
    # do the 2D coordinate rotation
    qcts.CoordRotation2D(ds3)
    # calculate the fluxes from the covariances
    qcts.CalculateFluxes(ds3)
    # approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    attr = 'Fh rotated and converted from virtual heat flux'
    qcts.FhvtoFh(ds3,'Ta_EC','Fh','Tv_CSAT','Fe_raw','ps','Ah_EC','Fh_rv',attr)
    # correct the H2O flux
    qcts.Fe_WPL(ds3,'Fe_wpl','Fe_raw','Fh_rv','Ta_EC','Ah_EC','ps')
    # correct the CO2 flux
    qcts.Fc_WPL(ds3,'Fc_wpl','Fc_raw','Fh_rv','Fe_wpl','Ta_EC','Ah_EC','Cc_7500_Av','ps')
    qcts.ApplyLinear(cf,ds3,'Fc_wpl')
    # calculate the net radiation from the Kipp and Zonen CNR1
    qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
    # combine the net radiation from the Kipp and Zonen CNR1 and the NRlite
    qcts.MergeSeries(ds3,'Fn','Fn_KZ','Fn_NR',[0,10])
    # interpolate over missing soil moisture values
    qcts.InterpolateOverMissing(ds3,series=['Sws_5cm'])
    # correct the measured soil heat flux for storage in the soil layer above the sensor
    qcts.CorrectFgForStorage(cf,ds3,'Fg','Fg_1','Ts','Sws')
    # calculate the available energy
    qcts.CalculateAvailableEnergy(ds3,'Fa','Fn','Fg')
    # re-apply the quality control checks (range, diurnal and rules)
    qcck.do_qcchecks(cf,ds3)
    return ds3

def l3qc_FoggDam(cf,ds2):
    """
        Corrections applied for Fogg Dam site
        Generates L3 from L2 data
        
        Functions performed:
            qcts.ApplyLinear (Ah_7500_Av, Cc_7500_Av, UzA, UxA, UyA, Rain)
            qcts.MergeSeries (Ah_EC, Ta_EC, Fn)
            qcts.TaFromTv
            qcts.CoordRotation2D
            qcts.CalculateFluxes
            qcts.FhvtoFh
            qcts.Fe_WPL
            qcts.Fc_WPL
            qcts.CalculateNetRadiation
            qcts.InterpolateOverMissing (Sws_5cm)
            qcts.AverageSeriesByElements (Fg)
            qcts.CorrectFgForStorage
            qcts.CalculateAvailableEnergy
            qcck.do_qcchecks
        """
    # make a copy of the L2 data
    ds3 = copy.deepcopy(ds2)
    ds3.globalattributes['Level'] = 'L3'
    ds3.globalattributes['EPDversion'] = sys.version
    ds3.globalattributes['QCVersion'] = __doc__
    ds3.globalattributes['Functions'] = 'ApplyLinear, MergeSeries, TaFromTv, CoordRotation2D, CalculateFluxes, Fe_WPL, Fc_WPL, CalculateNetRadiation, InterpolateOverMissing, AverageSeriesByElements, CorrectFgForStorage, CalculateAvailableEnergy, do_qcchecks'
    # apply linear corrections to the LI-7500 Ah and Fe data
    qcts.ApplyLinear(cf,ds3,'Ah_7500_Av')
    qcts.ApplyLinear(cf,ds3,'Cc_7500_Av')
    qcts.ApplyLinear(cf,ds3,'UzA')
    qcts.ApplyLinear(cf,ds3,'UxA')
    qcts.ApplyLinear(cf,ds3,'UyA')
    qcts.ApplyLinear(cf,ds3,'Rain')
    # merge the HMP and corrected 7500 data
    qcts.MergeSeries(ds3,'Ah_EC','Ah_HMP_5m','Ah_7500_Av',[0,10])
    # get the air temperature from the CSAT virtual temperature
    qcts.TaFromTv(ds3,'Ta_CSAT','Tv_CSAT','Ah_EC','ps')
    # merge the air temperature from the HMP with that derived from the CSAT
    qcts.MergeSeries(ds3,'Ta_EC','Ta_HMP_5m','Ta_CSAT',[0,10])
    # do the 2D coordinate rotation
    qcts.CoordRotation2D(ds3)
    # calculate the fluxes
    qcts.CalculateFluxes(ds3)
    # approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    attr = 'Fh rotated and converted from virtual heat flux'
    qcts.FhvtoFh(ds3,'Ta_EC','Fh','Tv_CSAT','Fe_raw','ps','Ah_EC','Fh_rv',attr)
    # correct the H2O flux
    qcts.Fe_WPL(ds3,'Fe_wpl','Fe_raw','Fh_rv','Ta_EC','Ah_EC','ps')
    # correct the CO2 flux
    qcts.Fc_WPL(ds3,'Fc_wpl','Fc_raw','Fh_rv','Fe_wpl','Ta_EC','Ah_EC','Cc_7500_Av','ps')
    # calculate the net radiation from the Kipp and Zonen CNR1
    qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
    # combine the net radiation from the Kipp and Zonen CNR1 and the NRlite
    qcts.MergeSeries(ds3,'Fn','Fn_KZ','Fn_NR',[0,10])
    # interpolate over missing soil moisture values
    qcts.InterpolateOverMissing(ds3,series=['Sws_10cm'])
    # average the soil heat flux data
    qcts.AverageSeriesByElements(ds3,'Fg_Av',['Fg_1','Fg_2'])
    # correct the measured soil heat flux for storage in the soil layer above the sensor
    qcts.CorrectFgForStorage(cf,ds3,'Fg','Fg_Av','Ts','Sws')
    # calculate the available energy
    qcts.CalculateAvailableEnergy(ds3,'Fa','Fn','Fg')
    # re-apply the quality control checks (range, diurnal and rules)
    qcck.do_qcchecks(cf,ds3)
    return ds3

def l3qc_Standard(cf,ds2):
    """
        Corrections applied for Standard site
        Generates L3 from L2 data
        
        Functions performed:
            qcts.ApplyLinear (Ah_7500_Av, Cc_7500_Av, UzA, UxA, UyA)
            qcts.MergeSeries (Ah_EC, Ta_EC, Fsd, Fn)
            qcts.TaFromTv
            qcts.CoordRotation2D
            qcts.CalculateFluxes
            qcts.FhvtoFh
            qcts.Fe_WPL
            qcts.Fc_WPL
            qcts.CalculateNetRadiation
            qcts.AverageSeriesByElements (Fg_Av, Ts, Sws)
            qcts.CorrectFgForStorage
            qcts.CalculateAvailableEnergy
            qcck.do_qcchecks
        """
    # make a copy of the L2 data
    ds3 = copy.deepcopy(ds2)
    ds3.globalattributes['Level'] = 'L3'
    ds3.globalattributes['EPDversion'] = sys.version
    ds3.globalattributes['QCVersion'] = __doc__
    ds3.globalattributes['Functions'] = 'ApplyLinear, MergeSeries, TaFromTv, CoordRotation2D, CalculateFluxes, Fe_WPL, Fc_WPL, CalculateNetRadiation, AverageSeriesByElements, CorrectFgForStorage, CalculateAvailableEnergy, do_qcchecks'
    # apply linear corrections to the data
    qcck.do_linear(cf,ds3)
    # merge the HMP and corrected 7500 data
    srclist = qccf.GetMergeList(cf,'Ah_EC',default=['Ah_HMP_01'])
    qcts.MergeSeries(ds3,'Ah_EC',srclist,[0,10])
    # get the air temperature from the CSAT virtual temperature
    qcts.TaFromTv(ds3,'Ta_CSAT','Tv_CSAT','Ah_EC','ps')
    # merge the air temperature from the HMP with that derived from the CSAT
    srclist = qccf.GetMergeList(cf,'Ta_EC',default=['Ta_HMP_01'])
    qcts.MergeSeries(ds3,'Ta_EC',srclist,[0,10])
    # do the 2D coordinate rotation
    qcts.CoordRotation2D(ds3)
    # calculate the fluxes
    qcts.CalculateFluxes(ds3)
    # approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    attr = 'Fh rotated and converted from virtual heat flux'
    qcts.FhvtoFh(ds3,'Ta_EC','Fh','Tv_CSAT','Fe_raw','ps','Ah_EC','Fh_rv',attr)
    # correct the H2O flux
    qcts.Fe_WPL(ds3,'Fe_wpl','Fe_raw','Fh_rv','Ta_EC','Ah_EC','ps')
    # correct the CO2 flux
    qcts.Fc_WPL(ds3,'Fc_wpl','Fc_raw','Fh_rv','Fe_wpl','Ta_EC','Ah_EC','Cc_7500_Av','ps')
    # merge the incoming shortwave radiation from the Kipp and Zonen CNR1 and any other pyranometers available
    srclist = qccf.GetMergeList(cf,'Fsd',default=['Fsd'])
    qcts.MergeSeries(ds3,'Fsd',srclist,[0,10])
    # calculate the net radiation from the Kipp and Zonen CNR1
    qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
    # combine the net radiation from the Kipp and Zonen CNR1 and the NRlite
    srclist = qccf.GetMergeList(cf,'Fn',default=['Fn_KZ'])
    qcts.MergeSeries(ds3,'Fn',srclist,[0,10])
    # average the soil heat flux data
    srclist = qccf.GetAverageList(cf,'Fg_01',default=['Fg_01a'])
    qcts.AverageSeriesByElements(ds3,'Fg_01',srclist)
    # average the soil temperature data
    srclist = qccf.GetAverageList(cf,'Ts_01',default=['Ts_01a'])
    qcts.AverageSeriesByElements(ds3,'Ts_01',srclist)
    # average soil moisture
    slist = [l for l in cf['Variables'].keys() if 'Sws_' in l]
    for ThisOne in slist:
        if ThisOne in cf['Variables'].keys() and 'AverageSeries' in cf['Variables'][ThisOne].keys():
            srclist = qccf.GetAverageList(cf,ThisOne)
            qcts.AverageSeriesByElements(ds3,ThisOne,srclist)
    # correct the measured soil heat flux for storage in the soil layer above the sensor
    qcts.CorrectFgForStorage(cf,ds3,'Fg','Fg_01','Ts_01')
    # calculate the available energy
    qcts.CalculateAvailableEnergy(ds3,'Fa','Fn','Fg')
    # re-apply the quality control checks (range, diurnal and rules)
    qcck.do_qcchecks(cf,ds3)
    return ds3

#def l3qc_SturtPlains(cf,ds2):
    #"""
        #Corrections applied for Sturt Plains site
        #Generates L3 from L2 data
        
        #Functions performed:
            #qcts.ApplyLinear (Ah_7500_Av, Cc_7500_Av, UzA, UxA, UyA, Rain, Sws_5cm)
            #qcts.MergeSeries (Ah_EC, Ta_EC, Fsd, Fsu, Fn)
            #qcts.TaFromTv
            #qcts.CoordRotation2D
            #qcts.CalculateFluxes
            #qcts.FhvtoFh
            #qcts.Fe_WPL
            #qcts.Fc_WPL
            #qcts.CalculateNetRadiation
            #qcts.InterpolateOverMissing (Sws_5cm)
            #qcts.AverageSeriesByElements (Fg)
            #qcts.CorrectFgForStorage
            #qcts.CalculateAvailableEnergy
            #qcck.do_qcchecks
        #"""
    ## make a copy of the L2 data
    #ds3 = copy.deepcopy(ds2)
    #ds3.globalattributes['Level'] = 'L3'
    #ds3.globalattributes['EPDversion'] = sys.version
    #ds3.globalattributes['QCVersion'] = __doc__
    #ds3.globalattributes['Functions'] = 'ApplyLinear, MergeSeries, TaFromTv, CoordRotation2D, CalculateFluxes, Fe_WPL, Fc_WPL, CalculateNetRadiation, InterpolateOverMissing, AverageSeriesByElements, CorrectFgForStorage, CalculateAvailableEnergy, do_qcchecks'
    ## apply linear corrections to the LI-7500 Ah and Fe data
    #qcts.ApplyLinear(cf,ds3,'Ah_7500_Av')
    #qcts.ApplyLinear(cf,ds3,'Cc_7500_Av')
    #qcts.ApplyLinear(cf,ds3,'UzA')
    #qcts.ApplyLinear(cf,ds3,'UxA')
    #qcts.ApplyLinear(cf,ds3,'UyA')
    #qcts.ApplyLinear(cf,ds3,'Rain')     # correct for error in logger program
    ## merge the HMP and corrected 7500 data
    #qcts.MergeSeries(ds3,'Ah_EC','Ah_HMP_5m','Ah_7500_Av',[0,10])
    ## get the air temperature from the CSAT virtual temperature
    #qcts.TaFromTv(ds3,'Ta_CSAT','Tv_CSAT','Ah_EC','ps')
    ## merge the air temperature from the HMP with that derived from the CSAT
    #qcts.MergeSeries(ds3,'Ta_EC','Ta_HMP_5m','Ta_CSAT',[0,10])
    ## do the 2D coordinate rotation
    #qcts.CoordRotation2D(ds3)
    ## calculate the fluxes
    #qcts.CalculateFluxes(ds3)
    ## approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    #attr = 'Fh rotated and converted from virtual heat flux'
    #qcts.FhvtoFh(ds3,'Ta_EC','Fh','Tv_CSAT','Fe_raw','ps','Ah_EC','Fh_rv',attr)
    ## correct the H2O flux
    #qcts.Fe_WPL(ds3,'Fe_wpl','Fe_raw','Fh_rv','Ta_EC','Ah_EC','ps')
    ## correct the CO2 flux
    #qcts.Fc_WPL(ds3,'Fc_wpl','Fc_raw','Fh_rv','Fe_wpl','Ta_EC','Ah_EC','Cc_7500_Av','ps')
    ## merge the shortwave radiation from the CNR1 and the CM11
    #qcts.MergeSeries(ds3,'Fsd','Fsd_CNR1','Fsd_CM11',[0,10])
    #qcts.MergeSeries(ds3,'Fsu','Fsu_CNR1','Fsu_CM11',[0,10])
    ## calculate the net radiation from the Kipp and Zonen CNR1
    #qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
    ## combine the net radiation from the Kipp and Zonen CNR1 and the NRlite
    #qcts.MergeSeries(ds3,'Fn','Fn_KZ','Fn_NR',[0,10])
    ## crude fix for values of soil moisture that are too high
    #qcts.ApplyLinear(cf,ds3,'Sws_5cm')
    ## interpolate over missing soil moisture values
    #qcts.InterpolateOverMissing(ds3,series=['Sws_5cm'])
    ## average the soil heat flux data
    #qcts.AverageSeriesByElements(ds3,'Fg_Av',['Fg_1','Fg_2'])
    ## correct the measured soil heat flux for storage in the soil layer above the sensor
    #qcts.CorrectFgForStorage(cf,ds3,'Fg','Fg_Av','Ts','Sws')
    ## calculate the available energy
    #qcts.CalculateAvailableEnergy(ds3,'Fa','Fn','Fg')
    ## re-apply the quality control checks (range, diurnal and rules)
    #qcck.do_qcchecks(cf,ds3)
    #return ds3

def l4qc_FillMetGaps(cf,ds3):
    """
        Fill gaps in met data from other sources
        Generates L4 from L3 data
        
        Variable Series:
            Meteorological (MList): Ah_EC, Cc_7500_Av, ps, Ta_EC, Ws_CSAT, Wd_CSAT
            Radiation (RList): Fld, Flu, Fn, Fsd, Fsu
            Soil water content (SwsList): all variables containing Sws in variable name
            Soil (SList): Fg, Ts, SwsList
            Turbulent fluxes (FList): Fc_wpl, Fe_wpl, Fh, ustar
            Output (OList): MList, RList, SList, FList
        
        Parameters loaded from control file:
            zmd: z-d
            z0: roughness height
        
        Functions performed:
            qcts.InterpolateOverMissing (OList for gaps shorter than 3 observations, OList gaps shorter than 7 observations)
            qcts.GapFillFromAlternate (MList, RList)
            qcts.GapFillFromClimatology (Ah_EC, Fn, Fg, ps, Ta_EC, Ws_CSAT, OList)
            qcts.GapFillFromRatios (Fe, Fh, Fc)
            qcts.ReplaceOnDiff (Ws_CSAT, ustar)
            qcts.UstarFromFh
            qcts.ReplaceWhereMissing (Ustar)
            qcck.do_qcchecks
        """
    level = 'L4'    # level of processing
    # get z-d (measurement height minus displacement height) and z0 from the control file
    zmd = float(cf['General']['zmd'])   # z-d for site
    z0 = float(cf['General']['z0'])     # z0 for site
    # make a copy of the L4 data, data from the alternate sites will be merged with this copy
    ds4 = copy.deepcopy(ds3)
    ds4.globalattributes['Level'] = level
    ds4.globalattributes['EPDversion'] = sys.version
    ds4.globalattributes['QCVersion'] = __doc__
    ds4.globalattributes['Functions'] = 'InterpolateOverMissing, GapFillFromAlternate, GapFillFromClimatology, GapFillFromRatios, ReplaceOnDiff, UstarFromFh, ReplaceWhereMissing, do_qcchecks'
    # linear interpolation to fill missing values over gaps of 1 hour
    qcts.InterpolateOverMissing(cf,ds4,maxlen=2)
    # gap fill meteorological and radiation data from the alternate site(s)
    log.info(' Gap filling using data from alternate sites')
    qcts.GapFillFromAlternate(cf,ds4)
    # gap fill meteorological, radiation and soil data using climatology
    log.info(' Gap filling using site climatology')
    qcts.GapFillFromClimatology(cf,ds4)
    # gap fill using neural networks
    # qcts.GapFillFluxesUsingNN(cf,ds4)
    # gap fill using "match and replace"
    # qcts.GapFillFluxesUsingMR(cf,ds4)
    # gap fill using evaporative fraction (Fe/Fa), Bowen ratio (Fh/Fe) and ecosystem water use efficiency (Fc/Fe)
    log.info(' Gap filling Fe, Fh and Fc using ratios')
    qcts.GapFillFromRatios(cf,ds4)
    # !!! this section required for Daly Uncleared 2009 to deal with bad CSAT from 14/4/2009 to 22/10/2009 !!!
    # replace wind speed at Daly Uncleared when it differs from alternate site by more than threshold
    log.info(' Replacing Ws_CSAT when difference with alternate data exceeds threshold')
    qcts.ReplaceOnDiff(cf,ds4,series=['Ws_CSAT'])
    # calculate u* from Fh and corrected wind speed
    qcts.UstarFromFh(ds4,'uscalc','Ta_EC', 'Ah_EC', 'ps', 'Fh', 'Ws_CSAT', zmd, z0)
    qcts.ReplaceWhereMissing(ds4.series['ustar'],ds4.series['ustar'],ds4.series['uscalc'],0)
    # !!! this section required for Daly Uncleared 2009 to deal with bad CSAT from 14/4/2009 to 22/10/2009 !!!
    # replace measured u* with calculated u* when difference exceeds threshold
    log.info(' Replacing ustar when difference with alternate data exceeds threshold')
    qcts.ReplaceOnDiff(cf,ds4,series=['ustar'])
    # re-apply the quality control checks (range, diurnal and rules)
    log.info(' Doing QC checks on L4 data')
    qcck.do_qcchecks(cf,ds4)
    # interpolate over any ramaining gaps up to 3 hours in length
    qcts.InterpolateOverMissing(cf,ds4,maxlen=6)
    # fill any remaining gaps climatology
    qcts.GapFillFromClimatology(cf,ds4)
    return ds4

def l4qc_GapFilledFluxes(cf,ds3):
    """
        Integrate SOLO-ANN gap filled fluxes performed externally
        Generates L4 from L3 data
        Generates daily sums excel workbook
        
        Functions performed:
            qcts.AddMetVars
            qcts.ComputeDailySums
        """
    # make a copy of the L4 data
    ds4 = copy.deepcopy(ds3)
    ds4.globalattributes['Level'] = 'L4'
    ds4.globalattributes['EPDversion'] = sys.version
    ds4.globalattributes['QCVersion'] = __doc__
    ds4.globalattributes['Functions'] = 'SOLO ANN GapFilling 10-day window, AddMetVars, ComputeDailySums (not included)'
    # duplicate gapfilled fluxes for graphing comparison
    Fe,f = qcutils.GetSeriesasMA(ds4,'Fe_gapfilled')
    Fc,f = qcutils.GetSeriesasMA(ds4,'Fc_gapfilled')
    Fh,f = qcutils.GetSeriesasMA(ds4,'Fh_gapfilled')
    qcutils.CreateSeries(ds4,'Fe_wpl',Fe,['Fe_gapfilled'],'ANN gapfilled Fe','W/m2')
    qcutils.CreateSeries(ds4,'Fh_rmv',Fh,['Fh_gapfilled'],'ANN gapfilled Fh','W/m2')
    qcutils.CreateSeries(ds4,'Fc_wpl',Fc,['Fc_gapfilled'],'ANN gapfilled Fc','mg/m2/s')
    # add relevant meteorological values to L3 data
    qcts.AddMetVars(ds4)
    # compute daily statistics
    qcts.ComputeDailySums(cf,ds4)
    return ds4

