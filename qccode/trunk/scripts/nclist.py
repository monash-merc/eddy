import codecs
import netCDF4
import Tkinter, tkFileDialog
import unicodedata

netCDF4.default_encoding = 'latin-1'
# get the netCDF filename using the Tkinter function askopenfilename
root = Tkinter.Tk()
root.withdraw()               #Show file dialog without Tkinter window
ncFileName = tkFileDialog.askopenfilename(parent=root,title='Choose a netCDF file')
root.destroy()
if len(ncFileName)==0:
    sys.exit()
# get the list file name, this is the same as the netCDF file but with a
# file extension of '.txt' instead of '.nc'
ListFileName = ncFileName.replace('.nc','.txt')
ListFile = codecs.open(ListFileName,mode='w',encoding='latin-1')
# write a header to the screen and the list file
outstr = 'Details for netCDF file:'
print outstr
ListFile.write(outstr+'\n')
outstr = '   '+ncFileName
print outstr
ListFile.write(outstr+'\n')
# open the netCDF file object
ncFile = netCDF4.Dataset(ncFileName,'r')
# get the global attributes
outstr = 'The file has the following global attributes:'
print outstr
ListFile.write(outstr+'\n')
ncGlobalAttList = ncFile.ncattrs()
if len(ncGlobalAttList)!=0:
    for gattr in ncGlobalAttList:
        outstr = '   '+gattr+': '+getattr(ncFile,gattr)
        #outstr.encode('ascii','ignore')
        print outstr
        ListFile.write(outstr+'\n')
# get the dimensions in the netCDF file
ncDimNames = ncFile.dimensions.keys()
outstr = 'The file has the following dimensions (value in brackets):'
print outstr
ListFile.write(outstr+'\n')
for i in range(len(ncDimNames)):
    ncDimValue = len(ncFile.dimensions[ncDimNames[i]])
    outstr = '   '+ncDimNames[i]+' ('+str(ncDimValue)+')'
    print outstr
    ListFile.write(outstr+'\n')
# get the variables in the netCDF file
outstr = 'The file contains the following variables (dimensions in brackets):'
print outstr
ListFile.write(outstr+'\n')
ncVarNames = ncFile.variables.keys()
#ncVarNames.sort(key=str.lower)
ncVarNames.sort(key=unicode.lower)
for ThisOne in ncVarNames:
    var = ncFile.variables[ThisOne]
    dim = var.dimensions
    dimstr = '('
    for j in range(len(dim)):
        if j != 0: dimstr = dimstr+','
        dimstr = dimstr+dim[j]
    dimstr = dimstr+')'
    val = var[:]
    valstr = '['+str(val[0])+' ... '+str(val[-1])+']'
    outstr = '   '+ThisOne+' '+dimstr+' '+valstr
    print outstr
    ListFile.write(outstr+'\n')
    attList = var.ncattrs()
    for attr in attList:
        outstr = '      '+attr+': '+getattr(var,attr)
        print outstr
        ListFile.write(outstr+'\n')

ListFile.close()
ncFile.close()

