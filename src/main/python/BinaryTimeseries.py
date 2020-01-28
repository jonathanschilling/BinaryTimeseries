#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
This is a class to save evenly-sampled time series data in a very simple and
easy-to-read format. The key idea is to simply dump a header and then the raw
data values one after another into a binary file. When you want to read only a
small subset of the data, you can specify a time or an index range. A scaling
and an offset can be defined for the data values (e.g. from an ADC). Examples
of how to use this class can be found in Examples.py.
 
@author: Jonathan Schilling (jonathan.schilling@mail.de)
@version: 1.0.2 first official Python implementation
"""

import os
import mmap
import struct
import numpy as np

def dtype2str(dtype):
    if   (dtype==0): return "None"
    elif (dtype==1): return "byte"
    elif (dtype==2): return "short"
    elif (dtype==3): return "int"
    elif (dtype==4): return "long"
    elif (dtype==5): return "float"
    elif (dtype==6): return "double"
    else:            return "ERROR"

def dtype_size(dtype):
    if   (dtype==0): return 0
    elif (dtype==1): return 1
    elif (dtype==2): return 2
    elif (dtype==3): return 4
    elif (dtype==4): return 8
    elif (dtype==5): return 4
    elif (dtype==6): return 8
    else:            return None

class BinaryTimeseries(object):

    _debug = False
    _fmap = None
    bo = '>'
    dtype_time = None
    t0 = None
    dt = None
    dtype_scaling = None
    offset = None
    scale  = None
    raw_data = None
    num_samples = None
    data_size = 0
    
    def __init__(self, mmap_fileno):
        
        # memory-map the file, size 0 means whole file, read-only for safety
        self._fmap = mmap.mmap(mmap_fileno, 0, access=mmap.ACCESS_READ)
        
        # start at the beginning
        self._fmap.seek(0)
        
        # try big-endian byte order first
        endianessCheck = struct.unpack(self.bo+'h', self._fmap.read(2))[0]
        if not (endianessCheck==1 or endianessCheck==256):
            raise ValueError("endianessCheck is neither 1 or 256 but "+str(endianessCheck))
        if (endianessCheck==256):
            # nope, input file is little-endian
            self.bo = '<'
            if self._debug: print("byteorder is little-endian")
        elif self._debug: print("byteorder is big-endian")
        
        # determine dtype of timestamps
        self.dtype_time = struct.unpack('b', self._fmap.read(1))[0]
        if self.dtype_time==4 or self.dtype_time==6:
            if self._debug: print("dtype_time: "+dtype2str(self.dtype_time))
        else:
            raise ValueError("dtype_time is not 4 (long) or 6 (double), but "+str(self.dtype_time))
        
        # read time axis specification
        if self.dtype_time==4: # long
            self.t0 = struct.unpack(self.bo+'q', self._fmap.read(8))[0]
            self.dt = struct.unpack(self.bo+'q', self._fmap.read(8))[0]
        else: # double
            self.t0 = struct.unpack(self.bo+'d', self._fmap.read(8))[0]
            self.dt = struct.unpack(self.bo+'d', self._fmap.read(8))[0]
        if self._debug:
            print("t0: "+str(self.t0))
            print("dt: "+str(self.dt))
        
        # read dtype of scaling
        self.dtype_scaling = struct.unpack('b', self._fmap.read(1))[0]
        if self.dtype_scaling>=0 and self.dtype_scaling<=6:
            if self._debug: print("dtype_scaling: "+dtype2str(self.dtype_scaling))
        else:
            raise ValueError("dtype_scaling is not in valid range (0..6), but "+str(self.dtype_scaling))
        
        # read scaling parameters
        if   self.dtype_scaling==0: # no scaling
            self._fmap.seek(16, os.SEEK_CUR)
        elif self.dtype_scaling==1: # byte
            self.offset = struct.unpack(        'b', self._fmap.read(1))[0]
            self._fmap.seek(7, os.SEEK_CUR)
            self.scale  = struct.unpack(        'b', self._fmap.read(1))[0]
            self._fmap.seek(7, os.SEEK_CUR)
        elif self.dtype_scaling==2: # short
            self.offset = struct.unpack(self.bo+'h', self._fmap.read(2))[0]
            self._fmap.seek(6, os.SEEK_CUR)
            self.scale  = struct.unpack(self.bo+'h', self._fmap.read(2))[0]
            self._fmap.seek(6, os.SEEK_CUR)
        elif self.dtype_scaling==3: # int
            self.offset = struct.unpack(self.bo+'i', self._fmap.read(4))[0]
            self._fmap.seek(4, os.SEEK_CUR)
            self.scale  = struct.unpack(self.bo+'i', self._fmap.read(4))[0]
            self._fmap.seek(4, os.SEEK_CUR)
        elif self.dtype_scaling==4: # long
            self.offset = struct.unpack(self.bo+'q', self._fmap.read(8))[0]
            self.scale  = struct.unpack(self.bo+'q', self._fmap.read(8))[0]
        elif self.dtype_scaling==5: # float
            self.offset = struct.unpack(self.bo+'f', self._fmap.read(4))[0]
            self._fmap.seek(4, os.SEEK_CUR)
            self.scale  = struct.unpack(self.bo+'f', self._fmap.read(4))[0]
            self._fmap.seek(4, os.SEEK_CUR)
        elif self.dtype_scaling==6: # double
            self.offset = struct.unpack(self.bo+'d', self._fmap.read(8))[0]
            self.scale  = struct.unpack(self.bo+'d', self._fmap.read(8))[0]
        if self._debug:
            print("offset: "+str(self.offset))
            print(" scale: "+str(self.scale))
        
        # skip reserved bytes
        self._fmap.seek(23, os.SEEK_CUR)
        
        # read dtype of raw data
        self.dtype_data = struct.unpack('b', self._fmap.read(1))[0]
        if self.dtype_data>=1 and self.dtype_data<=6:
            self.size_raw_sample = dtype_size(self.dtype_data)
            if self._debug: print("dtype_data: "+dtype2str(self.dtype_data))
        else:
            raise ValueError("dtype_data is not in valid range (1..6), but "+str(self.dtype_data))
        
        # read number of samples
        self.num_samples = struct.unpack(self.bo+'i', self._fmap.read(4))[0]
        if self._debug: print("num_samples: "+str(self.num_samples))
        
        # check to see if an error was made in counting bytes
        current_pos = self._fmap.tell()
        if (current_pos != 64):
            raise RuntimeError("fpos should be 64 after reading the header, but it is "+str(current_pos))
        
        # check if file size is large enough to fit all data specified in header
        self.data_size = self.num_samples*self.size_raw_sample
        if len(self._fmap)<64+self.data_size:
            raise RuntimeError("length of file not large enough; has "+str(len(self._fmap))
                +", expected "+str(64+self.data_size))
       
    def set_debug(self, debug):
        self._debug = debug
    
    def get_dtype_time(self):
        return self.dtype_time
    
    def get_t0(self):
        return self.t0
    
    def get_dt(self):
        return self.dt
    
    def get_raw(self):
        raw_data = None
        # read raw data
        self._fmap.seek(64)
        if   self.dtype_data==1: # byte
            raw_data = struct.unpack(self.bo+str(self.num_samples)+'b', self._fmap.read(self.data_size))
        elif self.dtype_data==2: # short
            raw_data = struct.unpack(self.bo+str(self.num_samples)+'h', self._fmap.read(self.data_size))
        elif self.dtype_data==3: # int
            raw_data = struct.unpack(self.bo+str(self.num_samples)+'i', self._fmap.read(self.data_size))
        elif self.dtype_data==4: # long
            raw_data = struct.unpack(self.bo+str(self.num_samples)+'q', self._fmap.read(self.data_size))
        elif self.dtype_data==5: # float
            raw_data = struct.unpack(self.bo+str(self.num_samples)+'f', self._fmap.read(self.data_size))
        elif self.dtype_data==6: # double
            raw_data = struct.unpack(self.bo+str(self.num_samples)+'d', self._fmap.read(self.data_size))
        return raw_data
    
    def get_scaled(self):
        raw_data = self.get_raw()
        scaled_data = None
         # apply the scaling if available
        if   self.dtype_scaling==0: # no scaling
            scaled_data = np.array(raw_data)
        else:
            scaled_data = np.add(np.multiply(np.array(raw_data), self.scale), self.offset)
        return scaled_data
    
    def get_raw_indexRange(self, fromIdx, numSamplesToRead):
        if (fromIdx<0 or fromIdx>self.num_samples-1):
            raise ValueError("fromIdx "+str(fromIdx)+" out of range; allowed: 0 to "+str(self.num_samples-1))
        if (numSamplesToRead<=0 or fromIdx+numSamplesToRead>self.num_samples):
            raise ValueError("numSamplesToRead "+str(numSamplesToRead)+
                             " out of range; allowed 1 to "+str(self.num_samples-fromIdx))
        
        raw_data = None
        # read raw data
        self._fmap.seek(64+fromIdx*self.size_raw_sample)
        read_size = numSamplesToRead*self.size_raw_sample
        if   self.dtype_data==1: # byte
            raw_data = struct.unpack(self.bo+str(numSamplesToRead)+'b', self._fmap.read(read_size))
        elif self.dtype_data==2: # short
            raw_data = struct.unpack(self.bo+str(numSamplesToRead)+'h', self._fmap.read(read_size))
        elif self.dtype_data==3: # int
            raw_data = struct.unpack(self.bo+str(numSamplesToRead)+'i', self._fmap.read(read_size))
        elif self.dtype_data==4: # long
            raw_data = struct.unpack(self.bo+str(numSamplesToRead)+'q', self._fmap.read(read_size))
        elif self.dtype_data==5: # float
            raw_data = struct.unpack(self.bo+str(numSamplesToRead)+'f', self._fmap.read(read_size))
        elif self.dtype_data==6: # double
            raw_data = struct.unpack(self.bo+str(numSamplesToRead)+'d', self._fmap.read(read_size))
        return np.array(raw_data)
    
    def get_scaled_indexRange(self, fromIdx, numSamplesToRead):
        raw_data = self.get_raw(fromIdx, numSamplesToRead)
         # apply the scaling if available
        if   self.dtype_scaling==0: # no scaling
            return raw_data
        else:
            return np.add(np.multiply(raw_data, self.scale), self.offset)
        
    def __enter__(self):
        return self
    
    def __exit__(self, _type, _value, _tb):
        self._fmap.close()
    
if __name__=='__main__':
    
    filename="../../test/resources/L_S_F.bts"

    with open(filename, "rb") as f:
        with BinaryTimeseries(f.fileno()) as bts:
            raw_data = bts.get_raw_indexRange(0, 10)
            