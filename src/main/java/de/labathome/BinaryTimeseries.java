package de.labathome;

import java.nio.ByteBuffer;

/**
 * This is a class to save evenly-sampled time series data in a very simple and easy-to-read format.
 * The key idea is to simply dump a header and then the raw data values one after another into a ByteBuffer.
 * When you want to read only a small subset of the data, you can specify a time or an index range.
 * A scaling and an offset can be defined for the data values (e.g. from an ADC).
 * Examples of how to use this class can be found in Examples.java.
 * 
 * @author Jonathan Schilling (jonathan.schilling@mail.de)
 */
public class BinaryTimeseries {
	
	/**
	 * Identifier value used to indicate that no raw data scaling is used.
	 */
	public static final byte DTYPE_NONE = 0;
	
	/**
	 * Identifier value for {@code byte} datatype. <br/> Length: 1 Byte <br/> Range: -128 ... 127
	 */
	public static final byte DTYPE_BYTE = 1;
	
	/**
	 * Identifier value for {@code short} datatype. <br/> Length: 2 Bytes <br/> Range: -32768 ... 32767
	 */
	public static final byte DTYPE_SHORT = 2;
	
	/**
	 * Identifier value for {@code int} datatype. <br/> Length: 4 Bytes <br/> Range: -2147483648 ... 2147483647
	 */
	public static final byte DTYPE_INT = 3;
	
	/**
	 * Identifier value for {@code long} datatype. <br/> Length: 8 Bytes <br/> Range: -9223372036854775808 ... 9223372036854775807
	 */
	public static final byte DTYPE_LONG = 4;
	
	/**
	 * Identifier value for {@code float} datatype. <br/> Length: 4 Bytes <br/> 7 decimal digits
	 */
	public static final byte DTYPE_FLOAT = 5;
	
	/**
	 * Identifier value for {@code double} datatype. <br/> Length: 8 Bytes <br/> 16 decimal digits
	 */
	public static final byte DTYPE_DOUBLE = 6;
	
	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * The number of values is given by the length of the {@code target} array, into which the values are put.
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0 reference timestamp; will go into {@code target[0]}
	 * @param dt time interval between two samples
	 */
	public static final void buildTimebase(final double[] target, final double t0, final double dt) {
		final int sourceOffset = 0, targetOffset = 0;
		buildTimebase(sourceOffset, target, targetOffset, target.length, t0, dt);
	}
	
	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * All entries in {@code target} from {@code sourceOffset} up to and including {@code sourceOffset+numSamples-1} are filled with appropriate values of the timebase.
	 * @param sourceOffset offset in the time series indices
	 * @param target [numSamples] array into which to put the timebase values t_i
	 * @param targetOffset index at which to put t_0 in the {@code target} array
	 * @param numSamples number of time stamps to generate; has to greater than or equal to {@code target.length}
	 * @param t0 reference timestamp; will go into {@code target[targetOffset]}
	 * @param dt time interval between two consecutive samples
	 */
	public static final void buildTimebase(final int sourceOffset, final double[] target, final int targetOffset, final int numSamples, final double t0, final double dt) {
		for (int i=0; i<numSamples; ++i) {
			target[targetOffset+i] = t0+(sourceOffset+i)*dt;
		}
	}

	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * The number of values is given by the length of the {@code target} array, into which the values are put.
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0 reference timestamp; will go into {@code target[0]}
	 * @param dt time interval between two samples
	 */
	public static final void buildTimebase(final long[] target, final long t0, final long dt) {
		final int sourceOffset = 0, targetOffset = 0;
		buildTimebase(sourceOffset, target, targetOffset, target.length, t0, dt);
	}
	
	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * All entries in {@code target} from {@code sourceOffset} up to and including {@code sourceOffset+numSamples-1} are filled with appropriate values of the timebase.
	 * @param sourceOffset offset in the time series indices
	 * @param target [numSamples] array into which to put the timebase values t_i
	 * @param targetOffset index at which to put t_0 in the {@code target} array
	 * @param numSamples number of time stamps to generate; has to greater than or equal to {@code target.length}
	 * @param t0 reference timestamp; will go into {@code target[targetOffset]}
	 * @param dt time interval between two consecutive samples
	 */
	public static final void buildTimebase(final int sourceOffset, final long[] target, final int targetOffset, final int numSamples, final long t0, final long dt) {
		for (int i=0; i<numSamples; ++i) {
			target[targetOffset+i] = t0+(sourceOffset+i)*dt;
		}
	}

	/**
	 * Given a timebase (t0, dt), compute the first index of timestamps inside the given time interval [{@code t_l}, {@code t_u}].
	 * @param t0 reference timestamp from the file
	 * @param dt time interval between two consecutive samples from the file
	 * @param t_l lower boundary of the time interval to read data from
	 * @return first index inside the time interval [{@code t_l}, {@code t_u}]
	 * @see Eqn. (5) in the documentation
	 */
	public static final int firstIndexInside(final double t0, final double dt, final double t_l) {
		return (int) Math.ceil ((t_l-t0)/dt);
	}
	
	/**
	 * Given a timebase (t0, dt), compute the first index of timestamps inside the given time interval [{@code t_l}, {@code t_u}].
	 * @param t0 reference timestamp from the file
	 * @param dt time interval between two consecutive samples from the file
	 * @param t_u upper boundary of the time interval to read data from
	 * @return last index inside the time interval [{@code t_l}, {@code t_u}]
	 * @see Eqn. (6) in the documentation
	 */
	public static final int lastIndexInside(final double t0, final double dt, final double t_u) {
		return (int) Math.floor((t_u-t0)/dt);
	}
	
	/**
	 * Given a timebase (t0, dt), compute the first index of timestamps inside the given time interval [{@code t_l}, {@code t_u}].
	 * @param t0 reference timestamp from the file
	 * @param dt time interval between two consecutive samples from the file
	 * @param t_l lower boundary of the time interval to read data from
	 * @return first index inside the time interval [{@code t_l}, {@code t_u}]
	 * @see <a href="https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil">https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil</a>
	 * @see Eqn. (7) in the documentation
	 */
	public static final int firstIndexInside(final long t0, final long dt, final long t_l) {
		return (int) ((t_l-t0 + dt - 1) / dt);
	}
	
	/**
	 * Given a timebase (t0, dt), compute the first index of timestamps inside the given time interval [{@code t_l}, {@code t_u}].
	 * @param t0 reference timestamp from the file
	 * @param dt time interval between two consecutive samples from the file
	 * @param t_u upper boundary of the time interval to read data from
	 * @return last index inside the time interval [{@code t_l}, {@code t_u}]
	 * @see Eqn. (8) in the documentation
	 */
	public static final int lastIndexInside(final long t0, final long dt, final long t_u) {
		return (int) ((t_u-t0         ) / dt);
	}
	
	/**
	 * Compute the file offset (and buffer size when reading the file) in bytes given the size of the contained raw data type and the number of samples/index.
	 * These values can be obtained from first just reading the header and then continuing with reading the whole file.
	 * @param dataSize size of the raw data values in bytes
	 * @param index sample index or number of samples in the file
	 * @return byte index or file size to hold the given amount of data using a BinaryTimeseries
	 */
	public static final int fileOffset(final int dataSize, final int index) {
		return 64+dataSize*index;
	}
	
	/***********************
	 *                     *
	 *   WRITING METHODS   *
	 *                     *
	 ***********************/
	
	/**
	 * Write a 1 as {@code short} to the {@code target} file.
	 * This is used to check if correct endianess is used in reading; wrong endianess would lead to reading this as -128.
	 * @param target buffer into which to write the time series data
	 */
	public static final void writeEndianessCheckValue(final ByteBuffer target) {
		target.putShort((short)1);
	}

	/**
	 * Write the timebase parameters t0 and dt to the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param t0 reference timestamp
	 * @param dt time interval between two consecutive samples
	 */
	public static final void writeTimebase(final ByteBuffer target, final long t0, final long dt) {
		target.put(DTYPE_LONG);
		target.putLong(t0);
		target.putLong(dt);
	}

	/**
	 * Write the timebase parameters t0 and dt to the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param t0 reference timestamp
	 * @param dt time interval between two consecutive samples
	 */
	public static final void writeTimebase(final ByteBuffer target, final double t0, final double dt) {
		target.put(DTYPE_DOUBLE);
		target.putDouble(t0);
		target.putDouble(dt);
	}
	
	
	
	
	
	/**
	 * Write the identifier value into the {@code target} buffer that tells the reader that no scaling is available.
	 * @param target buffer into which to write the time series data
	 */
	public static final void writeScalingDisabled(final ByteBuffer target) {
		target.put(DTYPE_NONE);
		target.put(new byte[8+8]);
	}
	
	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param o offset of the raw data values
	 * @param s scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final byte o, final byte s) {
		target.put(DTYPE_BYTE);
		target.put(o); target.put(new byte[8 - Byte.BYTES]);
		target.put(s); target.put(new byte[8 - Byte.BYTES]);
	}
	
	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param o offset of the raw data values
	 * @param s scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final short o, final short s) {
		target.put(DTYPE_SHORT);
		target.putShort(o); target.put(new byte[8 - Short.BYTES]);
		target.putShort(s); target.put(new byte[8 - Short.BYTES]);
	}
	
	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param o offset of the raw data values
	 * @param s scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final int o, final int s) {
		target.put(DTYPE_INT);
		target.putInt(o); target.put(new byte[8 - Integer.BYTES]);
		target.putInt(s); target.put(new byte[8 - Integer.BYTES]);
	}
	
	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param o offset of the raw data values
	 * @param s scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final long o, final long s) {
		target.put(DTYPE_LONG);
		target.putLong(o); // assumes Long.BYTES == 8
		target.putLong(s); // assumes Long.BYTES == 8
	}
	
	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param o offset of the raw data values
	 * @param s scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final float o, final float s) {
		target.put(DTYPE_FLOAT);
		target.putFloat(o); target.put(new byte[8 - Float.BYTES]);
		target.putFloat(s); target.put(new byte[8 - Float.BYTES]);
	}
	
	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param o offset of the raw data values
	 * @param s scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final double o, final double s) {
		target.put(DTYPE_DOUBLE);
		target.putDouble(o); // assumes Double.BYTES == 8
		target.putDouble(s); // assumes Double.BYTES == 8
	}
	
	
	
	
	
	/**
	 * Write zeros for the reserved area in the header.
	 * @param target buffer into which to write the time series data
	 */
	public static final void writeReservedDummy(final ByteBuffer target) {
		target.put(new byte[23]);
	}
	
	
	
	
	/**
	 * Write the raw data values into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target buffer
	 */
	public static final void writeData(final ByteBuffer target, final byte[] values) {
		target.put(DTYPE_BYTE);
		target.putInt(values.length);
		for (byte b: values) { target.put(b); }
	}
	
	/**
	 * Write the raw data values into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target buffer
	 */
	public static final void writeData(final ByteBuffer target, final short[] values) {
		target.put(DTYPE_SHORT);
		target.putInt(values.length);
		for (short s: values) { target.putShort(s); }
	}
	
	/**
	 * Write the raw data values into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target buffer
	 */
	public static final void writeData(final ByteBuffer target, final int[] values) {
		target.put(DTYPE_INT);
		target.putInt(values.length);
		for (int i: values) { target.putInt(i); }
	}
	
	/**
	 * Write the raw data values into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target buffer
	 */
	public static final void writeData(final ByteBuffer target, final long[] values) {
		target.put(DTYPE_LONG);
		target.putInt(values.length);
		for (long l: values) { target.putLong(l); }
	}
	
	/**
	 * Write the raw data values into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target buffer
	 */
	public static final void writeData(final ByteBuffer target, final float[] values) {
		target.put(DTYPE_FLOAT);
		target.putInt(values.length);
		for (float f: values) { target.putFloat(f); }
	}
	
	/**
	 * Write the raw data values into the {@code target} buffer.
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target buffer
	 */
	public static final void writeData(final ByteBuffer target, final double[] values) {
		target.put(DTYPE_DOUBLE);
		target.putInt(values.length);
		for (double d: values) { target.putDouble(d); }
	}
	
	
	
	
	public static final void write(final ByteBuffer target, Object t0, Object dt, Object rawData) {
		write(target, t0, dt, rawData, null, null);
	}
	
	public static final void write(final ByteBuffer target, Object t0, Object dt, Object rawData, Object scalingFactor, Object scalingOffset) {
		BinaryTimeseries.writeEndianessCheckValue(target);
		if (t0 == null) { throw new RuntimeException("t0 cannot be null"); }
		if (dt == null) { throw new RuntimeException("dt cannot be null"); }
		if (Long.class.equals(t0.getClass()) && Long.class.equals(dt.getClass())) {
			BinaryTimeseries.writeTimebase(target, (long)t0, (long)dt);
		} else if (Double.class.equals(t0.getClass()) && Double.class.equals(dt.getClass())) {
			BinaryTimeseries.writeTimebase(target, (double)t0, (double)dt);
		} else {
			throw new RuntimeException("t0 and dt must be of the same class and either long or double");
		}
		if (scalingFactor == null && scalingOffset == null) {
			BinaryTimeseries.writeScalingDisabled(target);
		} else {
			if (scalingFactor == null) { throw new RuntimeException("if scalingOffset is given, also give scalingFactor"); }
			if (scalingOffset == null) { throw new RuntimeException("if scalingFactor is given, also give scalingOffset"); }
			if (Byte.class.equals(scalingFactor.getClass()) && Byte.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (byte)scalingOffset, (byte)scalingFactor);
			} else if (Short.class.equals(scalingFactor.getClass()) && Short.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (short)scalingOffset, (short)scalingFactor);
			} else if (Integer.class.equals(scalingFactor.getClass()) && Integer.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (int)scalingOffset, (int)scalingFactor);
			} else if (Long.class.equals(scalingFactor.getClass()) && Long.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (long)scalingOffset, (long)scalingFactor);
			} else if (Float.class.equals(scalingFactor.getClass()) && Float.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (float)scalingOffset, (float)scalingFactor);
			} else if (Double.class.equals(scalingFactor.getClass()) && Double.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (double)scalingOffset, (double)scalingFactor);
			} else {
				throw new RuntimeException("scalingOffset and scalingFactor must be of the same class, which can be one of (byte, short, int, long, float, double)");
			}
		}
		BinaryTimeseries.writeReservedDummy(target);
		if (rawData == null) { throw new RuntimeException("rawData must not be null"); }
		if (rawData.getClass().isArray()) {
			if (byte.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (byte[])rawData);
			} else if (short.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (short[])rawData);
			} else if (int.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (int[])rawData);
			} else if (long.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (long[])rawData);
			} else if (float.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (float[])rawData);
			} else if (double.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (double[])rawData);
			} else {
				throw new RuntimeException("rawData elements must be of one of the following types: byte, short, int, long, float, double");
			}
		} else {
			throw new RuntimeException("rawData must be an array");
		}
	}
	
	
	
	
	
	
	/***********************
	 *                     *
	 *   READING METHODS   *
	 *                     *
	 ***********************/
	
	public static final boolean readEndianessOk(final ByteBuffer source) {
		final short firstShort = source.getShort();
		if (firstShort == 1) {
			return true;
		} else if (firstShort == -128) {
			return false;
		} else {
			throw new RuntimeException("first short read from source was neither 1 nor -128 but "+firstShort);
		}
	}
	
	
	public static final byte readTimeType(final ByteBuffer source) {
		final byte time_dtype = source.get();
		return time_dtype;
	}
	
	public static final long readTimeT0_long(final ByteBuffer source) {
		final long t0 = source.getLong();
		return t0;
	}
	
	public static final long readTimeDt_long(final ByteBuffer source) {
		final long dt = source.getLong();
		return dt;
	}
	
	public static final double readTimeT0_double(final ByteBuffer source) {
		final double t0 = source.getDouble();
		return t0;
	}
	
	public static final double readTimeDt_double(final ByteBuffer source) {
		final double dt = source.getDouble();
		return dt;
	}
	
	
	
	
	public static final Number[] readTimebase(final ByteBuffer source) {
		final Number[] t0_dt = new Number[2];
		
		final byte time_dtype = readTimeType(source);
		switch(time_dtype) {
		case DTYPE_LONG:
			t0_dt[0] = readTimeT0_long(source);
			t0_dt[1] = readTimeDt_long(source);
			break;
		case DTYPE_DOUBLE:
			t0_dt[0] = readTimeT0_double(source);
			t0_dt[1] = readTimeDt_double(source);
			break;
		default:
			t0_dt[0] = 0;
			t0_dt[1] = 1;
		}
		
		return t0_dt;
	}
	
	
	
	
	/**
	 * Check the given dtype byte to see if the given file has a value scaling or not.
	 * @param scaling_dtype dtype byte as read from input buffer
	 * @return false if the data has no scaling; true if it has scaling
	 */
	public static final boolean hasScaling(final byte scaling_dtype) {
		return (scaling_dtype != 0);
	}
	
	public static final byte readScalingType(final ByteBuffer source) {
		final byte scaling_dtype = source.get();
		return scaling_dtype;
	}
	
	public static final void readScalingDisabled(final ByteBuffer source) {
		final byte[] dummy = new byte[8+8];
		source.get(dummy);
	}
	
	public static final byte readScalingOffset_byte(final ByteBuffer source) {
		final byte scalingOffset = source.get();
		return scalingOffset;
	}
	
	public static final byte readScalingFactor_byte(final ByteBuffer source) {
		final byte scalingFactor = source.get();
		return scalingFactor;
	}
	
	public static final short readScalingOffset_short(final ByteBuffer source) {
		final short scalingOffset = source.getShort();
		return scalingOffset;
	}
	
	public static final short readScalingFactor_short(final ByteBuffer source) {
		final short scalingFactor = source.getShort();
		return scalingFactor;
	}
	
	public static final int readScalingOffset_int(final ByteBuffer source) {
		final int scalingOffset = source.getInt();
		return scalingOffset;
	}
	
	public static final int readScalingFactor_int(final ByteBuffer source) {
		final int scalingFactor = source.getInt();
		return scalingFactor;
	}
	
	public static final long readScalingOffset_long(final ByteBuffer source) {
		final long scalingOffset = source.getLong();
		return scalingOffset;
	}
	
	public static final long readScalingFactor_long(final ByteBuffer source) {
		final long scalingFactor = source.getLong();
		return scalingFactor;
	}
	
	public static final float readScalingOffset_float(final ByteBuffer source) {
		final float scalingOffset = source.getFloat();
		return scalingOffset;
	}
	
	public static final float readScalingFactor_float(final ByteBuffer source) {
		final float scalingFactor = source.getFloat();
		return scalingFactor;
	}
	
	public static final double readScalingOffset_double(final ByteBuffer source) {
		final double scalingOffset = source.getDouble();
		return scalingOffset;
	}
	
	public static final double readScalingFactor_double(final ByteBuffer source) {
		final double scalingFactor = source.getDouble();
		return scalingFactor;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static final void readReservedDummy(final ByteBuffer source) {
		final byte[] dummy = new byte[23];
		source.get(dummy);
	}
	
	
	
	public static final byte readDataType(final ByteBuffer source) {
		final byte data_dtype = source.get();
		return data_dtype;
	}
	
	public static final int readNumSamples(final ByteBuffer source) {
		final int numSamples = source.getInt();
		return numSamples;
	}
	
	public static final void readRawData(final ByteBuffer source, final byte[] target, final int targetOffset, final int numSamples) {
		for (int i=0; i<numSamples; ++i) {
			target[targetOffset+i] = source.get();
		}
	}
	
	public static final void readRawData(final ByteBuffer source, final short[] target, final int targetOffset, final int numSamples) {
		for (int i=0; i<numSamples; ++i) {
			target[targetOffset+i] = source.getShort();
		}
	}
	
	public static final void readRawData(final ByteBuffer source, final int[] target, final int targetOffset, final int numSamples) {
		for (int i=0; i<numSamples; ++i) {
			target[targetOffset+i] = source.getInt();
		}
	}
	
	public static final void readRawData(final ByteBuffer source, final long[] target, final int targetOffset, final int numSamples) {
		for (int i=0; i<numSamples; ++i) {
			target[targetOffset+i] = source.getLong();
		}
	}
	
	public static final void readRawData(final ByteBuffer source, final float[] target, final int targetOffset, final int numSamples) {
		for (int i=0; i<numSamples; ++i) {
			target[targetOffset+i] = source.getFloat();
		}
	}
	
	public static final void readRawData(final ByteBuffer source, final double[] target, final int targetOffset, final int numSamples) {
		for (int i=0; i<numSamples; ++i) {
			target[targetOffset+i] = source.getDouble();
		}
	}
	
	public static final byte[] readScaledDataIntoByte(final ByteBuffer source) {
		
		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {
			
			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final byte[] target = new byte[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			
			} else if (scaling_dtype == DTYPE_BYTE) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final byte[] target = new byte[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final byte[] target = new byte[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final byte[] target = new byte[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final byte[] target = new byte[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final byte[] target = new byte[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else {
				// silently ignore unknown scaling values
				readScalingDisabled(source);
				
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final byte[] target = new byte[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = source.get();
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) source.getShort();
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) source.getInt();
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) source.getLong();
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) source.getFloat();
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (byte) source.getDouble();
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);

			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);
			final byte[] target = new byte[numSamples];
			if (data_dtype == DTYPE_BYTE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (byte) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (byte) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (byte) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (byte) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (byte) source.getDouble();
				}
			}
			// silently ignore unknown data dtype and return an array filled with zeros
			return target;
		}
	}
	
	public static final short[] readScaledDataIntoShort(final ByteBuffer source) {
		
		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {
			
			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final short[] target = new short[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			
			} else if (scaling_dtype == DTYPE_BYTE) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final short[] target = new short[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final short[] target = new short[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final short[] target = new short[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final short[] target = new short[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final short[] target = new short[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else {
				// silently ignore unknown scaling values
				readScalingDisabled(source);
				
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final short[] target = new short[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = source.get();
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) source.getShort();
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) source.getInt();
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) source.getLong();
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) source.getFloat();
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (short) source.getDouble();
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);

			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);
			final short[] target = new short[numSamples];
			if (data_dtype == DTYPE_BYTE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (short) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (short) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (short) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (short) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (short) source.getDouble();
				}
			}
			// silently ignore unknown data dtype and return an array filled with zeros
			return target;
		}
	}
	
	public static final int[] readScaledDataIntoInt(final ByteBuffer source) {
		
		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {
			
			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final int[] target = new int[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			
			} else if (scaling_dtype == DTYPE_BYTE) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final int[] target = new int[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final int[] target = new int[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final int[] target = new int[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final int[] target = new int[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final int[] target = new int[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else {
				// silently ignore unknown scaling values
				readScalingDisabled(source);
				
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final int[] target = new int[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = source.get();
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) source.getShort();
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) source.getInt();
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) source.getLong();
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) source.getFloat();
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (int) source.getDouble();
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);

			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);
			final int[] target = new int[numSamples];
			if (data_dtype == DTYPE_BYTE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (int) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (int) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (int) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (int) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (int) source.getDouble();
				}
			}
			// silently ignore unknown data dtype and return an array filled with zeros
			return target;
		}
	}
	
	
	
	public static final long[] readScaledDataIntoLong(final ByteBuffer source) {
		
		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {
			
			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final long[] target = new long[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			
			} else if (scaling_dtype == DTYPE_BYTE) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final long[] target = new long[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final long[] target = new long[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final long[] target = new long[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final long[] target = new long[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final long[] target = new long[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else {
				// silently ignore unknown scaling values
				readScalingDisabled(source);
				
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final long[] target = new long[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = source.get();
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) source.getShort();
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) source.getInt();
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) source.getLong();
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) source.getFloat();
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (long) source.getDouble();
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);

			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);
			final long[] target = new long[numSamples];
			if (data_dtype == DTYPE_BYTE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (long) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (long) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (long) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (long) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (long) source.getDouble();
				}
			}
			// silently ignore unknown data dtype and return an array filled with zeros
			return target;
		}
	}
	
	
	public static final float[] readScaledDataIntoFloat(final ByteBuffer source) {
		
		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {
			
			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final float[] target = new float[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			
			} else if (scaling_dtype == DTYPE_BYTE) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final float[] target = new float[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final float[] target = new float[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final float[] target = new float[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final float[] target = new float[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final float[] target = new float[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else {
				// silently ignore unknown scaling values
				readScalingDisabled(source);
				
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final float[] target = new float[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = source.get();
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) source.getShort();
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) source.getInt();
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) source.getLong();
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) source.getFloat();
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (float) source.getDouble();
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);

			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);
			final float[] target = new float[numSamples];
			if (data_dtype == DTYPE_BYTE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (float) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (float) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (float) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (float) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (float) source.getDouble();
				}
			}
			// silently ignore unknown data dtype and return an array filled with zeros
			return target;
		}
	}
	
	public static final double[] readScaledDataIntoDouble(final ByteBuffer source) {
		
		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {
			
			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final double[] target = new double[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			
			} else if (scaling_dtype == DTYPE_BYTE) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final double[] target = new double[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final double[] target = new double[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final double[] target = new double[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final double[] target = new double[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);

				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final double[] target = new double[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			} else {
				// silently ignore unknown scaling values
				readScalingDisabled(source);
				
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);
				final double[] target = new double[numSamples];
				if (data_dtype == DTYPE_BYTE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = source.get();
					}
				} else if (data_dtype == DTYPE_SHORT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) source.getShort();
					}
				} else if (data_dtype == DTYPE_INT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) source.getInt();
					}
				} else if (data_dtype == DTYPE_LONG) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) source.getLong();
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) source.getFloat();
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					for (int i=0; i<numSamples; ++i) {
						target[i] = (double) source.getDouble();
					}
				}
				// silently ignore unknown data dtype and return an array filled with zeros
				return target;
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);

			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);
			final double[] target = new double[numSamples];
			if (data_dtype == DTYPE_BYTE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (double) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (double) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (double) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (double) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				for (int i=0; i<numSamples; ++i) {
					target[i] = (double) source.getDouble();
				}
			}
			// silently ignore unknown data dtype and return an array filled with zeros
			return target;
		}
	}
}
