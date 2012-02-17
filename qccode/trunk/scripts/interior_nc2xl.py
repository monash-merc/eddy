from qcio import loadcontrolfile, nc_read_series, xl_write_series
import Tkinter, tkSimpleDialog
import sys


def autonc2xl(cf,Level):
    
    # get the variables
    ds = nc_read_series(cf,Level)
    
    # write the variables to the excel file
    xl_write_series(cf,ds,Level)
    
    if __name__ == "main":
        print 'nc2xl: All done'