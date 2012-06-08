"""
    OzFlux QC v2.0 7 Jun 2012;

    Version History:
    <<v1.0: 21 July 2011, code diversion reconciliation>>
    <<v1.4 30 Sep 2011, final version arrising from OzFlux Black Mountain data workshop>>
    <<v1.9.9a 8 June 2012, version arrising from conclusion of OzFlux UTS data workshop>>
"""

import sys
import logging
import ast
import constants as c
import copy
import numpy
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
    # do the CSAT diagnostic check
    qcck.do_CSATcheck(cf,ds2)
    # do the LI-7500 diagnostic check
    qcck.do_7500check(cf,ds2)
    # constrain albedo estimates to full sun angles
    qcts.albedo(cf,ds2)
    log.info(' Finished the albedo constraints')    # apply linear corrections to the data
    log.info(' Applying linear corrections ...')
    qcck.do_linear(cf,ds2)
    # write series statistics to file
    qcutils.GetSeriesStats(cf,ds2)
    return ds2

def l3qc(cf,ds2):
    """
        Corrections
        Generates L3 from L2 data
        
        Functions performed:
            qcts.AddMetVars (optional)
            qcts.CorrectSWC (optional*)
            qcck.do_linear (all sites)
            qcutils.GetMergeList + qcts.MergeSeries Ah_EC (optional)x
            qcts.TaFromTv (optional)
            qcutils.GetMergeList + qcts.MergeSeries Ta_EC (optional)x
            qcts.CoordRotation2D (all sites)
            qcts.MassmanApprox (optional*)y
            qcts.Massman (optional*)y
            qcts.CalculateFluxes (used if Massman not optioned)x
            qcts.CalculateFluxesRM (used if Massman optioned)y
            qcts.FhvtoFh (all sites)
            qcts.Fe_WPL (WPL computed on fluxes, as with Campbell algorithm)+x
            qcts.Fc_WPL (WPL computed on fluxes, as with Campbell algorithm)+x
            qcts.Fe_WPLcov (WPL computed on kinematic fluxes (ie, covariances), as with WPL80)+y
            qcts.Fc_WPLcov (WPL computed on kinematic fluxes (ie, covariances), as with WPL80)+y
            qcts.CalculateNetRadiation (optional)
            qcutils.GetMergeList + qcts.MergeSeries Fsd (optional)
            qcutils.GetMergeList + qcts.MergeSeries Fn (optional*)
            qcts.InterpolateOverMissing (optional)
            AverageSeriesByElements (optional)
            qcts.CorrectFgForStorage (all sites)
            qcts.Average3SeriesByElements (optional)
            qcts.CalculateAvailableEnergy (optional)
            qcck.do_qcchecks (all sites)
            qcck.gaps (optional)
            
            *:  requires ancillary measurements for paratmerisation
            +:  each site requires one pair, either Fe_WPL & Fc_WPL (default) or Fe_WPLCov & FcWPLCov
            x:  required together in option set
            y:  required together in option set
        """
    # make a copy of the L2 data
    ds3 = copy.deepcopy(ds2)
    ds3.globalattributes['Level'] = 'L3'
    ds3.globalattributes['EPDversion'] = sys.version
    ds3.globalattributes['QCVersion'] = __doc__
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList'):
        l3functions = ast.literal_eval(cf['General']['FunctionList'])
    else:
        l3functions = ['do_linear', 'MergeSeriesAhTa', 'TaFromTv', 'CalculateMetVars', 'CoordRotation2D', 'CalculateFluxes', 'FhvtoFh', 'WPL', 'CalculateNetRadiation', 'PreCorrectSoilAverage', 'CorrectFgForStorage', 'CalculateAvailableEnergy', 'do_qcchecks','do_Ah7500check']
    ds3.globalattributes['Functions'] = l3functions
    
    # correct measured soil water content using empirical relationship to collected samples
    if 'CorrectSWC' in l3functions:
        log.info(' Correcting soil moisture data ...')
        qcts.CorrectSWC(cf,ds3)
    
    # apply linear corrections to the data
    log.info(' Applying linear corrections ...')
    qcck.do_linear(cf,ds3)
    
    # merge the HMP and corrected 7500 data
    if 'MergeSeriesAhTa' in l3functions:
        srclist = qcutils.GetMergeList(cf,'Ah',default=['Ah_HMP_01'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Ah',srclist,[0,10])
    
    # get the air temperature from the CSAT virtual temperature
        qcts.TaFromTv(cf,ds3)
    
    # merge the HMP and corrected CSAT data
        srclist = qcutils.GetMergeList(cf,'Ta',default=['Ta_HMP_01'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Ta',srclist,[0,10])
    
    # add relevant meteorological values to L3 data
    log.info(' Adding standard met variables to database')
    qcts.CalculateMeteorologicalVariables(cf,ds3)
    
    # do the 2D coordinate rotation
    qcts.CoordRotation2D(cf,ds3)
    
    # do the Massman frequency attenuation correction
    if 'Massman' in l3functions or 'MassmanStandard' in l3functions:
        qcts.Massman(cf,ds3)
    
    # calculate the fluxes
    if 'Massman' not in l3functions:
        qcts.CalculateFluxes(cf,ds3)
    
    # calculate the fluxes from covariances
    if 'Massman' in l3functions:
        qcts.CalculateFluxesRM(cf,ds3)
    
    # approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    qcts.FhvtoFh(cf,ds3)
    
    # correct the H2O & CO2 flux due to effects of flux on density measurements
    if 'WPLcov' in l3functions:
        qcts.do_WPL(cf,ds3,cov='True')
    else:
        qcts.do_WPL(cf,ds3)
    
    # calculate the net radiation from the Kipp and Zonen CNR1
    if 'CalculateNetRadiation' in l3functions:
        srclist = qcutils.GetMergeList(cf,'Fsd',default=['Fsd'])
        qcts.MergeSeries(ds3,'Fsd',srclist,[0,10])
        qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
        srclist = qcutils.GetMergeList(cf,'Fn',default=['Fn_KZ'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Fn',srclist,[0,10])
    
    # combine wind speed from the CSAT and the Wind Sentry
    if 'MergeSeriesWS' in l3functions:
        srclist = qcutils.GetMergeList(cf,'Ws',default=['Ws_WS_01','Ws_CSAT'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Ws',srclist,[0,10])
    
    # average ground heat flux before correcting for storage above sensors
    if 'PreCorrectSoilAverage' in l3functions:
        srclist = qcutils.GetAverageList(cf,'Fg',default=['Fg_01a'])
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,'Fg',srclist)
    
    # average the soil temperature data
    srclist = qcutils.GetAverageList(cf,'Ts',default=['Ts_01a'])
    if len(srclist) > 0:
        qcts.AverageSeriesByElements(ds3,'Ts',srclist)
    
    # average soil moisture
    srclist = qcutils.GetAverageList(cf,'Sws',default=['Sws_01a'])
    if len(srclist) > 0:
        qcts.AverageSeriesByElements(ds3,'Sws',srclist)
        
    # correct the measured soil heat flux for storage in the soil layer above the sensor
    qcts.CorrectFg(cf,ds3)
    
    # average ground heat flux after correcting for storage above sensors
    if 'PostCorrectSoilAverage' in l3functions:
        srclist = qcutils.GetAverageList(cf,'Fg',default=['Fg_01a'])
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,'Fg',srclist)
    
    # calculate the available energy
    if 'CalculateAvailableEnergy' in l3functions:
        qcts.CalculateAvailableEnergy(cf,ds3)
    
    # calculate bulk stomatal resistance from Penman-Monteith inversion using bulk transfer coefficient (Stull 1988)
    if 'rstFromPenmanMonteith' in l3functions:
        qcts.get_stomatalresistance(cf,ds3)
    
    # re-apply the quality control checks (range, diurnal and rules)
    qcck.do_qcchecks(cf,ds3)
    
    # coordinate gaps in the three main fluxes
    if 'gaps' in l3functions:
        qcck.gaps(cf,ds3)
    
    # coordinate gaps in Ah_7500_Av with Fc_wpl
    qcck.do_Ah7500check(cf,ds3)
    
    qcutils.GetSeriesStats(cf,ds3)
    return ds3

def l4qc(cf,ds3):
    """
        Fill gaps in met data from other sources
        Integrate SOLO-ANN gap filled fluxes performed externally
        Generates L4 from L3 data
        Generates daily sums excel workbook
        
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
            qcts.AddMetVars
            qcts.ComputeDailySums
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
    ds4 = qcts.do_l4qc(cf,ds3,level)
    ds4.globalattributes['Level'] = level
    ds4.globalattributes['EPDversion'] = sys.version
    ds4.globalattributes['QCVersion'] = __doc__
        
    qcutils.GetSeriesStats(cf,ds4)
    return ds4
