"""
    OzFlux QC Plus v2.5 18 May 2013;

    Version History:
    <<v1.0: 21 Jul 2011, code diversion reconciliation>>
    <<v1.4 30 Sep 2011, final version arrising from OzFlux Black Mountain data workshop>>
    <<v2.0 8 Jun 2012, version arrising from conclusion of OzFlux UTS data workshop>>
    <<v2.1 3 Feb 2013, MOST footprint model (Kljun et al. 2004) implemented at L3>>
    <<v2.2 11 Feb 2013, Climatology implemented at L3 and L4; Penman-Monteith updated to output Gc, correct Gst computation to use air-to-soil q gradient rather than saturation deficit>>
    <<v2.3 24 Feb 2013, ET calculation added, controlfiles revised to simplify function calls>>
    <<v2.4 19 Apr 2013, WPL error introduced in v2.3 corrected, weighted footprint output added>>
    <<v2.5 18 May 2013, Footprint model complete (rotated to geographic coordinates (m)-1d & 2d footprints, weighted footrpints, footprint climatology)>>
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
    ds2.globalattributes['L2Functions'] = 'RangeCheck, CSATcheck, 7500check, diurnalcheck, excludedates, excludehours, albedo'
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
    ds3.globalattributes['L3Functions'] = ''
    
    # bypass soil temperature correction for Sws (when Ts bad)
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassSwsTcorr') and cf['Functions']['BypassSwsTcorr'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', BypassSwsTcorr'
        log.info(' Re-computing Sws without temperature correction ...')
        qcts.BypassTcorr(cf,ds3)
    
    # correct measured soil water content using empirical relationship to collected samples
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='CorrectSWC') and cf['Functions']['CorrectSWC'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CorrectSWC'
        log.info(' Correcting soil moisture data ...')
        qcts.CorrectSWC(cf,ds3)
    
    # apply linear corrections to the data
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', do_linear'
        log.info(' Applying linear corrections ...')
        qcck.do_linear(cf,ds3)
    
    # determine HMP Ah if not output by datalogger
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='CalculateAh') and cf['Functions']['CalculateAh'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CalculateAh'
        log.info(' Adding HMP Ah to database')
        qcts.CalculateAhHMP(cf,ds3)
    
    # merge the HMP and corrected 7500 data
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='MergeSeriesAhTa') and cf['Functions']['MergeSeriesAhTa'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', MergeSeriesAhTa'
        srclist = qcutils.GetMergeList(cf,'Ah',default=['Ah_HMP_01'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Ah',srclist,[0,10])
    
    # get the air temperature from the CSAT virtual temperature
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', TaFromTv'
        qcts.TaFromTv(cf,ds3)
    
    # merge the HMP and corrected CSAT data
        srclist = qcutils.GetMergeList(cf,'Ta',default=['Ta_HMP_01'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Ta',srclist,[0,10])
    
    # add relevant meteorological values to L3 data
    ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CalculateMetVars'
    log.info(' Adding standard met variables to database')
    qcts.CalculateMeteorologicalVariables(cf,ds3)
    
    # do the 2D coordinate rotation
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CoordRotation2D'
        qcts.CoordRotation2D(cf,ds3)
    
    # do the Massman frequency attenuation correction
    if (((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')) and (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='Massman') and cf['Functions']['Massman'] == 'True')):
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', Massman'
        qcts.MassmanStandard(cf,ds3)
    
    # calculate the fluxes
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CalculateFluxes'
        if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='Massman') and cf['Functions']['Massman'] == 'True':
            qcts.CalculateFluxes(cf,ds3,massman='True')
        else:
            qcts.CalculateFluxes(cf,ds3)
    
    # approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', FhvtoFh'
        qcts.FhvtoFh(cf,ds3)
    
    # correct the H2O & CO2 flux due to effects of flux on density measurements
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='WPLcov') and cf['Functions']['WPLcov'] == 'True':
            ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', WPLcov'
            qcts.do_WPL(cf,ds3,cov='True')
        else:
            ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', WPL'
            qcts.do_WPL(cf,ds3)
    
    # calculate the net radiation from the Kipp and Zonen CNR1
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='CalculateNetRadiation') and cf['Functions']['CalculateNetRadiation'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CalculateNetRadiation'
        srclist = qcutils.GetMergeList(cf,'Fsd',default=['Fsd'])
        qcts.MergeSeries(ds3,'Fsd',srclist,[0,10])
        qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
        srclist = qcutils.GetMergeList(cf,'Fn',default=['Fn_KZ'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Fn',srclist,[0,10])
    
    # combine wind speed from the CSAT and the Wind Sentry
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='MergeSeriesWS') and cf['Functions']['MergeSeriesWS'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', MergeSeriesWS'
        srclist = qcutils.GetMergeList(cf,'Ws',default=['Ws_WS_01','Ws_CSAT'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Ws',srclist,[0,10])
    
    # average ground heat flux before correcting for storage above sensors
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='PostCorrectSoilAverage') and cf['Functions']['PostCorrectSoilAverage'] == 'True':
            ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', SoilAverage'
    
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        if 'SoilAverage' not in ds3.globalattributes['L3Functions']:
            ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', SoilAverage'
            srclist = qcutils.GetAverageList(cf,'Fg',default=['Fg_01a'])
            if len(srclist) > 0:
                qcts.AverageSeriesByElements(ds3,'Fg',srclist)
    
    # average the soil temperature data
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        srclist = qcutils.GetAverageList(cf,'Ts',default=['Ts_01a'])
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,'Ts',srclist)
    
    # average soil moisture
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        srclist = qcutils.GetAverageList(cf,'Sws',default=['Sws_01a'])
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,'Sws',srclist)
        
    # correct the measured soil heat flux for storage in the soil layer above the sensor
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CorrectFgForStorage'
        qcts.CorrectFg(cf,ds3)
    
    # average ground heat flux after correcting for storage above sensors
    if ((not qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections')) or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='BypassCorrections') and cf['Functions']['BypassCorrections'] != 'True')):
        if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='PostCorrectSoilAverage') and cf['Functions']['PostCorrectSoilAverage'] == 'True':
            srclist = qcutils.GetAverageList(cf,'Fg',default=['Fg_01a'])
            if len(srclist) > 0:
                qcts.AverageSeriesByElements(ds3,'Fg',srclist)
    
    # calculate the available energy
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='CalculateAvailableEnergy') and cf['Functions']['CalculateAvailableEnergy'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CalculateAvailableEnergy'
        qcts.CalculateAvailableEnergy(cf,ds3)
    
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='DiagnosticMode'):
        if cf['Functions']['DiagnosticMode'] == 'False':
            qcutils.prepOzFluxVars(cf,ds3)
    else:
        qcutils.prepOzFluxVars(cf,ds3)
    
    # calculate specific humidity and saturated specific humidity profile
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='qTprofile') and cf['Functions']['qTprofile'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', qTprofile'
        qcts.CalculateSpecificHumidityProfile(cf,ds3)
    
    # calculate Penman-Monteith inversion
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='PenmanMonteith') and cf['Functions']['PenmanMonteith'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', PenmanMonteith'
        qcts.do_PenmanMonteith(cf,ds3)
    
    # calculate bulk Richardson numbers
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='bulkRichardson') and cf['Functions']['bulkRichardson'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', bulkRichardson'
        qcts.do_bulkRichardson(cf,ds3)
    
    # re-apply the quality control checks (range, diurnal and rules)
    ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', do_qcchecks'
    qcck.do_qcchecks(cf,ds3)
    
    # apply the ustar filter
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='ustarFilter') and cf['Functions']['ustarFilter'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', ustarFilter'
        qcts.FilterFcByUstar(cf,ds3)
    
    # coordinate gaps in the three main fluxes
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='gaps') and cf['Functions']['gaps'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', gaps'
        qcck.gaps(cf,ds3)
    
    # convert Fc [mg m-2 s-1] to NEE [umol m-2 s-1] and NEP = - NEE
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='convertFc') and cf['Functions']['convertFc'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', convertFc'
        qcts.ConvertFc(cf,ds3)
    
    # coordinate gaps in Ah_7500_Av with Fc_wpl
    ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', do_Ah7500check'
    qcck.do_Ah7500check(cf,ds3)
    
    # calcluate ET at observation interval
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='CalculateET') and cf['Functions']['CalculateET'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', CalculateET'
        log.info(' Calculating ET')
        qcts.CalculateET(cf,ds3,'L3')
    
    qcutils.GetSeriesStats(cf,ds3)
    
    # run MOST (Buckingham Pi) 2d footprint model (Kljun et al. 2004)
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='footprint') and cf['Functions']['footprint'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', footprint'
        qcts.do_footprint_2d(cf,ds3)
    
    # compute climatology for L3 data
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='climatology') and cf['Functions']['climatology'] == 'True':
        ds3.globalattributes['L3Functions'] = ds3.globalattributes['L3Functions']+', climatology'
        qcts.do_climatology(cf,ds3)
    
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
    # check to ensure L4 functions are defined in controlfile
    if qcutils.cfkeycheck(cf,Base='Functions'):
        x=0
    else:
        log.error('FunctionList not found in control file')
        ds4 = copy.deepcopy(ds3)
        ds4.globalattributes['Level'] = 'L3'
        ds4.globalattributes['L4Functions'] = 'No L4 functions applied'
        return ds4
    
    # import SOFM/SOLO ANN gap-filled fluxes from external process
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='SOLO') and cf['Functions']['SOLO'] == 'True':
        ds4 = qcio.nc_read_series(cf,'L4')
        ds4.globalattributes['L4Functions'] = 'SOLO ANN GapFilling 10-day window'
        qcts.do_solo(cf,ds4)
        x=x+1
    # copy L3 database if not generated from external file
    else:
        ds4 = copy.deepcopy(ds3)
        ds4.globalattributes['L4Functions'] = ''
    
    ds4.globalattributes['Level'] = 'L4'
    ds4.globalattributes['EPDversion'] = sys.version
    ds4.globalattributes['QCVersion'] = __doc__
    qcutils.prepOzFluxVars(cf,ds4)
    
    # linear interpolation to fill missing values over gaps of 1 hour
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='InterpolateOverMissing') and cf['Functions']['InterpolateOverMissing'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', InterpolateOverMissing'
        log.info(' Gap filling by linear interpolation to fill missing values over gaps of 1 hour')
        qcts.InterpolateOverMissing(cf,ds4,maxlen=2)
        x=x+1
    
    # gap fill meteorological and radiation data from the alternate site(s)
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='GapFillFromAlternate') and cf['Functions']['GapFillFromAlternate'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', GapFillFromAlternate'
        log.info(' Gap filling using data from alternate sites')
        qcts.GapFillFromAlternate(cf,ds4)
        x=x+1
    
    # gap fill meteorological, radiation and soil data using climatology
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='GapFillFromClimatology') and cf['Functions']['GapFillFromClimatology'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', GapFillFromClimatology'
        log.info(' Gap filling using site climatology')
        qcts.GapFillFromClimatology(cf,ds4)
        x=x+1
    
    # gap fill using evaporative fraction (Fe/Fa), Bowen ratio (Fh/Fe) and ecosystem water use efficiency (Fc/Fe)
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='GapFillFromRatios') and cf['Functions']['GapFillFromRatios'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', GapFillFromRatios'
        log.info(' Gap filling Fe, Fh and Fc using ratios')
        qcts.GapFillFromRatios(cf,ds4)
        x=x+1
    
    # calculate u* from Fh and corrected wind speed
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='UstarFromFh') and cf['Functions']['UstarFromFh'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', UstarFromFh'
        us_in,us_out = qcts.UstarFromFh(cf,ds4)
    
    # add relevant meteorological values to L4 data
    if 'SOLO' not in ds4.globalattributes['L4Functions'] and ((qcutils.cfkeycheck(cf,Base='Functions',ThisOne='CalculateMetVars') and cf['Functions']['CalculateMetVars'] == 'True') or (qcutils.cfkeycheck(cf,Base='Functions',ThisOne='PenmanMonteith') and cf['Functions']['PenmanMonteith'] == 'True')):
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', CalculateMetVars'
        log.info(' Adding standard met variables to database')
        qcts.CalculateMeteorologicalVariables(cf,ds4)
    
    # calcluate ET at observation interval
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='CalculateET') and cf['Functions']['CalculateET'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', CalculateET'
        log.info(' Calculating ET')
        qcts.CalculateET(cf,ds4,'L4')
    
    # merge CSAT and wind sentry wind speed
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='MergeSeriesWS') and cf['Functions']['MergeSeriesWS'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', MergeSeriesWS'
        srclist = qcutils.GetMergeList(cf,'Ws',default=['Ws_WS_01','Ws_CSAT'])
        if len(srclist) > 0:
            qcts.MergeSeries(ds4,'Ws',srclist,[0,10])
    
    # calculate rst, rc and Gst, Gc from Penman-Monteith inversion
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='PenmanMonteith') and cf['Functions']['PenmanMonteith'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', PenmanMonteith'
        qcts.do_PenmanMonteith(cf,ds4)
    
    # re-apply the quality control checks (range, diurnal and rules)
    log.info(' Doing QC checks on L4 data')
    qcck.do_qcchecks(cf,ds4)
    
    # interpolate over any ramaining gaps up to 3 hours in length
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='InterpolateOverMissing') and cf['Functions']['InterpolateOverMissing'] == 'True':
        qcts.InterpolateOverMissing(cf,ds4,maxlen=6)
    
    # fill any remaining gaps climatology
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='GapFillFromClimatology') and cf['Functions']['GapFillFromClimatology'] == 'True':
        qcts.GapFillFromClimatology(cf,ds4)
    
    # convert Fc [mg m-2 s-1] to NEE [umol m-2 s-1] and NEP = - NEE
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='convertFc') and cf['Functions']['convertFc'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', convertFc'
        qcts.ConvertFc(cf,ds4)
    
    if x == 0:
        log.warning('Neither Met nor SOLO located in FunctionList, no L4 functions applied')
        ds4.globalattributes['Level'] = 'L3'
        ds4.globalattributes['L3functions_add'] = ds4.globalattributes['L4Functions']+''
        ds4.globalattributes['L4Functions'] = 'No L4 functions applied'
    
    # calculate daily statistics
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='Sums') and cf['Functions']['Sums'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', Sums'
        qcts.do_sums(cf,ds4)
    
    qcutils.GetSeriesStats(cf,ds4)
    
    # run MOST (Buckingham Pi) 2d footprint model (Kljun et al. 2004)
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='footprint') and cf['Functions']['footprint'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', footprint'
        qcts.do_footprint_2d(cf,ds4,level='L4')
    
    # compute climatology for L3 data
    if qcutils.cfkeycheck(cf,Base='Functions',ThisOne='climatology') and cf['Functions']['climatology'] == 'True':
        ds4.globalattributes['L4Functions'] = ds4.globalattributes['L4Functions']+', climatology'
        qcts.do_climatology(cf,ds4)
    
    return ds4
