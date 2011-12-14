import constants as c
import matplotlib.pyplot as plt
import numpy
import qcio
import qcplot
import qcutils

nfig = 0
plotwidth = 10.9
plotheight = 7.5

fname = qcio.get_ncfilename()
ds = qcio.nc_read_series_file(fname)
SiteName = ds.globalattributes['SiteName']
nrecs = int(ds.globalattributes['NumRecs'])
dt = int(ds.globalattributes['TimeStep'])
nperhr = int(float(60)/dt+0.5)
nperday = int(float(24)*nperhr+0.5)
ndays = nrecs/nperday
nrecs=ndays*nperday

# get the datetime series
DateTime = ds.series['DateTime']
# find the start index of the first whole day (time=00:30)
si = 0
while DateTime[si].minute!=30:
    si = si + 1
# find the end index of the last whole day (time=00:00)
ei = len(DateTime) - 1
while DateTime[ei].hour+DateTime[ei].minute!=0:
    ei = ei - 1
DateTime = DateTime[si:ei+1]
#Mnth_30min_1d,flag = qcutils.GetSeriesasMA(ds,'Month',si=0,ei=nrecs-1)
Mnth_30min_1d,flag = qcutils.GetSeriesasMA(ds,'Month',si=si,ei=ei)

#ah_7500_30min_1d,flag = qcutils.GetSeriesasMA(ds,'Ah_7500_Av',si=0,ei=nrecs-1)
#ah_HMP1_30min_1d,flag = qcutils.GetSeriesasMA(ds,'Ah_HMP_01',si=0,ei=nrecs-1)
ah_7500_30min_1d,flag = qcutils.GetSeriesasMA(ds,'Ah_7500_Av',si=si,ei=ei)
ah_HMP1_30min_1d,flag = qcutils.GetSeriesasMA(ds,'Ah_HMP_01',si=si,ei=ei)
ah_7500_30min_2d = numpy.ma.reshape(ah_7500_30min_1d,[ndays,nperday])
ah_HMP1_30min_2d = numpy.ma.reshape(ah_HMP1_30min_1d,[ndays,nperday])

mask = numpy.ma.mask_or(ah_7500_30min_2d.mask,ah_HMP1_30min_2d.mask)  # mask based on dependencies, set all to missing if any missing
ah_7500_30min_2d = numpy.ma.array(ah_7500_30min_2d,mask=mask)         # apply the mask
ah_HMP1_30min_2d = numpy.ma.array(ah_HMP1_30min_2d,mask=mask)

ah_7500_daily_avg = numpy.ma.average(ah_7500_30min_2d,axis=1)
ah_HMP1_daily_avg = numpy.ma.average(ah_HMP1_30min_2d,axis=1)

ah_diff_daily_avg = ah_7500_daily_avg - ah_HMP1_daily_avg

DT_daily = DateTime[0:nrecs:nperday]
Mnth_30min_2d = Mnth_30min_1d.reshape(ndays,nperday)

nfig = nfig + 1
fig = plt.figure(nfig,figsize=(plotwidth,plotheight))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
qcplot.tsplot(DT_daily,ah_7500_daily_avg,sub=[3,1,1],ylabel='Ah_7500')
qcplot.tsplot(DT_daily,ah_HMP1_daily_avg,sub=[3,1,2],ylabel='Ah_HMP_01')
qcplot.tsplot(DT_daily,ah_diff_daily_avg,sub=[3,1,3],ylabel='7500-HMP')

nfig = nfig + 1
fig = plt.figure(nfig,figsize=(plotwidth,plotheight))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
qcplot.xyplot(ah_HMP1_daily_avg,ah_diff_daily_avg,sub=[1,1,1],regr=1,title="Daily Average",xlabel='Ah_HMP (g/m3)',ylabel='7500-HMP (g/m3)')

MnthList = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
nfig = nfig + 1
fig = plt.figure(nfig,figsize=(plotwidth,plotheight))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
j = 0
for i in [12,1,2,3,4,5,6,7,8,9,10,11]:
    j = j + 1
    index = numpy.where(Mnth_30min_1d==i)[0]
    if len(index)!=0:
        x = ah_HMP1_30min_1d[index]
        y = ah_7500_30min_1d[index]
        if j in [1,2,3,4,5,6,7,8,9]:
            xlabel = None
        else:
            xlabel = 'HMP (g/m3)'
        if j in [2,3,5,6,8,9,11,12]:
            ylabel = None
        else:
            ylabel = '7500 (g/m3)'
        qcplot.xyplot(x,y,sub=[4,3,j],regr=1,title=MnthList[i-1],xlabel=xlabel,ylabel=ylabel)

plt.show()
