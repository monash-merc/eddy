import numpy
from qcio import loadcontrolfile, nc_write_series, xl_read_series, xl_read_flags
from qcts import get_yearmonthdayhourminutesecond,get_qcflag,do_functions,do_attributes


def autoxl2nc(cf,InLevel,OutLevel):
    
    # get the data series from the Excel file
    ds = xl_read_series(cf,InLevel)
    # get the year, month, day, hour, minute and second from the xl date/time
    get_yearmonthdayhourminutesecond(cf,ds)
    # get the quality control flags
    if InLevel == 'L1':
        get_qcflag(ds)
    # get the flags from gap filled 'L3' or 'L4' Excel file
    if InLevel == 'L2' or InLevel == 'L3' or InLevel == 'L4':
        VariablesInFile = ds.series.keys()
        for ThisOne in ['xlDateTime','Gap','Year','Month','Day','Hour','Minute','Second','Hdh']:
            if ThisOne in VariablesInFile:
                VariablesInFile.remove(ThisOne)
        ds1 = xl_read_flags(cf,ds,InLevel,VariablesInFile)
        if InLevel == 'L4':
            for ThisOne in ['Fc_gapfilled','Fe_gapfilled','Fh_gapfilled']:
                ds1.series[ThisOne]['Flag'] = ds.series['Gap']['Data']
        ds = ds1
    # do any functions to create new series
    do_functions(cf,ds)
    # get the netCDF attributes from the control file
    do_attributes(cf,ds)

    # write the data to the netCDF file
    nc_write_series(cf,ds,OutLevel)

    if __name__ == "main":
        print 'xl2nc: All done'