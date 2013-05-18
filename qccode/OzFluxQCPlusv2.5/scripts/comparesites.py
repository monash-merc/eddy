import matplotlib
import numpy
import pylab
import qcio
import qcutils
import qcplot

def xyplot(x,y,sub=[1,1,1],regr=0,label=None):
    '''Generic XY scatter plot routine'''
    pylab.subplot(sub[0],sub[1],sub[2])
    pylab.plot(x,y,'b.')
    ax = pylab.gca()
    if label!=None:
        pylab.text(0.05,0.88,label,transform=ax.transAxes)
    if regr!=0:
        coefs = numpy.ma.polyfit(x,y,1)
        xfit = numpy.ma.array([numpy.ma.minimum(x),numpy.ma.maximum(x)])
        yfit = numpy.polyval(coefs,xfit)
        r = numpy.ma.corrcoef(x,y)
        eqnstr = 'y = %.3fx + %.3f, r = %.3f'%(coefs[0],coefs[1],r[0][1])
        pylab.plot(xfit,yfit,'r--',linewidth=3)
        pylab.text(0.5,1.0,eqnstr,fontsize=8,horizontalalignment='center',transform=ax.transAxes)

ds1_filename = qcio.get_ncfilename(path='D:/ARCSpatial/Sites/',title='Choose the main site netCDF file')
ds2_filename = qcio.get_ncfilename(path='D:/ARCSpatial/Sites/',title='Choose the secondary site netCDF file')
#ds1_filename = 'D:/ARCSpatial/Sites/DalyUncleared/Data/Processed/2008/DalyUncleared_2008_L4.nc'
#ds2_filename = 'D:/ARCSpatial/Sites/DalyPasture/Data/Processed/2008/DalyPasture_2008_L4.nc'
out_filename = qcio.get_saveasfilename('D:/ARCSpatial/Sites/')

ds1 = qcio.nc_read_series_file(ds1_filename)
ds2 = qcio.nc_read_series_file(ds2_filename)
OutFile = open(out_filename,'w')

Hdh,f = qcutils.GetSeriesasMA(ds1,'Hdh')

met = ['Ta_EC','Ah_EC','Cc_7500_Av','Ws_CSAT','Wd_CSAT']
rad = ['Fsd','Fsu','Fld','Flu','Fn','Fg']
flux = ['Fh','Fe_wpl','Fc_wpl','Fm','ustar']

fignum = 0
for ThisList in [met,rad,flux]:
    fignum = fignum + 1
    pylab.figure(fignum,figsize=(8,11))
    pylab.subplots_adjust(left=0.075,right=0.925,bottom=0.075,top=0.925,wspace=0.3,hspace=0.3)
    textstr = 'Primary site (y) is '+ds1.globalattributes['SiteName']
    textstr = textstr+' '+str(ds1.series['DateTime'][0])+' to '+str(ds1.series['DateTime'][-1])
    pylab.figtext(0.5,0.96,textstr,horizontalalignment='center')
    textstr = 'Secondary site (x) is '+ds2.globalattributes['SiteName']
    textstr = textstr+' '+str(ds2.series['DateTime'][0])+' to '+str(ds2.series['DateTime'][-1])
    pylab.figtext(0.5,0.94,textstr,horizontalalignment='center')
    NumRows = len(ThisList)
    n = 0
    for ThisOne in ThisList:
        OutFile.write(ThisOne+' regression results'+'\n')
        y,f = qcutils.GetSeriesasMA(ds1,ThisOne)
        x,f = qcutils.GetSeriesasMA(ds2,ThisOne)
        # all points
        n = n + 1
        OutFile.write(' All points'+'\n')
        coefs = numpy.ma.polyfit(x,y,1)
        r = numpy.ma.corrcoef(x,y)
        eqnstr = '  y = %.3fx + %.3f, r = %.3f'%(coefs[0],coefs[1],r[0][1])
        OutFile.write(eqnstr+'\n')
        slope = numpy.ma.sum(x*y)/numpy.ma.sum(x*x)
        r = numpy.ma.corrcoef(slope*x,y)
        eqnstr = '  y = %.3fx, r = %.3f'%(slope,r[0][1])
        OutFile.write(eqnstr+'\n')
        xyplot(x,y,sub=[NumRows,3,n],regr=1,label=ThisOne)
        # daytime only
        n = n + 1
        index = numpy.where((Hdh>=float(8))&(Hdh<=float(18)))
        OutFile.write(' Day'+'\n')
        coefs = numpy.ma.polyfit(x[index],y[index],1)
        r = numpy.ma.corrcoef(x[index],y[index])
        eqnstr = '  y = %.3fx + %.3f, r = %.3f'%(coefs[0],coefs[1],r[0][1])
        OutFile.write(eqnstr+'\n')
        slope = numpy.ma.sum(x[index]*y[index])/numpy.ma.sum(x[index]*x[index])
        r = numpy.ma.corrcoef(slope*x[index],y[index])
        eqnstr = '  y = %.3fx, r = %.3f'%(slope,r[0][1])
        OutFile.write(eqnstr+'\n')
        xyplot(x[index],y[index],sub=[NumRows,3,n],regr=1,label='Day')
        # nighttime only
        n = n + 1
        index = numpy.where((Hdh>float(18))|(Hdh<float(8)))
        OutFile.write(' Night'+'\n')
        coefs = numpy.ma.polyfit(x[index],y[index],1)
        r = numpy.ma.corrcoef(x[index],y[index])
        eqnstr = '  y = %.3fx + %.3f, r = %.3f'%(coefs[0],coefs[1],r[0][1])
        OutFile.write(eqnstr+'\n')
        slope = numpy.ma.sum(x[index]*y[index])/numpy.ma.sum(x[index]*x[index])
        r = numpy.ma.corrcoef(slope*x[index],y[index])
        eqnstr = '  y = %.3fx, r = %.3f'%(slope,r[0][1])
        OutFile.write(eqnstr+'\n')
        xyplot(x[index],y[index],sub=[NumRows,3,n],regr=1,label='Night')
OutFile.close()
pylab.show()
