# -*- coding: utf-8 -*-
import unittest
import sys
bts_path = '../../main/python'
if not bts_path in sys.path:
	sys.path.insert(0, bts_path)
from BinaryTimeseries import BinaryTimeseries

class GeneratedApiTests(unittest.TestCase):

	def test_L_N_B(self):
		with open('../resources/L_N_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_N_S(self):
		with open('../resources/L_N_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_N_I(self):
		with open('../resources/L_N_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_N_L(self):
		with open('../resources/L_N_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_N_F(self):
		with open('../resources/L_N_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_N_D(self):
		with open('../resources/L_N_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_B_B(self):
		with open('../resources/L_B_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_B_S(self):
		with open('../resources/L_B_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_B_I(self):
		with open('../resources/L_B_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_B_L(self):
		with open('../resources/L_B_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_B_F(self):
		with open('../resources/L_B_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_B_D(self):
		with open('../resources/L_B_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_S_B(self):
		with open('../resources/L_S_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_S_S(self):
		with open('../resources/L_S_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_S_I(self):
		with open('../resources/L_S_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_S_L(self):
		with open('../resources/L_S_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_S_F(self):
		with open('../resources/L_S_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_S_D(self):
		with open('../resources/L_S_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_I_B(self):
		with open('../resources/L_I_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_I_S(self):
		with open('../resources/L_I_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_I_I(self):
		with open('../resources/L_I_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_I_L(self):
		with open('../resources/L_I_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_I_F(self):
		with open('../resources/L_I_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_I_D(self):
		with open('../resources/L_I_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_L_B(self):
		with open('../resources/L_L_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_L_S(self):
		with open('../resources/L_L_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_L_I(self):
		with open('../resources/L_L_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_L_L(self):
		with open('../resources/L_L_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_L_F(self):
		with open('../resources/L_L_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_L_D(self):
		with open('../resources/L_L_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_F_B(self):
		with open('../resources/L_F_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_F_S(self):
		with open('../resources/L_F_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_F_I(self):
		with open('../resources/L_F_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_F_L(self):
		with open('../resources/L_F_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_F_F(self):
		with open('../resources/L_F_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_F_D(self):
		with open('../resources/L_F_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_D_B(self):
		with open('../resources/L_D_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_D_S(self):
		with open('../resources/L_D_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_D_I(self):
		with open('../resources/L_D_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_D_L(self):
		with open('../resources/L_D_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_D_F(self):
		with open('../resources/L_D_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_L_D_D(self):
		with open('../resources/L_D_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_N_B(self):
		with open('../resources/D_N_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_N_S(self):
		with open('../resources/D_N_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_N_I(self):
		with open('../resources/D_N_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_N_L(self):
		with open('../resources/D_N_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_N_F(self):
		with open('../resources/D_N_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_N_D(self):
		with open('../resources/D_N_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_B_B(self):
		with open('../resources/D_B_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_B_S(self):
		with open('../resources/D_B_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_B_I(self):
		with open('../resources/D_B_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_B_L(self):
		with open('../resources/D_B_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_B_F(self):
		with open('../resources/D_B_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_B_D(self):
		with open('../resources/D_B_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_S_B(self):
		with open('../resources/D_S_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_S_S(self):
		with open('../resources/D_S_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_S_I(self):
		with open('../resources/D_S_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_S_L(self):
		with open('../resources/D_S_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_S_F(self):
		with open('../resources/D_S_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_S_D(self):
		with open('../resources/D_S_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_I_B(self):
		with open('../resources/D_I_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_I_S(self):
		with open('../resources/D_I_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_I_I(self):
		with open('../resources/D_I_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_I_L(self):
		with open('../resources/D_I_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_I_F(self):
		with open('../resources/D_I_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_I_D(self):
		with open('../resources/D_I_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_L_B(self):
		with open('../resources/D_L_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_L_S(self):
		with open('../resources/D_L_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_L_I(self):
		with open('../resources/D_L_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_L_L(self):
		with open('../resources/D_L_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_L_F(self):
		with open('../resources/D_L_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_L_D(self):
		with open('../resources/D_L_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_F_B(self):
		with open('../resources/D_F_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_F_S(self):
		with open('../resources/D_F_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_F_I(self):
		with open('../resources/D_F_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_F_L(self):
		with open('../resources/D_F_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_F_F(self):
		with open('../resources/D_F_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_F_D(self):
		with open('../resources/D_F_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_D_B(self):
		with open('../resources/D_D_B.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_D_S(self):
		with open('../resources/D_D_S.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_D_I(self):
		with open('../resources/D_D_I.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_D_L(self):
		with open('../resources/D_D_L.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_D_F(self):
		with open('../resources/D_D_F.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
	def test_D_D_D(self):
		with open('../resources/D_D_D.bts', 'rb') as f:
			bts = BinaryTimeseries(f.fileno())
if __name__ == '__main__':
    unittest.main()

