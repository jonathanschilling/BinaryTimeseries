package de.labathome;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class GenerateTestData {

	public static final byte[] time_dtypes = new byte[] {
			BinaryTimeseries.DTYPE_LONG,
			BinaryTimeseries.DTYPE_DOUBLE
	};

	public static final byte[] scaling_dtypes = new byte[] {
			BinaryTimeseries.DTYPE_NONE,
			BinaryTimeseries.DTYPE_BYTE,
			BinaryTimeseries.DTYPE_SHORT,
			BinaryTimeseries.DTYPE_INT,
			BinaryTimeseries.DTYPE_LONG,
			BinaryTimeseries.DTYPE_FLOAT,
			BinaryTimeseries.DTYPE_DOUBLE
	};

	public static final byte[] data_dtypes = new byte[] {
			BinaryTimeseries.DTYPE_BYTE,
			BinaryTimeseries.DTYPE_SHORT,
			BinaryTimeseries.DTYPE_INT,
			BinaryTimeseries.DTYPE_LONG,
			BinaryTimeseries.DTYPE_FLOAT,
			BinaryTimeseries.DTYPE_DOUBLE
	};

	public static final int[] data_sizes = new int[] {
			Byte.BYTES,
			Short.BYTES,
			Integer.BYTES,
			Long.BYTES,
			Float.BYTES,
			Double.BYTES };
	
	public static final Map<Byte, String> javaName;
	public static final Map<Byte, String> javaClassName;
	static {
		javaName = new HashMap<>();
		javaName.put(BinaryTimeseries.DTYPE_NONE,   "null");
		javaName.put(BinaryTimeseries.DTYPE_BYTE,   "byte");
		javaName.put(BinaryTimeseries.DTYPE_SHORT,  "short");
		javaName.put(BinaryTimeseries.DTYPE_INT,    "int");
		javaName.put(BinaryTimeseries.DTYPE_LONG,   "long");
		javaName.put(BinaryTimeseries.DTYPE_FLOAT,  "float");
		javaName.put(BinaryTimeseries.DTYPE_DOUBLE, "double");
		
		javaClassName = new HashMap<>();
		javaClassName.put(BinaryTimeseries.DTYPE_NONE,   "null");
		javaClassName.put(BinaryTimeseries.DTYPE_BYTE,   "Byte");
		javaClassName.put(BinaryTimeseries.DTYPE_SHORT,  "Short");
		javaClassName.put(BinaryTimeseries.DTYPE_INT,    "Integer");
		javaClassName.put(BinaryTimeseries.DTYPE_LONG,   "Long");
		javaClassName.put(BinaryTimeseries.DTYPE_FLOAT,  "Float");
		javaClassName.put(BinaryTimeseries.DTYPE_DOUBLE, "Double");
	}
	
	// number of samples to put into the test files
	public static final String numSamplesStr = "10";
	public static final int numSamples = Integer.parseInt(numSamplesStr);

	// t0 to put into the test files
	public static final String t0Str = "13.0";
	public static final double t0 = Double.parseDouble(t0Str);
	public static final long t0_L = (long) t0;
	public static final double t0_D = (double) t0;

	// dt to put into the test files
	public static final String dtStr = "37.0";
	public static final double dt = Double.parseDouble(dtStr);
	public static final long dt_L = (long) dt;
	public static final double dt_D = (double) dt;

	// offset o in the test files
	public static final String scalingOffsetStr = "1.2";
	public static final double scalingOffset = Double.parseDouble(scalingOffsetStr);
	public static final byte scalingOffset_B = (byte) scalingOffset;
	public static final short scalingOffset_S = (short) scalingOffset;
	public static final int scalingOffset_I = (int) scalingOffset;
	public static final long scalingOffset_L = (long) scalingOffset;
	public static final float scalingOffset_F = (float) scalingOffset;
	public static final double scalingOffset_D = (double) scalingOffset;

	// scaling s in the test files
	public static final String scalingFactorStr = "24.3";
	public static final double scalingFactor = Double.parseDouble(scalingFactorStr);
	public static final byte scalingFactor_B = (byte) scalingFactor;
	public static final short scalingFactor_S = (short) scalingFactor;
	public static final int scalingFactor_I = (int) scalingFactor;
	public static final long scalingFactor_L = (long) scalingFactor;
	public static final float scalingFactor_F = (float) scalingFactor;
	public static final double scalingFactor_D = (double) scalingFactor;

	// time series:
	// idx |  time | raw value (=offset+scale*(t-t0))
	//  0  |  13.0 |   1.2
	//  1  |  50.0 |  25.5
	//  2  |  87.0 |  49.8
	//  3  | 124.0 |  74.1
	//  4  | 161.0 |  98.4
	//  5  | 198.0 | 122.7
	//  6  | 235.0 | 147.0
	//  7  | 272.0 | 171.3
	//  8  | 309.0 | 195.6
	//  9  | 346.0 | 219.9
	
	

	// interval specification for testing of subset reading
	public static final int numSamplesSubset = 5;
	public static final int sourceOffset = 2;
	public static final int targetOffset = 0;

	public static final String t_lStr = "80.0";
	public static final int expectedFirstIndexInside = 2;
	public static final String t_uStr = "300.0";
	public static final int expectedLastIndexInside = 7;
	
	public static void main(String[] args) {
		generateTestData();
	}

	/**
	 * This method generates files which should comply with the BinaryTimeseries specification.
	 * These files will be used to test the Java and Python implementations of the BinaryTimeseries API.
	 * If run as a JUnit test, the file offsets are checked for consistency with the specification.
	 */
	@Test
	public static void generateTestData() {
		
		// The following three nested loops go over all combinations of time type,
		// scaling type and data type and generate the test time series for each
		// of these combinations. The generated reference data is saved in files
		// src/test/resources/<tT + "_" + tS + "_" + tD>.bts, where tT is 'L' or 'D'
		// for long or double timestamps, tS is one of 'N', ..., 'D' for the type
		// of the scaling parameters and tD is one of 'B', 'S', ..., 'D' for the type
		// of the raw data. An example filename therefore is 'L_S_F.bts'
		// for long timestamps, short scaling offset and factor and float raw data.
		
		for (int time_dtype_idx = 0; time_dtype_idx < time_dtypes.length; ++time_dtype_idx) {
			final byte time_dtype = time_dtypes[time_dtype_idx];
			final String tT = BinaryTimeseries.dtypeStr(time_dtype);
			
			for (int scaling_dtype_idx = 0; scaling_dtype_idx < scaling_dtypes.length; ++scaling_dtype_idx) {
				final byte scaling_dtype = scaling_dtypes[scaling_dtype_idx];
				final String tS = BinaryTimeseries.dtypeStr(scaling_dtype);
				
				for (int data_dtype_idx = 0; data_dtype_idx < data_dtypes.length; ++data_dtype_idx) {
					final byte data_dtype = data_dtypes[data_dtype_idx];
					final String tD = BinaryTimeseries.dtypeStr(data_dtype);

					final int data_size = data_sizes[data_dtype_idx];

					final String testId = tT + "_" + tS + "_" + tD;
					
					// compute file size from reserved number of header bytes, sample size and number of samples
					final int filesize = 64 + data_size * numSamples;

					final byte[] binaryTimeseries = new byte[filesize];

					// 'manually' build a BinaryTimeseries
					final ByteBuffer referenceTarget = ByteBuffer.wrap(binaryTimeseries);
					assertEquals(0, referenceTarget.position());
					
					// endianess check short
					referenceTarget.putShort((short) 1);
					assertEquals(2, referenceTarget.position());
					
					// dtype of time
					referenceTarget.put(time_dtype);
					assertEquals(3, referenceTarget.position());

					// t0
					if (time_dtype == BinaryTimeseries.DTYPE_LONG) {
						referenceTarget.putLong(t0_L);
					} else if (time_dtype == BinaryTimeseries.DTYPE_DOUBLE) {
						referenceTarget.putDouble(t0_D);
					}
					assertEquals(11, referenceTarget.position());

					// dt
					if (time_dtype == BinaryTimeseries.DTYPE_LONG) {
						referenceTarget.putLong(dt_L);
					} else if (time_dtype == BinaryTimeseries.DTYPE_DOUBLE) {
						referenceTarget.putDouble(dt_D);
					}
					assertEquals(19, referenceTarget.position());

					// scaling dtype
					referenceTarget.put(scaling_dtype);
					assertEquals(20, referenceTarget.position());

					// scaling offset
					if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
						referenceTarget.put(new byte[8]);
					} else {
						if (scaling_dtype == BinaryTimeseries.DTYPE_BYTE) {
							referenceTarget.put(scalingOffset_B);
							referenceTarget.put(new byte[8 - Byte.BYTES]);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_SHORT) {
							referenceTarget.putShort(scalingOffset_S);
							referenceTarget.put(new byte[8 - Short.BYTES]);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_INT) {
							referenceTarget.putInt(scalingOffset_I);
							referenceTarget.put(new byte[8 - Integer.BYTES]);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_LONG) {
							referenceTarget.putLong(scalingOffset_L);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_FLOAT) {
							referenceTarget.putFloat(scalingOffset_F);
							referenceTarget.put(new byte[8 - Float.BYTES]);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_DOUBLE) {
							referenceTarget.putDouble(scalingOffset_D);
						}
					}
					assertEquals(28, referenceTarget.position());

					// scaling factor
					if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
						referenceTarget.put(new byte[8]);
					} else {
						if (scaling_dtype == BinaryTimeseries.DTYPE_BYTE) {
							referenceTarget.put(scalingFactor_B);
							referenceTarget.put(new byte[8 - Byte.BYTES]);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_SHORT) {
							referenceTarget.putShort(scalingFactor_S);
							referenceTarget.put(new byte[8 - Short.BYTES]);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_INT) {
							referenceTarget.putInt(scalingFactor_I);
							referenceTarget.put(new byte[8 - Integer.BYTES]);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_LONG) {
							referenceTarget.putLong(scalingFactor_L);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_FLOAT) {
							referenceTarget.putFloat(scalingFactor_F);
							referenceTarget.put(new byte[8 - Float.BYTES]);
						} else if (scaling_dtype == BinaryTimeseries.DTYPE_DOUBLE) {
							referenceTarget.putDouble(scalingFactor_D);
						}
					}
					assertEquals(36, referenceTarget.position());

					// reserved dummy space
					referenceTarget.put(new byte[23]);
					assertEquals(59, referenceTarget.position());

					// type of raw data
					referenceTarget.put(data_dtype);
					assertEquals(60, referenceTarget.position());

					// number of samples
					referenceTarget.putInt(numSamples);
					assertEquals(64, referenceTarget.position());

					// actual data
					if (data_dtype == BinaryTimeseries.DTYPE_BYTE) {
						byte sample = 0;
						for (int i = 0; i < numSamples; ++i) {
							sample = (byte) (scalingOffset + i * scalingFactor);
							referenceTarget.put(sample);
						}
					} else if (data_dtype == BinaryTimeseries.DTYPE_SHORT) {
						short sample = 0;
						for (int i = 0; i < numSamples; ++i) {
							sample = (short) (scalingOffset + i * scalingFactor);
							referenceTarget.putShort(sample);
						}
					} else if (data_dtype == BinaryTimeseries.DTYPE_INT) {
						int sample = 0;
						for (int i = 0; i < numSamples; ++i) {
							sample = (int) (scalingOffset + i * scalingFactor);
							referenceTarget.putInt(sample);
						}
					} else if (data_dtype == BinaryTimeseries.DTYPE_LONG) {
						long sample = 0;
						for (int i = 0; i < numSamples; ++i) {
							sample = (long) (scalingOffset + i * scalingFactor);
							referenceTarget.putLong(sample);
						}
					} else if (data_dtype == BinaryTimeseries.DTYPE_FLOAT) {
						float sample = (float) 0.0;
						for (int i = 0; i < numSamples; ++i) {
							sample = (float) (scalingOffset + i * scalingFactor);
							referenceTarget.putFloat(sample);
						}
					} else if (data_dtype == BinaryTimeseries.DTYPE_DOUBLE) {
						double sample = 0.0;
						for (int i = 0; i < numSamples; ++i) {
							sample = (double) (scalingOffset + i * scalingFactor);
							referenceTarget.putDouble(sample);
						}
					}
					assertEquals(filesize, referenceTarget.position());
					
					// write reference data to a binary file
					try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/"+testId+".bts", "rw")) {
						MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(
								FileChannel.MapMode.READ_WRITE, 0, filesize);

						long wrintingStart = -System.nanoTime();
						
						mappedByteBuffer.put(binaryTimeseries);
						
						System.out.println("writing of '"+testId+".bts' took " + (int) (Math.round((wrintingStart + System.nanoTime()) / 1e3)) + " us");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
