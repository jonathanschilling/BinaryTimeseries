# -*- coding: utf-8 -*-

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
with BinaryTimeseries('../src/test/resources/L_D_D.bts', debug=True) as bts:
    print("raw between t_lower=45 and t_upper=200:")
    print(bts.get_raw_timeRange(45, 200))
    
    print("scaled between t_lower=45 and t_upper=200:")
    print(bts.get_scaled_timeRange(45, 200))
    
    print("timestamps between t_lower=45 and t_upper=200:")
    print(bts.get_timestamps_timeRange(45, 200))
