package de.labathome;

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
		return (data_dtype & 1<<7) == 1;
	}
	
	/**
	 * Enable scaling for the given dtype, no matter if it was enabled previously or not.
	 * @param data_dtype input datatype
	 * @return copy of data_dtype with the highest bit set to true
	 */
	public static final byte withScaling(final byte data_dtype) {
		return (byte) (data_dtype | (1<<7));
	}
	
	/**
	 * Disable scaling for the given dtype, no matter if it was enabled previously or not.
	 * @param data_dtype input dtype, 0...5
	 * @return copy of data_dtype with the highest bit set to false
	 */
	public static final byte withoutScaling(final byte data_dtype) {
		return (byte) (data_dtype & ~(1<<7));
	}
	
	/**
	 * Compute the timebase values for a given t_0 and Delta_t.
	 * The number of values is given by the length of the {@code target} array, into which the values are put.
	 * @param target [N] array into which to put the timebase values t_i.
	 * @param t0 reference timestamp; will go into {@code target[0]}
	 * @param dt time interval between two samples
	 */
	public static void buildTimebase(double[] target, double t0, double dt) {
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
	public static void buildTimebase(double[] target, double t0, double dt, int startIdx, int endIdx) {
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
	public static void buildTimebase(long[] target, long t0, long dt) {
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
	public static void buildTimebase(long[] target, long t0, long dt, int startIdx, int endIdx) {
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
	public static void indexInterval(int[] target, long t0, long dt, long from, long upto) {
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
	public static void indexInterval(int[] target, double t0, double dt, double from, double upto) {
		target[0] = (int)Math.ceil( (from-t0)/dt);
		target[1] = (int)Math.floor((upto-t0)/dt);
	}

	/**
	 * Compute the file size (and buffer size when reading the file) given the size of the contained data and the number of samples.
	 * These values can be obtained from first just reading the header and then continuing with reading the whole file.
	 * @param valSize size of the raw data values in bytes
	 * @param nSamples number of samples in the file
	 * @return file size to hold the given amount of data using a BinaryTimeseries
	 */
	public static int filesize(int valSize, int nSamples) {
		return 64+valSize*nSamples;
	}
}
