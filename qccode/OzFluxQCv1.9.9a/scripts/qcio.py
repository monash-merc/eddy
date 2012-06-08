from configobj import ConfigObj
import ast
import datetime
import numpy
import os
import sys
import time
import Tkinter, tkFileDialog
import xlrd
import xlwt
import netCDF4
import logging
import qcts
import qcutils

log = logging.getLogger('qc.io')

class DataStructure(object):
    def __init__(self):
        self.series = {}
        self.globalattributes = {}

def autonc2xl(cf,Level):
    # get the variables
    ds = nc_read_series(cf,Level)
    # write the variables to the excel file
    xl_write_series(cf,ds,Level)

def autoxl2nc(cf,InLevel,OutLevel):
    # get the data series from the Excel file
    ds = xl_read_series(cf,InLevel)
    # get the year, month, day, hour, minute and second from the xl date/time
    qcts.get_yearmonthdayhourminutesecond(cf,ds)
    # get the quality control flags
    if InLevel == 'L1':
        qcts.get_qcflag(ds)
    # get the flags from gap filled 'L3' or 'L4' Excel file
    if InLevel != 'L1':
        VariablesInFile = ds.series.keys()
        for ThisOne in ['xlDateTime','Gap','Year','Month','Day','Hour','Minute','Second','Hdh','Ddd']:
            if ThisOne in VariablesInFile:
                VariablesInFile.remove(ThisOne)
        ds1 = xl_read_flags(cf,ds,InLevel,VariablesInFile)
        if InLevel == 'L4':
            for ThisOne in ['Fc_gapfilled','Fe_gapfilled','Fh_gapfilled']:
                if ThisOne in ds.series.keys():
                    ds1.series[ThisOne]['Flag'] = ds.series['Gap']['Data']
        ds = ds1
    # do any functions to create new series
    qcts.do_functions(cf,ds)
    # get the netCDF attributes from the control file
    qcts.do_attributes(cf,ds)
    # write the data to the netCDF file
    nc_write_series(cf,ds,OutLevel)

def get_controlfilecontents(ControlFileName):
    log.info(' Processing the control file ')
    if len(ControlFileName)!=0:
        cf = ConfigObj(ControlFileName)
        cf['ControlFileName'] = ControlFileName
    else:
        cf = ConfigObj()
    return cf

def get_controlfilename(ControlFilePath):
    log.info(' Choosing the control file ')
    root = Tkinter.Tk(); root.withdraw()
    ControlFileName = tkFileDialog.askopenfilename(initialdir=ControlFilePath)
    root.destroy()
#    if len(ControlFileName)==0:
#        sys.exit()
    return ControlFileName

def get_datetime(ds):
    ''' Creates a series of Python datetime objects from the year, month,
    day, hour, minute and second series stored in the netCDF file.'''
    log.info(' Getting the date and time series')
    nRecs = len(ds.series['Year']['Data'])
    ds.series[unicode('DateTime')] = {}
    ds.series['DateTime']['Data'] = []
    for i in range(nRecs):
        ds.series['DateTime']['Data'].append(datetime.datetime(int(ds.series['Year']['Data'][i]),
                                                       int(ds.series['Month']['Data'][i]),
                                                       int(ds.series['Day']['Data'][i]),
                                                       int(ds.series['Hour']['Data'][i]),
                                                       int(ds.series['Minute']['Data'][i]),
                                                       int(ds.series['Second']['Data'][i])))

def get_ncdtype(Series):
    sd = Series.dtype.name
    dt = 'f'
    if sd=='float64': dt = 'd'
    if sd=='int32': dt = 'i'
    if sd=='int64': dt = 'l'
    return dt

def get_ncfilename(path='.',title='Choose a netCDF file to open'):
    '''Get a netCDF file name'''
    root = Tkinter.Tk(); root.withdraw()
    ncFileName = tkFileDialog.askopenfilename(parent=root,initialdir=path,title=title)
    root.destroy()
    if len(ncFileName)==0:
        sys.exit()
    return ncFileName

def get_saveasfilename(path='.',title='Save file as'):
    '''Get a file name for saving'''
    root = Tkinter.Tk(); root.withdraw()
    SaveAsFileName = tkFileDialog.asksaveasfilename(parent=root,initialdir=path,title=title)
    root.destroy()
    if len(SaveAsFileName)==0:
        sys.exit()
    return SaveAsFileName

def get_xlfilename(path='.'):
    '''Get an Excel file name'''
    root = Tkinter.Tk(); root.withdraw()
    xlFileName = tkFileDialog.askopenfilename(parent=root,initialdir=path,title='Choose an Excel file to open')
    root.destroy
    return str(xlFileName)

def loadcontrolfile(ControlFilePath):
    ControlFileName = get_controlfilename(ControlFilePath)
    cf = get_controlfilecontents(ControlFileName)
    return cf

def nc_read_series(cf,level):
    ''' Read a netCDF file and put the data and meta-data into a DataStructure'''
    ncFullName = cf['Files'][level]['ncFilePath']+cf['Files'][level]['ncFileName']
    log.info(' Reading netCDF file '+ncFullName)
    netCDF4.default_encoding = 'latin-1'
    ncFile = netCDF4.Dataset(ncFullName,'r')
    ds = DataStructure()
    gattrlist = ncFile.ncattrs()
    if len(gattrlist)!=0:
        for gattr in gattrlist:
            ds.globalattributes[gattr] = getattr(ncFile,gattr)
    for ThisOne in ncFile.variables.keys():
        if '_QCFlag' not in ThisOne:
            # create the series in the data structure
            ds.series[unicode(ThisOne)] = {}
            # get the data variable object
            ds.series[ThisOne]['Data'] = ncFile.variables[ThisOne][:]
            # check for a QC flag and if it exists, load it
            if ThisOne+'_QCFlag' in ncFile.variables.keys():
                #ncVar = ncFile.variables[ThisOne+'_QCFlag']
                ds.series[ThisOne]['Flag'] = ncFile.variables[ThisOne+'_QCFlag'][:]
            # get the variable attributes
            vattrlist = ncFile.variables[ThisOne].ncattrs()
            if len(vattrlist)!=0:
                ds.series[ThisOne]['Attr'] = {}
                for vattr in vattrlist:
                    ds.series[ThisOne]['Attr'][vattr] = getattr(ncFile.variables[ThisOne],vattr)
    ncFile.close()
    # get a series of Python datetime objects
    get_datetime(ds)
    return ds

def nc_read_series_file(ncFullName):
    ''' Read a netCDF file and put the data and meta-data into a DataStructure'''
    log.info(' Reading netCDF file '+ncFullName)
    netCDF4.default_encoding = 'latin-1'
    ncFile = netCDF4.Dataset(ncFullName,'r')
    ds = DataStructure()
    gattrlist = ncFile.ncattrs()
    if len(gattrlist)!=0:
        for gattr in gattrlist:
            ds.globalattributes[gattr] = getattr(ncFile,gattr)
    for ThisOne in ncFile.variables.keys():
        if '_QCFlag' not in ThisOne:
            # create the series in the data structure
            ds.series[unicode(ThisOne)] = {}
            # get the data variable object
            ds.series[ThisOne]['Data'] = ncFile.variables[ThisOne][:]
            # check for a QC flag and if it exists, load it
            if ThisOne+'_QCFlag' in ncFile.variables.keys():
                ds.series[ThisOne]['Flag'] = ncFile.variables[ThisOne+'_QCFlag'][:]
            # get the variable attributes
            vattrlist = ncFile.variables[ThisOne].ncattrs()
            if len(vattrlist)!=0:
                ds.series[ThisOne]['Attr'] = {}
                for vattr in vattrlist:
                    ds.series[ThisOne]['Attr'][vattr] = getattr(ncFile.variables[ThisOne],vattr)
    ncFile.close()
    # get a series of Python datetime objects
    get_datetime(ds)
    return ds
def nc_write_OzFlux_series(cf,ds,level):
    ncFullName = cf['Files'][level]['ncFilePath']+cf['Files'][level]['ncFileName']
    log.info(' Writing netCDF file '+ncFullName)
    ncFile = netCDF4.Dataset(ncFullName,'w',format='NETCDF3_CLASSIC')
    for ThisOne in ds.globalattributes.keys():
        setattr(ncFile,ThisOne,ds.globalattributes[ThisOne])
    t = time.localtime()
    RunDateTime = str(datetime.datetime(t[0],t[1],t[2],t[3],t[4],t[5]))
    setattr(ncFile,'RunDateTime',RunDateTime)
    nRecs = len(ds.series['xlDateTime']['Data'])
    setattr(ncFile,'NumRecs',str(nRecs))
    setattr(ncFile,'Level',level)
    ncFile.createDimension('Time',nRecs)
    SeriesList = ast.literal_eval(cf['Output']['OFL2'])
    VariableList = ds.series.keys()
    for ThisOne in ds.series.keys():
        if ThisOne not in SeriesList:
            VariableList.remove(ThisOne)
    SeriesList = VariableList
    for ThisOne in ['xlDateTime','Year','Month','Day','Hour','Minute','Second','Hdh']:
        if ThisOne in SeriesList:
            dt = get_ncdtype(ds.series[ThisOne]['Data'])
            ncVar = ncFile.createVariable(ThisOne,dt,('Time',))
            ncVar[:] = ds.series[ThisOne]['Data'].tolist()
            setattr(ncVar,'Description',ThisOne)
            setattr(ncVar,'units','none')
            SeriesList.remove(ThisOne)
    if 'DateTime' in SeriesList:
        SeriesList.remove('DateTime')
    for ThisOne in SeriesList:
        if 'Data' in ds.series[ThisOne].keys():
            dt = get_ncdtype(ds.series[ThisOne]['Data'])
            ncVar = ncFile.createVariable(ThisOne,dt,('Time',))
            ncVar[:] = ds.series[ThisOne]['Data'].tolist()
        if 'Attr' in ds.series[ThisOne].keys():
            for attr in ds.series[ThisOne]['Attr']:
                setattr(ncVar,attr,ds.series[ThisOne]['Attr'][attr])
        if 'Flag' in ds.series[ThisOne].keys():
            dt = get_ncdtype(ds.series[ThisOne]['Flag'])
            ncVar = ncFile.createVariable(ThisOne+'_QCFlag',dt,('Time',))
            ncVar[:] = ds.series[ThisOne]['Flag'].tolist()
            setattr(ncVar,'Description','QC flag')
            setattr(ncVar,'units','none')
    ncFile.close()

def nc_write_series(cf,ds,level):
    ncFullName = cf['Files'][level]['ncFilePath']+cf['Files'][level]['ncFileName']
    log.info(' Writing netCDF file '+ncFullName)
    if qcutils.cfkeycheck(cf,Base='General',ThisOne='netCDFv3') and cf['General']['netCDFv3'] == 'False':
        ncFile = netCDF4.Dataset(ncFullName,'w')
    else:
        ncFile = netCDF4.Dataset(ncFullName,'w',format='NETCDF3_CLASSIC')
    for ThisOne in ds.globalattributes.keys():
        setattr(ncFile,ThisOne,ds.globalattributes[ThisOne])
    t = time.localtime()
    RunDateTime = str(datetime.datetime(t[0],t[1],t[2],t[3],t[4],t[5]))
    setattr(ncFile,'RunDateTime',RunDateTime)
    nRecs = len(ds.series['xlDateTime']['Data'])
    setattr(ncFile,'NumRecs',str(nRecs))
    setattr(ncFile,'Level',level)
    ncFile.createDimension('Time',nRecs)
    SeriesList = ds.series.keys()
    for ThisOne in ['xlDateTime','Year','Month','Day','Hour','Minute','Second','Hdh']:
        if ThisOne in SeriesList:
            dt = get_ncdtype(ds.series[ThisOne]['Data'])
            ncVar = ncFile.createVariable(ThisOne,dt,('Time',))
            ncVar[:] = ds.series[ThisOne]['Data'].tolist()
            setattr(ncVar,'Description',ThisOne)
            setattr(ncVar,'units','none')
            SeriesList.remove(ThisOne)
    if 'DateTime' in SeriesList:
        SeriesList.remove('DateTime')
    for ThisOne in SeriesList:
        if 'Data' in ds.series[ThisOne].keys():
            dt = get_ncdtype(ds.series[ThisOne]['Data'])
            ncVar = ncFile.createVariable(ThisOne,dt,('Time',))
            ncVar[:] = ds.series[ThisOne]['Data'].tolist()
        if 'Attr' in ds.series[ThisOne].keys():
            for attr in ds.series[ThisOne]['Attr']:
                setattr(ncVar,attr,ds.series[ThisOne]['Attr'][attr])
        if 'Flag' in ds.series[ThisOne].keys():
            dt = get_ncdtype(ds.series[ThisOne]['Flag'])
            ncVar = ncFile.createVariable(ThisOne+'_QCFlag',dt,('Time',))
            ncVar[:] = ds.series[ThisOne]['Flag'].tolist()
            setattr(ncVar,'Description','QC flag')
            setattr(ncVar,'units','none')
    ncFile.close()

def xl_read_flags(cf,ds,level,VariablesInFile):
    # First data row in Excel worksheets.
    FirstDataRow = int(cf['Files'][level]['xl1stDataRow']) - 1
    # Get the full name of the Excel file from the control file.
    xlFullName = cf['Files'][level]['xlFilePath']+cf['Files'][level]['xlFileName']
    # Get the Excel workbook object.
    if os.path.isfile(xlFullName):
        xlBook = xlrd.open_workbook(xlFullName)
    else:
        log.error(' Excel file '+xlFullName+' not found, choose another')
        xlFullName = get_xlfilename()
        if len(xlFullName)==0:
            return
        xlBook = xlrd.open_workbook(xlFullName)
    ds.globalattributes['xlFullName'] = xlFullName
    
    for ThisOne in VariablesInFile:
        if 'xl' in cf['Variables'][ThisOne].keys():
            log.info(' Getting flags for '+ThisOne+' from spreadsheet')
            ActiveSheet = xlBook.sheet_by_name('Flag')
            LastDataRow = int(ActiveSheet.nrows)
            HeaderRow = ActiveSheet.row_values(int(cf['Files'][level]['xlHeaderRow'])-1)
            if cf['Variables'][ThisOne]['xl']['name'] in HeaderRow:
                xlCol = HeaderRow.index(cf['Variables'][ThisOne]['xl']['name'])
                Values = ActiveSheet.col_values(xlCol)[FirstDataRow:LastDataRow]
                Types = ActiveSheet.col_types(xlCol)[FirstDataRow:LastDataRow]
                ds.series[ThisOne]['Flag'] = numpy.array([-9999]*len(Values),numpy.float64)
                for i in range(len(Values)):
                    if Types[i]==2: #xlType=3 means a date/time value, xlType=2 means a number
                        ds.series[ThisOne]['Flag'][i] = numpy.float64(Values[i])
                    else:
                        log.error('  xl_read_flags: flags for '+ThisOne+' not found in xl file')
    return ds

def xl_read_series(cf,level):
    # Instance the data structure object.
    ds = DataStructure()
    # First data row in Excel worksheets.
    FirstDataRow = int(cf['Files'][level]['xl1stDataRow']) - 1
    # Get the full name of the Excel file from the control file.
    xlFullName = cf['Files'][level]['xlFilePath']+cf['Files'][level]['xlFileName']
    # Get the Excel workbook object.
    if os.path.isfile(xlFullName):
        log.info(' Opening and reading Excel file '+xlFullName)
        xlBook = xlrd.open_workbook(xlFullName)
        log.info(' Opened and read Excel file '+xlFullName)
    else:
        log.error(' Excel file '+xlFullName+' not found, choose another')
        xlFullName = get_xlfilename()
        if len(xlFullName)==0:
            return
        log.info(' Opening and reading Excel file '+xlFullName)
        xlBook = xlrd.open_workbook(xlFullName)
        log.info(' Opened and read Excel file '+xlFullName)
    ds.globalattributes['xlFullName'] = xlFullName
    # Get the Excel file modification date and time, these will be
    # written to the netCDF file to uniquely identify the version
    # of the Excel file used to create this netCDF file.
    s = os.stat(xlFullName)
    t = time.localtime(s.st_mtime)
    ds.globalattributes['xlModDateTime'] = str(datetime.datetime(t[0],t[1],t[2],t[3],t[4],t[5]))
    # Loop over the variables defined in the 'Variables' section of the
    # configuration file.
    for ThisOne in cf['Variables'].keys():
        if 'xl' in cf['Variables'][ThisOne].keys():
            log.info(' Getting data for '+ThisOne+' from spreadsheet')
            ActiveSheet = xlBook.sheet_by_name(cf['Variables'][ThisOne]['xl']['sheet'])
            LastDataRow = int(ActiveSheet.nrows)
            HeaderRow = ActiveSheet.row_values(int(cf['Files'][level]['xlHeaderRow'])-1)
            if cf['Variables'][ThisOne]['xl']['name'] in HeaderRow:
                ds.series[unicode(ThisOne)] = {}
                xlCol = HeaderRow.index(cf['Variables'][ThisOne]['xl']['name'])
                Values = ActiveSheet.col_values(xlCol)[FirstDataRow:LastDataRow]
                Types = ActiveSheet.col_types(xlCol)[FirstDataRow:LastDataRow]
                ds.series[ThisOne]['Data'] = numpy.array([-9999]*len(Values),numpy.float64)
                for i in range(len(Values)):
                    if (Types[i]==3) or (Types[i]==2): #xlType=3 means a date/time value, xlType=2 means a number
                        ds.series[ThisOne]['Data'][i] = numpy.float64(Values[i])
            else:
                log.error('  xl_read_series: series '+ThisOne+' not found in xl file')
    return ds

def xl_write_series(cf,ds,level):
    log.info(' Opening the Excel file ')
    nRecs = len(ds.series['xlDateTime']['Data'])
    xlFileName = cf['Files'][level]['xlFilePath']+cf['Files'][level]['xlFileName']
    xlFile = xlwt.Workbook()
    if cf['General']['Platform'] == 'Mac':
        xlFile.dates_1904 = True
    xlDataSheet = xlFile.add_sheet('Data')
    xlFlagSheet = xlFile.add_sheet('Flag')
    xlCol = 0
    VariablesInFile = ds.series.keys()
    VariablesToOutput = ast.literal_eval(cf['Output'][level])
    # write the xl date/time value to the first column of the worksheets
    d_xf = xlwt.easyxf(num_format_str='dd/mm/yyyy hh:mm')
    for j in range(nRecs):
        xlDataSheet.write(j+10,xlCol,ds.series['xlDateTime']['Data'][j],d_xf)
    xlFlagSheet.write(9,xlCol,'TIMESTAMP')
    xlDataSheet.write(9,xlCol,'TIMESTAMP')
    xlDataSheet.write(0,xlCol,'Site:')
    xlDataSheet.write(0,xlCol+1,ds.globalattributes['site'])
    xlDataSheet.write(2,xlCol,'Institution:')
    xlDataSheet.write(2,xlCol+1,ds.globalattributes['institution'])
    xlDataSheet.write(1,xlCol,'Latitude:')
    xlDataSheet.write(1,xlCol+1,ds.globalattributes['latitude'])
    xlDataSheet.write(1,xlCol+2,'Longitude:')
    xlDataSheet.write(1,xlCol+3,ds.globalattributes['longitude'])
    xlDataSheet.write(3,xlCol,'Contact:')
    xlDataSheet.write(3,xlCol+1,ds.globalattributes['contact'])
    xlFlagSheet.write(0,xlCol,'0:')
    xlFlagSheet.write(0,xlCol+1,ds.globalattributes['Flag0'])
    xlFlagSheet.write(0,xlCol+2,'1:')
    xlFlagSheet.write(0,xlCol+3,ds.globalattributes['Flag1'])
    xlFlagSheet.write(0,xlCol+4,'2:')
    xlFlagSheet.write(0,xlCol+5,ds.globalattributes['Flag2'])
    xlFlagSheet.write(0,xlCol+6,'3:')
    xlFlagSheet.write(0,xlCol+7,ds.globalattributes['Flag3'])
    xlFlagSheet.write(0,xlCol+8,'4:')
    xlFlagSheet.write(0,xlCol+9,ds.globalattributes['Flag4'])
    xlFlagSheet.write(0,xlCol+10,'5:')
    xlFlagSheet.write(0,xlCol+11,ds.globalattributes['Flag5'])
    xlFlagSheet.write(0,xlCol+12,'6:')
    xlFlagSheet.write(0,xlCol+13,ds.globalattributes['Flag6'])
    xlFlagSheet.write(0,xlCol+14,'7:')
    xlFlagSheet.write(0,xlCol+15,ds.globalattributes['Flag7'])
    xlFlagSheet.write(1,xlCol,'10:')
    xlFlagSheet.write(1,xlCol+1,ds.globalattributes['Flag10'])
    xlFlagSheet.write(1,xlCol+2,'11:')
    xlFlagSheet.write(1,xlCol+3,ds.globalattributes['Flag11'])
    xlFlagSheet.write(1,xlCol+4,'12:')
    xlFlagSheet.write(1,xlCol+5,ds.globalattributes['Flag12'])
    xlFlagSheet.write(1,xlCol+6,'13:')
    xlFlagSheet.write(1,xlCol+7,ds.globalattributes['Flag13'])
    xlFlagSheet.write(1,xlCol+8,'14:')
    xlFlagSheet.write(1,xlCol+9,ds.globalattributes['Flag14'])
    xlFlagSheet.write(1,xlCol+10,'15:')
    xlFlagSheet.write(1,xlCol+11,ds.globalattributes['Flag15'])
    xlFlagSheet.write(1,xlCol+12,'16:')
    xlFlagSheet.write(1,xlCol+13,ds.globalattributes['Flag16'])
    xlFlagSheet.write(1,xlCol+14,'17:')
    xlFlagSheet.write(1,xlCol+15,ds.globalattributes['Flag17'])
    xlFlagSheet.write(1,xlCol+16,'18:')
    xlFlagSheet.write(1,xlCol+17,ds.globalattributes['Flag18'])
    xlFlagSheet.write(1,xlCol+18,'19:')
    xlFlagSheet.write(1,xlCol+19,ds.globalattributes['Flag19'])
    xlFlagSheet.write(2,xlCol,'30:')
    xlFlagSheet.write(2,xlCol+1,ds.globalattributes['Flag30'])
    xlFlagSheet.write(2,xlCol+2,'31:')
    xlFlagSheet.write(2,xlCol+3,ds.globalattributes['Flag31'])
    xlFlagSheet.write(2,xlCol+4,'32:')
    xlFlagSheet.write(2,xlCol+5,ds.globalattributes['Flag32'])
    xlFlagSheet.write(2,xlCol+6,'33:')
    xlFlagSheet.write(2,xlCol+7,ds.globalattributes['Flag33'])
    xlFlagSheet.write(2,xlCol+8,'34:')
    xlFlagSheet.write(2,xlCol+9,ds.globalattributes['Flag34'])
    xlFlagSheet.write(2,xlCol+10,'35:')
    xlFlagSheet.write(2,xlCol+11,ds.globalattributes['Flag35'])
    xlFlagSheet.write(2,xlCol+12,'36:')
    xlFlagSheet.write(2,xlCol+13,ds.globalattributes['Flag36'])
    xlFlagSheet.write(2,xlCol+14,'37:')
    xlFlagSheet.write(2,xlCol+15,ds.globalattributes['Flag37'])
    xlFlagSheet.write(2,xlCol+16,'38:')
    xlFlagSheet.write(2,xlCol+17,ds.globalattributes['Flag38'])
    xlFlagSheet.write(2,xlCol+18,'39:')
    xlFlagSheet.write(2,xlCol+19,ds.globalattributes['Flag39'])
    xlFlagSheet.write(3,xlCol,'51:')
    xlFlagSheet.write(3,xlCol+1,ds.globalattributes['Flag51'])
    xlFlagSheet.write(3,xlCol+2,'52:')
    xlFlagSheet.write(3,xlCol+3,ds.globalattributes['Flag52'])
    xlFlagSheet.write(4,xlCol,'61:')
    xlFlagSheet.write(4,xlCol+1,ds.globalattributes['Flag61'])
    xlFlagSheet.write(4,xlCol+2,'62:')
    xlFlagSheet.write(4,xlCol+3,ds.globalattributes['Flag62'])
    xlFlagSheet.write(4,xlCol+4,'63:')
    xlFlagSheet.write(4,xlCol+5,ds.globalattributes['Flag63'])
    xlFlagSheet.write(4,xlCol+6,'64:')
    xlFlagSheet.write(4,xlCol+7,ds.globalattributes['Flag64'])
    xlFlagSheet.write(5,xlCol,'70:')
    xlFlagSheet.write(5,xlCol+1,ds.globalattributes['Flag70'])
    xlFlagSheet.write(5,xlCol+2,'80:')
    xlFlagSheet.write(5,xlCol+3,ds.globalattributes['Flag80'])
    xlFlagSheet.write(5,xlCol+4,'81:')
    xlFlagSheet.write(5,xlCol+5,ds.globalattributes['Flag81'])
    xlFlagSheet.write(5,xlCol+6,'82:')
    xlFlagSheet.write(5,xlCol+7,ds.globalattributes['Flag82'])
    #d_xf = xlwt.easyxf('font: height 160',num_format_str='dd/mm/yyyy hh:mm')
    d_xf = xlwt.easyxf(num_format_str='dd/mm/yyyy hh:mm')
    for j in range(nRecs):
        xlFlagSheet.write(j+10,xlCol,ds.series['xlDateTime']['Data'][j],d_xf)
    # remove the date and time variables from the list to output
    for ThisOne in ['xlDateTime','Year','Month','Day','Hour','Minute','Second','Hdh']:
        if ThisOne in VariablesInFile:
            VariablesInFile.remove(ThisOne)
    # now start looping over the other variables in the xl file
    xlCol = xlCol + 1
    # list of variables in the data structure
    #VariablesInFile.sort(key=str.lower)
    # list of variables to write out (specified in the control file)
    #VariablesToOutput.sort(key=str.lower)
    # loop over variables to be output to xl file
    for ThisOne in VariablesToOutput:
        if ThisOne in VariablesInFile:
            # put up a progress message
            log.info(' Writing '+ThisOne+' into column '+str(xlCol)+' of the Excel file')
            # specify the style of the output
            #d_xf = xlwt.easyxf('font: height 160')
            d_xf = xlwt.easyxf()
            # write the units and the variable name to the header rows in the xl file
            Description = ds.series[ThisOne]['Attr']['long_name']
            Units = ds.series[ThisOne]['Attr']['units']
            #xlDataSheet.write(8,xlCol,Units,d_xf)
            #xlDataSheet.write(9,xlCol,ThisOne,d_xf)
            xlDataSheet.write(7,xlCol,Description)
            xlDataSheet.write(8,xlCol,Units)
            xlDataSheet.write(9,xlCol,ThisOne)
            # loop over the values in the variable series (array writes don't seem to work)
            for j in range(nRecs):
                #xlDataSheet.write(j+10,xlCol,float(ds.series[ThisOne]['Data'][j]),d_xf)
                xlDataSheet.write(j+10,xlCol,float(ds.series[ThisOne]['Data'][j]))
            # check to see if this variable has a quality control flag
            if 'Flag' in ds.series[ThisOne].keys():
                # write the QC flag name to the xk file
                #xlFlagSheet.write(9,xlCol,ThisOne,d_xf)
                xlFlagSheet.write(9,xlCol,ThisOne)
                # specify the format of the QC flag (integer)
                #d_xf = xlwt.easyxf('font: height 160',num_format_str='0')
                d_xf = xlwt.easyxf(num_format_str='0')
                # loop over QV flag values and write to xl file
                for j in range(nRecs):
                    xlFlagSheet.write(j+10,xlCol,ds.series[ThisOne]['Flag'][j],d_xf)
            # increment the column pointer
            xlCol = xlCol + 1
    # tell the user what we are doing
    log.info(' Saving the Excel file ')
    # save the xl file
    xlFile.save(xlFileName)

