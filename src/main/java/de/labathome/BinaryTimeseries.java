package de.labathome;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This is a class to save evenly-sampled time series data in a very simple and easy-to-read format.
 * The key idea is to simply dump a header and then the values one after another into a ByteBuffer.
 * When you want to read only a small subset of the data, you can specify a time or index range.
 * A scaling and an offset can be defined for raw values (e.g. from an ADC).
 * Examples of how to use this class can be found in TestBinaryTimeseries.java .
 * 
 * @author Jonathan Schilling (jonathan.schilling@mail.de)
 */
public class BinaryTimeseries {
	
	/**
	 * Identifier value for {@code byte} datatype. <br/> Length: 1 Byte <br/> Range: -128 ... 127
	 */
	public static final byte DTYPE_BYTE  = 0; 
	
	/**
	 * Identifier value for {@code short} datatype. <br/> Length: 2 Bytes <br/> Range: -32768 ... 32767
	 */
	public static final byte DTYPE_SHORT = 1;
	
	/**
	 * Identifier value for {@code int} datatype. <br/> Length: 4 Bytes <br/> Range: -2147483648 ... 2147483647
	 */
	public static final byte DTYPE_INT   = 2;
	
	/**
	 * Identifier value for {@code long} datatype. <br/> Length: 8 Bytes <br/> Range: -9223372036854775808 ... 9223372036854775807
	 */
	public static final byte DTYPE_LONG  = 3;
	
	/**
	 * Identifier value for {@code float} datatype. <br/> Length: 4 Bytes <br/> 7 decimal digits
	 */
	public static final byte DTYPE_FLOAT = 4;
	
	/**
	 * Identifier value for {@code double} datatype. <br/> Length: 8 Bytes <br/> 16 decimal digits
	 */
	public static final byte DTYPE_DOUBLE= 5;
	
	/**
	 * Extract the highest bit of the given dtype byte to see if the given file has a value scaling or not.
	 * @param data_dtype dtype byte as read from input buffer
	 * @return false if the data has no scaling; true if it has scaling
	 */
	public static final boolean hasScaling(final byte data_dtype) {
		return (data_dtype & 1<<7) == 0;
	}
	
	/**
	 * Enable scaling for the given dtype, no matter if it was enabled previously or not.
	 * @param data_dtype input datatype
	 * @return copy of data_dtype with the highest bit set to true
	 */
	public static final byte withScaling(final byte data_dtype) {
		return (byte) (data_dtype & ~(1<<7));
	}
	
	/**
	 * Disable scaling for the given dtype, no matter if it was enabled previously or not.
	 * @param data_dtype input dtype, 0...5
	 * @return copy of data_dtype with the highest bit set to false
	 */
	public static final byte withoutScaling(final byte data_dtype) {
		return (byte) (data_dtype | (1<<7));
	}
	
	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * The number of values is given by the length of the {@code target} array, into which the values are put.
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0 reference timestamp; will go into {@code target[0]}
	 * @param dt time interval between two samples
	 */
	public static void buildTimebase(double[] target, final double t0, final double dt) {
		buildTimebase(target, t0, dt, 0, target.length-1);
	}
	
	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * All entries in {@code target} from {@code startIdx} up to and including {@code endIdx} are filled with appropriate values of the timebase.
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0 reference timestamp; will go into {@code target[0]}
	 * @param dt time interval between two samples
	 * @param startIdx first index into which the timebase values will be put
	 * @param endIdx last index into which the timebase values will be put
	 */
	public static void buildTimebase(double[] target, final double t0, final double dt, final int startIdx, final int endIdx) {
		for (int i=startIdx; i<=endIdx; ++i) {
			target[i-startIdx] = t0+i*dt;
		}
	}

	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * The number of values is given by the length of the {@code target} array, into which the values are put.
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0 reference timestamp; will go into {@code target[0]}
	 * @param dt time interval between two samples
	 */
	public static void buildTimebase(long[] target, final long t0, final long dt) {
		buildTimebase(target, t0, dt, 0, target.length-1);
	}
	
	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * All entries in {@code target} from {@code startIdx} up to and including {@code endIdx} are filled with appropriate values of the timebase.
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0 reference timestamp; will go into {@code target[0]}
	 * @param dt time interval between two samples
	 * @param startIdx first index into which the timebase values will be put
	 * @param endIdx last index into which the timebase values will be put
	 */
	public static void buildTimebase(long[] target, final long t0, final long dt, final int startIdx, final int endIdx) {
		for (int i=startIdx; i<=endIdx; ++i) {
			target[i-startIdx] = t0+i*dt;
		}
	}

	/**
	 * Given a timebase (t_0, Delta_t), compute the first and last index of timestamps inside a given time interval [{@code from}, {@code upto}].
	 * @param target [2] {@code target[0]} will contain the first index, {@code target[1]} the last index of samples between {@code from} and {@code upto}.
	 * @param t0 reference timestamp from the file
	 * @param dt time interval between two samples from the file
	 * @param from lower boundary of the time interval to read data from; t_l in the documentation
	 * @param upto upper boundary of the time interval to read data from; t_u in the documentation
	 * @see <a href="https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil">https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil</a>
	 */
	public static void indexInterval(int[] target, final long t0, final long dt, final long from, final long upto) {
		target[0] = (int) ((from-t0 + dt - 1) / dt); //  ceil for lower end
		target[1] = (int) ((upto-t0)/dt);            // floor for upper end
	}

	/**
	 * Given a timebase (t_0, Delta_t), compute the first and last index of timestamps inside a given time interval [{@code from}, {@code upto}].
	 * @param target [2] {@code target[0]} will contain the first index, {@code target[1]} the last index of samples between {@code from} and {@code upto}.
	 * @param t0 reference timestamp from the file
	 * @param dt time interval between two samples from the file
	 * @param from lower boundary of the time interval to read data from; t_l in the documentation
	 * @param upto upper boundary of the time interval to read data from; t_u in the documentation
	 */
	public static void indexInterval(int[] target, final double t0, final double dt, final double from, final double upto) {
		target[0] = (int) Math.ceil( (from-t0)/dt);
		target[1] = (int) Math.floor((upto-t0)/dt);
	}

	/**
	 * Compute the file size (and buffer size when reading the file) given the size of the contained data and the number of samples.
	 * These values can be obtained from first just reading the header and then continuing with reading the whole file.
	 * @param valSize size of the raw data values in bytes
	 * @param nSamples number of samples in the file
	 * @return file size to hold the given amount of data using a BinaryTimeseries
	 */
	public static int filesize(final int valSize, final int nSamples) {
		return 64+valSize*nSamples;
	}
	
	
	/***********************
	 *                     *
	 *   WRITING METHODS   *
	 *                     *
	 ***********************/
	
	
	
	
	/**
	 * Write a 1 as {@code short} to the {@code target} file.
	 * This is used to check if correct endianess is used in reading; wrong endianess would lead to reading this as -128.
	 * @param target
	 */
	public static void writeEndianessCheckValue(ByteBuffer target) {
		target.putShort((short)1);
	}

	
	
	
	
	
	/**
	 * 
	 * @param target
	 * @param t0
	 * @param dt
	 */
	public static void writeTimebase(ByteBuffer target, final long t0, final long dt) {
		target.put(DTYPE_LONG);
		target.putLong(t0);
		target.putLong(dt);
	}

	/**
	 * 
	 * @param target
	 * @param t0
	 * @param dt
	 */
	public static void writeTimebase(ByteBuffer target, final double t0, final double dt) {
		target.put(DTYPE_DOUBLE);
		target.putDouble(t0);
		target.putDouble(dt);
	}
	
	
	
	
	
	
	public static void writeScalingDisabled(ByteBuffer target) {
		target.put(withoutScaling((byte)0));
		target.put(new byte[8+8]); // dummy for optional scaling values
	}
	
	public static void writeScaling(ByteBuffer target, final byte o, final byte s) {
		target.put(withScaling(DTYPE_BYTE));
		target.put(o); target.put(new byte[8 - Byte.BYTES]); // offset
		target.put(s); target.put(new byte[8 - Byte.BYTES]); // scale
	}
	
	public static void writeDtypeWithScaling(ByteBuffer target, final short o, final short s) {
		target.put(withScaling(DTYPE_SHORT));
		target.putShort(o); target.put(new byte[8 - Short.BYTES]); // offset
		target.putShort(s); target.put(new byte[8 - Short.BYTES]); // scale
	}
	
	public static void writeScaling(ByteBuffer target, final int o, final int s) {
		target.put(withScaling(DTYPE_INT));
		target.putInt(o); target.put(new byte[8 - Integer.BYTES]); // offset
		target.putInt(s); target.put(new byte[8 - Integer.BYTES]); // scale
	}
	
	public static void writeScaling(ByteBuffer target, final long o, final long s) {
		target.put(withScaling(DTYPE_LONG));
		target.putLong(o); // offset
		target.putLong(s); // scale
	}
	
	public static void writeScaling(ByteBuffer target, final float o, final float s) {
		target.put(withScaling(DTYPE_FLOAT));
		target.putFloat(o); target.put(new byte[8 - Float.BYTES]); // offset
		target.putFloat(s); target.put(new byte[8 - Float.BYTES]); // scale
	}
	
	public static void writeScaling(ByteBuffer target, final double o, final double s) {
		target.put(withScaling(DTYPE_DOUBLE));
		target.putDouble(o); // offset
		target.putDouble(s); // scale
	}
	
	
	
	
	
	
	public static void writeReservedDummy(ByteBuffer target) {
		target.put(new byte[23]);
	}
	
	
	
	
	
	public static void writeData(ByteBuffer target, final byte[] values) {
		target.put(DTYPE_BYTE);
		target.putInt(values.length);
		for (byte b: values) { target.put(b); }
	}
	
	public static void writeData(ByteBuffer target, final short[] values) {
		target.put(DTYPE_SHORT);
		target.putInt(values.length);
		for (short s: values) { target.putShort(s); }
	}
	
	public static void writeData(ByteBuffer target, final int[] values) {
		target.put(DTYPE_INT);
		target.putInt(values.length);
		for (int i: values) { target.putInt(i); }
	}
	
	public static void writeData(ByteBuffer target, final long[] values) {
		target.put(DTYPE_LONG);
		target.putInt(values.length);
		for (long l: values) { target.putLong(l); }
	}
	
	public static void writeData(ByteBuffer target, final float[] values) {
		target.put(DTYPE_FLOAT);
		target.putInt(values.length);
		for (float f: values) { target.putFloat(f); }
	}
	
	public static void writeData(ByteBuffer target, final double[] values) {
		target.put(DTYPE_DOUBLE);
		target.putInt(values.length);
		for (double d: values) { target.putDouble(d); }
	}
	
	
	
	
	
	
	
	/***********************
	 *                     *
	 *   READING METHODS   *
	 *                     *
	 ***********************/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// old stuff below
	

	public static void write(ByteBuffer target, long t0, long dt, byte[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tL_dB(ByteBuffer source, Object[] target) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		((long[])(target[0]))[0] = t0_dt[0]; // t0
		((long[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_byte(source);
		if (target[1] == null || ((byte[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (byte[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, byte[][] target, int targetIdx, long from, long upto) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_byte(source);
		target[targetIdx] = new byte[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, double t0, double dt, byte[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tD_dB(ByteBuffer source, Object[] target) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		((double[])(target[0]))[0] = t0_dt[0]; // t0
		((double[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_byte(source);
		if (target[1] == null || ((byte[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (byte[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, byte[][] target, int targetIdx, double from, double upto) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_byte(source);
		target[targetIdx] = new byte[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, long t0, long dt, short[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tL_dS(ByteBuffer source, Object[] target) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		((long[])(target[0]))[0] = t0_dt[0]; // t0
		((long[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_short(source);
		if (target[1] == null || ((short[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (short[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, short[][] target, int targetIdx, long from, long upto) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_short(source);
		target[targetIdx] = new short[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, double t0, double dt, short[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tD_dS(ByteBuffer source, Object[] target) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		((double[])(target[0]))[0] = t0_dt[0]; // t0
		((double[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_short(source);
		if (target[1] == null || ((short[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (short[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, short[][] target, int targetIdx, double from, double upto) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_short(source);
		target[targetIdx] = new short[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, long t0, long dt, int[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tL_dI(ByteBuffer source, Object[] target) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		((long[])(target[0]))[0] = t0_dt[0]; // t0
		((long[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_int(source);
		if (target[1] == null || ((int[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (int[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, int[][] target, int targetIdx, long from, long upto) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_int(source);
		target[targetIdx] = new int[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, double t0, double dt, int[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tD_dI(ByteBuffer source, Object[] target) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		((double[])(target[0]))[0] = t0_dt[0]; // t0
		((double[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_int(source);
		if (target[1] == null || ((int[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (int[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, int[][] target, int targetIdx, double from, double upto) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_int(source);
		target[targetIdx] = new int[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, long t0, long dt, long[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tL_dL(ByteBuffer source, Object[] target) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		((long[])(target[0]))[0] = t0_dt[0]; // t0
		((long[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_long(source);
		if (target[1] == null || ((long[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (long[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, long[][] target, int targetIdx, long from, long upto) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_long(source);
		target[targetIdx] = new long[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, double t0, double dt, long[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tD_dL(ByteBuffer source, Object[] target) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		((double[])(target[0]))[0] = t0_dt[0]; // t0
		((double[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_long(source);
		if (target[1] == null || ((long[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (long[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, long[][] target, int targetIdx, double from, double upto) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_long(source);
		target[targetIdx] = new long[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, long t0, long dt, float[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tL_dF(ByteBuffer source, Object[] target) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		((long[])(target[0]))[0] = t0_dt[0]; // t0
		((long[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_float(source);
		if (target[1] == null || ((float[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (float[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, float[][] target, int targetIdx, long from, long upto) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_float(source);
		target[targetIdx] = new float[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, double t0, double dt, float[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tD_dF(ByteBuffer source, Object[] target) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		((double[])(target[0]))[0] = t0_dt[0]; // t0
		((double[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_float(source);
		if (target[1] == null || ((float[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (float[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, float[][] target, int targetIdx, double from, double upto) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_float(source);
		target[targetIdx] = new float[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, long t0, long dt, double[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tL_dD(ByteBuffer source, Object[] target) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		((long[])(target[0]))[0] = t0_dt[0]; // t0
		((long[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_double(source);
		if (target[1] == null || ((double[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (double[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, double[][] target, int targetIdx, long from, long upto) {
		readHeader(source);
		long[] t0_dt = readTimeInfo_long(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_double(source);
		target[targetIdx] = new double[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}

	public static void write(ByteBuffer target, double t0, double dt, double[] values) {
		writeEndianessCheckValue(target);
		writeTimebase(target, t0, dt);
		writeValues(target, values);
	}
	public static void read_tD_dD(ByteBuffer source, Object[] target) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		((double[])(target[0]))[0] = t0_dt[0]; // t0
		((double[])(target[0]))[1] = t0_dt[1]; // dt
		int numValues = readNumValues_double(source);
		if (target[1] == null || ((double[])target[1]).length != numValues) {
			throw new RuntimeException("target array has to be of length " + numValues);
		}
		readValues(source, (double[])target[1]);
	}
	public static void read_timeRange(ByteBuffer source, double[][] target, int targetIdx, double from, double upto) {
		readHeader(source);
		double[] t0_dt = readTimeInfo_double(source);
		int[] intervalIndices = new int[2];
		indexInterval(intervalIndices, t0_dt[0], t0_dt[1], from, upto);
		readNumValues_double(source);
		target[targetIdx] = new double[intervalIndices[1]-intervalIndices[0]+1];
		readValues(source, target[targetIdx], intervalIndices[0]);
	}
	
		
	



	// writing methods
	

	public static int writeValues(ByteBuffer target, byte[] values) {
		int initialPos = target.position();
		target.put(DTYPE_BYTE);
		target.putInt(values.length);
		for (byte b: values) { target.put(b); }
		int finalPos = target.position(), expectedPos = initialPos+5+Byte.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("target pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return finalPos;
	}

	public static int writeValues(ByteBuffer target, short[] values) {
		int initialPos = target.position();
		target.put(DTYPE_SHORT);
		target.putInt(values.length);
		for (short s: values) { target.putShort(s); }
		int finalPos = target.position(), expectedPos = initialPos+5+Short.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("target pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return finalPos;
	}

	public static int writeValues(ByteBuffer target, int[] values) {
		int initialPos = target.position();
		target.put(DTYPE_INT);
		target.putInt(values.length);
		for (int i: values) { target.putInt(i); }
		int finalPos = target.position(), expectedPos = initialPos+5+Integer.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("target pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return finalPos;
	}

	public static int writeValues(ByteBuffer target, long[] values) {
		int initialPos = target.position();
		target.put(DTYPE_LONG);
		target.putInt(values.length);
		for (long l: values) { target.putLong(l); }
		int finalPos = target.position(), expectedPos = initialPos+5+Long.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("target pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return finalPos;
	}

	public static int writeValues(ByteBuffer target, float[] values) {
		int initialPos = target.position();
		target.put(DTYPE_FLOAT);
		target.putInt(values.length);
		for (float f: values) { target.putFloat(f); }
		int finalPos = target.position(), expectedPos = initialPos+5+Float.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("target pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return finalPos;
	}

	public static int writeValues(ByteBuffer target, double[] values) {
		int initialPos = target.position();
		target.put(DTYPE_DOUBLE);
		target.putInt(values.length);
		for (double d: values) { target.putDouble(d); }
		int finalPos = target.position(), expectedPos = initialPos+5+Double.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("target pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return finalPos;
	}

	// reading methods
	public static void readHeader(ByteBuffer source) {
		int initialPos = source.position();
		// used to check if correct endianess is used in reading; wrong endianess would lead to reading this as 256.
		short endianessCheck = source.getShort();
		if (endianessCheck==256) {
			// if byteorder of source stream is wrong, reverse it now
			source.order( source.order()==ByteOrder.LITTLE_ENDIAN ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN );
		} else if (endianessCheck != 1) {
			throw new RuntimeException("first short value should be 1 (correct endianess) or 256 (wrong endianess), but not "+endianessCheck);
		}
		int finalPos = source.position(), expectedPos = initialPos+2;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
	}

	public static long[] readTimeInfo_long(ByteBuffer source) {
		int initialPos = source.position();
		byte dtype = source.get();
		if (dtype != DTYPE_LONG) {
			throw new RuntimeException("dtype of time info should be long ("+DTYPE_LONG+") but is "+dtype);
		}
		long t0 = source.getLong();
		long dt = source.getLong();
		int finalPos = source.position(), expectedPos = initialPos+17;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return new long[] { t0, dt };
	}

	public static double[] readTimeInfo_double(ByteBuffer source) {
		int initialPos = source.position();
		byte dtype = source.get();
		if (dtype != DTYPE_DOUBLE) {
			throw new RuntimeException("dtype of time info should be double ("+DTYPE_DOUBLE+") but is "+dtype);
		}
		double t0 = source.getDouble();
		double dt = source.getDouble();
		int finalPos = source.position(), expectedPos = initialPos+17;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return new double[] { t0, dt };
	}


	public static int readNumValues_byte(ByteBuffer source) {
		int initialPos = source.position();
		int dtype = source.get();
		if (dtype != DTYPE_BYTE) {
			throw new RuntimeException("dtype of data info should be byte ("+DTYPE_BYTE+") but is "+dtype);
		}
		int numValues = source.getInt();
		int finalPos = source.position(), expectedPos = initialPos+5;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return numValues;
	}

	public static void readValues(ByteBuffer source, byte[] values) {
		readValues(source, values, 0);
	}
	public static void readValues(ByteBuffer source, byte[] values, int offset) {
		if (offset != 0) source.position(source.position() + offset*Byte.BYTES);
		int initialPos = source.position();
		for (int i=0; i<values.length; ++i) { values[i]=source.get(); }
		int finalPos = source.position(), expectedPos = initialPos+Byte.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("target pos should be at "+expectedPos+" but is at "+finalPos);
		}
	}


	public static int readNumValues_short(ByteBuffer source) {
		int initialPos = source.position();
		int dtype = source.get();
		if (dtype != DTYPE_SHORT) {
			throw new RuntimeException("dtype of data info should be short ("+DTYPE_SHORT+") but is "+dtype);
		}
		int numValues = source.getInt();
		int finalPos = source.position(), expectedPos = initialPos+5;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return numValues;
	}

	public static void readValues(ByteBuffer source, short[] values) {
		readValues(source, values, 0);
	}
	public static void readValues(ByteBuffer source, short[] values, int offset) {
		if (offset != 0) source.position(source.position() + offset*Short.BYTES);
		int initialPos = source.position();
		for (int i=0; i<values.length; ++i) { values[i]=source.getShort(); }
		int finalPos = source.position(), expectedPos = initialPos+Short.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
	}


	public static int readNumValues_int(ByteBuffer source) {
		int initialPos = source.position();
		int dtype = source.get();
		if (dtype != DTYPE_INT) {
			throw new RuntimeException("dtype of data info should be int ("+DTYPE_INT+") but is "+dtype);
		}
		int numValues = source.getInt();
		int finalPos = source.position(), expectedPos = initialPos+5;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return numValues;
	}

	public static void readValues(ByteBuffer source, int[] values) {
		readValues(source, values, 0);
	}
	public static void readValues(ByteBuffer source, int[] values, int offset) {
		if (offset != 0) source.position(source.position() + offset*Integer.BYTES);
		int initialPos = source.position();
		for (int i=0; i<values.length; ++i) { values[i]=source.getInt(); }
		int finalPos = source.position(), expectedPos = initialPos+Integer.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
	}


	public static int readNumValues_long(ByteBuffer source) {
		int initialPos = source.position();
		int dtype = source.get();
		if (dtype != DTYPE_LONG) {
			throw new RuntimeException("dtype of data info should be long ("+DTYPE_LONG+") but is "+dtype);
		}
		int numValues = source.getInt();
		int finalPos = source.position(), expectedPos = initialPos+5;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return numValues;
	}

	public static void readValues(ByteBuffer source, long[] values) {
		readValues(source, values, 0);
	}
	public static void readValues(ByteBuffer source, long[] values, int offset) {
		if (offset != 0) source.position(source.position() + offset*Long.BYTES);
		int initialPos = source.position();
		for (int i=0; i<values.length; ++i) { values[i]=source.getLong(); }
		int finalPos = source.position(), expectedPos = initialPos+Long.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
	}


	public static int readNumValues_float(ByteBuffer source) {
		int initialPos = source.position();
		int dtype = source.get();
		if (dtype != DTYPE_FLOAT) {
			throw new RuntimeException("dtype of data info should be float ("+DTYPE_FLOAT+") but is "+dtype);
		}
		int numValues = source.getInt();
		int finalPos = source.position(), expectedPos = initialPos+5;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return numValues;
	}

	public static void readValues(ByteBuffer source, float[] values) {
		readValues(source, values, 0);
	}
	public static void readValues(ByteBuffer source, float[] values, int offset) {
		if (offset != 0) source.position(source.position() + offset*Float.BYTES);
		int initialPos = source.position();
		for (int i=0; i<values.length; ++i) { values[i]=source.getFloat(); }
		int finalPos = source.position(), expectedPos = initialPos+Float.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
	}



	public static int readNumValues_double(ByteBuffer source) {
		int initialPos = source.position();
		int dtype = source.get();
		if (dtype != DTYPE_DOUBLE) {
			throw new RuntimeException("dtype of data info should be double ("+DTYPE_DOUBLE+") but is "+dtype);
		}
		int numValues = source.getInt();
		int finalPos = source.position(), expectedPos = initialPos+5;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
		return numValues;
	}

	public static void readValues(ByteBuffer source, double[] values) {
		readValues(source, values, 0);
	}
	public static void readValues(ByteBuffer source, double[] values, int offset) {
		if (offset != 0) source.position(source.position() + offset*Double.BYTES);
		int initialPos = source.position();
		for (int i=0; i<values.length; ++i) { values[i]=source.getDouble(); }
		int finalPos = source.position(), expectedPos = initialPos+Double.BYTES*values.length;
		if (finalPos != expectedPos) {
			throw new RuntimeException("source pos should be at "+expectedPos+" but is at "+finalPos);
		}
	}
	
}
