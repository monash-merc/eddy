import ast
import constants as c
import time
import matplotlib.dates as mdt
import matplotlib.pyplot as plt
import numpy
import qcutils
import logging

log = logging.getLogger('qc.plot')

def plottimeseries(cf,nFig,SeriesList,dsa,dsb,si,ei):
    SiteName = dsa.globalattributes['SiteName']
    PlotDescription = cf['Plots'][str(nFig)]['Title']
    dt = int(dsa.globalattributes['TimeStep'])
    if qcutils.cfkeycheck(cf,'PlotSpec','Width'):
        PlotWidth = ast.literal_eval(cf['PlotSpec']['Width'])
        PlotHeight = ast.literal_eval(cf['PlotSpec']['Height'])
    else:
        PlotWidth = 13
        PlotHeight = 9
    ts_YAxOrg = 0.08
    ts_XAxOrg = 0.06
    ts_XAxLen = 0.6
    hr_XAxLen = 0.1
    Month = dsa.series['Month']['Data'][0]
    print time.strftime('%X')+' Plotting series: ',SeriesList
    nGraphs = len(SeriesList)
    ts_YAxLen = (0.85 - (nGraphs - 1)*0.02)/nGraphs
    yaxOrgOffset = (0.85 - ts_YAxLen)/(nGraphs - 1)
    hr1_XAxOrg = ts_XAxOrg+ts_XAxLen+0.07
    hr1_XAxLen = hr_XAxLen
    hr2_XAxOrg = hr1_XAxOrg+hr1_XAxLen+0.05
    hr2_XAxLen = hr_XAxLen
    bar_XAxOrg = hr1_XAxOrg+hr1_XAxLen+0.05+hr1_XAxLen+0.05
    bar_XAxLen = hr_XAxLen
    L1XArray = numpy.array(dsa.series['DateTime']['Data'][si:ei])
    L2XArray = numpy.array(dsb.series['DateTime']['Data'][si:ei])
    XAxMin = min(L2XArray)
    XAxMax = max(L2XArray)
    loc,fmt = get_ticks(XAxMin,XAxMax)
    plt.ioff()
    fig = plt.figure(int(nFig),figsize=(PlotWidth,PlotHeight))
    fig.clf()
    plt.figtext(0.5,0.95,SiteName+': '+PlotDescription,ha='center',size=16)
    print time.strftime('%X')+' Generating the plot'
    for ThisOne, n in zip(SeriesList,range(nGraphs)):
        if ThisOne in dsa.series.keys() and ThisOne in dsb.series.keys():
            aflag = dsa.series[ThisOne]['Flag']
            bflag = dsb.series[ThisOne]['Flag']
            Units = dsa.series[ThisOne]['Attr']['Units']
            YAxOrg = ts_YAxOrg + n*yaxOrgOffset
            L1YArray = numpy.ma.masked_where(dsa.series[ThisOne]['Data'][si:ei]==-9999,
                                             dsa.series[ThisOne]['Data'][si:ei])
            nRecs = numpy.ma.size(L1YArray)
            nNotM = numpy.ma.count(L1YArray)
            nMskd = numpy.ma.count_masked(L1YArray)
            if numpy.ma.count(L1YArray)==0:
                L1YArray = numpy.ma.zeros(numpy.size(L1YArray))
            # check the control file to see if the Y ax1s minima have been specified
            if 'YLMin' in cf['Plots'][str(nFig)]:                               # Y axis minima specified
                minlist = ast.literal_eval(cf['Plots'][str(nFig)]['YLMin'])     # Evaluate the minima list
                log.info(minlist)
                if str(minlist[SeriesList.index(ThisOne)])=='Auto':             # This entry is 'Auto' ...
                    LYAxMin = numpy.ma.minimum(L1YArray)                       # ... so take the array minimum value
                else:
                    LYAxMin = float(minlist[SeriesList.index(ThisOne)])        # Evaluate the entry for this series
            else:
                LYAxMin = numpy.ma.minimum(L1YArray)                           # Y axis minima not given, use auto
            # now do the same for the Y axis maxima
            if 'YLMax' in cf['Plots'][str(nFig)]:
                maxlist = ast.literal_eval(cf['Plots'][str(nFig)]['YLMax'])
                if str(maxlist[SeriesList.index(ThisOne)])=='Auto':
                    LYAxMax = numpy.ma.maximum(L1YArray)
                else:
                    LYAxMax = float(maxlist[SeriesList.index(ThisOne)])
            else:
                LYAxMax = numpy.ma.maximum(L1YArray)
            ts_axL = fig.add_axes([ts_XAxOrg, YAxOrg, ts_XAxLen, ts_YAxLen])
            ts_axL.hold(False)
            ts_axL.plot(L1XArray, L1YArray, 'b-')
            ts_axL.xaxis.set_major_locator(loc)
            ts_axL.xaxis.set_major_formatter(fmt)
            ts_axL.set_xlim(XAxMin,XAxMax)
            ts_axL.set_ylim(LYAxMin,LYAxMax)
            if n==0:
                ts_axL.set_xlabel('Date',visible=True)
            else:
                ts_axL.set_xlabel('',visible=False)
            TextStr = ThisOne+'('+Units+')'+str(nRecs)+' '+str(nNotM)+' '+str(nMskd)
            plt.figtext(ts_XAxOrg+0.01,YAxOrg+ts_YAxLen-0.025,TextStr,color='b')
            if n > 0: plt.setp(ts_axL.get_xticklabels(), visible=False)
            #Plot the Level 2 data series on the same X axis but with the scale on the right Y axis.
            L2YArray = numpy.ma.masked_where(dsb.series[ThisOne]['Data'][si:ei]==-9999,
                                             dsb.series[ThisOne]['Data'][si:ei])
            nRecs = numpy.ma.size(L2YArray)
            nNotM = numpy.ma.count(L2YArray)
            nMskd = numpy.ma.count_masked(L2YArray)
            if numpy.ma.count(L2YArray)==0:
                L2YArray = numpy.ma.zeros(numpy.size(L2YArray))
            # check the control file to see if the Y ax1s minima have been specified
            if 'YRMin' in cf['Plots'][str(nFig)]:                               # Y axis minima specified
                minlist = ast.literal_eval(cf['Plots'][str(nFig)]['YRMin'])     # Evaluate the minima list
                if str(minlist[SeriesList.index(ThisOne)])=='Auto':             # This entry is 'Auto' ...
                    RYAxMin = numpy.ma.minimum(L2YArray)                        # ... so take the array minimum value
                else:
                    RYAxMin = float(minlist[SeriesList.index(ThisOne)])         # Evaluate the entry for this series
            else:
                RYAxMin = numpy.ma.minimum(L2YArray)                            # Y axis minima not given, use auto
            # now do the same for the Y axis maxima
            if 'YRMax' in cf['Plots'][str(nFig)]:
                maxlist = ast.literal_eval(cf['Plots'][str(nFig)]['YRMax'])
                if str(maxlist[SeriesList.index(ThisOne)])=='Auto':
                    RYAxMax = numpy.ma.maximum(L2YArray)
                else:
                    RYAxMax = float(maxlist[SeriesList.index(ThisOne)])
            else:
                RYAxMax = numpy.ma.maximum(L2YArray)
            ts_axR = ts_axL.twinx()
            ts_axR.plot(L2XArray, L2YArray, 'r-')
            ts_axR.xaxis.set_major_locator(loc)
            ts_axR.xaxis.set_major_formatter(fmt)
            ts_axR.set_xlim(XAxMin,XAxMax)
            ts_axR.set_ylim(RYAxMin,RYAxMax)
            ts_axR.set_xlabel('',visible=False)
            TextStr = str(nNotM)+' '+str(nMskd)
            plt.figtext(ts_XAxOrg+ts_XAxLen-0.01,YAxOrg+ts_YAxLen-0.025,TextStr,color='r',horizontalalignment='right')
            plt.setp(ts_axR.get_xticklabels(), visible=False)
            #Plot the diurnal averages.
            #Hr1,Av1,Sd1,Mx1,Mn1=get_diurnalstats(dsa.series['Hdh']['Data'][si:ei],
                                                 #dsa.series[ThisOne]['Data'][si:ei],dt)
            #hr1_ax = fig.add_axes([hr1_XAxOrg,YAxOrg,hr1_XAxLen,ts_YAxLen])
            #hr1_ax.hold(False)
            #hr1_ax.plot(Hr1,Av1,'y-',Hr1,Mx1,'r-',Hr1,Mn1,'b-')
            #plt.xlim(0,24)
            #plt.xticks([0,6,12,18,24])
            #if n > 0: plt.setp(hr1_ax.get_xticklabels(), visible=False)
            Hr2,Av2,Sd2,Mx2,Mn2=get_diurnalstats(dsb.series['Hdh']['Data'][si:ei],
                                                 dsb.series[ThisOne]['Data'][si:ei],dt)
            Av2 = numpy.ma.masked_where(Av2==-9999,Av2)
            Sd2 = numpy.ma.masked_where(Sd2==-9999,Sd2)
            Mx2 = numpy.ma.masked_where(Mx2==-9999,Mx2)
            Mn2 = numpy.ma.masked_where(Mn2==-9999,Mn2)
            hr2_ax = fig.add_axes([hr1_XAxOrg,YAxOrg,hr2_XAxLen,ts_YAxLen])
            hr2_ax.hold(False)
            nSd = None
            if ThisOne in cf['Variables'].keys():
                if 'DiurnalCheck' in cf['Variables'][ThisOne].keys():
                    NSdarr = numpy.array(eval(cf['Variables'][ThisOne]['DiurnalCheck']['NumSd']),dtype=float)
                    nSd = NSdarr[Month-1]
                    hr2_ax.plot(Hr2,Av2,'y-',Hr2,Mx2,'r-',Hr2,Av2+nSd*Sd2,'r.',Hr2,Mn2,'b-',Hr2,Av2-nSd*Sd2,'b.')
                else:
                    hr2_ax.plot(Hr2,Av2,'y-',Hr2,Mx2,'r-',Hr2,Mn2,'b-')
            else:
                hr2_ax.plot(Hr2,Av2,'y-',Hr2,Mx2,'r-',Hr2,Mn2,'b-')
            plt.xlim(0,24)
            plt.xticks([0,6,12,18,24])
            if n==0:
                hr2_ax.set_xlabel('Hour',visible=True)
            else:
                hr2_ax.set_xlabel('',visible=False)
                plt.setp(hr2_ax.get_xticklabels(), visible=False)
            #if n > 0: plt.setp(hr2_ax.get_xticklabels(), visible=False)
            # vertical lines to show frequency distribution of flags
            bins = numpy.arange(0.5,23.5)
            ind = bins[:len(bins)-1]+0.5
            index = numpy.where(numpy.mod(bflag,10)==0)    # find the elements with flag = 0, 10, 20 etc
            bflag[index] = 0                               # set them all to 0
            hist, bin_edges = numpy.histogram(bflag, bins=bins)
            ymin = hist*0
            delta = 0.01*(numpy.max(hist)-numpy.min(hist))
            bar_ax = fig.add_axes([hr2_XAxOrg,YAxOrg,bar_XAxLen,ts_YAxLen])
            bar_ax.set_ylim(0,numpy.max(hist))
            bar_ax.vlines(ind,ymin,hist)
            for i,j in zip(ind,hist):
                if j>0.05*numpy.max(hist): bar_ax.text(i,j+delta,str(int(i)),ha='center',size='small')
            if n==0:
                bar_ax.set_xlabel('Flag',visible=True)
            else:
                bar_ax.set_xlabel('',visible=False)
                plt.setp(bar_ax.get_xticklabels(), visible=False)
            #if n > 0: plt.setp(bar_ax.get_xticklabels(), visible=False)
        else:
            txt = '  plttimeseries: series '+ThisOne+' not in data structure'
            print txt
    fig.show()

def plotxy(nFig,plt_cf,dsa,dsb,si,ei):
    fig = plt.figure(int(nFig))
    fig.clf()
    XSeries = eval(plt_cf['XSeries'])
    YSeries = eval(plt_cf['YSeries'])
    for xname,yname in zip(XSeries,YSeries):
        xa,flag = qcutils.GetSeriesasMA(dsa,xname,si=si,ei=ei)
        ya,flag = qcutils.GetSeriesasMA(dsa,yname,si=si,ei=ei)
        xb,flag = qcutils.GetSeriesasMA(dsb,xname,si=si,ei=ei)
        yb,flag = qcutils.GetSeriesasMA(dsb,yname,si=si,ei=ei)
        xyplot(xa,ya,sub=[1,2,1],xlabel=xname,ylabel=yname)
        xyplot(xb,yb,sub=[1,2,2],regr=1,xlabel=xname,ylabel=yname)
    fig.show()

def xyplot(x,y,sub=[1,1,1],regr=0,title=None,xlabel=None,ylabel=None):
    '''Generic XY scatter plot routine'''
    plt.subplot(sub[0],sub[1],sub[2])
    plt.plot(x,y,'b.')
    ax = plt.gca()
    if xlabel!=None: plt.xlabel(xlabel)
    if ylabel!=None: plt.ylabel(ylabel)
    if title!=None: plt.title(title)
    if regr!=0:
        coefs = numpy.ma.polyfit(x,y,1)
        xfit = numpy.ma.array([numpy.ma.minimum(x),numpy.ma.maximum(x)])
        yfit = numpy.polyval(coefs,xfit)
        r = numpy.ma.corrcoef(x,y)
        eqnstr = 'y = %.3fx + %.3f, r = %.3f'%(coefs[0],coefs[1],r[0][1])
        plt.plot(xfit,yfit,'r--',linewidth=3)
        plt.text(0.5,0.925,eqnstr,fontsize=8,horizontalalignment='center',transform=ax.transAxes)

def tsplot(x,y,sub=[1,1,1],title=None,xlabel=None,ylabel=None,colours=None,lineat=None):
    plt.subplot(sub[0],sub[1],sub[2])
    MTFmt = mdt.DateFormatter('%m/%Y')
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

def get_diurnalstats(DecHour,Data,dt):
    nInts = 24*int((60/dt)+0.5)
    Hr = numpy.zeros(nInts) + float(-9999)
    Av = numpy.zeros(nInts) + float(-9999)
    Sd = numpy.zeros(nInts) + float(-9999)
    Mx = numpy.zeros(nInts) + float(-9999)
    Mn = numpy.zeros(nInts) + float(-9999)
    for i in range(nInts):
        Hr[i] = float(i)*dt/60.
        li = numpy.where((abs(DecHour-Hr[i])<c.eps)&(abs(Data-float(-9999))>c.eps))
        if numpy.size(li)!=0:
            Av[i] = numpy.mean(Data[li])
            Sd[i] = numpy.std(Data[li])
            Mx[i] = numpy.max(Data[li])
            Mn[i] = numpy.min(Data[li])
    return Hr, Av, Sd, Mx, Mn

def get_ticks(start, end):
    from datetime import timedelta as td
    delta = end - start

    if delta <= td(minutes=10):
        loc = mdt.MinuteLocator()
        fmt = mdt.DateFormatter('%H:%M')
    elif delta <= td(minutes=30):
        loc = mdt.MinuteLocator(byminute=range(0,60,5))
        fmt = mdt.DateFormatter('%H:%M')
    elif delta <= td(hours=1):
        loc = mdt.MinuteLocator(byminute=range(0,60,15))
        fmt = mdt.DateFormatter('%H:%M')
    elif delta <= td(hours=6):
        loc = mdt.HourLocator()
        fmt = mdt.DateFormatter('%H:%M')
    elif delta <= td(days=1):
        loc = mdt.HourLocator(byhour=range(0,24,3))
        fmt = mdt.DateFormatter('%H:%M')
    elif delta <= td(days=3):
        loc = mdt.HourLocator(byhour=range(0,24,12))
        fmt = mdt.DateFormatter('%d/%m %H')
    elif delta <= td(weeks=2):
        loc = mdt.DayLocator()
        fmt = mdt.DateFormatter('%d/%m')
    elif delta <= td(weeks=12):
        loc = mdt.WeekdayLocator()
        fmt = mdt.DateFormatter('%d/%m')
    elif delta <= td(weeks=104):
        loc = mdt.MonthLocator()
        fmt = mdt.DateFormatter('%d/%m')
    else:
        loc = mdt.MonthLocator(interval=3)
        fmt = mdt.DateFormatter('%d/%m/%y')
    return loc,fmt