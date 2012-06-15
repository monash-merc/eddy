import ast
import constants as c
from matplotlib.mlab import griddata
import numpy
import time
import qcio
import xlwt

def get_diurnalstats(DecHour,Data,dt):
    nInts = 24*int((60/dt)+0.5)
    Num = numpy.ma.zeros(nInts,dtype=int)
    Hr = numpy.ma.zeros(nInts,dtype=float)
    for i in range(nInts):
        Hr[i] = float(i)*dt/60.
    Av = numpy.ma.masked_all(nInts)
    Sd = numpy.ma.masked_all(nInts)
    Mx = numpy.ma.masked_all(nInts)
    Mn = numpy.ma.masked_all(nInts)
    if numpy.size(Data)!=0:
        for i in range(nInts):
            li = numpy.ma.where((abs(DecHour-Hr[i])<c.eps)&(abs(Data-float(-9999))>c.eps))
            Num[i] = numpy.size(li)
            if Num[i]!=0:
                Av[i] = numpy.ma.mean(Data[li])
                Sd[i] = numpy.ma.std(Data[li])
                Mx[i] = numpy.ma.maximum(Data[li])
                Mn[i] = numpy.ma.minimum(Data[li])
    return Num, Hr, Av, Sd, Mx, Mn

cf = qcio.loadcontrolfile('../ControlFiles')
ncFullName = cf['Files']['in']['ncFilePath']+cf['Files']['in']['ncFileName']
xlFileName = cf['Files']['out']['xlFilePath']+cf['Files']['out']['xlFileName']

xlFile = xlwt.Workbook()

ds = qcio.nc_read_series_file(ncFullName)

MList = ast.literal_eval(cf['Output']['met'])
RList = ast.literal_eval(cf['Output']['rad'])
SList = ast.literal_eval(cf['Output']['soil'])
FList = ast.literal_eval(cf['Output']['flux'])
OList = MList+RList+SList+FList                                   # output series

monthabr = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
Hdh = ds.series['Hdh']['Data']

for ThisOne in OList:
    print time.strftime('%X')+' Doing climatology for '+ThisOne
    xlSheet = xlFile.add_sheet(ThisOne)
    xlCol = 0
    data = numpy.ma.masked_where(abs(ds.series[ThisOne]['Data']-float(-9999))<c.eps,ds.series[ThisOne]['Data'])
    for month in range(1,13):
        mi = numpy.where(ds.series['Month']['Data']==month)[0]
        Num,Hr,Av,Sd,Mx,Mn = get_diurnalstats(Hdh[mi],data[mi],30)
        Num = numpy.ma.filled(Num,float(-9999))
        Hr = numpy.ma.filled(Hr,float(-9999))
        if month==1:
            xlSheet.write(1,xlCol,'Hour')
            for j in range(len(Hr)):
                xlSheet.write(j+2,xlCol,Hr[j])
            xlCol = xlCol + 1
        xlSheet.write(0,xlCol,monthabr[month-1])
        xlSheet.write(1,xlCol,'Num')
        xlSheet.write(1,xlCol+1,'Av')
        xlSheet.write(1,xlCol+2,'Sd')
        xlSheet.write(1,xlCol+3,'Mx')
        xlSheet.write(1,xlCol+4,'Mn')
        Av = numpy.ma.filled(Av,float(-9999))
        Sd = numpy.ma.filled(Sd,float(-9999))
        Mx = numpy.ma.filled(Mx,float(-9999))
        Mn = numpy.ma.filled(Mn,float(-9999))
        for j in range(len(Hr)):
            xlSheet.write(j+2,xlCol,Num[j])
            xlSheet.write(j+2,xlCol+1,Av[j])
            xlSheet.write(j+2,xlCol+2,Sd[j])
            xlSheet.write(j+2,xlCol+3,Mx[j])
            xlSheet.write(j+2,xlCol+4,Mn[j])
        xlCol = xlCol + 5
# calculate the evaporative fraction
xlSheet = xlFile.add_sheet('EF')
xlCol = 0
EF = numpy.ma.zeros([48,12]) + float(-9999)
print time.strftime('%X')+' Doing evaporative fraction'
for month in range(1,13):
    mi = numpy.where((ds.series['Month']['Data']==month))[0]
    Hdh = numpy.ma.masked_where(abs(ds.series['Hdh']['Data'][mi]-float(-9999))<c.eps,
                                ds.series['Hdh']['Data'][mi])
    Fn = numpy.ma.masked_where(abs(ds.series['Fn']['Data'][mi]-float(-9999))<c.eps,
                               ds.series['Fn']['Data'][mi])
    Fg = numpy.ma.masked_where(abs(ds.series['Fg']['Data'][mi]-float(-9999))<c.eps,
                               ds.series['Fg']['Data'][mi])
    Fa = Fn - Fg
    Fe = numpy.ma.masked_where(abs(ds.series['Fe_wpl']['Data'][mi]-float(-9999))<c.eps,
                               ds.series['Fe_wpl']['Data'][mi])
    Fa_Num,Hr,Fa_Av,Sd,Mx,Mn = get_diurnalstats(Hdh,Fa,30)
    Fe_Num,Hr,Fe_Av,Sd,Mx,Mn = get_diurnalstats(Hdh,Fe,30)
    index = numpy.ma.where((Fa_Num>4)&(Fe_Num>4))
    EF[:,month-1][index] = Fe_Av[index]/Fa_Av[index]
# reject EF values greater than or less than 1.5
EF = numpy.ma.masked_where(abs(EF)>1.5,EF)
EF = numpy.ma.filled(EF,float(-9999))
# write the EF to the Excel object
xlSheet.write(0,xlCol,'Hour')
for j in range(len(Hr)):
    xlSheet.write(j+1,xlCol,Hr[j])
xlCol = xlCol + 1
d_xf = xlwt.easyxf(num_format_str='0.00')
for month in range(1,13):
    xlSheet.write(0,xlCol,monthabr[month-1])
    for j in range(len(Hr)):
        xlSheet.write(j+1,xlCol,EF[j,month-1],d_xf)
    xlCol = xlCol + 1
# do the 2D interpolation to fill missing EF values
EF_3x3=numpy.tile(EF,(3,3))
nmn=numpy.shape(EF_3x3)[1]
mni=numpy.arange(0,nmn)
nhr=numpy.shape(EF_3x3)[0]
hri=numpy.arange(0,nhr)
mn,hr=numpy.meshgrid(mni,hri)
EF_3x3_1d=numpy.reshape(EF_3x3,numpy.shape(EF_3x3)[0]*numpy.shape(EF_3x3)[1])
mn_1d=numpy.reshape(mn,numpy.shape(mn)[0]*numpy.shape(mn)[1])
hr_1d=numpy.reshape(hr,numpy.shape(hr)[0]*numpy.shape(hr)[1])
index=numpy.where(EF_3x3_1d!=-9999)
EF_3x3i=griddata(mn_1d[index],hr_1d[index],EF_3x3_1d[index],mni,hri)
EFi=numpy.ma.filled(EF_3x3i[nhr/3:2*nhr/3,nmn/3:2*nmn/3],0)
xlSheet = xlFile.add_sheet('EFi')
xlCol = 0
# write the interpolated EF values to the Excel object
xlSheet.write(0,xlCol,'Hour')
for j in range(len(Hr)):
    xlSheet.write(j+1,xlCol,Hr[j])
xlCol = xlCol + 1
d_xf = xlwt.easyxf(num_format_str='0.00')
for month in range(1,13):
    xlSheet.write(0,xlCol,monthabr[month-1])
    for j in range(len(Hr)):
        xlSheet.write(j+1,xlCol,EFi[j,month-1],d_xf)
    xlCol = xlCol + 1

# calculate the Bowen ratio
xlSheet = xlFile.add_sheet('BR')
xlCol = 0
BR = numpy.ma.zeros([48,12]) + float(-9999)
print time.strftime('%X')+' Doing Bowen ratio'
for month in range(1,13):
    mi = numpy.where((ds.series['Month']['Data']==month))[0]
    Hdh = numpy.ma.masked_where(abs(ds.series['Hdh']['Data'][mi]-float(-9999))<c.eps,
                                ds.series['Hdh']['Data'][mi])
    Fe = numpy.ma.masked_where(abs(ds.series['Fe_wpl']['Data'][mi]-float(-9999))<c.eps,
                               ds.series['Fe_wpl']['Data'][mi])
    Fh = numpy.ma.masked_where(abs(ds.series['Fh']['Data'][mi]-float(-9999))<c.eps,
                               ds.series['Fh']['Data'][mi])
    Fh_Num,Hr,Fh_Av,Sd,Mx,Mn = get_diurnalstats(Hdh,Fh,30)
    Fe_Num,Hr,Fe_Av,Sd,Mx,Mn = get_diurnalstats(Hdh,Fe,30)
    index = numpy.ma.where((Fh_Num>4)&(Fe_Num>4))
    BR[:,month-1][index] = Fh_Av[index]/Fe_Av[index]
# reject BR values greater than or less than 5
BR = numpy.ma.masked_where(abs(BR)>20.0,BR)
BR = numpy.ma.filled(BR,float(-9999))
# write the BR to the Excel object
xlSheet.write(0,xlCol,'Hour')
for j in range(len(Hr)):
    xlSheet.write(j+1,xlCol,Hr[j])
xlCol = xlCol + 1
d_xf = xlwt.easyxf(num_format_str='0.00')
for month in range(1,13):
    xlSheet.write(0,xlCol,monthabr[month-1])
    for j in range(len(Hr)):
        xlSheet.write(j+1,xlCol,BR[j,month-1],d_xf)
    xlCol = xlCol + 1
# do the 2D interpolation to fill missing BR values
# tile to 3,3 array so we have a patch in the centre, this helps
# deal with edge effects
BR_3x3=numpy.tile(BR,(3,3))
nmn=numpy.shape(BR_3x3)[1]
mni=numpy.arange(0,nmn)
nhr=numpy.shape(BR_3x3)[0]
hri=numpy.arange(0,nhr)
mn,hr=numpy.meshgrid(mni,hri)
BR_3x3_1d=numpy.reshape(BR_3x3,numpy.shape(BR_3x3)[0]*numpy.shape(BR_3x3)[1])
mn_1d=numpy.reshape(mn,numpy.shape(mn)[0]*numpy.shape(mn)[1])
hr_1d=numpy.reshape(hr,numpy.shape(hr)[0]*numpy.shape(hr)[1])
index=numpy.where(BR_3x3_1d!=-9999)
BR_3x3i=griddata(mn_1d[index],hr_1d[index],BR_3x3_1d[index],mni,hri)
BRi=numpy.ma.filled(BR_3x3i[nhr/3:2*nhr/3,nmn/3:2*nmn/3],0)
xlSheet = xlFile.add_sheet('BRi')
xlCol = 0
# write the interpolated BR values to the Excel object
xlSheet.write(0,xlCol,'Hour')
for j in range(len(Hr)):
    xlSheet.write(j+1,xlCol,Hr[j])
xlCol = xlCol + 1
d_xf = xlwt.easyxf(num_format_str='0.00')
for month in range(1,13):
    xlSheet.write(0,xlCol,monthabr[month-1])
    for j in range(len(Hr)):
        xlSheet.write(j+1,xlCol,BRi[j,month-1],d_xf)
    xlCol = xlCol + 1

# calculate the ecosystem water use efficiency
xlSheet = xlFile.add_sheet('WUE')
xlCol = 0
WUE = numpy.ma.zeros([48,12]) + float(-9999)
print time.strftime('%X')+' Doing ecosystem WUE'
for month in range(1,13):
    mi = numpy.where((ds.series['Month']['Data']==month))[0]
    Hdh = numpy.ma.masked_where(abs(ds.series['Hdh']['Data'][mi]-float(-9999))<c.eps,
                                ds.series['Hdh']['Data'][mi])
    Fe = numpy.ma.masked_where(abs(ds.series['Fe_wpl']['Data'][mi]-float(-9999))<c.eps,
                               ds.series['Fe_wpl']['Data'][mi])
    Fc = numpy.ma.masked_where(abs(ds.series['Fc_wpl']['Data'][mi]-float(-9999))<c.eps,
                               ds.series['Fc_wpl']['Data'][mi])
    Fc_Num,Hr,Fc_Av,Sd,Mx,Mn = get_diurnalstats(Hdh,Fc,30)
    Fe_Num,Hr,Fe_Av,Sd,Mx,Mn = get_diurnalstats(Hdh,Fe,30)
    index = numpy.ma.where((Fc_Num>4)&(Fe_Num>4))
    WUE[:,month-1][index] = Fc_Av[index]/Fe_Av[index]
# reject WUE values greater than 0.04 or less than -0.004
WUE = numpy.ma.masked_where((WUE>0.04)|(WUE<-0.004),WUE)
WUE = numpy.ma.filled(WUE,float(-9999))
# write the WUE to the Excel object
xlSheet.write(0,xlCol,'Hour')
for j in range(len(Hr)):
    xlSheet.write(j+1,xlCol,Hr[j])
xlCol = xlCol + 1
d_xf = xlwt.easyxf(num_format_str='0.00000')
for month in range(1,13):
    xlSheet.write(0,xlCol,monthabr[month-1])
    for j in range(len(Hr)):
        xlSheet.write(j+1,xlCol,WUE[j,month-1],d_xf)
    xlCol = xlCol + 1
# do the 2D interpolation to fill missing WUE values
WUE_3x3=numpy.tile(WUE,(3,3))
nmn=numpy.shape(WUE_3x3)[1]
mni=numpy.arange(0,nmn)
nhr=numpy.shape(WUE_3x3)[0]
hri=numpy.arange(0,nhr)
mn,hr=numpy.meshgrid(mni,hri)
WUE_3x3_1d=numpy.reshape(WUE_3x3,numpy.shape(WUE_3x3)[0]*numpy.shape(WUE_3x3)[1])
mn_1d=numpy.reshape(mn,numpy.shape(mn)[0]*numpy.shape(mn)[1])
hr_1d=numpy.reshape(hr,numpy.shape(hr)[0]*numpy.shape(hr)[1])
index=numpy.where(WUE_3x3_1d!=-9999)
WUE_3x3i=griddata(mn_1d[index],hr_1d[index],WUE_3x3_1d[index],mni,hri)
WUEi=numpy.ma.filled(WUE_3x3i[nhr/3:2*nhr/3,nmn/3:2*nmn/3],0)
xlSheet = xlFile.add_sheet('WUEi')
xlCol = 0
# write the interpolated WUE values to the Excel object
xlSheet.write(0,xlCol,'Hour')
for j in range(len(Hr)):
    xlSheet.write(j+1,xlCol,Hr[j])
xlCol = xlCol + 1
d_xf = xlwt.easyxf(num_format_str='0.00000')
for month in range(1,13):
    xlSheet.write(0,xlCol,monthabr[month-1])
    for j in range(len(Hr)):
        xlSheet.write(j+1,xlCol,WUEi[j,month-1],d_xf)
    xlCol = xlCol + 1

print time.strftime('%X')+' Saving Excel file '+xlFileName
xlFile.save(xlFileName)

print time.strftime('%X')+' Climatology: All done'
