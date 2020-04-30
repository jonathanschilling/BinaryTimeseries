package de.labathome;

import java.nio.ByteBuffer;

/**
 * This is a class to save evenly-sampled time series data in a very simple and
 * easy-to-read format. The key idea is to simply dump a header and then the raw
 * data values one after another into a ByteBuffer. When you want to read only a
 * small subset of the data, you can specify a time or an index range. A scaling
 * and an offset can be defined for the data values (e.g. from an ADC). Examples
 * of how to use this class can be found in Examples.java.
 * 
 * @author Jonathan Schilling (jonathan.schilling@mail.de)
 */
public class BinaryTimeseries {

	/**
	 * Identifier value used to indicate that no raw data scaling is used.
	 */
	public static final byte DTYPE_NONE = 0;

	/**
	 * Identifier value for {@code byte} datatype. <br>
	 * Length: 1 Byte <br>
	 * Range: -128 ... 127
	 */
	public static final byte DTYPE_BYTE = 1;

	/**
	 * Identifier value for {@code short} datatype. <br>
	 * Length: 2 Bytes <br>
	 * Range: -32768 ... 32767
	 */
	public static final byte DTYPE_SHORT = 2;

	/**
	 * Identifier value for {@code int} datatype. <br>
	 * Length: 4 Bytes <br>
	 * Range: -2147483648 ... 2147483647
	 */
	public static final byte DTYPE_INT = 3;

	/**
	 * Identifier value for {@code long} datatype. <br>
	 * Length: 8 Bytes <br>
	 * Range: -9223372036854775808 ... 9223372036854775807
	 */
	public static final byte DTYPE_LONG = 4;

	/**
	 * Identifier value for {@code float} datatype. <br>
	 * Length: 4 Bytes <br>
	 * 7 decimal digits
	 */
	public static final byte DTYPE_FLOAT = 5;

	/**
	 * Identifier value for {@code double} datatype. <br>
	 * Length: 8 Bytes <br>
	 * 16 decimal digits
	 */
	public static final byte DTYPE_DOUBLE = 6;

	/**
	 * Get a human-readable identification string for a given data type.
	 * 
	 * @param dtype one of [DTYPE_NONE, DTYPE_BYTE, DTYPE_SHORT, DTYPE_INT,
	 *              DTYPE_LONG, DTYPE_FLOAT, DTYPE_DOUBLE]
	 * @return one of ["N", "B", "S", "I", "L", "F", "D"] or "?" if it could not be
	 *         identified
	 */
	public static final String dtypeStr(final byte dtype) {
		if (dtype == BinaryTimeseries.DTYPE_NONE) {
			return "N";
		} else if (dtype == BinaryTimeseries.DTYPE_BYTE) {
			return "B";
		} else if (dtype == BinaryTimeseries.DTYPE_SHORT) {
			return "S";
		} else if (dtype == BinaryTimeseries.DTYPE_INT) {
			return "I";
		} else if (dtype == BinaryTimeseries.DTYPE_LONG) {
			return "L";
		} else if (dtype == BinaryTimeseries.DTYPE_FLOAT) {
			return "F";
		} else if (dtype == BinaryTimeseries.DTYPE_DOUBLE) {
			return "D";
		} else {
			return "?";
		}
	}

	/**
	 * Compute the timebase values for a given t_0 and Delta_t. The number of values
	 * is given by the length of the {@code target} array, into which the values are
	 * put.
	 * 
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0     reference timestamp; will go into {@code target[0]}
	 * @param dt     time interval between two samples
	 */
	public static final void buildTimebase(final long[] target, final long t0, final long dt) {
		final int sourceOffset = 0, targetOffset = 0;
		buildTimebase(sourceOffset, target, targetOffset, target.length, t0, dt);
	}

	/**
	 * Compute the timebase values for a given t_0 and Delta_t. All entries in
	 * {@code target} from {@code sourceOffset} up to and including
	 * {@code sourceOffset+numSamples-1} are filled with appropriate values of the
	 * timebase.
	 * 
	 * @param sourceOffset offset in the time series indices
	 * @param target       [numSamples] array into which to put the timebase values
	 *                     t_i
	 * @param targetOffset index at which to put t_0 in the {@code target} array
	 * @param numSamples   number of time stamps to generate; has to greater than or
	 *                     equal to {@code target.length}
	 * @param t0           reference timestamp; will go into
	 *                     {@code target[targetOffset]}
	 * @param dt           time interval between two consecutive samples
	 */
	public static final void buildTimebase(final int sourceOffset, final long[] target, final int targetOffset,
			final int numSamples, final long t0, final long dt) {
		for (int i = 0; i < numSamples; ++i) {
			target[targetOffset + i] = t0 + (sourceOffset + i) * dt;
		}
	}

	/**
	 * Compute the timebase values for a given t_0 and Delta_t. The number of values
	 * is given by the length of the {@code target} array, into which the values are
	 * put.
	 * 
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0     reference timestamp; will go into {@code target[0]}
	 * @param dt     time interval between two samples
	 */
	public static final void buildTimebase(final double[] target, final double t0, final double dt) {
		final int sourceOffset = 0, targetOffset = 0;
		buildTimebase(sourceOffset, target, targetOffset, target.length, t0, dt);
	}

	/**
	 * Compute the timebase values for a given t_0 and Delta_t. All entries in
	 * {@code target} from {@code sourceOffset} up to and including
	 * {@code sourceOffset+numSamples-1} are filled with appropriate values of the
	 * timebase.
	 * 
	 * @param sourceOffset offset in the time series indices
	 * @param target       [numSamples] array into which to put the timebase values
	 *                     t_i
	 * @param targetOffset index at which to put t_0 in the {@code target} array
	 * @param numSamples   number of time stamps to generate; has to greater than or
	 *                     equal to {@code target.length}
	 * @param t0           reference timestamp; will go into
	 *                     {@code target[targetOffset]}
	 * @param dt           time interval between two consecutive samples
	 */
	public static final void buildTimebase(final int sourceOffset, final double[] target, final int targetOffset,
			final int numSamples, final double t0, final double dt) {
		for (int i = 0; i < numSamples; ++i) {
			target[targetOffset + i] = t0 + (sourceOffset + i) * dt;
		}
	}

	/**
	 * Given a timebase (t0, dt), compute the first index of timestamps inside the
	 * given time interval [{@code t_l}, {@code t_u}]. See also Eqn. (7) in the
	 * documentation.
	 * 
	 * @param t0  reference timestamp from the file
	 * @param dt  time interval between two consecutive samples from the file
	 * @param t_l lower boundary of the time interval to read data from
	 * @return first index inside the time interval [{@code t_l}, {@code t_u}]
	 * @see <a href=
	 *      "https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil">https://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil</a>
	 */
	public static final int firstIndexInside(final long t0, final long dt, final long t_l) {
		return (int) ((t_l - t0 + dt - 1) / dt);
	}

	/**
	 * Given a timebase (t0, dt), compute the first index of timestamps inside the
	 * given time interval [{@code t_l}, {@code t_u}]. See also Eqn. (8) in the
	 * documentation.
	 * 
	 * @param t0  reference timestamp from the file
	 * @param dt  time interval between two consecutive samples from the file
	 * @param t_u upper boundary of the time interval to read data from
	 * @return last index inside the time interval [{@code t_l}, {@code t_u}]
	 */
	public static final int lastIndexInside(final long t0, final long dt, final long t_u) {
		return (int) ((t_u - t0) / dt);
	}

	/**
	 * Given a timebase (t0, dt), compute the first index of timestamps inside the
	 * given time interval [{@code t_l}, {@code t_u}]. See also Eqn. (5) in the
	 * documentation.
	 * 
	 * @param t0  reference timestamp from the file
	 * @param dt  time interval between two consecutive samples from the file
	 * @param t_l lower boundary of the time interval to read data from
	 * @return first index inside the time interval [{@code t_l}, {@code t_u}]
	 */
	public static final int firstIndexInside(final double t0, final double dt, final double t_l) {
		return (int) Math.ceil((t_l - t0) / dt);
	}

	/**
	 * Given a timebase (t0, dt), compute the first index of timestamps inside the
	 * given time interval [{@code t_l}, {@code t_u}]. See also Eqn. (6) in the
	 * documentation.
	 * 
	 * @param t0  reference timestamp from the file
	 * @param dt  time interval between two consecutive samples from the file
	 * @param t_u upper boundary of the time interval to read data from
	 * @return last index inside the time interval [{@code t_l}, {@code t_u}]
	 */
	public static final int lastIndexInside(final double t0, final double dt, final double t_u) {
		return (int) Math.floor((t_u - t0) / dt);
	}

	/**
	 * Compute the file offset (and buffer size when reading the file) in bytes
	 * given the size of the contained raw data type and the number of
	 * samples/index. These values can be obtained from first just reading the
	 * header and then continuing with reading the whole file.
	 * 
	 * @param dataSize size of the raw data values in bytes
	 * @param index    sample index or number of samples in the file
	 * @return byte index or file size to hold the given amount of data using a
	 *         BinaryTimeseries
	 */
	public static final int fileOffset(final int dataSize, final int index) {
		return 64 + dataSize * index;
	}

	/**
	 * Given 64 header bytes, generate a human-readable explanation similar to Tab.
	 * 2 of the documentation which lists the header contents. This routine stops
	 * explaning at the first occurrence of an invalid entry.
	 * 
	 * @param header [64] binary header bytes
	 * @return a multi-line text (\n as line separator) which tells the user about
	 *         the contents of the given header info
	 */
	public static final String explainHeader(byte[] header) {
		if (header == null || header.length != 64) {
			return "header should not be null and have a length of 64 bytes";
		}

		int previousPosition = 0;
		String headerExplanation = "";
		final ByteBuffer source = ByteBuffer.wrap(header);

		previousPosition = source.position();
		final short firstShort = source.getShort();
		headerExplanation += String.format(" %2d  %2d reads 0x%02X", previousPosition,
				source.position() - previousPosition, firstShort);
		if (firstShort == 1) {
			headerExplanation += " => correct endianess\n";
		} else if (firstShort == 256) {
			headerExplanation += " => incorrect endianess";
			return headerExplanation;
		} else {
			headerExplanation += " => invalid endianess check value: " + firstShort;
			return headerExplanation;
		}

		previousPosition = source.position();
		final byte time_dtype = source.get();
		headerExplanation += String.format(" %2d  %2d reads 0x%01X", previousPosition,
				source.position() - previousPosition, time_dtype);
		if (time_dtype == DTYPE_LONG) {
			headerExplanation += " => timestamps as long\n";
		} else if (time_dtype == DTYPE_DOUBLE) {
			headerExplanation += " => timestamps as double";
			return headerExplanation;
		} else {
			headerExplanation += " => invalid timestamps data type: " + time_dtype;
			return headerExplanation;
		}

		if (time_dtype == DTYPE_LONG) {
			previousPosition = source.position();
			long t0 = source.getLong();
			headerExplanation += String.format(" %2d  %2d reads 0x%016X => t0 = %d\n", previousPosition,
					source.position() - previousPosition, t0, t0);
			previousPosition = source.position();
			long dt = source.getLong();
			headerExplanation += String.format(" %2d  %2d reads 0x%016X => dt = %d\n", previousPosition,
					source.position() - previousPosition, dt, dt);
		} else if (time_dtype == DTYPE_LONG) {
			previousPosition = source.position();
			double t0 = source.getDouble();
			headerExplanation += String.format(" %2d  %2d reads 0x%016X => t0 = %g\n", previousPosition,
					source.position() - previousPosition, t0, t0);
			previousPosition = source.position();
			double dt = source.getDouble();
			headerExplanation += String.format(" %2d  %2d reads 0x%016X => dt = %g\n", previousPosition,
					source.position() - previousPosition, dt, dt);
		}

		previousPosition = source.position();
		final byte scaling_dtype = source.get();
		headerExplanation += String.format(" %2d  %2d reads 0x%01X", previousPosition,
				source.position() - previousPosition, scaling_dtype);
		if (scaling_dtype == DTYPE_NONE) {
			headerExplanation += " => no scaling\n";
		} else if (scaling_dtype == DTYPE_BYTE) {
			headerExplanation += " => scaling as byte\n";
		} else if (scaling_dtype == DTYPE_SHORT) {
			headerExplanation += " => scaling as short\n";
		} else if (scaling_dtype == DTYPE_INT) {
			headerExplanation += " => scaling as int\n";
		} else if (scaling_dtype == DTYPE_LONG) {
			headerExplanation += " => scaling as long\n";
		} else if (scaling_dtype == DTYPE_FLOAT) {
			headerExplanation += " => scaling as float\n";
		} else if (scaling_dtype == DTYPE_DOUBLE) {
			headerExplanation += " => scaling as double\n";
		} else {
			headerExplanation += " => invalid scaling type: " + scaling_dtype;
			return headerExplanation;
		}

		if (scaling_dtype == DTYPE_NONE) {
			final byte[] dummy = new byte[8];

			previousPosition = source.position();
			source.get(dummy);
			String contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format("[%2d] %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

			previousPosition = source.position();
			source.get(dummy);
			contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format("[%2d] %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

		} else if (scaling_dtype == DTYPE_BYTE) {
			final byte[] dummy = new byte[8 - Byte.BYTES];

			previousPosition = source.position();
			byte scalingOffset = source.get();
			headerExplanation += String.format(" %2d  %2d reads 0x%01X => scalingOffset = %d\n", previousPosition,
					source.position() - previousPosition, scalingOffset, scalingOffset);

			previousPosition = source.position();
			source.get(dummy);
			String contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

			previousPosition = source.position();
			byte scalingFactor = source.get();
			headerExplanation += String.format(" %2d  %2d reads 0x%01X => scalingFactor = %d\n", previousPosition,
					source.position() - previousPosition, scalingFactor, scalingFactor);

			previousPosition = source.position();
			source.get(dummy);
			contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

		} else if (scaling_dtype == DTYPE_SHORT) {
			final byte[] dummy = new byte[8 - Short.BYTES];

			previousPosition = source.position();
			short scalingOffset = source.getShort();
			headerExplanation += String.format(" %2d  %2d reads 0x%02X => scalingOffset = %d\n", previousPosition,
					source.position() - previousPosition, scalingOffset, scalingOffset);

			previousPosition = source.position();
			source.get(dummy);
			String contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

			previousPosition = source.position();
			short scalingFactor = source.getShort();
			headerExplanation += String.format(" %2d  %2d reads 0x%02X => scalingFactor = %d\n", previousPosition,
					source.position() - previousPosition, scalingFactor, scalingFactor);

			previousPosition = source.position();
			source.get(dummy);
			contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

		} else if (scaling_dtype == DTYPE_INT) {
			final byte[] dummy = new byte[8 - Integer.BYTES];

			previousPosition = source.position();
			int scalingOffset = source.getInt();
			headerExplanation += String.format(" %2d  %2d reads 0x%04X => scalingOffset = %d\n", previousPosition,
					source.position() - previousPosition, scalingOffset, scalingOffset);

			previousPosition = source.position();
			source.get(dummy);
			String contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

			previousPosition = source.position();
			int scalingFactor = source.getInt();
			headerExplanation += String.format(" %2d  %2d reads 0x%04X => scalingFactor = %d\n", previousPosition,
					source.position() - previousPosition, scalingFactor, scalingFactor);

			previousPosition = source.position();
			source.get(dummy);
			contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

		} else if (scaling_dtype == DTYPE_LONG) {
			previousPosition = source.position();
			long scalingOffset = source.getLong();
			headerExplanation += String.format(" %2d  %2d reads 0x%016X => scalingOffset = %d\n", previousPosition,
					source.position() - previousPosition, scalingOffset, scalingOffset);

			previousPosition = source.position();
			long scalingFactor = source.getLong();
			headerExplanation += String.format(" %2d  %2d reads 0x%016X => scalingFactor = %d\n", previousPosition,
					source.position() - previousPosition, scalingFactor, scalingFactor);

		} else if (scaling_dtype == DTYPE_FLOAT) {
			final byte[] dummy = new byte[8 - Float.BYTES];

			previousPosition = source.position();
			float scalingOffset = source.getFloat();
			headerExplanation += String.format(" %2d  %2d reads 0x%04X => scalingOffset = %g\n", previousPosition,
					source.position() - previousPosition, scalingOffset, scalingOffset);

			previousPosition = source.position();
			source.get(dummy);
			String contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

			previousPosition = source.position();
			float scalingFactor = source.getFloat();
			headerExplanation += String.format(" %2d  %2d reads 0x%04X => scalingFactor = %g\n", previousPosition,
					source.position() - previousPosition, scalingFactor, scalingFactor);

			previousPosition = source.position();
			source.get(dummy);
			contents = "";
			for (int i = 0; i < dummy.length - 1; ++i) {
				contents += String.format("0x%02X ", dummy[i]);
			}
			contents += String.format("0x%02X", dummy[dummy.length - 1]);
			headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
					source.position() - previousPosition, contents);

		} else if (scaling_dtype == DTYPE_DOUBLE) {
			previousPosition = source.position();
			double scalingOffset = source.getDouble();
			headerExplanation += String.format(" %2d  %2d reads 0x%08X => scalingOffset = %g\n", previousPosition,
					source.position() - previousPosition, scalingOffset, scalingOffset);

			previousPosition = source.position();
			double scalingFactor = source.getDouble();
			headerExplanation += String.format(" %2d  %2d reads 0x%08X => scalingFactor = %g\n", previousPosition,
					source.position() - previousPosition, scalingFactor, scalingFactor);
		}

		previousPosition = source.position();
		final byte[] reservedDummy = new byte[23];
		source.get(reservedDummy);
		String contents = "";
		for (int i = 0; i < reservedDummy.length - 1; ++i) {
			contents += String.format("0x%02X ", reservedDummy[i]);
		}
		contents += String.format("0x%02X", reservedDummy[reservedDummy.length - 1]);
		headerExplanation += String.format(" %2d  %2d reads %s\n", previousPosition,
				source.position() - previousPosition, contents);

		previousPosition = source.position();
		final byte data_dtype = source.get();
		headerExplanation += String.format(" %2d  %2d reads 0x%01X", previousPosition,
				source.position() - previousPosition, data_dtype);
		if (data_dtype == DTYPE_BYTE) {
			headerExplanation += " => raw data as byte\n";
		} else if (data_dtype == DTYPE_SHORT) {
			headerExplanation += " => raw data as short\n";
		} else if (data_dtype == DTYPE_INT) {
			headerExplanation += " => raw data as int\n";
		} else if (data_dtype == DTYPE_LONG) {
			headerExplanation += " => raw data as long\n";
		} else if (data_dtype == DTYPE_FLOAT) {
			headerExplanation += " => raw data as float\n";
		} else if (data_dtype == DTYPE_DOUBLE) {
			headerExplanation += " => raw data as double\n";
		} else {
			headerExplanation += " => invalid raw data type: " + data_dtype;
			return headerExplanation;
		}

		previousPosition = source.position();
		final int numSamples = source.getInt();
		headerExplanation += String.format(" %2d  %2d reads 0x%04X", previousPosition,
				source.position() - previousPosition, numSamples);
		if (numSamples > 0) {
			headerExplanation += String.format(" => number of samples = %d", numSamples);
		} else {
			headerExplanation += String.format(" => invalid number of samples (would be interpreted as %d)",
					numSamples);
		}

		return headerExplanation;
	}

	/***********************
	 * * WRITING METHODS * *
	 ***********************/

	/**
	 * Write a 1 as {@code short} to the {@code target} file. This is used to check
	 * if correct endianess is used in reading; wrong endianess would lead to
	 * reading this as 256.
	 * 
	 * @param target buffer into which to write the time series data
	 */
	public static final void writeEndianessCheckValue(final ByteBuffer target) {
		target.putShort((short) 1);
	}

	/**
	 * Write the timebase parameters t0 and dt to the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param t0     reference timestamp
	 * @param dt     time interval between two consecutive samples
	 */
	public static final void writeTimebase(final ByteBuffer target, final long t0, final long dt) {
		target.put(DTYPE_LONG);
		target.putLong(t0);
		target.putLong(dt);
	}

	/**
	 * Write the timebase parameters t0 and dt to the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param t0     reference timestamp
	 * @param dt     time interval between two consecutive samples
	 */
	public static final void writeTimebase(final ByteBuffer target, final double t0, final double dt) {
		target.put(DTYPE_DOUBLE);
		target.putDouble(t0);
		target.putDouble(dt);
	}

	/**
	 * Write the identifier value into the {@code target} buffer that tells the
	 * reader that no scaling is available.
	 * 
	 * @param target buffer into which to write the time series data
	 */
	public static final void writeScalingDisabled(final ByteBuffer target) {
		target.put(DTYPE_NONE);
		target.put(new byte[8 + 8]);
	}

	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param o      offset of the raw data values
	 * @param s      scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final byte o, final byte s) {
		target.put(DTYPE_BYTE);
		target.put(o);
		target.put(new byte[8 - Byte.BYTES]);
		target.put(s);
		target.put(new byte[8 - Byte.BYTES]);
	}

	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param o      offset of the raw data values
	 * @param s      scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final short o, final short s) {
		target.put(DTYPE_SHORT);
		target.putShort(o);
		target.put(new byte[8 - Short.BYTES]);
		target.putShort(s);
		target.put(new byte[8 - Short.BYTES]);
	}

	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param o      offset of the raw data values
	 * @param s      scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final int o, final int s) {
		target.put(DTYPE_INT);
		target.putInt(o);
		target.put(new byte[8 - Integer.BYTES]);
		target.putInt(s);
		target.put(new byte[8 - Integer.BYTES]);
	}

	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param o      offset of the raw data values
	 * @param s      scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final long o, final long s) {
		target.put(DTYPE_LONG);
		target.putLong(o); // assumes Long.BYTES == 8
		target.putLong(s); // assumes Long.BYTES == 8
	}

	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param o      offset of the raw data values
	 * @param s      scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final float o, final float s) {
		target.put(DTYPE_FLOAT);
		target.putFloat(o);
		target.put(new byte[8 - Float.BYTES]);
		target.putFloat(s);
		target.put(new byte[8 - Float.BYTES]);
	}

	/**
	 * Write the scaling parameters o and s into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param o      offset of the raw data values
	 * @param s      scaling factor of the raw data values
	 */
	public static final void writeScaling(final ByteBuffer target, final double o, final double s) {
		target.put(DTYPE_DOUBLE);
		target.putDouble(o); // assumes Double.BYTES == 8
		target.putDouble(s); // assumes Double.BYTES == 8
	}

	/**
	 * Write zeros for the reserved area in the header.
	 * 
	 * @param target buffer into which to write the time series data
	 */
	public static final void writeReservedDummy(final ByteBuffer target) {
		target.put(new byte[23]);
	}

	/**
	 * Write the raw data values into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target
	 *               buffer
	 */
	public static final void writeData(final ByteBuffer target, final byte[] values) {
		target.put(DTYPE_BYTE);
		target.putInt(values.length);
		for (byte b : values) {
			target.put(b);
		}
	}

	/**
	 * Write the raw data values into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target
	 *               buffer
	 */
	public static final void writeData(final ByteBuffer target, final short[] values) {
		target.put(DTYPE_SHORT);
		target.putInt(values.length);
		for (short s : values) {
			target.putShort(s);
		}
	}

	/**
	 * Write the raw data values into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target
	 *               buffer
	 */
	public static final void writeData(final ByteBuffer target, final int[] values) {
		target.put(DTYPE_INT);
		target.putInt(values.length);
		for (int i : values) {
			target.putInt(i);
		}
	}

	/**
	 * Write the raw data values into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target
	 *               buffer
	 */
	public static final void writeData(final ByteBuffer target, final long[] values) {
		target.put(DTYPE_LONG);
		target.putInt(values.length);
		for (long l : values) {
			target.putLong(l);
		}
	}

	/**
	 * Write the raw data values into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target
	 *               buffer
	 */
	public static final void writeData(final ByteBuffer target, final float[] values) {
		target.put(DTYPE_FLOAT);
		target.putInt(values.length);
		for (float f : values) {
			target.putFloat(f);
		}
	}

	/**
	 * Write the raw data values into the {@code target} buffer.
	 * 
	 * @param target buffer into which to write the time series data
	 * @param values raw data values; all of them will be dumped into the target
	 *               buffer
	 */
	public static final void writeData(final ByteBuffer target, final double[] values) {
		target.put(DTYPE_DOUBLE);
		target.putInt(values.length);
		for (double d : values) {
			target.putDouble(d);
		}
	}

	/**
	 * Create a BinaryTimeseries with the timebase parameters {@code t0} and
	 * {@code dt} and the unscaled {@code rawData}.
	 * 
	 * @param target  buffer into which to write the time series data
	 * @param t0      reference timestamp; can be {@code double} or {@code long}
	 * @param dt      reference timestamp; can be {@code double} or {@code long}
	 * @param rawData raw data array; can be {@code byte[]}, {@code short[]},
	 *                {@code int[]}, {@code long[]}, {@code float[]} or
	 *                {@code double[]}
	 */
	public static final void write(final ByteBuffer target, Object t0, Object dt, Object rawData) {
		write(target, t0, dt, rawData, null, null);
	}

	/**
	 * Create a BinaryTimeseries with the timebase parameters {@code t0} and
	 * {@code dt} and the unscaled {@code rawData} and the scaling parameters
	 * {@code scalingOffset} and {@code scalingFactor}.
	 * 
	 * @param target        buffer into which to write the time series data
	 * @param t0            reference timestamp; can be {@code double} or
	 *                      {@code long}
	 * @param dt            reference timestamp; can be {@code double} or
	 *                      {@code long}
	 * @param rawData       raw data array; can be {@code byte[]}, {@code short[]},
	 *                      {@code int[]}, {@code long[]}, {@code float[]} or
	 *                      {@code double[]}
	 * @param scalingOffset scaling offset of the raw data to be saved into the
	 *                      file; can be {@code null}, {@code byte}, {@code short},
	 *                      {@code int}, {@code long}, {@code float} or
	 *                      {@code double}
	 * @param scalingFactor scaling factor of the raw data to be saved into the
	 *                      file; can be {@code null}, {@code byte}, {@code short},
	 *                      {@code int}, {@code long}, {@code float} or
	 *                      {@code double}
	 */
	public static final void write(final ByteBuffer target, Object t0, Object dt, Object rawData, Object scalingOffset,
			Object scalingFactor) {
		BinaryTimeseries.writeEndianessCheckValue(target);
		if (t0 == null) {
			throw new RuntimeException("t0 cannot be null");
		}
		if (dt == null) {
			throw new RuntimeException("dt cannot be null");
		}
		if (Long.class.equals(t0.getClass()) && Long.class.equals(dt.getClass())) {
			BinaryTimeseries.writeTimebase(target, (long) t0, (long) dt);
		} else if (Double.class.equals(t0.getClass()) && Double.class.equals(dt.getClass())) {
			BinaryTimeseries.writeTimebase(target, (double) t0, (double) dt);
		} else {
			throw new RuntimeException("t0 and dt must be of the same class and either long or double");
		}
		if (scalingFactor == null && scalingOffset == null) {
			BinaryTimeseries.writeScalingDisabled(target);
		} else {
			if (scalingFactor == null) {
				throw new RuntimeException("if scalingOffset is given, also give scalingFactor");
			}
			if (scalingOffset == null) {
				throw new RuntimeException("if scalingFactor is given, also give scalingOffset");
			}
			if (Byte.class.equals(scalingFactor.getClass()) && Byte.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (byte) scalingOffset, (byte) scalingFactor);
			} else if (Short.class.equals(scalingFactor.getClass()) && Short.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (short) scalingOffset, (short) scalingFactor);
			} else if (Integer.class.equals(scalingFactor.getClass())
					&& Integer.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (int) scalingOffset, (int) scalingFactor);
			} else if (Long.class.equals(scalingFactor.getClass()) && Long.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (long) scalingOffset, (long) scalingFactor);
			} else if (Float.class.equals(scalingFactor.getClass()) && Float.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (float) scalingOffset, (float) scalingFactor);
			} else if (Double.class.equals(scalingFactor.getClass()) && Double.class.equals(scalingFactor.getClass())) {
				BinaryTimeseries.writeScaling(target, (double) scalingOffset, (double) scalingFactor);
			} else {
				throw new RuntimeException(
						"scalingOffset and scalingFactor must be of the same class, which can be one of (byte, short, int, long, float, double)");
			}
		}
		BinaryTimeseries.writeReservedDummy(target);
		if (rawData == null) {
			throw new RuntimeException("rawData must not be null");
		}
		if (rawData.getClass().isArray()) {
			if (byte.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (byte[]) rawData);
			} else if (short.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (short[]) rawData);
			} else if (int.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (int[]) rawData);
			} else if (long.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (long[]) rawData);
			} else if (float.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (float[]) rawData);
			} else if (double.class.equals(rawData.getClass().getComponentType())) {
				BinaryTimeseries.writeData(target, (double[]) rawData);
			} else {
				throw new RuntimeException(
						"rawData elements must be of one of the following types: byte, short, int, long, float, double");
			}
		} else {
			throw new RuntimeException("rawData must be an array");
		}
	}

	/***********************
	 * * READING METHODS * *
	 ***********************/

	/**
	 * Read the 64 byte header from the given source buffer.
	 * 
	 * @param source buffer from which to read the first 64 bytes
	 * @return the first 64 bytes as read from {@code source}
	 */
	public static final byte[] readHeader(final ByteBuffer source) {
		final byte[] header = new byte[64];
		source.get(header);
		return header;
	}

	/**
	 * Read the first {@code short} value from the {@code source} buffer and check
	 * if it was correctly read as 1.
	 * 
	 * @param source buffer from which to read
	 * @return true if the read value was 1, false if if was 256 (indicating wrong
	 *         endianess of {@code source}
	 * @throws RuntimeException in any case something else than 1 or 256 was read
	 */
	public static final boolean readEndianessOk(final ByteBuffer source) {
		final short firstShort = source.getShort();
		if (firstShort == 1) {
			return true;
		} else if (firstShort == 256) {
			return false;
		} else {
			throw new RuntimeException("first short read from source was neither 1 nor 256 but " + firstShort);
		}
	}

	/**
	 * Read the timestamp data type from the {@code source} buffer.
	 * 
	 * @param source buffer from which to read
	 * @return timestamp dtype; should either be {@code DTYPE_LONG} or
	 *         {@code DTYPE_DOUBLE}
	 */
	public static final byte readTimeType(final ByteBuffer source) {
		final byte time_dtype = source.get();
		return time_dtype;
	}

	/**
	 * Read the reference timestamp t_0 from the given {@code source} buffer.
	 * 
	 * @param source buffer from which to read
	 * @return reference timestamp read as {@code long}
	 */
	public static final long readTimeT0_long(final ByteBuffer source) {
		final long t0 = source.getLong();
		return t0;
	}

	/**
	 * Read the time interval between two consecutive samples Delta_t from the given
	 * {@code source} buffer.
	 * 
	 * @param source buffer from which to read
	 * @return time interval between two consecutive samples Delta_t read as
	 *         {@code long}
	 */
	public static final long readTimeDt_long(final ByteBuffer source) {
		final long dt = source.getLong();
		return dt;
	}

	/**
	 * Read the reference timestamp t_0 from the given {@code source} buffer.
	 * 
	 * @param source buffer from which to read
	 * @return reference timestamp read as {@code double}
	 */
	public static final double readTimeT0_double(final ByteBuffer source) {
		final double t0 = source.getDouble();
		return t0;
	}

	/**
	 * Read the time interval between two consecutive samples Delta_t from the given
	 * {@code source} buffer.
	 * 
	 * @param source buffer from which to read
	 * @return time interval between two consecutive samples Delta_t read as
	 *         {@code double}
	 */
	public static final double readTimeDt_double(final ByteBuffer source) {
		final double dt = source.getDouble();
		return dt;
	}

	/**
	 * Check the given dtype byte to see if the given file has a value scaling or
	 * not.
	 * 
	 * @param scaling_dtype dtype byte as read from input buffer
	 * @return false if the data has no scaling; true if it has scaling
	 */
	public static final boolean hasScaling(final byte scaling_dtype) {
		return (scaling_dtype != 0);
	}

	/**
	 * Read the scaling data type from the given {@code source} buffer.
	 * 
	 * @param source buffer from which to read
	 * @return one of {@code DTYPE_NONE}, {@code DTYPE_BYTE}, {@code DTYPE_SHORT},
	 *         {@code DTYPE_INT}, {@code DTYPE_LONG}, {@code DTYPE_FLOAT} or
	 *         {@code DTYPE_DOUBLE}
	 */
	public static final byte readScalingType(final ByteBuffer source) {
		final byte scaling_dtype = source.get();
		return scaling_dtype;
	}

	/**
	 * Proceed with reading from the given {@code source} buffer over the bytes
	 * reserved in the header for optional scaling parameters. Currently, this
	 * simply reads 16 bytes from the buffer.
	 * 
	 * @param source buffer from which to read
	 */
	public static final void readScalingDisabled(final ByteBuffer source) {
		source.get(new byte[8 + 8]);
	}

	/**
	 * Read the scaling offset from the given {@code source} buffer as {@code byte}
	 * and proceed reading until a total of 8 bytes has been read to stay aligned
	 * with the defined file structure.
	 * 
	 * @param source buffer from which to read
	 * @return scaling offset read as {@code byte}
	 */
	public static final byte readScalingOffset_byte(final ByteBuffer source) {
		final byte scalingOffset = source.get();
		source.get(new byte[8 - Byte.BYTES]);
		return scalingOffset;
	}

	/**
	 * Read the scaling factor from the given {@code source} buffer as {@code byte}
	 * and proceed reading until a total of 8 bytes has been read to stay aligned
	 * with the defined file structure.
	 * 
	 * @param source buffer from which to read
	 * @return scaling factor read as {@code byte}
	 */
	public static final byte readScalingFactor_byte(final ByteBuffer source) {
		final byte scalingFactor = source.get();
		source.get(new byte[8 - Byte.BYTES]);
		return scalingFactor;
	}

	/**
	 * Read the scaling offset from the given {@code source} buffer as {@code short}
	 * and proceed reading until a total of 8 bytes has been read to stay aligned
	 * with the defined file structure.
	 * 
	 * @param source buffer from which to read
	 * @return scaling offset read as {@code short}
	 */
	public static final short readScalingOffset_short(final ByteBuffer source) {
		final short scalingOffset = source.getShort();
		source.get(new byte[8 - Short.BYTES]);
		return scalingOffset;
	}

	/**
	 * Read the scaling factor from the given {@code source} buffer as {@code short}
	 * and proceed reading until a total of 8 bytes has been read to stay aligned
	 * with the defined file structure.
	 * 
	 * @param source buffer from which to read
	 * @return scaling factor read as {@code short}
	 */
	public static final short readScalingFactor_short(final ByteBuffer source) {
		final short scalingFactor = source.getShort();
		source.get(new byte[8 - Short.BYTES]);
		return scalingFactor;
	}

	/**
	 * Read the scaling offset from the given {@code source} buffer as {@code int}
	 * and proceed reading until a total of 8 bytes has been read to stay aligned
	 * with the defined file structure.
	 * 
	 * @param source buffer from which to read
	 * @return scaling offset read as {@code int}
	 */
	public static final int readScalingOffset_int(final ByteBuffer source) {
		final int scalingOffset = source.getInt();
		source.get(new byte[8 - Integer.BYTES]);
		return scalingOffset;
	}

	/**
	 * Read the scaling factor from the given {@code source} buffer as {@code int}
	 * and proceed reading until a total of 8 bytes has been read to stay aligned
	 * with the defined file structure.
	 * 
	 * @param source buffer from which to read
	 * @return scaling factor read as {@code int}
	 */
	public static final int readScalingFactor_int(final ByteBuffer source) {
		final int scalingFactor = source.getInt();
		source.get(new byte[8 - Integer.BYTES]);
		return scalingFactor;
	}

	/**
	 * Read the scaling offset from the given {@code source} buffer as {@code long}.
	 * 
	 * @param source buffer from which to read
	 * @return scaling offset read as {@code long}
	 */
	public static final long readScalingOffset_long(final ByteBuffer source) {
		final long scalingOffset = source.getLong();
		return scalingOffset;
	}

	/**
	 * Read the scaling factor from the given {@code source} buffer as {@code long}.
	 * 
	 * @param source buffer from which to read
	 * @return scaling factor read as {@code long}
	 */
	public static final long readScalingFactor_long(final ByteBuffer source) {
		final long scalingFactor = source.getLong();
		return scalingFactor;
	}

	/**
	 * Read the scaling offset from the given {@code source} buffer as {@code float}
	 * and proceed reading until a total of 8 bytes has been read to stay aligned
	 * with the defined file structure.
	 * 
	 * @param source buffer from which to read
	 * @return scaling offset read as {@code float}
	 */
	public static final float readScalingOffset_float(final ByteBuffer source) {
		final float scalingOffset = source.getFloat();
		source.get(new byte[8 - Float.BYTES]);
		return scalingOffset;
	}

	/**
	 * Read the scaling factor from the given {@code source} buffer as {@code float}
	 * and proceed reading until a total of 8 bytes has been read to stay aligned
	 * with the defined file structure.
	 * 
	 * @param source buffer from which to read
	 * @return scaling factor read as {@code float}
	 */
	public static final float readScalingFactor_float(final ByteBuffer source) {
		final float scalingFactor = source.getFloat();
		source.get(new byte[8 - Float.BYTES]);
		return scalingFactor;
	}

	/**
	 * Read the scaling offset from the given {@code source} buffer as
	 * {@code double}.
	 * 
	 * @param source buffer from which to read
	 * @return scaling offset read as {@code double}
	 */
	public static final double readScalingOffset_double(final ByteBuffer source) {
		final double scalingOffset = source.getDouble();
		return scalingOffset;
	}

	/**
	 * Read the scaling factor from the given {@code source} buffer as
	 * {@code double}.
	 * 
	 * @param source buffer from which to read
	 * @return scaling factor read as {@code double}
	 */
	public static final double readScalingFactor_double(final ByteBuffer source) {
		final double scalingFactor = source.getDouble();
		return scalingFactor;
	}

	/**
	 * Proceed with reading from the given {@code source} buffer over the bytes
	 * reserved in the header. Currently, this simply reads 23 bytes from the
	 * buffer.
	 * 
	 * @param source buffer from which to read
	 */
	public static final void readReservedDummy(final ByteBuffer source) {
		source.get(new byte[23]);
	}

	/**
	 * Read the raw data type from the given {@code source} buffer.
	 * 
	 * @param source buffer from which to read
	 * @return one of {@code DTYPE_BYTE}, {@code DTYPE_SHORT}, {@code DTYPE_INT},
	 *         {@code DTYPE_LONG}, {@code DTYPE_FLOAT} or {@code DTYPE_DOUBLE}
	 */
	public static final byte readDataType(final ByteBuffer source) {
		final byte data_dtype = source.get();
		return data_dtype;
	}

	/**
	 * Read the number of samples from the given {@code source} buffer.
	 * 
	 * @param source buffer from which to read
	 * @return number of samples; should be &gt; 0
	 */
	public static final int readNumSamples(final ByteBuffer source) {
		final int numSamples = source.getInt();
		return numSamples;
	}

	/**
	 * Read the raw data from the given {@code source} buffer and put them into a
	 * given {@code target} array at an offset specified by {@code targetOffset}.
	 * 
	 * @param source       buffer from which to read
	 * @param target       array into which to read the given samples
	 * @param targetOffset offset in {@code target} at which to put the first sample
	 *                     read from the {@code source} buffer
	 * @param numSamples   number of samples to be read from the {@code source}
	 *                     buffer
	 */
	public static final void readRawData(final ByteBuffer source, final byte[] target, final int targetOffset,
			final int numSamples) {
		for (int i = 0; i < numSamples; ++i) {
			target[targetOffset + i] = source.get();
		}
	}

	/**
	 * Read the raw data from the given {@code source} buffer and put them into a
	 * given {@code target} array at an offset specified by {@code targetOffset}.
	 * 
	 * @param source       buffer from which to read
	 * @param target       array into which to read the given samples
	 * @param targetOffset offset in {@code target} at which to put the first sample
	 *                     read from the {@code source} buffer
	 * @param numSamples   number of samples to be read from the {@code source}
	 *                     buffer
	 */
	public static final void readRawData(final ByteBuffer source, final short[] target, final int targetOffset,
			final int numSamples) {
		for (int i = 0; i < numSamples; ++i) {
			target[targetOffset + i] = source.getShort();
		}
	}

	/**
	 * Read the raw data from the given {@code source} buffer and put them into a
	 * given {@code target} array at an offset specified by {@code targetOffset}.
	 * 
	 * @param source       buffer from which to read
	 * @param target       array into which to read the given samples
	 * @param targetOffset offset in {@code target} at which to put the first sample
	 *                     read from the {@code source} buffer
	 * @param numSamples   number of samples to be read from the {@code source}
	 *                     buffer
	 */
	public static final void readRawData(final ByteBuffer source, final int[] target, final int targetOffset,
			final int numSamples) {
		for (int i = 0; i < numSamples; ++i) {
			target[targetOffset + i] = source.getInt();
		}
	}

	/**
	 * Read the raw data from the given {@code source} buffer and put them into a
	 * given {@code target} array at an offset specified by {@code targetOffset}.
	 * 
	 * @param source       buffer from which to read
	 * @param target       array into which to read the given samples
	 * @param targetOffset offset in {@code target} at which to put the first sample
	 *                     read from the {@code source} buffer
	 * @param numSamples   number of samples to be read from the {@code source}
	 *                     buffer
	 */
	public static final void readRawData(final ByteBuffer source, final long[] target, final int targetOffset,
			final int numSamples) {
		for (int i = 0; i < numSamples; ++i) {
			target[targetOffset + i] = source.getLong();
		}
	}

	/**
	 * Read the raw data from the given {@code source} buffer and put them into a
	 * given {@code target} array at an offset specified by {@code targetOffset}.
	 * 
	 * @param source       buffer from which to read
	 * @param target       array into which to read the given samples
	 * @param targetOffset offset in {@code target} at which to put the first sample
	 *                     read from the {@code source} buffer
	 * @param numSamples   number of samples to be read from the {@code source}
	 *                     buffer
	 */
	public static final void readRawData(final ByteBuffer source, final float[] target, final int targetOffset,
			final int numSamples) {
		for (int i = 0; i < numSamples; ++i) {
			target[targetOffset + i] = source.getFloat();
		}
	}

	/**
	 * Read the raw data from the given {@code source} buffer and put them into a
	 * given {@code target} array at an offset specified by {@code targetOffset}.
	 * 
	 * @param source       buffer from which to read
	 * @param target       array into which to read the given samples
	 * @param targetOffset offset in {@code target} at which to put the first sample
	 *                     read from the {@code source} buffer
	 * @param numSamples   number of samples to be read from the {@code source}
	 *                     buffer
	 */
	public static final void readRawData(final ByteBuffer source, final double[] target, final int targetOffset,
			final int numSamples) {
		for (int i = 0; i < numSamples; ++i) {
			target[targetOffset + i] = source.getDouble();
		}
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code byte[]} array.
	 * 
	 * @param source buffer from which to read
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final byte[] readData_byte(final ByteBuffer source) {
		final int firstDataIndex = 0, lastDataIndex = -1;
		return readData_byte(source, firstDataIndex, lastDataIndex);
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code byte[]} array.
	 * 
	 * @param source         buffer from which to read
	 * @param firstDataIndex the index of the first sample to read from the given
	 *                       {@code buffer}
	 * @param lastDataIndex  the index of the last sample to read from the given
	 *                       {@code buffer}; -1 means read all available samples
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 *         in the range {@code firstDataIndex} up to and including
	 *         {@code lastDataIndex}
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final byte[] readData_byte(final ByteBuffer source, final int firstDataIndex,
			final int lastDataIndex) {

		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {

			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final byte[] target = new byte[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;

			} else if (scaling_dtype == DTYPE_SHORT) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final byte[] target = new byte[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final byte[] target = new byte[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final byte[] target = new byte[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final byte[] target = new byte[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final byte[] target = new byte[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (byte) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else {
				throw new RuntimeException("unknown scaling dtype");
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);
			readReservedDummy(source);
			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);

			final int numToRead;
			if (lastDataIndex == -1) {
				numToRead = numSamples - firstDataIndex;
			} else {
				numToRead = lastDataIndex - firstDataIndex + 1;
			}
			final int currentPosition = source.position();

			final byte[] target = new byte[numToRead];
			if (data_dtype == DTYPE_BYTE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Byte.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Short.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (byte) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Integer.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (byte) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Long.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (byte) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Float.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (byte) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Double.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (byte) source.getDouble();
				}
			} else {
				throw new RuntimeException("unknown data dtype");
			}
			return target;
		}
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code short[]} array.
	 * 
	 * @param source buffer from which to read
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final short[] readData_short(final ByteBuffer source) {
		final int firstDataIndex = 0, lastDataIndex = -1;
		return readData_short(source, firstDataIndex, lastDataIndex);
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code short[]} array.
	 * 
	 * @param source         buffer from which to read
	 * @param firstDataIndex the index of the first sample to read from the given
	 *                       {@code buffer}
	 * @param lastDataIndex  the index of the last sample to read from the given
	 *                       {@code buffer}; -1 means read all available samples
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 *         in the range {@code firstDataIndex} up to and including
	 *         {@code lastDataIndex}
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final short[] readData_short(final ByteBuffer source, final int firstDataIndex,
			final int lastDataIndex) {

		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {

			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final short[] target = new short[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;

			} else if (scaling_dtype == DTYPE_SHORT) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final short[] target = new short[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final short[] target = new short[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final short[] target = new short[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final short[] target = new short[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final short[] target = new short[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (short) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else {
				throw new RuntimeException("unknown scaling dtype");
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);
			readReservedDummy(source);
			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);

			final int numToRead;
			if (lastDataIndex == -1) {
				numToRead = numSamples - firstDataIndex;
			} else {
				numToRead = lastDataIndex - firstDataIndex + 1;
			}
			final int currentPosition = source.position();

			final short[] target = new short[numToRead];
			if (data_dtype == DTYPE_BYTE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Byte.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Short.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (short) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Integer.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (short) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Long.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (short) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Float.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (short) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Double.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (short) source.getDouble();
				}
			} else {
				throw new RuntimeException("unknown data dtype");
			}
			return target;
		}
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code int[]} array.
	 * 
	 * @param source buffer from which to read
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final int[] readData_int(final ByteBuffer source) {
		final int firstDataIndex = 0, lastDataIndex = -1;
		return readData_int(source, firstDataIndex, lastDataIndex);
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code int[]} array.
	 * 
	 * @param source         buffer from which to read
	 * @param firstDataIndex the index of the first sample to read from the given
	 *                       {@code buffer}
	 * @param lastDataIndex  the index of the last sample to read from the given
	 *                       {@code buffer}; -1 means read all available samples
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 *         in the range {@code firstDataIndex} up to and including
	 *         {@code lastDataIndex}
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final int[] readData_int(final ByteBuffer source, final int firstDataIndex, final int lastDataIndex) {

		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {

			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final int[] target = new int[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;

			} else if (scaling_dtype == DTYPE_SHORT) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final int[] target = new int[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final int[] target = new int[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final int[] target = new int[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final int[] target = new int[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final int[] target = new int[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (int) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else {
				throw new RuntimeException("unknown scaling dtype");
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);
			readReservedDummy(source);
			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);

			final int numToRead;
			if (lastDataIndex == -1) {
				numToRead = numSamples - firstDataIndex;
			} else {
				numToRead = lastDataIndex - firstDataIndex + 1;
			}
			final int currentPosition = source.position();

			final int[] target = new int[numToRead];
			if (data_dtype == DTYPE_BYTE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Byte.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Short.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (int) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Integer.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (int) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Long.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (int) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Float.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (int) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Double.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (int) source.getDouble();
				}
			} else {
				throw new RuntimeException("unknown data dtype");
			}
			return target;
		}
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code long[]} array.
	 * 
	 * @param source buffer from which to read
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final long[] readData_long(final ByteBuffer source) {
		final int firstDataIndex = 0, lastDataIndex = -1;
		return readData_long(source, firstDataIndex, lastDataIndex);
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code long[]} array.
	 * 
	 * @param source         buffer from which to read
	 * @param firstDataIndex the index of the first sample to read from the given
	 *                       {@code buffer}
	 * @param lastDataIndex  the index of the last sample to read from the given
	 *                       {@code buffer}; -1 means read all available samples
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 *         in the range {@code firstDataIndex} up to and including
	 *         {@code lastDataIndex}
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final long[] readData_long(final ByteBuffer source, final int firstDataIndex,
			final int lastDataIndex) {

		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {

			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final long[] target = new long[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;

			} else if (scaling_dtype == DTYPE_SHORT) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final long[] target = new long[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final long[] target = new long[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final long[] target = new long[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final long[] target = new long[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final long[] target = new long[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (long) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else {
				throw new RuntimeException("unknown scaling dtype");
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);
			readReservedDummy(source);
			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);

			final int numToRead;
			if (lastDataIndex == -1) {
				numToRead = numSamples - firstDataIndex;
			} else {
				numToRead = lastDataIndex - firstDataIndex + 1;
			}
			final int currentPosition = source.position();

			final long[] target = new long[numToRead];
			if (data_dtype == DTYPE_BYTE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Byte.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Short.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (long) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Integer.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (long) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Long.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (long) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Float.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (long) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Double.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (long) source.getDouble();
				}
			} else {
				throw new RuntimeException("unknown data dtype");
			}
			return target;
		}
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code float[]} array.
	 * 
	 * @param source buffer from which to read
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final float[] readData_float(final ByteBuffer source) {
		final int firstDataIndex = 0, lastDataIndex = -1;
		return readData_float(source, firstDataIndex, lastDataIndex);
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code float[]} array.
	 * 
	 * @param source         buffer from which to read
	 * @param firstDataIndex the index of the first sample to read from the given
	 *                       {@code buffer}
	 * @param lastDataIndex  the index of the last sample to read from the given
	 *                       {@code buffer}; -1 means read all available samples
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 *         in the range {@code firstDataIndex} up to and including
	 *         {@code lastDataIndex}
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final float[] readData_float(final ByteBuffer source, final int firstDataIndex,
			final int lastDataIndex) {

		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {

			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final float[] target = new float[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;

			} else if (scaling_dtype == DTYPE_SHORT) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final float[] target = new float[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final float[] target = new float[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final float[] target = new float[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final float[] target = new float[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final float[] target = new float[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (float) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else {
				throw new RuntimeException("unknown scaling dtype");
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);
			readReservedDummy(source);
			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);

			final int numToRead;
			if (lastDataIndex == -1) {
				numToRead = numSamples - firstDataIndex;
			} else {
				numToRead = lastDataIndex - firstDataIndex + 1;
			}
			final int currentPosition = source.position();

			final float[] target = new float[numToRead];
			if (data_dtype == DTYPE_BYTE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Byte.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Short.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (float) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Integer.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (float) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Long.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (float) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Float.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (float) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Double.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (float) source.getDouble();
				}
			} else {
				throw new RuntimeException("unknown data dtype");
			}
			return target;
		}
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code double[]} array.
	 * 
	 * @param source buffer from which to read
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final double[] readData_double(final ByteBuffer source) {
		final int firstDataIndex = 0, lastDataIndex = -1;
		return readData_double(source, firstDataIndex, lastDataIndex);
	}

	/**
	 * Read the scaling parameters, the reserved (dummy) area, the raw data type and
	 * number of samples and the raw data; scale the raw data according to the
	 * (possibly present) scaling parameters and put the resulting samples into a
	 * {@code double[]} array.
	 * 
	 * @param source         buffer from which to read
	 * @param firstDataIndex the index of the first sample to read from the given
	 *                       {@code buffer}
	 * @param lastDataIndex  the index of the last sample to read from the given
	 *                       {@code buffer}; -1 means read all available samples
	 * @return an array containing the (scaled) data from the {@code source} buffer
	 *         in the range {@code firstDataIndex} up to and including
	 *         {@code lastDataIndex}
	 * @throws RuntimeException if an unknown scaling type or data type was
	 *                          encountered
	 */
	public static final double[] readData_double(final ByteBuffer source, final int firstDataIndex,
			final int lastDataIndex) {

		final byte scaling_dtype = readScalingType(source);
		if (hasScaling(scaling_dtype)) {

			if (scaling_dtype == DTYPE_BYTE) {
				final byte scalingOffset = readScalingOffset_byte(source);
				final byte scalingFactor = readScalingFactor_byte(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final double[] target = new double[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;

			} else if (scaling_dtype == DTYPE_SHORT) {
				final short scalingOffset = readScalingOffset_short(source);
				final short scalingFactor = readScalingFactor_short(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final double[] target = new double[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_INT) {
				final int scalingOffset = readScalingOffset_int(source);
				final int scalingFactor = readScalingFactor_int(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final double[] target = new double[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_LONG) {
				final long scalingOffset = readScalingOffset_long(source);
				final long scalingFactor = readScalingFactor_long(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final double[] target = new double[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_FLOAT) {
				final float scalingOffset = readScalingOffset_float(source);
				final float scalingFactor = readScalingFactor_float(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final double[] target = new double[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else if (scaling_dtype == DTYPE_DOUBLE) {
				final double scalingOffset = readScalingOffset_double(source);
				final double scalingFactor = readScalingFactor_double(source);
				readReservedDummy(source);
				final byte data_dtype = readDataType(source);
				final int numSamples = readNumSamples(source);

				final int numToRead;
				if (lastDataIndex == -1) {
					numToRead = numSamples - firstDataIndex;
				} else {
					numToRead = lastDataIndex - firstDataIndex + 1;
				}
				final int currentPosition = source.position();

				final double[] target = new double[numToRead];
				if (data_dtype == DTYPE_BYTE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Byte.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numSamples; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.get());
					}
				} else if (data_dtype == DTYPE_SHORT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Short.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getShort());
					}
				} else if (data_dtype == DTYPE_INT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Integer.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getInt());
					}
				} else if (data_dtype == DTYPE_LONG) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Long.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getLong());
					}
				} else if (data_dtype == DTYPE_FLOAT) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Float.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getFloat());
					}
				} else if (data_dtype == DTYPE_DOUBLE) {
					if (firstDataIndex != 0) {
						final int bytesToSkip = Double.BYTES * firstDataIndex;
						source.position(currentPosition + bytesToSkip);
					}
					for (int i = 0; i < numToRead; ++i) {
						target[i] = (double) (scalingOffset + scalingFactor * source.getDouble());
					}
				} else {
					throw new RuntimeException("unknown data dtype");
				}
				return target;
			} else {
				throw new RuntimeException("unknown scaling dtype");
			}
		} else {
			// no scaling provided, so read raw data
			readScalingDisabled(source);
			readReservedDummy(source);
			final byte data_dtype = readDataType(source);
			final int numSamples = readNumSamples(source);

			final int numToRead;
			if (lastDataIndex == -1) {
				numToRead = numSamples - firstDataIndex;
			} else {
				numToRead = lastDataIndex - firstDataIndex + 1;
			}
			final int currentPosition = source.position();

			final double[] target = new double[numToRead];
			if (data_dtype == DTYPE_BYTE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Byte.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = source.get();
				}
			} else if (data_dtype == DTYPE_SHORT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Short.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (double) source.getShort();
				}
			} else if (data_dtype == DTYPE_INT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Integer.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (double) source.getInt();
				}
			} else if (data_dtype == DTYPE_LONG) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Long.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (double) source.getLong();
				}
			} else if (data_dtype == DTYPE_FLOAT) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Float.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (double) source.getFloat();
				}
			} else if (data_dtype == DTYPE_DOUBLE) {
				if (firstDataIndex != 0) {
					final int bytesToSkip = Double.BYTES * firstDataIndex;
					source.position(currentPosition + bytesToSkip);
				}
				for (int i = 0; i < numToRead; ++i) {
					target[i] = (double) source.getDouble();
				}
			} else {
				throw new RuntimeException("unknown data dtype");
			}
			return target;
		}
	}
}
