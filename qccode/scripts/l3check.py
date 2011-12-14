import sys
import math
import matplotlib.pyplot as plt
import matplotlib.dates as mdt
import numpy
import qcio
import qcutils

def xyplot(x,y,sub=[1,1,1],regr=0,title=None,xlabel=None,ylabel=None,fname=None):
    '''Generic XY scatter plot routine'''
    wspace = 0.0
    hspace = 0.0
    plt.subplot(sub[0],sub[1],sub[2])
    plt.plot(x,y,'b.')
    ax = plt.gca()
    if xlabel!=None:
        plt.xlabel(xlabel)
    if ylabel!=None:
        plt.ylabel(ylabel)
        wspace = 0.3
    if title!=None:
        plt.title(title)
        hspace = 0.3
    if regr!=0:
        coefs = numpy.ma.polyfit(x,y,1)
        xfit = numpy.ma.array([numpy.ma.minimum(x),numpy.ma.maximum(x)])
        yfit = numpy.polyval(coefs,xfit)
        r = numpy.ma.corrcoef(x,y)
        eqnstr = 'y = %.3fx + %.3f, r = %.3f'%(coefs[0],coefs[1],r[0][1])
        plt.plot(xfit,yfit,'r--',linewidth=3)
        plt.text(0.5,0.925,eqnstr,fontsize=8,horizontalalignment='center',transform=ax.transAxes)
    plt.subplots_adjust(wspace=wspace,hspace=hspace)

def tsplot(x,y,sub=[1,1,1],title=None,xlabel=None,ylabel=None,colours=None,lineat=None):
    plt.subplot(sub[0],sub[1],sub[2])
    if (y.all() is numpy.ma.masked):
        y = numpy.ma.zeros(len(y))
    if colours!=None:
        plt.scatter(x,y,c=colours)
    else:
        plt.scatter(x,y)
    if lineat!=None:
        plt.plot((x[0],x[-1]),(float(lineat),float(lineat)))
    plt.xlim((x[0],x[-1]))
    ax = plt.gca()
    ax.xaxis.set_major_formatter(MTFmt)
    if title!=None:
        plt.title(title)
    if ylabel!=None:
        ax.yaxis.set_label_text(ylabel)
    if xlabel!=None:
        ax.xaxis.set_label_text(xlabel)

def hrplot(x,y,sub=[1,1,1],title=None,xlabel=None,ylabel=None,colours=None):
    plt.subplot(sub[0],sub[1],sub[2])
    if (y.all() is numpy.ma.masked):
        y = numpy.ma.zeros(len(y))
    if colours!=None:
        plt.scatter(x,y,c=colours)
    else:
        plt.scatter(x,y)
    plt.xlim(0,24)
    plt.xticks([0,6,12,18,24])
    if title!=None:
        plt.title(title)
    if ylabel!=None:
        plt.ylabel(ylabel)
    if xlabel!=None:
        plt.xlabel(xlabel)

PlotWidth = 10.9
PlotHeight = 7.5
PlotWidth_portrait = 7.5
PlotHeight_portrait = 10.9
nFig = 0

# get the netCDF filename
ncfilename = qcio.get_ncfilename()
# read the netCDF file and return the data structure "ds"
ds3 = qcio.nc_read_series_file(ncfilename)
# get the time step
ts = int(ds3.globalattributes['TimeStep'])
# get the site name
SiteName = ds3.globalattributes['SiteName']
# get the datetime series
DateTime = ds3.series['DateTime']
# find the start index of the first whole day (time=00:30)
si = 0
while DateTime[si].minute!=30:
    si = si + 1
# find the end index of the last whole day (time=00:00)
ei = len(DateTime) - 1
while DateTime[ei].hour+DateTime[ei].minute!=0:
    ei = ei - 1
DateTime = DateTime[si:ei+1]
# get the 30 minute data from the data structure
#  radiation first ...
Mnth_30min,flag = qcutils.GetSeriesasMA(ds3,'Month',si=si,ei=ei)
Hour_30min,flag = qcutils.GetSeriesasMA(ds3,'Hour',si=si,ei=ei)
Mnit_30min,flag = qcutils.GetSeriesasMA(ds3,'Minute',si=si,ei=ei)
Fsd_30min,flag = qcutils.GetSeriesasMA(ds3,'Fsd',si=si,ei=ei)
Fsu_30min,flag = qcutils.GetSeriesasMA(ds3,'Fsu',si=si,ei=ei)
Fld_30min,flag = qcutils.GetSeriesasMA(ds3,'Fld',si=si,ei=ei)
Flu_30min,flag = qcutils.GetSeriesasMA(ds3,'Flu',si=si,ei=ei)
Fn_30min,flag = qcutils.GetSeriesasMA(ds3,'Fn',si=si,ei=ei)
#  then fluxes ...
Fa_30min,flag = qcutils.GetSeriesasMA(ds3,'Fa',si=si,ei=ei)
Fe_30min,flag = qcutils.GetSeriesasMA(ds3,'Fe_wpl',si=si,ei=ei)
Fh_30min,flag = qcutils.GetSeriesasMA(ds3,'Fh',si=si,ei=ei)
Fc_30min,flag = qcutils.GetSeriesasMA(ds3,'Fc_wpl',si=si,ei=ei)
Fg_30min,flag = qcutils.GetSeriesasMA(ds3,'Fg',si=si,ei=ei)
us_30min,flag = qcutils.GetSeriesasMA(ds3,'ustar',si=si,ei=ei)
#  then meteorology ...
Ta_30min,flag = qcutils.GetSeriesasMA(ds3,'Ta_EC',si=si,ei=ei)
Ah_30min,flag = qcutils.GetSeriesasMA(ds3,'Ah_EC',si=si,ei=ei)
Cc_30min,flag = qcutils.GetSeriesasMA(ds3,'Cc_7500_Av',si=si,ei=ei)
Rain_30min,flag = qcutils.GetSeriesasMA(ds3,'Rain',si=si,ei=ei)
Ws_30min,flag = qcutils.GetSeriesasMA(ds3,'Ws_CSAT',si=si,ei=ei)
#  then soil ...
Sws_30min,flag = qcutils.GetSeriesasMA(ds3,'Sws_01',si=si,ei=ei)
Ts_30min,flag = qcutils.GetSeriesasMA(ds3,'Ts_01',si=si,ei=ei)

# *** start of section based on 30 minute data ***
# scatter plot of (Fh+Fe) versys Fa, all data
mask = numpy.ma.mask_or(Fa_30min.mask,Fe_30min.mask)
mask = numpy.ma.mask_or(mask,Fh_30min.mask)
Fa_SEB = numpy.ma.array(Fa_30min,mask=mask)     # apply the mask
FhpFe_SEB = numpy.ma.array(Fh_30min,mask=mask) + numpy.ma.array(Fe_30min,mask=mask)
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(8,8))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
xyplot(Fa_SEB,FhpFe_SEB,sub=[2,2,1],regr=1,title="All hours",xlabel='Fa (W/m2)',ylabel='Fh+Fe (W/m2)')
# scatter plot of (Fh+Fe) versus Fa, day time
Fa_day = numpy.ma.masked_where(Fsd_30min<10,Fa_30min)
Fe_day = numpy.ma.masked_where(Fsd_30min<10,Fe_30min)
Fh_day = numpy.ma.masked_where(Fsd_30min<10,Fh_30min)
mask = numpy.ma.mask_or(Fa_day.mask,Fe_day.mask)
mask = numpy.ma.mask_or(mask,Fh_day.mask)
Fa_day = numpy.ma.array(Fa_day,mask=mask)         # apply the mask
Fe_day = numpy.ma.array(Fe_day,mask=mask)
Fh_day = numpy.ma.array(Fh_day,mask=mask)
FhpFe_day = Fh_day + Fe_day
xyplot(Fa_day,FhpFe_day,sub=[2,2,2],regr=1,title="Day",xlabel='Fa (W/m2)',ylabel='Fh+Fe (W/m2)')
# scatter plot of (Fh+Fe) versus Fa, night time
Fa_night = numpy.ma.masked_where(Fsd_30min>10,Fa_30min)
Fe_night = numpy.ma.masked_where(Fsd_30min>10,Fe_30min)
Fh_night = numpy.ma.masked_where(Fsd_30min>10,Fh_30min)
mask = numpy.ma.mask_or(Fa_night.mask,Fe_night.mask)
mask = numpy.ma.mask_or(mask,Fh_night.mask)
Fa_night = numpy.ma.array(Fa_night,mask=mask)         # apply the mask
Fe_night = numpy.ma.array(Fe_night,mask=mask)
Fh_night = numpy.ma.array(Fh_night,mask=mask)
FhpFe_night = Fh_night + Fe_night
xyplot(Fa_night,FhpFe_night,sub=[2,2,3],regr=1,title="Night",xlabel='Fa (W/m2)',ylabel='Fh+Fe (W/m2)')
# scatter plot of Fc versus ustar, night time
Fc_night = numpy.ma.masked_where(Fsd_30min>10,Fc_30min)
us_night = numpy.ma.masked_where(Fsd_30min>10,us_30min)
mask = numpy.ma.mask_or(Fc_night.mask,us_night.mask)
Fc_night = numpy.ma.array(Fc_night,mask=mask)         # apply the mask
us_night = numpy.ma.array(us_night,mask=mask)
xyplot(us_night,Fc_night,sub=[2,2,4],title="Night",xlabel='u* (m/s)',ylabel='Fc (mg/m2/s)')
#fig.show()
fig.savefig('../plots/SEB_30minutes.png',format='png')

# *** start of section based on daily averages ***
MTFmt = mdt.DateFormatter('%m/%Y')
# get the number of days in the data set
ntsInDay = float(24.0*60.0/float(ts))
if math.modf(ntsInDay)[0]!=0:
    print 'qccheck_l3: Time step is not a sub-multiple of 60 minutes ', ts
    sys.exit
ntsInDay = int(ntsInDay)
nDays = float(len(DateTime))/ntsInDay
if math.modf(nDays)[0]!=0:
    print 'qccheck_l3: Not a whole number of days ', nDays
    sys.exit
nDays = int(nDays)
# reshape the 1D array of 30 minute data into a 2D array of (nDays,ntsInDay)
DT_daily = DateTime[0::ntsInDay]
Mnth_daily = Mnth_30min.reshape(nDays,ntsInDay)
Hour_daily = Hour_30min.reshape(nDays,ntsInDay)
Mnit_daily = Mnit_30min.reshape(nDays,ntsInDay)
Fsd_daily = Fsd_30min.reshape(nDays,ntsInDay)
Fn_daily = Fn_30min.reshape(nDays,ntsInDay)
Fa_daily = Fa_30min.reshape(nDays,ntsInDay)
Fe_daily = Fe_30min.reshape(nDays,ntsInDay)
Fh_daily = Fh_30min.reshape(nDays,ntsInDay)
Fc_daily = Fc_30min.reshape(nDays,ntsInDay)
Rain_daily = Rain_30min.reshape(nDays,ntsInDay)
Sws_daily = Sws_30min.reshape(nDays,ntsInDay)
Ts_daily = Ts_30min.reshape(nDays,ntsInDay)
us_daily = us_30min.reshape(nDays,ntsInDay)

# get the SEB ratio
# get the daytime data, defined by Fsd>10 W/m2
Fa_day = numpy.ma.masked_where(Fsd_daily<10,Fa_daily)
Fe_day = numpy.ma.masked_where(Fsd_daily<10,Fe_daily)
Fh_day = numpy.ma.masked_where(Fsd_daily<10,Fh_daily)
mask = numpy.ma.mask_or(Fa_day.mask,Fe_day.mask)  # mask based on dependencies, set all to missing if any missing
mask = numpy.ma.mask_or(mask,Fh_day.mask)
Fa_day = numpy.ma.array(Fa_day,mask=mask)         # apply the mask
Fe_day = numpy.ma.array(Fe_day,mask=mask)
Fh_day = numpy.ma.array(Fh_day,mask=mask)
Fa_day_avg = numpy.ma.average(Fa_day,axis=1)      # get the daily average
Fe_day_avg = numpy.ma.average(Fe_day,axis=1)
Fh_day_avg = numpy.ma.average(Fh_day,axis=1)      # get the number of values in the daily average
SEB_day_num = numpy.ma.count(Fh_day,axis=1)       # get the SEB ratio
SEB_day_avg = (Fe_day_avg+Fh_day_avg)/Fa_day_avg
SEB_day_avg = numpy.ma.masked_where(SEB_day_num<=5,SEB_day_avg)
index = numpy.ma.where(SEB_day_avg.mask==True)
SEB_day_num[index] = 0

# get the EF
# get the daytime data, defined by Fsd>10 W/m2
Fa_day = numpy.ma.masked_where(Fsd_daily<10,Fa_daily)
Fe_day = numpy.ma.masked_where(Fsd_daily<10,Fe_daily)
mask = numpy.ma.mask_or(Fa_day.mask,Fe_day.mask)  # mask based on dependencies, set all to missing if any missing
Fa_day = numpy.ma.array(Fa_day,mask=mask)         # apply the mask
Fe_day = numpy.ma.array(Fe_day,mask=mask)
Fa_day_avg = numpy.ma.average(Fa_day,axis=1)      # get the daily average
Fe_day_avg = numpy.ma.average(Fe_day,axis=1)
EF_day_num = numpy.ma.count(Fe_day,axis=1)        # get the number of values in the daily average
EF_day_avg = Fe_day_avg/Fa_day_avg                # get the EF ratio
EF_day_avg = numpy.ma.masked_where(EF_day_num<=5,EF_day_avg)
index = numpy.ma.where(EF_day_avg.mask==True)
EF_day_num[index] = 0

# get the BR
# get the daytime data, defined by Fsd>10 W/m2
Fe_day = numpy.ma.masked_where(Fsd_daily<10,Fe_daily)
Fh_day = numpy.ma.masked_where(Fsd_daily<10,Fh_daily)
mask = numpy.ma.mask_or(Fe_day.mask,Fh_day.mask)  # mask based on dependencies, set all to missing if any missing
Fe_day = numpy.ma.array(Fe_day,mask=mask)         # apply the mask
Fh_day = numpy.ma.array(Fh_day,mask=mask)
Fe_day_avg = numpy.ma.average(Fe_day,axis=1)      # get the daily average
Fh_day_avg = numpy.ma.average(Fh_day,axis=1)
BR_day_num = numpy.ma.count(Fh_day,axis=1)        # get the number of values in the daily average
BR_day_avg = Fh_day_avg/Fe_day_avg                # get the BR ratio
BR_day_avg = numpy.ma.masked_where(BR_day_num<=5,BR_day_avg)
index = numpy.ma.where(BR_day_avg.mask==True)
BR_day_num[index] = 0

# get the Wue
# get the daytime data, defined by Fsd>10 W/m2
Fe_day = numpy.ma.masked_where(Fsd_daily<10,Fe_daily)
Fc_day = numpy.ma.masked_where(Fsd_daily<10,Fc_daily)
mask = numpy.ma.mask_or(Fe_day.mask,Fc_day.mask)  # mask based on dependencies, set all to missing if any missing
Fe_day = numpy.ma.array(Fe_day,mask=mask)         # apply the mask
Fc_day = numpy.ma.array(Fc_day,mask=mask)
Fe_day_avg = numpy.ma.average(Fe_day,axis=1)      # get the daily average
Fc_day_avg = numpy.ma.average(Fc_day,axis=1)
WUE_day_num = numpy.ma.count(Fc_day,axis=1)       # get the number of values in the daily average
WUE_day_avg = Fc_day_avg/Fe_day_avg
WUE_day_avg = numpy.ma.masked_where(WUE_day_num<=5,WUE_day_avg)
index = numpy.ma.where(WUE_day_avg.mask==True)
WUE_day_num[index] = 0
# get the soil moisture
Sws_daily_avg = numpy.ma.average(Sws_daily,axis=1)
Sws_daily_num = numpy.ma.count(Sws_daily,axis=1)
# get the rainfall
Rain_daily_sum = numpy.ma.sum(Rain_daily,axis=1)
Rain_daily_num = numpy.ma.count(Rain_daily,axis=1)
# plot the SEB, EF and Wue
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth,PlotHeight))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
tsplot(DT_daily,SEB_day_avg,sub=[6,1,1],colours=SEB_day_num,ylabel='(Fh+Fe)/Fa',lineat=1)
tsplot(DT_daily,EF_day_avg,sub=[6,1,2],colours=EF_day_num,ylabel='EF=Fe/Fa')
tsplot(DT_daily,BR_day_avg,sub=[6,1,3],colours=BR_day_num,ylabel='BR=Fe/Fh')
tsplot(DT_daily,WUE_day_avg,sub=[6,1,4],colours=WUE_day_num,ylabel='WUE=Fc/Fe',lineat=0)
tsplot(DT_daily,Sws_daily_avg,sub=[6,1,5],colours=Sws_daily_num,ylabel='Sws')
tsplot(DT_daily,Rain_daily_sum,sub=[6,1,6],colours=Rain_daily_num,ylabel='Rain')
#fig.show()
fig.savefig('../plots/DailyRatios.png',format='png')

# now we do the daily averages of the fluxes and the meteorology
# get the 1D array of 30 minute data into a 2D array with a dimension for
#  the day number and a dimension for the time of day
Fsd_daily = Fsd_30min.reshape(nDays,ntsInDay)
Fa_daily = Fa_30min.reshape(nDays,ntsInDay)
Fe_daily = Fe_30min.reshape(nDays,ntsInDay)
Fh_daily = Fh_30min.reshape(nDays,ntsInDay)
Fc_daily = Fc_30min.reshape(nDays,ntsInDay)
# ... then get the day time values only (defined by Fsd>10 W/m2)
Fsd_day = numpy.ma.masked_where(Fsd_daily<10,Fsd_daily)
Fa_day = numpy.ma.masked_where(Fsd_daily<10,Fa_daily)
Fe_day = numpy.ma.masked_where(Fsd_daily<10,Fe_daily)
Fh_day = numpy.ma.masked_where(Fsd_daily<10,Fh_daily)
Fc_day = numpy.ma.masked_where(Fsd_daily<10,Fc_daily)
Fc_night = numpy.ma.masked_where(Fsd_daily>=10,Fc_daily)
# ... then get the daily averages
Fsd_day_avg = numpy.ma.average(Fsd_day,axis=1)      # get the daily average
Fa_day_avg = numpy.ma.average(Fa_day,axis=1)      # get the daily average
Fe_day_avg = numpy.ma.average(Fe_day,axis=1)      # get the daily average
Fh_day_avg = numpy.ma.average(Fh_day,axis=1)      # get the daily average
Fc_day_avg = numpy.ma.average(Fc_day,axis=1)      # get the daily average
Fc_night_avg = numpy.ma.average(Fc_night,axis=1)      # get the daily average
# ... then the number of values in each day time block
Fsd_day_num = numpy.ma.count(Fsd_day,axis=1)
Fa_day_num = numpy.ma.count(Fa_day,axis=1)
Fe_day_num = numpy.ma.count(Fe_day,axis=1)
Fh_day_num = numpy.ma.count(Fh_day,axis=1)
Fc_day_num = numpy.ma.count(Fc_day,axis=1)
Fc_night_num = numpy.ma.count(Fc_night,axis=1)
# ... now plot the day time averages with the colour of the points controlled
#     by the number of values used to get the average
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth,PlotHeight))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
tsplot(DT_daily,Fsd_day_avg,sub=[5,1,1],colours=Fsd_day_num,ylabel='Fsd (W/m2)')
tsplot(DT_daily,Fa_day_avg,sub=[5,1,2],colours=Fa_day_num,ylabel='Fa (W/m2)')
tsplot(DT_daily,Fe_day_avg,sub=[5,1,3],colours=Fe_day_num,ylabel='Fe (W/m2)')
tsplot(DT_daily,Fh_day_avg,sub=[5,1,4],colours=Fh_day_num,ylabel='Fh (W/m2)')
tsplot(DT_daily,Fc_day_avg,sub=[5,1,5],colours=Fc_day_num,ylabel='Fc (mg/m2/s)',lineat=0)
#fig.show()
fig.savefig('../plots/DailyRadn&Fluxes.png',format='png')

Ta_daily = Ta_30min.reshape(nDays,ntsInDay)
Ah_daily = Ah_30min.reshape(nDays,ntsInDay)
Cc_daily = Cc_30min.reshape(nDays,ntsInDay)
Ws_daily = Ws_30min.reshape(nDays,ntsInDay)
Cc_day = numpy.ma.masked_where(Fsd_daily<10,Cc_daily)
Ta_daily_avg = numpy.ma.average(Ta_daily,axis=1)      # get the daily average
Ta_daily_num = numpy.ma.count(Ta_daily,axis=1)
Ah_daily_avg = numpy.ma.average(Ah_daily,axis=1)      # get the daily average
Ah_daily_num = numpy.ma.count(Ah_daily,axis=1)
Cc_day_avg = numpy.ma.average(Cc_day,axis=1)          # get the daily average
Cc_day_num = numpy.ma.count(Cc_day,axis=1)
Ws_daily_avg = numpy.ma.average(Ws_daily,axis=1)      # get the daily average
Ws_daily_num = numpy.ma.count(Ws_daily,axis=1)
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth,PlotHeight))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
tsplot(DT_daily,Ta_daily_avg,sub=[5,1,1],colours=Ta_daily_num,ylabel='Ta (C)')
tsplot(DT_daily,Ah_daily_avg,sub=[5,1,2],colours=Ah_daily_num,ylabel='Ah (g/m3)')
tsplot(DT_daily,Cc_day_avg,sub=[5,1,3],colours=Cc_day_num,ylabel='CO2 (mg/m3)')
tsplot(DT_daily,Ws_daily_avg,sub=[5,1,4],colours=Ws_daily_num,ylabel='WS (m/s)')
tsplot(DT_daily,Rain_daily_sum,sub=[5,1,5],colours=Rain_daily_num,ylabel='Rain (mm)')
#fig.show()
fig.savefig('../plots/DailyMet.png',format='png')

# ... now do the nocturnal Fc and assorted drivers
# get the soil temperature
#Ts_daily_avg = numpy.ma.average(Ts_daily,axis=1)
#Ts_daily_num = numpy.ma.count(Ts_daily,axis=1)
Ts_day = numpy.ma.masked_where(Fsd_daily<10,Ts_daily)
Ts_night = numpy.ma.masked_where(Fsd_daily>=10,Ts_daily)
Ts_day_avg = numpy.ma.average(Ts_day,axis=1)          # get the daily average
Ts_day_num = numpy.ma.count(Ts_day,axis=1)
Ts_night_avg = numpy.ma.average(Ts_night,axis=1)          # get the daily average
Ts_night_num = numpy.ma.count(Ts_night,axis=1)
us_night = numpy.ma.masked_where(Fsd_daily>=10,us_daily)
us_night_avg = numpy.ma.average(us_night,axis=1)          # get the daily average
us_night_num = numpy.ma.count(us_night,axis=1)
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth,PlotHeight))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
tsplot(DT_daily,Fc_night_avg,sub=[6,1,1],colours=Fc_night_num,ylabel='Fc (mg/m2/s)')
tsplot(DT_daily,us_night_avg,sub=[6,1,2],colours=us_night_num,ylabel='us (night, m/s)')
tsplot(DT_daily,Ts_day_avg,sub=[6,1,3],colours=Ts_day_num,ylabel='Ts (day, C)')
tsplot(DT_daily,Ts_night_avg,sub=[6,1,4],colours=Ts_night_num,ylabel='Ts (night, C)')
tsplot(DT_daily,Sws_daily_avg,sub=[6,1,5],colours=Sws_daily_num,ylabel='Sws (%)')
tsplot(DT_daily,Rain_daily_sum,sub=[6,1,6],colours=Rain_daily_num,ylabel='Rain (mm)')
#fig.show()
fig.savefig('../plots/DailyFc&Drivers.png',format='png')

MnthList = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
# plot Fsd
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth_portrait,PlotHeight_portrait))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
j = 0
for i in [12,1,2,3,4,5,6,7,8,9,10,11]:
    j = j + 1
    index = numpy.where(Mnth_daily==i)[0]
    if len(index)!=0:
        hr = Hour_daily[index]+Mnit_daily[index]/float(60)
        Fsd_hr_avg = numpy.ma.average(Fsd_daily[index],axis=0)
        Fsd_hr_num = numpy.ma.count(Fsd_daily[index],axis=0)
        if j in [1,2,3,4,5,6,7,8,9]:
            xlabel = None
        else:
            xlabel = 'Hour'
        if j in [2,3,5,6,8,9,11,12]:
            ylabel = None
        else:
            ylabel = 'Fsd (W/m2)'
        hrplot(hr[0],Fsd_hr_avg,sub=[4,3,j],
               title=MnthList[i-1],xlabel=xlabel,ylabel=ylabel,
               colours=Fsd_hr_num)
#fig.show()
fig.savefig('../plots/DiurnalFsdByMonth.png',format='png')

# plot Fa
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth_portrait,PlotHeight_portrait))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
j = 0
for i in [12,1,2,3,4,5,6,7,8,9,10,11]:
    j = j + 1
    index = numpy.where(Mnth_daily==i)[0]
    if len(index)!=0:
        hr = Hour_daily[index]+Mnit_daily[index]/float(60)
        Fa_hr_avg = numpy.ma.average(Fa_daily[index],axis=0)
        Fa_hr_num = numpy.ma.count(Fa_daily[index],axis=0)
        if j in [1,2,3,4,5,6,7,8,9]:
            xlabel = None
        else:
            xlabel = 'Hour'
        if j in [2,3,5,6,8,9,11,12]:
            ylabel = None
        else:
            ylabel = 'Fa (W/m2)'
        hrplot(hr[0],Fa_hr_avg,sub=[4,3,j],
               title=MnthList[i-1],xlabel=xlabel,ylabel=ylabel,
               colours=Fa_hr_num)
#fig.show()
fig.savefig('../plots/DiurnalFaByMonth.png',format='png')

# plot Fn
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth_portrait,PlotHeight_portrait))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
j = 0
for i in [12,1,2,3,4,5,6,7,8,9,10,11]:
    j = j + 1
    index = numpy.where(Mnth_daily==i)[0]
    if len(index)!=0:
        hr = Hour_daily[index]+Mnit_daily[index]/float(60)
        Fn_hr_avg = numpy.ma.average(Fn_daily[index],axis=0)
        Fn_hr_num = numpy.ma.count(Fn_daily[index],axis=0)
        if j in [1,2,3,4,5,6,7,8,9]:
            xlabel = None
        else:
            xlabel = 'Hour'
        if j in [2,3,5,6,8,9,11,12]:
            ylabel = None
        else:
            ylabel = 'Fn (W/m2)'
        hrplot(hr[0],Fn_hr_avg,sub=[4,3,j],
               title=MnthList[i-1],xlabel=xlabel,ylabel=ylabel,
               colours=Fn_hr_num)
#fig.show()
fig.savefig('../plots/DiurnalFnByMonth.png',format='png')

# plot Fh
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth_portrait,PlotHeight_portrait))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
j = 0
for i in [12,1,2,3,4,5,6,7,8,9,10,11]:
    j = j + 1
    index = numpy.where(Mnth_daily==i)[0]
    if len(index)!=0:
        hr = Hour_daily[index]+Mnit_daily[index]/float(60)
        Fh_hr_avg = numpy.ma.average(Fh_daily[index],axis=0)
        Fh_hr_num = numpy.ma.count(Fh_daily[index],axis=0)
        if j in [1,2,3,4,5,6,7,8,9]:
            xlabel = None
        else:
            xlabel = 'Hour'
        if j in [2,3,5,6,8,9,11,12]:
            ylabel = None
        else:
            ylabel = 'Fh (W/m2)'
        hrplot(hr[0],Fh_hr_avg,sub=[4,3,j],
               title=MnthList[i-1],xlabel=xlabel,ylabel=ylabel,
               colours=Fh_hr_num)
#fig.show()
fig.savefig('../plots/DiurnalFhByMonth.png',format='png')

# plot Fe
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth_portrait,PlotHeight_portrait))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
j = 0
for i in [12,1,2,3,4,5,6,7,8,9,10,11]:
    j = j + 1
    index = numpy.where(Mnth_daily==i)[0]
    if len(index)!=0:
        hr = Hour_daily[index]+Mnit_daily[index]/float(60)
        Fe_hr_avg = numpy.ma.average(Fe_daily[index],axis=0)
        Fe_hr_num = numpy.ma.count(Fe_daily[index],axis=0)
        if j in [1,2,3,4,5,6,7,8,9]:
            xlabel = None
        else:
            xlabel = 'Hour'
        if j in [2,3,5,6,8,9,11,12]:
            ylabel = None
        else:
            ylabel = 'Fe (W/m2)'
        hrplot(hr[0],Fe_hr_avg,sub=[4,3,j],
               title=MnthList[i-1],xlabel=xlabel,ylabel=ylabel,
               colours=Fe_hr_num)
#fig.show()
fig.savefig('../plots/DiurnalFeByMonth.png',format='png')

# plot Fc
nFig = nFig + 1
fig = plt.figure(nFig,figsize=(PlotWidth_portrait,PlotHeight_portrait))
plt.figtext(0.5,0.95,SiteName,horizontalalignment='center',size=16)
j = 0
for i in [12,1,2,3,4,5,6,7,8,9,10,11]:
    j = j + 1
    index = numpy.where(Mnth_daily==i)[0]
    if len(index)!=0:
        hr = Hour_daily[index]+Mnit_daily[index]/float(60)
        Fc_hr_avg = numpy.ma.average(Fc_daily[index],axis=0)
        Fc_hr_num = numpy.ma.count(Fc_daily[index],axis=0)
        if j in [1,2,3,4,5,6,7,8,9]:
            xlabel = None
        else:
            xlabel = 'Hour'
        if j in [2,3,5,6,8,9,11,12]:
            ylabel = None
        else:
            ylabel = 'Fc (mg/m2/s)'
        hrplot(hr[0],Fc_hr_avg,sub=[4,3,j],
               title=MnthList[i-1],xlabel=xlabel,ylabel=ylabel,
               colours=Fc_hr_num)
#fig.show()
fig.savefig('../plots/DiurnalFcByMonth.png',format='png')

plt.show()