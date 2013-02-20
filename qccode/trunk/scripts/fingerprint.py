import sys
sys.path.append("../Common/")
import constants as c
import matplotlib.pyplot as plt
import numpy
import qcio

ncfilename = qcio.get_ncfilename(path='../../../TERN/Sites')
ds = qcio.nc_read_series_file(ncfilename)

TitleStr = ds.globalattributes['SiteName']+' '+ds.globalattributes['Level']
TitleStr = TitleStr+' from '+str(ds.series['DateTime'][0])+' to '+str(ds.series['DateTime'][-1])
nRecsInFile = int(ds.globalattributes['NumRecs'])
dt = int(ds.globalattributes['TimeStep'])
nPerHr = int(float(60)/dt+0.5)
nPerDay = int(float(24)*nPerHr+0.5)
#nDays = int(float(nRecsInFile)/float(nPerDay)+0.5)
nDays = nRecsInFile/nPerDay
nRecs = nDays*nPerDay

ta = numpy.reshape(ds.series['Ta_EC']['Data'][0:nRecs],[nDays,nPerDay])
ta = numpy.ma.masked_where(abs(ta-float(-9999))<c.eps,ta)
ah = numpy.reshape(ds.series['Ah_EC']['Data'][0:nRecs],[nDays,nPerDay])
ah = numpy.ma.masked_where(abs(ah-float(-9999))<c.eps,ah)
cc = numpy.reshape(ds.series['Cc_7500_Av']['Data'][0:nRecs],[nDays,nPerDay])
cc = numpy.ma.masked_where(abs(cc-float(-9999))<c.eps,cc)
ws = numpy.reshape(ds.series['Ws_CSAT']['Data'][0:nRecs],[nDays,nPerDay])
ws = numpy.ma.masked_where(abs(ws-float(-9999))<c.eps,ws)
fig = plt.figure(1,figsize=[10,8])
plt.figtext(0.5,0.95,TitleStr,horizontalalignment='center')
ta_ax = fig.add_axes([0.05,0.1,0.2,0.8])
cax = ta_ax.imshow(ta,extent=[0,24,1,nDays],aspect=0.3,origin='lower')
plt.xticks([0,6,12,18,24])
cbar = fig.colorbar(cax)
plt.figtext(0.125,0.92,'Ta_EC (C)',horizontalalignment='center')
ah_ax = fig.add_axes([0.275,0.1,0.2,0.8])
cax = ah_ax.imshow(ah,extent=[0,24,1,nDays],aspect=0.3,origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(ah_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.375,0.92,'Ah_EC (g/m3)',horizontalalignment='center')
cc_ax = fig.add_axes([0.5,0.1,0.2,0.8])
cax = cc_ax.imshow(cc,extent=[0,24,1,nDays],aspect=0.3,origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(cc_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.6,0.92,'CO2 (mg/m3)',horizontalalignment='center')
ws_ax = fig.add_axes([0.725,0.1,0.2,0.8])
cax = ws_ax.imshow(ws,extent=[0,24,1,nDays],aspect=0.3,origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(ws_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.825,0.92,'Ws_CSAT (m/s)',horizontalalignment='center')
fig.savefig('../plots/MetFingerprint.png',format='png')
plt.draw()

fh = numpy.reshape(ds.series['Fh']['Data'][0:nRecs],[nDays,nPerDay])
fh = numpy.ma.masked_where(abs(fh-float(-9999))<c.eps,fh)
fe = numpy.reshape(ds.series['Fe_wpl']['Data'][0:nRecs],[nDays,nPerDay])
fe = numpy.ma.masked_where(abs(fe-float(-9999))<c.eps,fe)
fc = numpy.reshape(ds.series['Fc_wpl']['Data'][0:nRecs],[nDays,nPerDay])
fc = numpy.ma.masked_where(abs(fc-float(-9999))<c.eps,fc)
us = numpy.reshape(ds.series['ustar']['Data'][0:nRecs],[nDays,nPerDay])
us = numpy.ma.masked_where(abs(us-float(-9999))<c.eps,us)
fig = plt.figure(2,figsize=[10,8])
plt.figtext(0.5,0.95,TitleStr,horizontalalignment='center')
fh_ax = fig.add_axes([0.05,0.1,0.2,0.8])
cax = fh_ax.imshow(fh,extent=[0,24,1,nDays],aspect=0.3,cmap='hsv',origin='lower')
plt.xticks([0,6,12,18,24])
cbar = fig.colorbar(cax)
plt.figtext(0.125,0.92,'Fh (W/m2)',horizontalalignment='center')
fe_ax = fig.add_axes([0.275,0.1,0.2,0.8])
cax = fe_ax.imshow(fe,extent=[0,24,1,nDays],aspect=0.3,cmap='hsv',origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(fe_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.375,0.92,'Fe_wpl (W/m2)',horizontalalignment='center')
fc_ax = fig.add_axes([0.5,0.1,0.2,0.8])
cax = fc_ax.imshow(fc,extent=[0,24,1,nDays],aspect=0.3,cmap='hsv',origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(fc_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.6,0.92,'Fc_wpl (mg/m2/s)',horizontalalignment='center')
us_ax = fig.add_axes([0.725,0.1,0.2,0.8])
cax = us_ax.imshow(us,extent=[0,24,1,nDays],aspect=0.3,cmap='hsv',origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(us_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.825,0.92,'ustar (m/s)',horizontalalignment='center')
fig.savefig('../plots/FluxFingerprint.png',format='png')
plt.draw()

fsd = numpy.reshape(ds.series['Fsd']['Data'][0:nRecs],[nDays,nPerDay])
fsd = numpy.ma.masked_where(abs(fsd-float(-9999))<c.eps,fsd)
fld = numpy.reshape(ds.series['Fld']['Data'][0:nRecs],[nDays,nPerDay])
fld = numpy.ma.masked_where(abs(fld-float(-9999))<c.eps,fld)
fn = numpy.reshape(ds.series['Fn']['Data'][0:nRecs],[nDays,nPerDay])
fn = numpy.ma.masked_where(abs(fn-float(-9999))<c.eps,fn)
fg = numpy.reshape(ds.series['Fg']['Data'][0:nRecs],[nDays,nPerDay])
fg = numpy.ma.masked_where(abs(fg-float(-9999))<c.eps,fg)
fig = plt.figure(3,figsize=[10,8])
plt.figtext(0.5,0.95,TitleStr,horizontalalignment='center')
fsd_ax = fig.add_axes([0.05,0.1,0.2,0.8])
cax = fsd_ax.imshow(fsd,extent=[0,24,1,nDays],aspect=0.3,cmap='hsv',origin='lower')
plt.xticks([0,6,12,18,24])
cbar = fig.colorbar(cax)
plt.figtext(0.125,0.92,'Fsd (W/m2)',horizontalalignment='center')
fld_ax = fig.add_axes([0.275,0.1,0.2,0.8])
cax = fld_ax.imshow(fld,extent=[0,24,1,nDays],aspect=0.3,cmap='hsv',origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(fld_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.375,0.92,'Fld (W/m2)',horizontalalignment='center')
fn_ax = fig.add_axes([0.5,0.1,0.2,0.8])
cax = fn_ax.imshow(fn,extent=[0,24,1,nDays],aspect=0.3,cmap='hsv',origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(fn_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.6,0.92,'Fn (W/m2)',horizontalalignment='center')
fg_ax = fig.add_axes([0.725,0.1,0.2,0.8])
cax = fg_ax.imshow(fg,extent=[0,24,1,nDays],aspect=0.3,cmap='hsv',origin='lower')
plt.xticks([0,6,12,18,24])
plt.setp(fg_ax.get_yticklabels(), visible=False)
cbar = fig.colorbar(cax)
plt.figtext(0.825,0.92,'Fg (W/m2)',horizontalalignment='center')
fig.savefig('../plots/RadnFingerprint.png',format='png')
plt.draw()

plt.show()

print 'FingerPrint: All Done'