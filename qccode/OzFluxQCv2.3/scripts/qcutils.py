import ast
import constants as c
import datetime
import dateutil
import math
import meteorologicalfunctions as mf
import numpy
import logging
import xlwt

log = logging.getLogger('qc.utils')

def bp(fx,tao):
    """
    Function to calculate the b and p coeficients of the Massman frequency correction.
    """
    bp = 2 * c.Pi * fx * tao
    return bp

def cfkeycheck(cf,Base='Variables',ThisOne=[],key=[]):
    if len(ThisOne) == 0:
        return
    if len(key) == 0:
        if Base in cf.keys() and ThisOne in cf[Base].keys():
            return ThisOne in cf[Base].keys()
        else:
            return
    else:
        if Base in cf.keys() and ThisOne in cf[Base].keys():
            return key in cf[Base][ThisOne].keys()
        else:
            return

def CreateSeries(ds,Label,Data,FList=[''],Flag=None,Descr='',Units='',Standard='not defined'):
    """
    Create a series (1d array) of data in the data structure.
    
    If the series already exists in the data structure, data values and QC flags will be
    overwritten but attributes will be preserved.  However, the long_name and Units attributes
    are treated differently.  The existing long_name will have Descr appended to it.  The
    existing units will be overwritten with units.
    
    This utility is the prefered method for creating or updating a data series because
    it imnplements a consistent method for creating series in the data structure.  Direct
    writes to the contents of the data structure are discouraged (unless PRI wrote the code!).
    """
    ds.series['_tmp_'] = {}                       # create a temporary series to avoid premature overwrites
    # put the data into the temporary series
    ds.series['_tmp_']['Data'] = numpy.ma.filled(Data,float(-9999))
    # copy or make the QC flag
    if Flag == None:
        ds.series['_tmp_']['Flag'] = MakeQCFlag(ds,FList)
    else:
        ds.series['_tmp_']['Flag'] = Flag
    # do the attributes
    ds.series['_tmp_']['Attr'] = {}
    if Label in ds.series.keys():                 # check to see if the series already exists
        for attr in ds.series[Label]['Attr']:     # if it does, copy the existing attributes
            if attr=='long_name':               # append new description to existing one
                attr_value = ds.series[Label]['Attr']['long_name']
                attr_value = attr_value + ', ' + Descr
                ds.series['_tmp_']['Attr'][attr] = attr_value
            elif attr=='units':                   # overwrite existing units with new ones
                ds.series['_tmp_']['Attr']['units'] = Units
            else:                                 # otherwise do a straight copy
                ds.series['_tmp_']['Attr'][attr] = ds.series[Label]['Attr'][attr]
    else:
        ds.series['_tmp_']['Attr']['long_name'] = Descr
        ds.series['_tmp_']['Attr']['units'] = Units
        ds.series['_tmp_']['Attr']['standard_name'] = Standard
    ds.series[unicode(Label)] = ds.series['_tmp_']     # copt temporary series to new series
    del ds.series['_tmp_']                        # delete the temporary series

def Fm(z, z0, L):
    ''' Integral form of the adiabatic correction to the wind speed profile.'''
    Fm = math.log(z/z0)                 # Neutral case
    if L<0:                             # Unstable case
        R0 = (1-c.gamma*z0/L)**0.25
        R1 = (1-c.gamma*z/L)**0.25
        x = ((R0+1)/(R1+1))**2
        Y = (R0*R0+1)/(R1*R1+1)
        w = z/z0
        V = 2 * numpy.arctan((R1-R0)/(1+R0*R1))
        Fm = math.log(w*Y*x)+V
    elif ((L>-200)|(L>200)):            # Neutral case
        Fm = math.log(z/z0)
    elif (z/L<=1):                      # Stable case, z < L
        x = math.log(z/z0)
        Y = c.beta*z/L
        Fm = x+Y
    elif ((z/L>1)&(z0/L<1)):            # Stable case, z > L > z0
        x = math.log(L/z0)
        Y = (1+c.beta)*math.log(z/L)
        Fm = x+c.beta+Y
    elif (z0/L>1):                      # Stable, L < z0
        Fm = (1+c.beta)*math.log(z/z0)
    else:
        print 'Error in function Fm'
    return Fm

def Fustar(T, Ah, p, Fh, u, z, z0, ustar):
#' Function used in iteration method to solve for ustar.
#' The function used is:
#'  ustar = u*k/Fm(z/L,z0/L)
#' where Fm is the integral form of the PHIm, the adiabatic
#' correction to the logarithmic wind speed profile.
#' Evaluate the function for ustar with this value for L.
    MO = mf.molen(T, Ah, p, ustar, Fh)
    Fustar = u*c.k/(Fm(z, z0, MO))
    return Fustar
    
def get_diurnalstats(DecHour,Data,dt):
    nInts = 24*int((60/dt)+0.5)
    Hr = numpy.zeros(nInts) + float(-9999)
    Av = numpy.zeros(nInts) + float(-9999)
    Sd = numpy.zeros(nInts) + float(-9999)
    Mx = numpy.zeros(nInts) + float(-9999)
    Mn = numpy.zeros(nInts) + float(-9999)
    Num = numpy.zeros(nInts) + float(-9999)
    for i in range(nInts):
        Hr[i] = float(i)*dt/60.
        li = numpy.where((abs(DecHour-Hr[i])<c.eps)&(abs(Data-float(-9999))>c.eps))
        Num[i] = numpy.size(li)
        if numpy.size(li)!=0:
            Av[i] = numpy.mean(Data[li])
            Sd[i] = numpy.std(Data[li])
            Mx[i] = numpy.max(Data[li])
            Mn[i] = numpy.min(Data[li])
    return Num, Hr, Av, Sd, Mx, Mn

def GetAverageList(cf,ThisOne,default=['']):
    if cfkeycheck(cf,ThisOne=ThisOne):
        if default == ['']:
            log.error(' GetAverageList: no "Source" series provided for '+ThisOne)
            return []
        if cfkeycheck(cf,ThisOne=ThisOne,key='AverageSeries'):
            alist = ast.literal_eval(cf['Variables'][ThisOne]['AverageSeries']['Source'])
            if alist == ['']:
                return []
        else:
            alist = default
    else:
        log.error('  GetAverageList: '+ThisOne+' not in control file')
        return []
    
    return alist

def GetAltName(cf,ds,ThisOne):
    '''
    Check to see if the specified variable name is in the data structure (ds).
    If it is, return the variable name unchanged.
    If it isn't, check the control file to see if an alternate name has been specified
     and return the alternate name if one exists.
    '''
    if ThisOne not in ds.series.keys():
        if ThisOne in cf['Variables'].keys():
            ThisOne = cf['Variables'][ThisOne]['AltVarName']
            if ThisOne not in ds.series.keys():
                print 'GetAltName: alternate variable name not in ds'
        else:
            print 'GetAltName: cant find ',ThisOne,' in ds or control file'
    return ThisOne

def GetAltNameFromCF(cf,ThisOne):
    '''
    Get an alternate variable name from the control file.
    '''
    if ThisOne in cf['Variables'].keys():
        if 'AltVarName' in cf['Variables'][ThisOne].keys():
            ThisOne = str(cf['Variables'][ThisOne]['AltVarName'])
        else:
            print 'GetAltNameFromCF: AltVarName key not in control file for '+str(ThisOne)
    else:
        print 'GetAltNameFromCF: '+str(ThisOne)+' not in control file'
    return ThisOne

def GetcbTicksFromCF(cf,ThisOne):
    '''
    Get colour bar tick labels from the control file.
    '''
    if ThisOne in cf['Variables'].keys():
        if 'Ticks' in cf['Variables'][ThisOne].keys():
            Ticks = eval(cf['Variables'][ThisOne]['Ticks'])
        else:
            print 'GetcbTicksFromCF: Ticks key not in control file for '+str(ThisOne)
    else:
        print 'GetcbTicksFromCF: '+str(ThisOne)+' not in control file'
    return Ticks

def GetRangesFromCF(cf,ThisOne):
    '''
    Get lower and upper range limits from the control file.
    '''
    if ThisOne in cf['Variables'].keys():
        if 'Lower' in cf['Variables'][ThisOne].keys():
            lower = float(cf['Variables'][ThisOne]['Lower'])
        else:
            print 'GetRangesFromCF: Lower key not in control file for '+str(ThisOne)
            lower = None
        if 'Upper' in cf['Variables'][ThisOne].keys():
            upper = float(cf['Variables'][ThisOne]['Upper'])
        else:
            print 'GetRangesFromCF: Upper key not in control file for '+str(ThisOne)
            upper = None
    else:
        print 'GetRangesFromCF: '+str(ThisOne)+' not in control file'
        lower, upper = None
    return lower, upper

def GetDateIndex(datetimeseries,date,ts=30,default=0,match='exact'):
    # return the index of a date/datetime string in an array of datetime objects
    #  datetimeseries - array of datetime objects
    #  date - a date or date/time string in a format dateutils can parse
    #  default - default value, integer
    try:
        i = datetimeseries.index(dateutil.parser.parse(date))
    except ValueError:
        i = default
    if match=='startnextday':
        while abs(datetimeseries[i].hour+float(datetimeseries[i].minute)/60-float(ts)/60)>c.eps:
            i = i + 1
    if match=='endpreviousday':
        while abs(datetimeseries[i].hour+float(datetimeseries[i].minute)/60)>c.eps:
            i = i - 1
    return i

def GetMergeList(cf,ThisOne,default=['']):
    if cfkeycheck(cf,ThisOne=ThisOne):
        if default == ['']:
            log.error(' GetMergeList: no "Source" series provided for '+ThisOne)
            return []
        if cfkeycheck(cf,ThisOne=ThisOne,key='MergeSeries'):
            mlist = ast.literal_eval(cf['Variables'][ThisOne]['MergeSeries']['Source'])
            if mlist == ['']:
                return []
        else:
            mlist = default
    else:
        log.error('  GetMergeList: '+ThisOne+' not in control file')
        return []
    
    return mlist

def GetPlotTitleFromCF(cf, nFig):
    if 'Plots' in cf:
        if str(nFig) in cf['Plots']:
            if 'Title' in cf['Plots'][str(nFig)]:
                Title = str(cf['Plots'][str(nFig)]['Title'])
            else:
                print 'GetPlotTitleFromCF: Variables key not in control file for plot '+str(nFig)
        else:
            print 'GetPlotTitleFromCF: '+str(nFig)+' key not in Plots section of control file'
    else:
        print 'GetPlotTitleFromCF: Plots key not in control file'
    return Title

def GetPlotVariableNamesFromCF(cf, n):
    if 'Plots' in cf:
        if str(n) in cf['Plots']:
            if 'Variables' in cf['Plots'][str(n)]:
                SeriesList = eval(cf['Plots'][str(n)]['Variables'])
            else:
                print 'GetPlotVariableNamesFromCF: Variables key not in control file for plot '+str(n)
        else:
            print 'GetPlotVariableNamesFromCF: '+str(n)+' key not in Plots section of control file'
    else:
        print 'GetPlotVariableNamesFromCF: Plots key not in control file'
    return SeriesList

def GetSeries(ds,ThisOne,si=0,ei=-1):
    Series = ds.series[ThisOne]['Data']
    if 'Flag' in ds.series[ThisOne].keys():
        Flag = ds.series[ThisOne]['Flag']
    else:
        nRecs = numpy.size(ds.series[ThisOne]['Data'])
        Flag = numpy.zeros(nRecs,dtype=int)
    if ei==-1:
        Series = Series[si:]
        Flag = Flag[si:]
    else:
        Series = Series[si:ei+1]
        Flag = Flag[si:ei+1]
    return Series,Flag

def GetSeriesasMA(ds,ThisOne,si=0,ei=-1):
    Series,Flag = GetSeries(ds,ThisOne,si,ei)
    Series,WasND = SeriestoMA(Series)
    return Series,Flag

def GetUnitsFromds(ds, ThisOne):
    units = ds.series[ThisOne]['Attr']['units']
    return units

def MAtoSeries(Series):
    """
    Convert a masked array to a numpy ndarray with masked elements set to -9999.
    Useage:
     Series, WasMA = MAtoSeries(Series)
     where:
      Series (input)    is the data series to be converted.
      WasMA  (returned) is a logical, True if the input series was a masked array.
      Series (output)   is the input series convered to an ndarray with -9999 values
                        for missing data.
    """
    WasMA = False
    if numpy.ma.isMA(Series):
        WasMA = True
        Series = numpy.ma.filled(Series,float(-9999))
    return Series, WasMA

def r(b, p, alpha):
    """
    Function to calculate the r coeficient of the Massman frequency correction.
    """
    r = ((b ** alpha) / (b ** alpha + 1)) * \
           ((b ** alpha) / (b ** alpha + p ** alpha)) * \
           (1 / (p ** alpha + 1))
    return r

def SeriestoMA(Series):
    """
    Convert a numpy ndarray to a masked array.
    Useage:
     Series, WasND = SeriestoMA(Series)
     where:
      Series (input)    is the data series to be converted.
      WasND  (returned) is a logical, True if the input series was an ndarray
      Series (output)   is the input series convered to a masked array.
    """
    WasND = False
    if not numpy.ma.isMA(Series):
        WasND = True
        Series = numpy.ma.masked_where(abs(Series-numpy.float64(-9999))<c.eps,Series)
    return Series, WasND

def SetUnitsInds(ds, ThisOne, units):
    ds.series[ThisOne]['Attr']['units'] = units

def GetSeriesStats(cf,ds):
    # open an Excel file for the flag statistics
    level = ds.globalattributes['Level']
    xlFileName = cf['Files'][level]['xlFlagFilePath']+cf['Files'][level]['xlFlagFileName']
    log.info(' Writing flag stats to Excel file '+xlFileName)
    xlFile = xlwt.Workbook()
    if cf['General']['Platform'] == 'Mac':
        xlFile.dates_1904 = True
    xlFlagSheet = xlFile.add_sheet('Flag')
    # get the flag statistics
    xlRow = 0
    xlCol = 0
    xlFlagSheet.write(xlRow,xlCol,'0:')
    xlFlagSheet.write(xlRow,xlCol+1,ds.globalattributes['Flag0'])
    xlFlagSheet.write(xlRow,xlCol+2,'1:')
    xlFlagSheet.write(xlRow,xlCol+3,ds.globalattributes['Flag1'])
    xlFlagSheet.write(xlRow,xlCol+4,'2:')
    xlFlagSheet.write(xlRow,xlCol+5,ds.globalattributes['Flag2'])
    xlFlagSheet.write(xlRow,xlCol+6,'3:')
    xlFlagSheet.write(xlRow,xlCol+7,ds.globalattributes['Flag3'])
    xlFlagSheet.write(xlRow,xlCol+8,'4:')
    xlFlagSheet.write(xlRow,xlCol+9,ds.globalattributes['Flag4'])
    xlFlagSheet.write(xlRow,xlCol+10,'5:')
    xlFlagSheet.write(xlRow,xlCol+11,ds.globalattributes['Flag5'])
    xlFlagSheet.write(xlRow,xlCol+12,'6:')
    xlFlagSheet.write(xlRow,xlCol+13,ds.globalattributes['Flag6'])
    xlFlagSheet.write(xlRow,xlCol+14,'7:')
    xlFlagSheet.write(xlRow,xlCol+15,ds.globalattributes['Flag7'])
    xlRow = xlRow + 1
    xlFlagSheet.write(xlRow,xlCol,'10:')
    xlFlagSheet.write(xlRow,xlCol+1,ds.globalattributes['Flag10'])
    xlFlagSheet.write(xlRow,xlCol+2,'11:')
    xlFlagSheet.write(xlRow,xlCol+3,ds.globalattributes['Flag11'])
    xlFlagSheet.write(xlRow,xlCol+4,'12:')
    xlFlagSheet.write(xlRow,xlCol+5,ds.globalattributes['Flag12'])
    xlFlagSheet.write(xlRow,xlCol+6,'13:')
    xlFlagSheet.write(xlRow,xlCol+7,ds.globalattributes['Flag13'])
    xlFlagSheet.write(xlRow,xlCol+8,'14:')
    xlFlagSheet.write(xlRow,xlCol+9,ds.globalattributes['Flag14'])
    xlFlagSheet.write(xlRow,xlCol+10,'15:')
    xlFlagSheet.write(xlRow,xlCol+11,ds.globalattributes['Flag15'])
    xlFlagSheet.write(xlRow,xlCol+12,'16:')
    xlFlagSheet.write(xlRow,xlCol+13,ds.globalattributes['Flag16'])
    xlFlagSheet.write(xlRow,xlCol+14,'17:')
    xlFlagSheet.write(xlRow,xlCol+15,ds.globalattributes['Flag17'])
    xlFlagSheet.write(xlRow,xlCol+16,'18:')
    xlFlagSheet.write(xlRow,xlCol+17,ds.globalattributes['Flag18'])
    xlFlagSheet.write(xlRow,xlCol+18,'19:')
    xlFlagSheet.write(xlRow,xlCol+19,ds.globalattributes['Flag19'])
    xlRow = xlRow + 1
    xlFlagSheet.write(xlRow,xlCol,'30:')
    xlFlagSheet.write(xlRow,xlCol+1,ds.globalattributes['Flag30'])
    xlFlagSheet.write(xlRow,xlCol+2,'31:')
    xlFlagSheet.write(xlRow,xlCol+3,ds.globalattributes['Flag31'])
    xlFlagSheet.write(xlRow,xlCol+4,'32:')
    xlFlagSheet.write(xlRow,xlCol+5,ds.globalattributes['Flag32'])
    xlFlagSheet.write(xlRow,xlCol+6,'33:')
    xlFlagSheet.write(xlRow,xlCol+7,ds.globalattributes['Flag33'])
    xlFlagSheet.write(xlRow,xlCol+8,'34:')
    xlFlagSheet.write(xlRow,xlCol+9,ds.globalattributes['Flag34'])
    xlFlagSheet.write(xlRow,xlCol+10,'35:')
    xlFlagSheet.write(xlRow,xlCol+11,ds.globalattributes['Flag35'])
    xlFlagSheet.write(xlRow,xlCol+12,'36:')
    xlFlagSheet.write(xlRow,xlCol+13,ds.globalattributes['Flag36'])
    xlFlagSheet.write(xlRow,xlCol+14,'37:')
    xlFlagSheet.write(xlRow,xlCol+15,ds.globalattributes['Flag37'])
    xlFlagSheet.write(xlRow,xlCol+16,'38:')
    xlFlagSheet.write(xlRow,xlCol+17,ds.globalattributes['Flag38'])
    xlFlagSheet.write(xlRow,xlCol+18,'39:')
    xlFlagSheet.write(xlRow,xlCol+19,ds.globalattributes['Flag39'])
    bins = numpy.arange(-0.5,23.5)
    xlRow = 5
    xlCol = 1
    for Value in bins[:len(bins)-1]:
        xlFlagSheet.write(xlRow,xlCol,int(Value+0.5))
        xlCol = xlCol + 1
    xlRow = xlRow + 1
    xlCol = 0
    dsVarNames = ds.series.keys()
    dsVarNames.sort(key=unicode.lower)
    for ThisOne in dsVarNames:
        data,flag = GetSeries(ds, ThisOne)
        hist, bin_edges = numpy.histogram(flag, bins=bins)
        xlFlagSheet.write(xlRow,xlCol,ThisOne)
        xlCol = xlCol + 1
        for Value in hist:
            xlFlagSheet.write(xlRow,xlCol,float(Value))
            xlCol = xlCol + 1
        xlCol = 0
        xlRow = xlRow + 1
    xlFile.save(xlFileName)

def haskey(cf,ThisOne,key):
    return key in cf['Variables'][ThisOne].keys()

def incf(cf,ThisOne):
    return ThisOne in cf['Variables'].keys()

def MakeQCFlag(ds,SeriesList):
    flag = []
    if len(SeriesList)<=0:
        #log.info('  MakeQCFlag: no series list specified')
        pass
    if len(SeriesList)==1:
        if SeriesList[0] in ds.series.keys():
            flag = ds.series[SeriesList[0]]['Flag'].copy()
        else:
            log.error('  MakeQCFlag: series '+str(SeriesList[0])+' not in ds.series')
    if len(SeriesList)>1:
        for ThisOne in SeriesList:
            if ThisOne in ds.series.keys():
                if len(flag)==0:
                    #flag = numpy.ones(numpy.size(ds.series[ThisOne]['Flag']))
                    flag = ds.series[ThisOne]['Flag'].copy()
                else:
                    tmp_flag = ds.series[ThisOne]['Flag'].copy()      # get a temporary copy of the flag
                    index = numpy.where(numpy.mod(tmp_flag,10)==0)    # find the elements with flag = 0, 10, 20 etc
                    tmp_flag[index] = 0                               # set them all to 0
                    flag = numpy.maximum(flag,tmp_flag)               # now take the maximum
            else:
                log.error('  MakeQCFlag: series '+ThisOne+' not in ds.series')
    return flag

def nxMom_nxScalar_alpha(zoL):
    nRecs = numpy.size(zoL)
    nxMom = numpy.ma.ones(nRecs) * 0.079
    nxScalar = numpy.ma.ones(nRecs) * 0.085
    alpha = numpy.ma.ones(nRecs) * 0.925
    #  get the index of stable conditions
    stable = numpy.ma.where(zoL>0)[0]
    #  now set the series to their stable values
    nxMom[stable] = 0.079 * (1 + 7.9 * zoL[stable]) ** 0.75
    nxScalar[stable] = 2.0 - 1.915 / (1 + 0.5 * zoL[stable])
    alpha[stable] = 1
    return nxMom, nxScalar, alpha

def polyval(p,x):
    """
    Replacement for the polyval routine in numpy.  This version doesnt check the
    input variables to make sure they are array_like.  This means that when
    masked arrays are treated correctly when they are passed to this routine.
    Parameters
    ----------
     p : a 1D array of coefficients, highest order first
     x : a 1D array of points at which to evaluate the polynomial described by
         the coefficents in p
    Example
    -------
    >>> x = numpy.array([1,2,3])
    >>> p = numpy.array([2,0])
    >>> qcutils.polyval(p,x)
        array([2,4,6])
    >>> y = numpy.array([1,-9999,3])
    >>> y = numpy.ma.masked_where(y==-9999,y)
    >>> qcutils.polyval(p,y)
    masked_array(data = [2 -- 6],
                 mask = [False True False],
                 fill_value = 999999)
    """
    y = 0
    for i in range(len(p)):
        y = x*y + p[i]
    return y

def prepOzFluxVars(cf,ds):
    invars = ['Ux', 'Uy', 'Uz', 'UxUx', 'UxUy', 'UxA', 'UxC', 'UxT', 'UyA', 'UyC', 'UyT', 'UyUy', 'AhAh', 'CcCc', 'UxUz', 'UyUz', 'UzA', 'UzC', 'UzT', 'UzUz', 'Re_umol']
    outvars = ['u', 'v', 'w', 'uu', 'uv', 'uA', 'uC', 'uT', 'vA', 'vC', 'vT', 'vv', 'AhAh', 'CcCc', 'uw', 'vw', 'wA', 'wC', 'wT', 'ww', 'Re']
    for i in range(len(invars)):
        if invars[i] in ds.series.keys() and outvars[i] not in ds.series.keys():
            CreateSeries(ds,outvars[i],ds.series[invars[i]]['Data'],Flag=ds.series[invars[i]]['Flag'],Descr=ds.series[invars[i]]['Attr']['long_name'],Units=ds.series[invars[i]]['Attr']['units'],Standard=ds.series[invars[i]]['Attr']['standard_name'])

def startlog(loggername,loggerfile):
    logger = logging.getLogger(loggername)
    logger.setLevel(logging.DEBUG)
    fh = logging.FileHandler(loggerfile)
    fh.setLevel(logging.DEBUG)
    ch = logging.StreamHandler()
    ch.setLevel(logging.INFO)
    formatter = logging.Formatter('%(asctime)s %(name)-8s %(levelname)-6s %(message)s', '%d-%m-%y %H:%M')
    fh.setFormatter(formatter)
    ch.setFormatter(formatter)
    logger.addHandler(fh)
    logger.addHandler(ch)
    return logger

def Wegstein(T, Ah, p, Fh, u, z, z0):

    NumIters = 50
    SolveErr = numpy.float64(0.001)
 
    FirstEst =  u*c.k/math.log(z/z0)
    ustar = Fustar(T, Ah, p, Fh, u, z, z0, FirstEst)
    Inc = ustar-FirstEst
    IncDiv = -Inc
    Residual = ustar-Fustar(T, Ah, p, Fh, u, z, z0, ustar)
 
    i = 1
    while (i<NumIters)&(float(Residual)>float(SolveErr)):
        IncDiv = (IncDiv/Residual)-1
        if (IncDiv == 0):
            print 'Wegstein: IncDiv equals 0'
            ustar = u*c.k/math.log(z/z0)
            break
        Inc = Inc/IncDiv
        ustar = ustar+Inc
        IncDiv = Residual
        Residual = ustar-Fustar(T, Ah, p, Fh, u, z, z0, ustar)
        if (abs(ustar)<=1):
            RangeErr = SolveErr
        else:
            RangeErr = SolveErr*abs(ustar)
        if (abs(Inc)<=RangeErr):
            if (abs(Residual)<=10*RangeErr):
                break
        i = i + 1
    if (i==NumIters):
        print 'Wegstein: did not converge'
        ustar = u*c.k/math.log(z/z0)
    return ustar
