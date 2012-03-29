"""
    OzFlux QC v1.8.2 19 Mar 2012;

    Version History:
    <<v1.0: 21 July 2011, code diversion reconciliation, PIsaac & JCleverly>>
    <<v1.0b 25 July 2011, with log capability, JCleverly>>
    <<v1.1b 26 July 2011, FhvtoFh output generalised and added to all sites qcl3, qcts functions modified to accept met constants or variables, JCleverly>>
    <<v1.2 23 Aug 2011, daily_sums functions moved to qcts module, JCleverly>>
    <<v1.3 26 Sep 2011, intermediate editing at OzFlux Black Mountain data workshop, PIsaac & JCleverly>>
    <<v1.4 30 Sep 2011, final version arrising from OzFlux Black Mountain data workshop, PIsaac & JCleverly>>
    <<v1.5 30 Nov 2011, revised l4qc calls in qc.py & Wombat modifications integrated, JCleverly>>
    <<v1.5.1 21 Feb 2012, code rationalisation and generalisation in progress, PIsaac & JCleverly>>
    <<v1.5.2 24 Feb 2012, de-bugging completion for ASM, PIsaac & JCleverly>>
    <<v1.6 24 Feb 2012, generalised qcls.l3qc, ASM tested ok L1-L4>>
    <<v1.7 27 Feb 2012, generalised qcls.l4qc, ASM tested ok L1-L4>>
    <<v1.8 16 Mar 2012, ASM and Standard (Gingin) tested ok L1-L3>>
    <<v1.8.1 19 Mar 2012, rst from Penman-Monteith inversion added to L3 & L4>>
    <<v1.8.2 19 Mar 2012, rst from Penman-Monteith inversion tested ok ASM L3 & L4>>
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
    # apply linear corrections to the data
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
        l3functions = ['do_linear', 'MergeSeriesAh', 'TaFromTv', 'MergeSeriesTa', 'CoordRotation2D', 'CalculateFluxes', 'FhvtoFh', 'Fe_WPL', 'Fc_WPL', 'MergeSeriesFsd', 'CalculateNetRadiation', 'MergeSeriesFn', 'AverageSeriesByElements', 'CorrectFgForStorage', 'CalculateAvailableEnergy', 'do_qcchecks']
    ds3.globalattributes['Functions'] = l3functions
    # add relevant meteorological values to L3 data
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'AddMetVars' in cf['General']['FunctionList']:
        log.info(' Adding standard met variables to database')
        qcts.AddMetVars(ds3)
    
    # correct measured soil water content using empirical relationship to collected samples
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'CorrectSWC' in cf['General']['FunctionList']:
        log.info(' Correcting soil moisture data ...')
        qcts.CorrectSWC(cf,ds3)
    
    # apply linear corrections to the data
    log.info(' Applying linear corrections ...')
    qcck.do_linear(cf,ds3)
    
    # merge the HMP and corrected 7500 data
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'MergeSeriesAh' in cf['General']['FunctionList']:
        if qcutils.cfkeycheck(cf,ThisOne='Ah_EC',key='MergeSeries') and 'Source' in cf['Variables']['Ah_EC']['MergeSeries'].keys():
            arg = ast.literal_eval(cf['Variables']['Ah_EC']['MergeSeries']['Source'])
            if len(arg) == 0:
                srclist = qcutils.GetMergeList(cf,'Ah_EC')
            else:
                srclist = qcutils.GetMergeList(cf,'Ah_EC',default=arg)
        else:
            srclist = qcutils.GetMergeList(cf,'Ah_EC',default="['Ah_HMP_01']")
        
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Ah_EC',srclist,[0,10])
    
    # get the air temperature from the CSAT virtual temperature
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'TaFromTv' in cf['General']['FunctionList']:
        if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='Ta2Tv'):
            args = ast.literal_eval(cf['FunctionArgs']['Ta2Tv'])
        else:
            args = ['Ta_CSAT','Tv_CSAT','Ah_EC','ps']
        
        qcts.TaFromTv(ds3,args[0],args[1],args[2],args[3])
    
    # merge the HMP and corrected CSAT data
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'MergeSeriesTa' in cf['General']['FunctionList']:
        if qcutils.cfkeycheck(cf,ThisOne='Ta_EC',key='MergeSeries') and 'Source' in cf['Variables']['Ta_EC']['MergeSeries'].keys():
            arg = ast.literal_eval(cf['Variables']['Ta_EC']['MergeSeries']['Source'])
            if len(arg) == 0:
                srclist = qcutils.GetMergeList(cf,'Ta_EC')
            else:
                srclist = qcutils.GetMergeList(cf,'Ta_EC',default=arg)
        else:
            srclist = qcutils.GetMergeList(cf,'Ta_EC',default="['Ta_HMP_01']")
        
        if len(srclist) > 0:
            qcts.MergeSeries(ds3,'Ta_EC',srclist,[0,10])
    
    # do the 2D coordinate rotation
    qcts.CoordRotation2D(cf,ds3)
    
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'Massman' in cf['General']['FunctionList']:
        # do the Massman frequency attenuation correction to approximate L
        qcts.MassmanApprox(cf,ds3)
        
        # do the Massman frequency attenuation correction
        qcts.Massman(cf,ds3)
    
    # calculate the fluxes
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'Massman' not in cf['General']['FunctionList']:
        if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='CF'):
            args = ast.literal_eval(cf['FunctionArgs']['CF'])
        else:
            args = ['Ta_EC','Ah_EC','ps']
        
        qcts.CalculateFluxes(ds3,args[0],args[1],args[2])
    
    # calculate the fluxes from covariances
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'Massman' in cf['General']['FunctionList']:
        qcts.CalculateFluxesRM(ds3)
    
    # approximate wT from virtual wT using wA (ref: Campbell OPECSystem manual)
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='FhvtoFhArgs'):
        attr = ast.literal_eval(cf['FunctionArgs']['FhvtoFhattr'])
        args = ast.literal_eval(cf['FunctionArgs']['FhvtoFhArgs'])
        qcts.FhvtoFh(ds3,args[0],args[1],args[2],args[3],args[4],args[5],args[6],attr)
    else:
        attr = 'Fh rotated and converted from virtual heat flux'
        args = ['Ta_EC','Fh','Tv_CSAT','Fe_raw','ps','Ah_EC','Fh_rv']
        qcts.FhvtoFh(ds3,args[0],args[1],args[2],args[3],args[4],args[5],args[6],attr)
    
    # correct the H2O & CO2 flux due to effects of flux on density measurements
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'Massman' not in cf['General']['FunctionList']:
        qcts.Fe_WPL(ds3,'Fe_wpl','Fe_raw','Fh_rv','Ta_EC','Ah_EC','ps')
        qcts.Fc_WPL(ds3,'Fc_wpl','Fc_raw','Fh_rv','Fe_wpl','Ta_EC','Ah_EC','Cc_7500_Av','ps')
    else:
        qcts.Fe_WPLcov(ds3,'Fe_wpl','wAM','Fh_rmv','Ta_HMP','Ah_HMP','ps')
        qcts.Fc_WPLcov(ds3,'Fc_wpl','wCM','Fh_rmv','wAwpl','Ta_HMP','Ah_HMP','Cc_7500_Av','ps')
    
    # calculate the net radiation from the Kipp and Zonen CNR1
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'CalculateNetRadiation' in cf['General']['FunctionList']:
        srclist = qcutils.GetMergeList(cf,'Fsd',default=['Fsd'])
        qcts.MergeSeries(ds3,'Fsd',srclist,[0,10])
        qcts.CalculateNetRadiation(ds3,'Fn_KZ','Fsd','Fsu','Fld','Flu')
    
    # combine the net radiation from the Kipp and Zonen CNR1 and the NRlite
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'MergeSeriesFn' in cf['General']['FunctionList']:
        srclist = qcutils.GetMergeList(cf,'Fn',default=['Fn_KZ'])
        qcts.MergeSeries(ds3,'Fn',srclist,[0,10])
    
    # interpolate over missing soil moisture values
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'InterpolateOverMissing' in cf['General']['FunctionList']:
        if qcutils.cfkeycheck(cf,Base='FunctionArgs', ThisOne='IOM'):
            qcts.InterpolateOverMissing(ds3,series=ast.literal_eval(cf,['FunctionArgs']['IOM']))
        else:
            qcts.InterpolateOverMissing(ds3,series=ast.literal_eval(cf,['FunctionArgs']['IOM']))
    
    # average soil measurements before correcting for storage above sensors
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'PreCorrectSoilAverage' in cf['General']['FunctionList']:
    # average the soil heat flux data
        if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='ASBEFg'):
            outvar = ast.literal_eval(cf['FunctionArgs']['ASBEFg'])
        else:
            outvar = 'Fg_Av'
        
        if qcutils.cfkeycheck(cf,ThisOne=outvar,key='AverageSeries') and 'Source' in cf['Variables'][outvar]['AverageSeries'].keys():
            arg = ast.literal_eval(cf['Variables'][outvar]['AverageSeries']['Source'])
            if len(arg) == 0:
                srclist = qcutils.GetAverageList(cf,outvar)
            else:
                srclist = qcutils.GetAverageList(cf,outvar,default=arg)
        else:
            srclist = qcutils.GetAverageList(cf,outvar,default=['Fg_01'])
        
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,outvar,srclist)
        
    # average the soil temperature data
        if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='ASBETs'):
            outvar = ast.literal_eval(cf['FunctionArgs']['ASBETs'])
        else:
            outvar = 'Ts_Av'
        
        if qcutils.cfkeycheck(cf,ThisOne=outvar,key='AverageSeries') and 'Source' in cf['Variables'][outvar]['AverageSeries'].keys():
            arg = ast.literal_eval(cf['Variables'][outvar]['AverageSeries']['Source'])
            if len(arg) == 0:
                srclist = qcutils.GetAverageList(cf,outvar)
            else:
                srclist = qcutils.GetAverageList(cf,outvar,default=arg)
        else:
            srclist = qcutils.GetAverageList(cf,outvar,default=['Ts_01'])
        
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,outvar,srclist)
        
    # average soil moisture
        if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='ASBEswc'):
            outvar = cf['FunctionArgs']['ASBEswc']
        else:
            outvar = 'Sws_Av'
        
        if qcutils.cfkeycheck(cf,ThisOne=outvar,key='AverageSeries') and 'Source' in cf['Variables'][outvar]['AverageSeries'].keys():
            arg = ast.literal_eval(cf['Variables'][outvar]['AverageSeries']['Source'])
            if len(arg) == 0:
                srclist = qcutils.GetAverageList(cf,outvar)
            else:
                srclist = qcutils.GetAverageList(cf,outvar,default=arg)
        else:
            srclist = qcutils.GetAverageList(cf,outvar,default=['Sws_01'])
        
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,outvar,srclist)
        
    # correct the measured soil heat flux for storage in the soil layer above the sensor
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='CFg1Args'):
        args = ast.literal_eval(cf['FunctionArgs']['CFg1Args'])
    else:
        args = ['Fg','Fg_Av','Ts_Av']
    if len(args) == 4:
        qcts.CorrectFgForStorage(cf,ds3,args[0],args[1],args[2],args[3])
    else:
        qcts.CorrectFgForStorage(cf,ds3,args[0],args[1],args[2])
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='CFg2Args'):
        args = ast.literal_eval(cf['FunctionArgs']['CFg2Args'])
        if len(args) == 4:
            qcts.CorrectFgForStorage(cf,ds3,args[0],args[1],args[2],args[3])
        else:
            qcts.CorrectFgForStorage(cf,ds3,args[0],args[1],args[2])
    if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='CFg3Args'):
        args = ast.literal_eval(cf['FunctionArgs']['CFg3Args'])
        if len(args) == 4:
            qcts.CorrectFgForStorage(cf,ds3,args[0],args[1],args[2],args[3])
        else:
            qcts.CorrectFgForStorage(cf,ds3,args[0],args[1],args[2])
    
    # average soil measurements after correcting for storage above sensors
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'PostCorrectSoilAverage' in cf['General']['FunctionList']:
        if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='ASBEFg'):
            outvar = ast.literal_eval(cf['FunctionArgs']['ASBEFg'])
        else:
            outvar = 'Fg_Av'
        
        if qcutils.cfkeycheck(cf,ThisOne=outvar,key='AverageSeries') and 'Source' in cf['Variables'][outvar]['AverageSeries'].keys():
            arg = ast.literal_eval(cf['Variables'][outvar]['AverageSeries']['Source'])
            if len(arg) == 0:
                srclist = qcutils.GetAverageList(cf,outvar)
            else:
                srclist = qcutils.GetAverageList(cf,outvar,default=arg)
        else:
            srclist = qcutils.GetAverageList(cf,outvar,default=['Fg_01'])
        
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,outvar,srclist)
        
    # average the soil temperature data
        if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='ASBETs'):
            outvar = ast.literal_eval(cf['FunctionArgs']['ASBETs'])
        else:
            outvar = 'Ts_Av'
        
        if qcutils.cfkeycheck(cf,ThisOne=outvar,key='AverageSeries') and 'Source' in cf['Variables'][outvar]['AverageSeries'].keys():
            arg = ast.literal_eval(cf['Variables'][outvar]['AverageSeries']['Source'])
            if len(arg) == 0:
                srclist = qcutils.GetAverageList(cf,outvar)
            else:
                srclist = qcutils.GetAverageList(cf,outvar,default=arg)
        else:
            srclist = qcutils.GetAverageList(cf,outvar,default=['Ts_01'])
        
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,outvar,srclist)
        
    # average soil moisture
        if qcutils.cfkeycheck(cf,Base='FunctionArgs',ThisOne='ASBEswc'):
            outvar = cf['FunctionArgs']['ASBEswc']
        else:
            outvar = 'Sws_Av'
        
        if qcutils.cfkeycheck(cf,ThisOne=outvar,key='AverageSeries') and 'Source' in cf['Variables'][outvar]['AverageSeries'].keys():
            arg = ast.literal_eval(cf['Variables'][outvar]['AverageSeries']['Source'])
            if len(arg) == 0:
                srclist = qcutils.GetAverageList(cf,outvar)
            else:
                srclist = qcutils.GetAverageList(cf,outvar,default=arg)
        else:
            srclist = qcutils.GetAverageList(cf,outvar,default=['Sws_01'])
        
        if len(srclist) > 0:
            qcts.AverageSeriesByElements(ds3,outvar,srclist)
        
    
    # calculate the available energy
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'CalculateAvailableEnergy' in cf['General']['FunctionList']:
        qcts.CalculateAvailableEnergy(ds3,'Fa','Fn','Fg')
    
    # calculate bulk stomatal resistance from Penman-Monteith inversion using bulk transfer coefficient (Stull 1988)
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'rstFromPenmanMonteith' in cf['General']['FunctionList']:
        Level = ds3.globalattributes['Level']
        qcts.get_stomatalresistance(cf,ds3,Level)
    
    # re-apply the quality control checks (range, diurnal and rules)
    qcck.do_qcchecks(cf,ds3)
    
    # coordinate gaps in the three main fluxes
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='FunctionList') and 'gaps' in cf['General']['FunctionList']:
        qcck.gaps(cf,ds3)
    
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
    # get z-d (measurement height minus displacement height) and z0 from the control file
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='Met'):
        if str(ast.literal_eval(cf['General']['Met'])) == 'True':
            if qcutils.cfkeycheck(cf,Base='Params',ThisOne='zmd') and qcutils.cfkeycheck(cf,Base='Params',ThisOne='z0'):
                zmd = float(cf['Params']['zmd'])   # z-d for site
                z0 = float(cf['Params']['z0'])     # z0 for site
            ds4 = copy.deepcopy(ds3)
            ds4.globalattributes['Level'] = level
            ds4.globalattributes['EPDversion'] = sys.version
            ds4.globalattributes['QCVersion'] = __doc__
            ds4.globalattributes['Functions'] = 'InterpolateOverMissing, GapFillFromAlternate, GapFillFromClimatology, GapFillFromRatios, ReplaceOnDiff, UstarFromFh, ReplaceWhereMissing, do_qcchecks'
            # make a copy of the L4 data, data from the alternate sites will be merged with this copy
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
    elif qcutils.cfkeycheck(cf,Base='General',ThisOne='SOLO'):
        if str(ast.literal_eval(cf['General']['SOLO'])) == 'True':
            ds4 = qcio.nc_read_series(cf,level)
            ds4.globalattributes['Level'] = 'L4'
            ds4.globalattributes['EPDversion'] = sys.version
            ds4.globalattributes['QCVersion'] = __doc__
            ds4.globalattributes['Functions'] = 'SOLO ANN GapFilling 10-day window, AddMetVars, ComputeDailySums (not included)'
            # duplicate gapfilled fluxes for graphing comparison
            if 'Fe_gapfilled' in ds4.series.keys():
                Fe,flag = qcutils.GetSeriesasMA(ds4,'Fe_gapfilled')
                qcutils.CreateSeries(ds4,'Fe_wpl',Fe,Flag=flag,Descr='ANN gapfilled Fe',Units='W/m2')
            if 'Fc_gapfilled' in ds4.series.keys():
                Fc,flag = qcutils.GetSeriesasMA(ds4,'Fc_gapfilled')
                qcutils.CreateSeries(ds4,'Fc_wpl',Fc,Flag=flag,Descr='ANN gapfilled Fc',Units='mg/m2/s')
            if 'Fh_gapfilled' in ds4.series.keys():
                Fh,flag = qcutils.GetSeriesasMA(ds4,'Fh_gapfilled')
                qcutils.CreateSeries(ds4,'Fh_rmv',Fh,Flag=flag,Descr='ANN gapfilled Fh',Units='W/m2')
            # add relevant meteorological values to L3 data
            if qcutils.cfkeycheck(cf, Base='General', ThisOne='NoMet'):
                if cf['General']['NoMet'] != 'True':
                    qcts.AddMetVars(ds4)
    else:
            ds4 = copy.deepcopy(ds3)
            ds4.globalattributes['Level'] = level
            ds4.globalattributes['EPDversion'] = sys.version
            ds4.globalattributes['QCVersion'] = __doc__
            ds4.globalattributes['Functions'] = 'Sums'
    
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
        qcts.ComputeDailySums(cf,ds4,SumList,SubSumList,MinMaxList,MeanList,SoilList)
    
    qcutils.GetSeriesStats(cf,ds4)
    return ds4
