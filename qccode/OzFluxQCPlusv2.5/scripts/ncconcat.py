import logging
import numpy
import qcio

# open the logging file
log = logging.getLogger('ncconcat')

ds = qcio.DataStructure()

# get the control file
cf = qcio.loadcontrolfile('../controlfiles')

InFile_list = cf['Files']['In'].keys()
# read in the first file
ncFileName = cf['Files']['In'][InFile_list[0]]
log.info('ncconcat: reading data from '+ncFileName)
print 'ncconcat: reading data from '+ncFileName
ds_n = qcio.nc_read_file(ncFileName)
# fill the global attributes
for ThisOne in ds_n.globalattributes.keys():
    ds.globalattributes[ThisOne] = ds_n.globalattributes[ThisOne]
# fill the variables
for ThisOne in ds_n.series.keys():
    ds.series[ThisOne] = {}
    ds.series[ThisOne]['Data'] = ds_n.series[ThisOne]['Data']
    if 'Flag' in ds_n.series[ThisOne].keys():
        ds.series[ThisOne]['Flag'] = ds_n.series[ThisOne]['Flag']
    ds.series[ThisOne]['Attr'] = {}
    for attr in ds_n.series[ThisOne]['Attr'].keys():
        ds.series[ThisOne]['Attr'][attr] = ds_n.series[ThisOne]['Attr'][attr]

# loop over the remaining files given in the control file
for n in InFile_list[1:]:
    ncFileName = cf['Files']['In'][InFile_list[int(n)]]
    log.info('ncconcat: reading data from '+ncFileName)
    print 'ncconcat: reading data from '+ncFileName
    ds_n = qcio.nc_read_file(ncFileName)
    nRecs_n = len(ds_n.series['xlDateTime']['Data'])
    nRecs = len(ds.series['xlDateTime']['Data'])
    for ThisOne in ds_n.series.keys():
        if ThisOne in ds.series.keys():
            ds.series[ThisOne]['Data'] = numpy.append(ds.series[ThisOne]['Data'],ds_n.series[ThisOne]['Data'])
            if 'Flag' in ds_n.series[ThisOne].keys():
                ds.series[ThisOne]['Flag'] = numpy.append(ds.series[ThisOne]['Flag'],ds_n.series[ThisOne]['Flag'])
        else:
            ds.series[ThisOne] = {}
            ds.series[ThisOne]['Data'] = numpy.array([-9999]*nRecs,dtype=numpy.float64)
            ds.series[ThisOne]['Flag'] = numpy.array([1]*nRecs,dtype=numpy.int32)
            ds.series[ThisOne]['Data'] = numpy.append(ds.series[ThisOne]['Data'],ds_n.series[ThisOne]['Data'])
            ds.series[ThisOne]['Flag'] = numpy.append(ds.series[ThisOne]['Flag'],ds_n.series[ThisOne]['Flag'])
            ds.series[ThisOne]['Attr'] = {}
            for attr in ds_n.series[ThisOne]['Attr'].keys():
                ds.series[ThisOne]['Attr'][attr] = ds_n.series[ThisOne]['Attr'][attr]
    
    for ThisOne in ds.series.keys():
        if ThisOne not in ds_n.series.keys():
            ds.series[ThisOne]['Data'] = numpy.append(ds.series[ThisOne]['Data'],numpy.array([-9999]*nRecs_n,dtype=numpy.float64))
            ds.series[ThisOne]['Flag'] = numpy.append(ds.series[ThisOne]['Flag'],numpy.array([1]*nRecs_n,dtype=numpy.int32))

ds.globalattributes['NumRecs'] = str(len(ds.series['xlDateTime']['Data']))

# write the netCDF file
ncFileName = cf['Files']['Out']['ncFileName']
log.info('ncconcat: writing data to '+ncFileName)
print 'ncconcat: writing data to '+ncFileName
qcio.nc_write_series(cf,ds,'Out')
