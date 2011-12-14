import ast
import qcutils

def GetMergeList(cf,ThisOne,default=""):
    if ThisOne in cf['Variables'].keys():
        if 'MergeSeries' in cf['Variables'][ThisOne].keys():
            if 'Source' in cf['Variables'][ThisOne]['MergeSeries'].keys():
                mlist = ast.literal_eval(cf['Variables'][ThisOne]['MergeSeries']['Source'])
            else:
                print '  GetMergeList: key "Source" not in control file MergeSeries section for '+ThisOne
                mlist = default
        else:
            #print '  GetMergeList: key "MergeSeries" not in control file for '+ThisOne
            mlist = default
    else:
        print '  GetMergeList: '+ThisOne+' not in control file'
        mlist = default
    return mlist

def GetAverageList(cf,ThisOne,default=""):
    if qcutils.incf(cf,ThisOne) and qcutils.haskey(cf,ThisOne,'AverageSeries'):
        if 'Source' in cf['Variables'][ThisOne]['AverageSeries'].keys():
            alist = ast.literal_eval(cf['Variables'][ThisOne]['AverageSeries']['Source'])
        else:
            if len(str(default))==0:
                print '  GetAverageList: key "Source" not in control file AverageSeries section for '+ThisOne
                alist = ""
            else:
                alist = str(default)
    else:
        if len(str(default))==0:
            print '  GetAverageList: '+ThisOne+ ' not in control file or it does not have the "AverageSeries" key'
            alist = ""
        else:
            alist = str(default)
    return alist