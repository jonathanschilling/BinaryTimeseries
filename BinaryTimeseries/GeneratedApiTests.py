# -*- coding: utf-8 -*-
import unittest
import sys
import numpy as np
from BinaryTimeseries import BinaryTimeseries

o = 1.2
s = 24.3
class GeneratedApiTests(unittest.TestCase):

    def test_read_L_N_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        with BinaryTimeseries('../src/test/resources/L_N_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_L_N_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        with BinaryTimeseries('../src/test/resources/L_N_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_L_N_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        with BinaryTimeseries('../src/test/resources/L_N_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_L_N_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        with BinaryTimeseries('../src/test/resources/L_N_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_L_N_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        with BinaryTimeseries('../src/test/resources/L_N_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_L_N_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        with BinaryTimeseries('../src/test/resources/L_N_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_L_B_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.int64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/L_B_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_B_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.int64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/L_B_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_B_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.int64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/L_B_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_B_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.int64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/L_B_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_B_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.float64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/L_B_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_B_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.float64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/L_B_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_S_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.int64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/L_S_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_S_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.int64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/L_S_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_S_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.int64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/L_S_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_S_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.int64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/L_S_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_S_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.float64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/L_S_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_S_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.float64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/L_S_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_I_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.int64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/L_I_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_I_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.int64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/L_I_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_I_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.int64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/L_I_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_I_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.int64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/L_I_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_I_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.float64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/L_I_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_I_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.float64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/L_I_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_L_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.int64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/L_L_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_L_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.int64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/L_L_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_L_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.int64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/L_L_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_L_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.int64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/L_L_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_L_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.float64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/L_L_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_L_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.float64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/L_L_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_F_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/L_F_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_F_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/L_F_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_F_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/L_F_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_F_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/L_F_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_F_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/L_F_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_F_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/L_F_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_D_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/L_D_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_D_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/L_D_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_D_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/L_D_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_D_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/L_D_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_D_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/L_D_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_L_D_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/L_D_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 4)
            self.assertEqual(bts.get_t0(), np.int64(13.0))
            self.assertEqual(bts.get_dt(), np.int64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_N_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        with BinaryTimeseries('../src/test/resources/D_N_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_D_N_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        with BinaryTimeseries('../src/test/resources/D_N_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_D_N_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        with BinaryTimeseries('../src/test/resources/D_N_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_D_N_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        with BinaryTimeseries('../src/test/resources/D_N_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_D_N_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        with BinaryTimeseries('../src/test/resources/D_N_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_D_N_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        with BinaryTimeseries('../src/test/resources/D_N_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 0)
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
    def test_read_D_B_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.int64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/D_B_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_B_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.int64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/D_B_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_B_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.int64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/D_B_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_B_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.int64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/D_B_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_B_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.float64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/D_B_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_B_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int8(s), dtype=np.float64), np.int8(o))
        with BinaryTimeseries('../src/test/resources/D_B_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 1)
            self.assertEqual(bts.get_offset(), np.int8(1.2))
            self.assertEqual(bts.get_scale(), np.int8(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_S_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.int64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/D_S_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_S_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.int64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/D_S_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_S_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.int64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/D_S_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_S_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.int64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/D_S_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_S_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.float64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/D_S_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_S_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int16(s), dtype=np.float64), np.int16(o))
        with BinaryTimeseries('../src/test/resources/D_S_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 2)
            self.assertEqual(bts.get_offset(), np.int16(1.2))
            self.assertEqual(bts.get_scale(), np.int16(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_I_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.int64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/D_I_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_I_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.int64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/D_I_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_I_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.int64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/D_I_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_I_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.int64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/D_I_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_I_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.float64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/D_I_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_I_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int32(s), dtype=np.float64), np.int32(o))
        with BinaryTimeseries('../src/test/resources/D_I_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 3)
            self.assertEqual(bts.get_offset(), np.int32(1.2))
            self.assertEqual(bts.get_scale(), np.int32(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_L_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.int64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/D_L_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_L_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.int64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/D_L_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_L_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.int64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/D_L_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_L_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.int64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/D_L_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_L_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.float64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/D_L_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_L_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.int64(s), dtype=np.float64), np.int64(o))
        with BinaryTimeseries('../src/test/resources/D_L_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 4)
            self.assertEqual(bts.get_offset(), np.int64(1.2))
            self.assertEqual(bts.get_scale(), np.int64(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_F_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/D_F_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_F_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/D_F_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_F_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/D_F_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_F_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/D_F_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_F_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/D_F_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_F_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float32(s), dtype=np.float64), np.float32(o))
        with BinaryTimeseries('../src/test/resources/D_F_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 5)
            self.assertEqual(bts.get_offset(), np.float32(1.2))
            self.assertEqual(bts.get_scale(), np.float32(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_D_B(self):
        raw_data = np.zeros([10], dtype=np.int8)
        for i in range(10):
            raw_data[i] = np.int8(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/D_D_B.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 1)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_D_S(self):
        raw_data = np.zeros([10], dtype=np.int16)
        for i in range(10):
            raw_data[i] = np.int16(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/D_D_S.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 2)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_D_I(self):
        raw_data = np.zeros([10], dtype=np.int32)
        for i in range(10):
            raw_data[i] = np.int32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/D_D_I.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 3)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_D_L(self):
        raw_data = np.zeros([10], dtype=np.int64)
        for i in range(10):
            raw_data[i] = np.int64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/D_D_L.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 4)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_D_F(self):
        raw_data = np.zeros([10], dtype=np.float32)
        for i in range(10):
            raw_data[i] = np.float32(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/D_D_F.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 5)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
    def test_read_D_D_D(self):
        raw_data = np.zeros([10], dtype=np.float64)
        for i in range(10):
            raw_data[i] = np.float64(o+i*s)
        scaled_data = np.add(np.multiply(raw_data, np.float64(s), dtype=np.float64), np.float64(o))
        with BinaryTimeseries('../src/test/resources/D_D_D.bts') as bts:
            self.assertEqual(bts.get_dtype_time(), 6)
            self.assertEqual(bts.get_t0(), np.float64(13.0))
            self.assertEqual(bts.get_dt(), np.float64(37.0))
            self.assertEqual(bts.get_dtype_scaling(), 6)
            self.assertEqual(bts.get_offset(), np.float64(1.2))
            self.assertEqual(bts.get_scale(), np.float64(24.3))
            self.assertEqual(bts.get_dtype_data(), 6)
            self.assertEqual(bts.get_num_samples(), 10)
            self.assertTrue(np.allclose(bts.get_raw(), raw_data))
            self.assertTrue(np.allclose(bts.get_scaled(), scaled_data))
if __name__ == '__main__':
    unittest.main()
