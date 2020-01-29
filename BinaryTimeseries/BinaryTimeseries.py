#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
This is a class to save evenly-sampled time series data in a very simple and
easy-to-read format. The key idea is to simply dump a header and then the raw
data values one after another into a binary file. When you want to read only a
small subset of the data, you can specify a time or an index range. A scaling
and an offset can be defined for the data values (e.g. from an ADC). Examples
of how to use this class can be found in Examples.py.

Inspired by an independent implementation by H. Thomsen.

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

def dtype2id(dtype):
    if   (dtype==0): raise ValueError("dtype has to be 1...6 and not "+str(dtype))
    elif (dtype==1): return 'b'
    elif (dtype==2): return 'h'
    elif (dtype==3): return 'i'
    elif (dtype==4): return 'q'
    elif (dtype==5): return 'f'
    elif (dtype==6): return 'd'
    else:            raise ValueError("dtype has to be 1...6 and not "+str(dtype))
    
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
    _file = None
    _fmap = None
    bo = '>'
    dtype_time = None
    t0 = None
    dt = None
    dtype_scaling = None
    offset = None
    scale  = None
    dtype_data = None
    size_raw_sample = 0
    num_samples = None
    data_size = 0
    
    # Opens a BinaryTimeseries and read the header.
    # file_nameOrNumber can be a str specifying a filename or 
    # a fileno, as e.g. used in
    # with open('filename.bts', 'rb') as f:
    #     with BinaryTimeseries(f.fileno()) as bts:
    #         print(bts.get_raw())
    # This permits use of in-memory mmaps as storage.
    def __init__(self, file_nameOrNumber):
        if self._fmap is not None and not self._fmap.closed:
            self._fmap.close()
        if self._file is not None and not self._file.closed:
            self._file.close()
        
        if type(file_nameOrNumber) is str:
            self._file = open(file_nameOrNumber, 'rb')
            # memory-map the file, size 0 means whole file, read-only for safety
            self._fmap = mmap.mmap(self._file.fileno(), 0, access=mmap.ACCESS_READ)
        elif type(file_nameOrNumber) is int:
            self._file= None
            # memory-map the file, size 0 means whole file, read-only for safety
            self._fmap = mmap.mmap(file_nameOrNumber, 0, access=mmap.ACCESS_READ)
        
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
      
    # needed for 'with BinaryTimeseries(filename) as bts:'
    def __enter__(self):
        return self
    
    # needed for 'with BinaryTimeseries(filename) as bts:'
    def __exit__(self, _type, _value, _tb):
        if self._fmap is not None and not self._fmap.closed:
            self._fmap.close()
        if self._file is not None and not self._file.closed:
            self._file.close()
        
    # if debug is set to True, generate debug output during reading the file
    def set_debug(self, debug):
        self._debug = debug
    
    # query the data type of the timestamps; 4: long, 6: double
    def get_dtype_time(self):
        return self.dtype_time
    
    # query the reference timestamp t_0
    def get_t0(self):
        return self.t0
    
    # query the sampling interval \Delta t
    def get_dt(self):
        return self.dt
    
    # query the data type of the scaling parameters; can be 0 (no scaling) to 6 (double)
    def get_dtype_scaling(self):
        return self.dtype_scaling
    
    # query the scaling offset; None if no scaling is present
    def get_offset(self):
        return self.offset
    
    # query the scaling factor; None if no scaling is present
    def get_scale(self):
        return self.scale
    
    # query the data type of the raw samples; can be 1 (byte) to 6 (double)
    def get_dtype_data(self):
        return self.dtype_data
    
    # query the number of samples; can be 1, ..., (2^31-1)
    def get_num_samples(self):
        return self.num_samples
        
    # read numSamplesToRead samples starting at index fromIdx and return the raw data
    def get_raw_indexRange(self, fromIdx, numSamplesToRead):
        if (fromIdx<0 or fromIdx>self.num_samples-1):
            raise ValueError("fromIdx "+str(fromIdx)+
                             " out of range; allowed: 0 to "+str(self.num_samples-1))
        
        if (numSamplesToRead<=0 or fromIdx+numSamplesToRead>self.num_samples):
            raise ValueError("numSamplesToRead "+str(numSamplesToRead)+
                             " out of range; allowed 1 to "+str(self.num_samples-fromIdx))
        
        raw_data = None
        # read raw data
        self._fmap.seek(64+fromIdx*self.size_raw_sample)
        read_size = numSamplesToRead*self.size_raw_sample
        unpack_str = self.bo+str(numSamplesToRead)+dtype2id(self.dtype_data)
        raw_data = struct.unpack(unpack_str, self._fmap.read(read_size))
        return np.array(raw_data)
    
    # read numSamplesToRead samples starting at index fromIdx and return the data with scaling applied (if available)
    def get_scaled_indexRange(self, fromIdx, numSamplesToRead):
        raw_data = self.get_raw_indexRange(fromIdx, numSamplesToRead)
         # apply the scaling if available
        if self.dtype_scaling==0: # no scaling
            return raw_data
        elif raw_data is not None:
            return np.add(np.multiply(raw_data, self.scale), self.offset)
        return None
    
    # given a sample index, compute the corresponding timestamp
    def get_t0_index(self, fromIdx):
        if (fromIdx<0 or fromIdx>self.num_samples-1):
            raise ValueError("fromIdx "+str(fromIdx)+
                             " out of range; allowed: 0 to "+str(self.num_samples-1))
        
        subset_t0 = self.t0 + self.dt*fromIdx
        return subset_t0
    
    # explicitly compute all timestamps in a given index range,
    # e.g. for plotting, where a timestamp is required for each sample
    def get_timestamps_indexRange(self, fromIdx, numSamplesToRead):
        if (fromIdx<0 or fromIdx>self.num_samples-1):
            raise ValueError("fromIdx "+str(fromIdx)+
                             " out of range; allowed: 0 to "+str(self.num_samples-1))
        
        if (numSamplesToRead<=0 or fromIdx+numSamplesToRead>self.num_samples):
            raise ValueError("numSamplesToRead "+str(numSamplesToRead)+
                             " out of range; allowed 1 to "+str(self.num_samples-fromIdx))
        
        t_start = self.t0 + self.dt*fromIdx
        t_end   = t_start + self.dt*(numSamplesToRead-1)
        if self.dtype_time==4: # long
            return np.linspace(t_start, t_end, numSamplesToRead, dtype=np.int64)
        elif self.dtype_time==6: # double
            return np.linspace(t_start, t_end, numSamplesToRead, dtype=np.float64)
        
    # read all samples whose timestamps are between t_lower and t_upper
    # and return the raw data; samples on the interval borders are included
    def get_raw_timeRange(self, t_lower, t_upper):
        if t_upper <= t_lower:
            raise ValueError("invalid time range given; please ensure t_lower < t_upper.")
        
        if self.dtype_time==4: # long timestamps => integer ceil/floor
            idx_i = 0
            if np.int64(t_lower) >= self.t0:
                idx_i = np.int64((np.int64(t_lower) - self.t0 + self.dt - 1)/self.dt)
                
            idx_j = self.num_samples-1
            if np.int64(t_upper) <= self.t0 + self.num_samples*self.dt:
                idx_j = np.int64((np.int64(t_upper) - self.t0) / self.dt)
            
            if idx_j-idx_i+1 <= 0:
                print("no samples present in given time interval")
                return None
            
            return self.get_raw_indexRange(int(idx_i), int(idx_j-idx_i+1))
                
        elif self.dtype_time==6: # long timestamps => regular ceil/floor
            idx_i = 0
            if np.float64(t_lower) >= self.t0:
                idx_i = np.ceil((np.float64(t_lower) - self.t0)/self.dt)
            
            idx_j = self.num_samples-1
            if np.float64(t_upper) <= self.t0 + self.num_samples*self.dt:
                idx_j = np.floor((np.float64(t_upper) - self.t0)/self.dt)
            
            if idx_j-idx_i+1 <= 0:
                print("no samples present in given time interval")
                return None
            
            return self.get_raw_indexRange(int(idx_i), int(idx_j-idx_i+1))
        else:
            return None
    
    # read all samples whose timestamps are between t_lower and t_upper
    # and return the data with scaling applied (if available);
    # samples on the interval borders are included
    def get_scaled_timeRange(self, t_lower, t_upper):
        raw_data = self.get_raw_timeRange(t_lower, t_upper)
         # apply the scaling if available
        if self.dtype_scaling==0: # no scaling
            return raw_data
        elif raw_data is not None:
            return np.add(np.multiply(raw_data, self.scale), self.offset)
        return None
    
    # explicitly compute the timestamps of all samples between t_lower and t_upper;
    # samples on the interval borders are included
    def get_timestamps_timeRange(self, t_lower, t_upper):
        if t_upper <= t_lower:
            raise ValueError("invalid time range given; please ensure t_lower < t_upper.")
        
        if self.dtype_time==4: # long timestamps => integer ceil/floor
            idx_i = 0
            if np.int64(t_lower) >= self.t0:
                idx_i = np.int64((np.int64(t_lower) - self.t0 + self.dt - 1)/self.dt)
                
            idx_j = self.num_samples-1
            if np.int64(t_upper) <= self.t0 + self.num_samples*self.dt:
                idx_j = np.int64((np.int64(t_upper) - self.t0) / self.dt)
            
            if idx_j-idx_i+1 <= 0:
                print("no samples present in given time interval")
                return None
            
            return self.get_timestamps_indexRange(int(idx_i), int(idx_j-idx_i+1))
                
        elif self.dtype_time==6: # long timestamps => regular ceil/floor
            idx_i = 0
            if np.float64(t_lower) >= self.t0:
                idx_i = np.ceil((np.float64(t_lower) - self.t0)/self.dt)
            
            idx_j = self.num_samples-1
            if np.float64(t_upper) <= self.t0 + self.num_samples*self.dt:
                idx_j = np.floor((np.float64(t_upper) - self.t0)/self.dt)
            
            if idx_j-idx_i+1 <= 0:
                print("no samples present in given time interval")
                return None
            
            return self.get_timestamps_indexRange(int(idx_i), int(idx_j-idx_i+1))
        else:
            return None
        
    # read all available samples and return the raw data array
    def get_raw(self):
        return self.get_raw_indexRange(0, self.num_samples)
    
    # read all available samples and return the data with scaling applied (if available)
    def get_scaled(self):
        return self.get_scaled_indexRange(0, self.num_samples)
    
    # explicitly compute all timestamps for all samples in this BinaryTimeseries
    def get_timestamps(self):
        return self.get_timestamps_indexRange(0, self.num_samples)
