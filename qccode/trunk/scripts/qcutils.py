import constants as c
import datetime
import dateutil
import math
import meteorologicalfunctions as mf
import numpy
import logging
import xlwt

log = logging.getLogger('qc.utils')

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

def CreateSeries(ds,Label,Data,FList=None,Flag=None,Descr=None,Units=None):
    ds.series[unicode(Label)] = {}
    ds.series[Label]['Data'] = numpy.ma.filled(Data,float(-9999))
    if Flag == None:
        ds.series[Label]['Flag'] = MakeQCFlag(ds,FList)
    else:
        ds.series[Label]['Flag'] = Flag
    ds.series[Label]['Attr'] = {}
    ds.series[Label]['Attr']['Description'] = Descr
    ds.series[Label]['Attr']['Units'] = Units

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
    
def GetDateIndex(datetimeseries,date,default):
    # return the index of a date/datetime string in an array of datetime objects
    #  datetimeseries - array of datetime objects
    #  date - a date or date/time string in a format dateutils can parse
    #  default - default value, integer
    try:
        dateindex = datetimeseries.index(dateutil.parser.parse(date))
    except ValueError:
        dateindex = default
    return dateindex

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
    Series = numpy.ma.masked_where(ds.series[ThisOne]['Data']==float(-9999),ds.series[ThisOne]['Data'])
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

def GetSeriesStats(cf,ds):
    # open an Excel file for the flag statistics
    level = ds.globalattributes['Level']
    xlFileName = cf['Files'][level]['xlFilePath']+'flagstats_'+level+'.xls'
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
    xlFlagSheet.write(xlRow,xlCol+10,'16:')
    xlFlagSheet.write(xlRow,xlCol+11,ds.globalattributes['Flag16'])
    xlFlagSheet.write(xlRow,xlCol+12,'17:')
    xlFlagSheet.write(xlRow,xlCol+13,ds.globalattributes['Flag17'])
    xlRow = xlRow + 1
    xlFlagSheet.write(xlRow,xlCol,'20:')
    xlFlagSheet.write(xlRow,xlCol+1,ds.globalattributes['Flag20'])
    xlFlagSheet.write(xlRow,xlCol+2,'21:')
    xlFlagSheet.write(xlRow,xlCol+3,ds.globalattributes['Flag21'])
    xlFlagSheet.write(xlRow,xlCol+4,'22:')
    xlFlagSheet.write(xlRow,xlCol+5,ds.globalattributes['Flag22'])
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
            log.error('  MakeQCFlag: series '+SeriesList[i]+' not in ds.series')
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
