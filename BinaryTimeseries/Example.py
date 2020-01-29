# -*- coding: utf-8 -*-

import os
from BinaryTimeseries import BinaryTimeseries

# read all available samples
with BinaryTimeseries('../src/test/resources/L_D_D.bts') as bts:
    print("raw:")
    print(bts.get_raw())
    
    print("scaled:")
    print(bts.get_scaled())
    
    print("timestamps:")
    print(bts.get_timestamps())

# read subset specified by timestamps
with BinaryTimeseries('../src/test/resources/L_D_D.bts') as bts:
    print("raw between t_lower=45 and t_upper=200:")
    print(bts.get_raw_timeRange(45, 200))
    
    print("scaled between t_lower=45 and t_upper=200:")
    print(bts.get_scaled_timeRange(45, 200))
    
    print("timestamps between t_lower=45 and t_upper=200:")
    print(bts.get_timestamps_timeRange(45, 200))

# list the t0 values of all files in a given directory
folder = "/data/jonathan/datasignalscache/w7x/180918045/"
#for qxt in ["qxt1/", "qxt2/", "qxt3/", "qxt4/"]:
#    for chanIdx in range(96):
#        fname = folder+qxt+"DATA.CH%02d.bts"%(chanIdx+1,)
#        if os.path.isfile(fname):
#            with BinaryTimeseries(fname) as bts:
#                print("'"+fname+"': t0=", (bts.get_t0()/1e9), "s")

import matplotlib.pyplot as plt
import time

# reading takes 2.5s
fname = folder+"qxt1/DATA.CH23.bts"
with BinaryTimeseries(fname) as bts:
    timestamps = bts.get_timestamps()
    
    start = time.time()
    scaled = bts.get_scaled()
    end = time.time()
    print("reading took ", end - start, "s")

    plt.figure()
    plt.plot(timestamps, scaled)
    plt.xlabel("t / ns")
    plt.ylabel("U / V")
    plt.tight_layout()

#%%

import sys
rbts_path = "/home/jonathan/Uni/04_PhD/00_programs/MHDpython/minfisher"
if not rbts_path in sys.path:
    sys.path.insert(0, rbts_path)
    
from readbinarytimeseries import readbinarytimeseries

# 150...200 ms --> clearly faster!
start = time.time()
scaled_data = readbinarytimeseries(fname, quiet=False)
end = time.time()
print("reading took ", end - start, "s")

