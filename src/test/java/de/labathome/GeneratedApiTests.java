package de.labathome;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.RandomAccessFile;import java.nio.ByteBuffer;import java.nio.MappedByteBuffer;import java.nio.channels.FileChannel;
import org.junit.jupiter.api.Test;

class GeneratedApiTests {
	// L_N_B
	@Test
	public void testBuiltTimebase_L() {
		final int numSamples = 10;
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int numSamplesSubset = 5;
		final int sourceOffset = 2;
		final int targetOffset = 0;
		
		// 'manually' build reference time stamps
		final long[] timebase = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			timebase[i] = t0_L + i*dt_L;
		}
		final long[] timebase_subset = new long[numSamplesSubset];
		System.arraycopy(timebase, sourceOffset, timebase_subset, targetOffset, numSamplesSubset);
		
		// no sourceOffset and targetOffset given
		final long[] targetTimebase = new long[numSamples];
		BinaryTimeseries.buildTimebase(targetTimebase, t0_L, dt_L);
		assertArrayEquals(timebase, targetTimebase);
		
		// sourceOffset and targetOffset are given
		final long[] targetTimebase_subset = new long[numSamplesSubset];
		BinaryTimeseries.buildTimebase(sourceOffset, targetTimebase_subset, targetOffset, numSamplesSubset, t0_L, dt_L);
		assertArrayEquals(timebase_subset, targetTimebase_subset);
	}

	@Test
	public void testFirstIndexInside_L() {
		final long t0_L  = (long) 13.0;
		final long dt_L  = (long) 37.0;
		final long t_l_L = (long) 80.0;
		assertEquals(2, BinaryTimeseries.firstIndexInside(t0_L, dt_L, t_l_L));
	}

	@Test
	public void testLastIndexInside_L() {
		final long t0_L  = (long) 13.0;
		final long dt_L  = (long) 37.0;
		final long t_u_L = (long) 300.0;
		assertEquals(7, BinaryTimeseries.lastIndexInside(t0_L, dt_L, t_u_L));
	}

	@Test
	public void testFileOffset_B() {
		assertEquals(74, BinaryTimeseries.fileOffset(Byte.BYTES, 10));
	}

	@Test
	public void testReadWrite_L_N_B() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_N_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_N_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_N_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_N_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_N_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_N_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_N_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_N_S
	@Test
	public void testFileOffset_S() {
		assertEquals(84, BinaryTimeseries.fileOffset(Short.BYTES, 10));
	}

	@Test
	public void testReadWrite_L_N_S() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_N_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_N_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_N_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_N_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_N_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_N_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_N_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_N_I
	@Test
	public void testFileOffset_I() {
		assertEquals(104, BinaryTimeseries.fileOffset(Integer.BYTES, 10));
	}

	@Test
	public void testReadWrite_L_N_I() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_N_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_N_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_N_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_N_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_N_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_N_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_N_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_N_L
	@Test
	public void testFileOffset_L() {
		assertEquals(144, BinaryTimeseries.fileOffset(Long.BYTES, 10));
	}

	@Test
	public void testReadWrite_L_N_L() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_N_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_N_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_N_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_N_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_N_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_N_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_N_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_N_F
	@Test
	public void testFileOffset_F() {
		assertEquals(104, BinaryTimeseries.fileOffset(Float.BYTES, 10));
	}

	@Test
	public void testReadWrite_L_N_F() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_N_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_N_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_N_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_N_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_N_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_N_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_N_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_N_D
	@Test
	public void testFileOffset_D() {
		assertEquals(144, BinaryTimeseries.fileOffset(Double.BYTES, 10));
	}

	@Test
	public void testReadWrite_L_N_D() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_N_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_N_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_N_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_N_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_N_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_N_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_N_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_B_B
	@Test
	public void testReadWrite_L_B_B() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_B_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_B_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_B_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_B_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_B_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_B_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_B_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_B_S
	@Test
	public void testReadWrite_L_B_S() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_B_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_B_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_B_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_B_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_B_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_B_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_B_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_B_I
	@Test
	public void testReadWrite_L_B_I() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_B_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_B_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_B_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_B_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_B_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_B_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_B_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_B_L
	@Test
	public void testReadWrite_L_B_L() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_B_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_B_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_B_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_B_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_B_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_B_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_B_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_B_F
	@Test
	public void testReadWrite_L_B_F() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_B_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_B_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_B_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_B_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_B_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_B_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_B_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_B_D
	@Test
	public void testReadWrite_L_B_D() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_B_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_B_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_B_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_B_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_B_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_B_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_B_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_S_B
	@Test
	public void testReadWrite_L_S_B() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_S_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_S_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_S_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_S_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_S_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_S_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_S_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_S_S
	@Test
	public void testReadWrite_L_S_S() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_S_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_S_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_S_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_S_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_S_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_S_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_S_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_S_I
	@Test
	public void testReadWrite_L_S_I() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_S_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_S_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_S_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_S_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_S_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_S_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_S_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_S_L
	@Test
	public void testReadWrite_L_S_L() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_S_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_S_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_S_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_S_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_S_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_S_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_S_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_S_F
	@Test
	public void testReadWrite_L_S_F() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_S_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_S_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_S_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_S_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_S_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_S_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_S_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_S_D
	@Test
	public void testReadWrite_L_S_D() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_S_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_S_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_S_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_S_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_S_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_S_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_S_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_I_B
	@Test
	public void testReadWrite_L_I_B() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_I_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_I_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_I_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_I_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_I_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_I_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_I_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_I_S
	@Test
	public void testReadWrite_L_I_S() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_I_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_I_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_I_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_I_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_I_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_I_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_I_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_I_I
	@Test
	public void testReadWrite_L_I_I() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_I_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_I_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_I_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_I_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_I_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_I_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_I_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_I_L
	@Test
	public void testReadWrite_L_I_L() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_I_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_I_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_I_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_I_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_I_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_I_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_I_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_I_F
	@Test
	public void testReadWrite_L_I_F() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_I_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_I_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_I_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_I_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_I_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_I_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_I_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_I_D
	@Test
	public void testReadWrite_L_I_D() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_I_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_I_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_I_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_I_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_I_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_I_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_I_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_L_B
	@Test
	public void testReadWrite_L_L_B() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_L_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_L_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_L_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_L_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_L_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_L_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_L_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_L_S
	@Test
	public void testReadWrite_L_L_S() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_L_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_L_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_L_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_L_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_L_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_L_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_L_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_L_I
	@Test
	public void testReadWrite_L_L_I() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_L_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_L_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_L_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_L_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_L_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_L_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_L_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_L_L
	@Test
	public void testReadWrite_L_L_L() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_L_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_L_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_L_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_L_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_L_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_L_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_L_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_L_F
	@Test
	public void testReadWrite_L_L_F() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_L_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_L_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_L_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_L_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_L_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_L_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_L_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_L_D
	@Test
	public void testReadWrite_L_L_D() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_L_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_L_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_L_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_L_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_L_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_L_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_L_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_F_B
	@Test
	public void testReadWrite_L_F_B() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_F_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_F_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_F_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_F_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_F_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_F_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_F_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_F_S
	@Test
	public void testReadWrite_L_F_S() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_F_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_F_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_F_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_F_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_F_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_F_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_F_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_F_I
	@Test
	public void testReadWrite_L_F_I() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_F_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_F_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_F_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_F_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_F_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_F_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_F_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_F_L
	@Test
	public void testReadWrite_L_F_L() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_F_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_F_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_F_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_F_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_F_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_F_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_F_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_F_F
	@Test
	public void testReadWrite_L_F_F() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_F_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_F_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_F_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_F_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_F_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_F_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_F_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_F_D
	@Test
	public void testReadWrite_L_F_D() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_F_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_F_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_F_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_F_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_F_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_F_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_F_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_D_B
	@Test
	public void testReadWrite_L_D_B() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_D_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_D_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_D_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_D_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_D_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_D_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_D_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_D_S
	@Test
	public void testReadWrite_L_D_S() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_D_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_D_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_D_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_D_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_D_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_D_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_D_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_D_I
	@Test
	public void testReadWrite_L_D_I() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_D_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_D_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_D_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_D_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_D_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_D_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_D_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_D_L
	@Test
	public void testReadWrite_L_D_L() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_D_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_D_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_D_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_D_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_D_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_D_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_D_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_D_F
	@Test
	public void testReadWrite_L_D_F() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_D_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_D_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_D_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_D_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_D_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_D_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_D_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// L_D_D
	@Test
	public void testReadWrite_L_D_D() {
		final long t0_L = (long) 13.0;
		final long dt_L = (long) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_L, dt_L);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_L_D_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/L_D_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_L_D_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_L_D_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_L_D_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_L, dt_L, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_L_D_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_L_D_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(4, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_L, BinaryTimeseries.readTimeT0_long(source));
		assertEquals(11, source.position());
		assertEquals(dt_L, BinaryTimeseries.readTimeDt_long(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_N_B
	@Test
	public void testBuiltTimebase_D() {
		final int numSamples = 10;
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int numSamplesSubset = 5;
		final int sourceOffset = 2;
		final int targetOffset = 0;
		
		// 'manually' build reference time stamps
		final double[] timebase = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			timebase[i] = t0_D + i*dt_D;
		}
		final double[] timebase_subset = new double[numSamplesSubset];
		System.arraycopy(timebase, sourceOffset, timebase_subset, targetOffset, numSamplesSubset);
		
		// no sourceOffset and targetOffset given
		final double[] targetTimebase = new double[numSamples];
		BinaryTimeseries.buildTimebase(targetTimebase, t0_D, dt_D);
		assertArrayEquals(timebase, targetTimebase);
		
		// sourceOffset and targetOffset are given
		final double[] targetTimebase_subset = new double[numSamplesSubset];
		BinaryTimeseries.buildTimebase(sourceOffset, targetTimebase_subset, targetOffset, numSamplesSubset, t0_D, dt_D);
		assertArrayEquals(timebase_subset, targetTimebase_subset);
	}

	@Test
	public void testFirstIndexInside_D() {
		final double t0_D  = (double) 13.0;
		final double dt_D  = (double) 37.0;
		final double t_l_D = (double) 80.0;
		assertEquals(2, BinaryTimeseries.firstIndexInside(t0_D, dt_D, t_l_D));
	}

	@Test
	public void testLastIndexInside_D() {
		final double t0_D  = (double) 13.0;
		final double dt_D  = (double) 37.0;
		final double t_u_D = (double) 300.0;
		assertEquals(7, BinaryTimeseries.lastIndexInside(t0_D, dt_D, t_u_D));
	}

	@Test
	public void testReadWrite_D_N_B() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_N_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_N_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_N_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_N_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_N_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_N_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_N_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_N_S
	@Test
	public void testReadWrite_D_N_S() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_N_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_N_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_N_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_N_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_N_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_N_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_N_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_N_I
	@Test
	public void testReadWrite_D_N_I() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_N_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_N_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_N_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_N_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_N_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_N_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_N_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_N_L
	@Test
	public void testReadWrite_D_N_L() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_N_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_N_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_N_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_N_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_N_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_N_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_N_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_N_F
	@Test
	public void testReadWrite_D_N_F() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_N_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_N_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_N_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_N_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_N_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_N_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_N_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_N_D
	@Test
	public void testReadWrite_D_N_D() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) i;
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScalingDisabled(target);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_N_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_N_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_N_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_N_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_N_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_N_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_N_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(0, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		BinaryTimeseries.readScalingDisabled(source);
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			referenceData_byte  [i] = (byte  )i;
			referenceData_short [i] = (short )i;
			referenceData_int   [i] = (int   )i;
			referenceData_long  [i] = (long  )i;
			referenceData_float [i] = (float )i;
			referenceData_double[i] = (double)i;
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_B_B
	@Test
	public void testReadWrite_D_B_B() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_B_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_B_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_B_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_B_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_B_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_B_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_B_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_B_S
	@Test
	public void testReadWrite_D_B_S() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_B_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_B_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_B_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_B_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_B_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_B_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_B_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_B_I
	@Test
	public void testReadWrite_D_B_I() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_B_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_B_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_B_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_B_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_B_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_B_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_B_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_B_L
	@Test
	public void testReadWrite_D_B_L() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_B_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_B_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_B_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_B_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_B_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_B_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_B_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_B_F
	@Test
	public void testReadWrite_D_B_F() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_B_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_B_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_B_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_B_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_B_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_B_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_B_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_B_D
	@Test
	public void testReadWrite_D_B_D() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final byte scalingOffset_B = (byte) 1.2;
		final byte scalingFactor_B = (byte) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_B + i*scalingFactor_B);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_B, scalingFactor_B);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_B_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_B_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_B_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_B_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_B_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_B, scalingFactor_B);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_B_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_B_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(1, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_B, BinaryTimeseries.readScalingOffset_byte(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_B, BinaryTimeseries.readScalingFactor_byte(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_B + i*scalingFactor_B);
			referenceData_byte  [i] = (byte  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_short [i] = (short )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_int   [i] = (int   )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_long  [i] = (long  )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_float [i] = (float )(scalingOffset_B + referenceValue*scalingFactor_B);
			referenceData_double[i] = (double)(scalingOffset_B + referenceValue*scalingFactor_B);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_S_B
	@Test
	public void testReadWrite_D_S_B() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_S_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_S_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_S_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_S_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_S_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_S_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_S_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_S_S
	@Test
	public void testReadWrite_D_S_S() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_S_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_S_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_S_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_S_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_S_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_S_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_S_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_S_I
	@Test
	public void testReadWrite_D_S_I() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_S_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_S_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_S_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_S_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_S_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_S_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_S_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_S_L
	@Test
	public void testReadWrite_D_S_L() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_S_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_S_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_S_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_S_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_S_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_S_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_S_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_S_F
	@Test
	public void testReadWrite_D_S_F() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_S_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_S_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_S_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_S_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_S_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_S_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_S_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_S_D
	@Test
	public void testReadWrite_D_S_D() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final short scalingOffset_S = (short) 1.2;
		final short scalingFactor_S = (short) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_S + i*scalingFactor_S);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_S, scalingFactor_S);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_S_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_S_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_S_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_S_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_S_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_S, scalingFactor_S);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_S_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_S_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(2, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_S, BinaryTimeseries.readScalingOffset_short(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_S, BinaryTimeseries.readScalingFactor_short(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_S + i*scalingFactor_S);
			referenceData_byte  [i] = (byte  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_short [i] = (short )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_int   [i] = (int   )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_long  [i] = (long  )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_float [i] = (float )(scalingOffset_S + referenceValue*scalingFactor_S);
			referenceData_double[i] = (double)(scalingOffset_S + referenceValue*scalingFactor_S);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_I_B
	@Test
	public void testReadWrite_D_I_B() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_I_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_I_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_I_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_I_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_I_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_I_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_I_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_I_S
	@Test
	public void testReadWrite_D_I_S() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_I_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_I_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_I_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_I_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_I_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_I_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_I_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_I_I
	@Test
	public void testReadWrite_D_I_I() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_I_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_I_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_I_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_I_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_I_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_I_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_I_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_I_L
	@Test
	public void testReadWrite_D_I_L() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_I_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_I_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_I_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_I_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_I_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_I_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_I_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_I_F
	@Test
	public void testReadWrite_D_I_F() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_I_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_I_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_I_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_I_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_I_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_I_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_I_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_I_D
	@Test
	public void testReadWrite_D_I_D() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final int scalingOffset_I = (int) 1.2;
		final int scalingFactor_I = (int) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_I + i*scalingFactor_I);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_I, scalingFactor_I);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_I_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_I_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_I_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_I_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_I_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_I, scalingFactor_I);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_I_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_I_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(3, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_I, BinaryTimeseries.readScalingOffset_int(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_I, BinaryTimeseries.readScalingFactor_int(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_I + i*scalingFactor_I);
			referenceData_byte  [i] = (byte  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_short [i] = (short )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_int   [i] = (int   )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_long  [i] = (long  )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_float [i] = (float )(scalingOffset_I + referenceValue*scalingFactor_I);
			referenceData_double[i] = (double)(scalingOffset_I + referenceValue*scalingFactor_I);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_L_B
	@Test
	public void testReadWrite_D_L_B() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_L_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_L_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_L_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_L_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_L_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_L_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_L_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_L_S
	@Test
	public void testReadWrite_D_L_S() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_L_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_L_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_L_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_L_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_L_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_L_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_L_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_L_I
	@Test
	public void testReadWrite_D_L_I() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_L_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_L_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_L_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_L_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_L_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_L_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_L_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_L_L
	@Test
	public void testReadWrite_D_L_L() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_L_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_L_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_L_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_L_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_L_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_L_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_L_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_L_F
	@Test
	public void testReadWrite_D_L_F() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_L_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_L_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_L_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_L_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_L_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_L_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_L_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_L_D
	@Test
	public void testReadWrite_D_L_D() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final long scalingOffset_L = (long) 1.2;
		final long scalingFactor_L = (long) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_L + i*scalingFactor_L);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_L, scalingFactor_L);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_L_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_L_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_L_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_L_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_L_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_L, scalingFactor_L);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_L_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_L_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(4, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_L, BinaryTimeseries.readScalingOffset_long(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_L, BinaryTimeseries.readScalingFactor_long(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_L + i*scalingFactor_L);
			referenceData_byte  [i] = (byte  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_short [i] = (short )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_int   [i] = (int   )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_long  [i] = (long  )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_float [i] = (float )(scalingOffset_L + referenceValue*scalingFactor_L);
			referenceData_double[i] = (double)(scalingOffset_L + referenceValue*scalingFactor_L);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_F_B
	@Test
	public void testReadWrite_D_F_B() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_F_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_F_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_F_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_F_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_F_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_F_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_F_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_F_S
	@Test
	public void testReadWrite_D_F_S() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_F_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_F_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_F_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_F_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_F_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_F_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_F_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_F_I
	@Test
	public void testReadWrite_D_F_I() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_F_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_F_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_F_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_F_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_F_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_F_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_F_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_F_L
	@Test
	public void testReadWrite_D_F_L() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_F_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_F_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_F_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_F_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_F_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_F_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_F_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_F_F
	@Test
	public void testReadWrite_D_F_F() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_F_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_F_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_F_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_F_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_F_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_F_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_F_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_F_D
	@Test
	public void testReadWrite_D_F_D() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final float scalingOffset_F = (float) 1.2;
		final float scalingFactor_F = (float) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_F + i*scalingFactor_F);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_F, scalingFactor_F);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_F_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_F_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_F_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_F_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_F_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_F, scalingFactor_F);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_F_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_F_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(5, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_F, BinaryTimeseries.readScalingOffset_float(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_F, BinaryTimeseries.readScalingFactor_float(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_F + i*scalingFactor_F);
			referenceData_byte  [i] = (byte  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_short [i] = (short )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_int   [i] = (int   )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_long  [i] = (long  )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_float [i] = (float )(scalingOffset_F + referenceValue*scalingFactor_F);
			referenceData_double[i] = (double)(scalingOffset_F + referenceValue*scalingFactor_F);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_D_B
	@Test
	public void testReadWrite_D_D_B() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final byte[] values = new byte[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (byte) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(1, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_D_B = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_D_B.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_D_B = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_D_B);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_D_B, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_D_B, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_D_B);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(1, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final byte[] rawData = new byte[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final byte referenceValue = (byte) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_D_S
	@Test
	public void testReadWrite_D_D_S() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final short[] values = new short[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (short) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(2, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_D_S = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_D_S.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_D_S = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_D_S);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_D_S, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_D_S, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_D_S);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(2, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final short[] rawData = new short[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final short referenceValue = (short) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_D_I
	@Test
	public void testReadWrite_D_D_I() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final int[] values = new int[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (int) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_D_I = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_D_I.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_D_I = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_D_I);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_D_I, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_D_I, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_D_I);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(3, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final int[] rawData = new int[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final int referenceValue = (int) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_D_L
	@Test
	public void testReadWrite_D_D_L() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final long[] values = new long[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (long) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_D_L = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_D_L.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_D_L = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_D_L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_D_L, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_D_L, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_D_L);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(4, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final long[] rawData = new long[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final long referenceValue = (long) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_D_F
	@Test
	public void testReadWrite_D_D_F() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final float[] values = new float[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (float) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(4, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_D_F = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_D_F.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_D_F = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_D_F);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_D_F, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_D_F, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_D_F);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(5, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final float[] rawData = new float[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final float referenceValue = (float) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

	// D_D_D
	@Test
	public void testReadWrite_D_D_D() {
		final double t0_D = (double) 13.0;
		final double dt_D = (double) 37.0;
		final double scalingOffset_D = (double) 1.2;
		final double scalingFactor_D = (double) 24.3;
		final int numSamples = 10;
		final double[] values = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			values[i] = (double) (scalingOffset_D + i*scalingFactor_D);
		}
		//writing
		int fileSize = BinaryTimeseries.fileOffset(8, numSamples);
		final byte[] targetArr = new byte[fileSize];
		final ByteBuffer target = ByteBuffer.wrap(targetArr);
		assertEquals(0, target.position());
		BinaryTimeseries.writeEndianessCheckValue(target);
		assertEquals(2, target.position());
		BinaryTimeseries.writeTimebase(target, t0_D, dt_D);
		assertEquals(19, target.position());
		BinaryTimeseries.writeScaling(target, scalingOffset_D, scalingFactor_D);
		assertEquals(36, target.position());
		BinaryTimeseries.writeReservedDummy(target);
		assertEquals(59, target.position());
		BinaryTimeseries.writeData(target, values);
		assertEquals(fileSize, target.position());
		byte[] referenceBTS_D_D_D = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile("src/test/resources/D_D_D.bts", "r")) {
			int fileLength = (int) memoryFile.length();
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
			referenceBTS_D_D_D = new byte[fileLength];
			mappedByteBuffer.get(referenceBTS_D_D_D);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertArrayEquals(referenceBTS_D_D_D, targetArr);
		target.position(0);
		BinaryTimeseries.write(target, t0_D, dt_D, values, scalingOffset_D, scalingFactor_D);
		assertEquals(fileSize, target.position());
		assertArrayEquals(referenceBTS_D_D_D, targetArr);
		// reading
		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_D_D_D);
		assertEquals(0, source.position());
		assertEquals(true, BinaryTimeseries.readEndianessOk(source));
		assertEquals(2, source.position());
		assertEquals(6, BinaryTimeseries.readTimeType(source));
		assertEquals(3, source.position());
		assertEquals(t0_D, BinaryTimeseries.readTimeT0_double(source));
		assertEquals(11, source.position());
		assertEquals(dt_D, BinaryTimeseries.readTimeDt_double(source));
		assertEquals(19, source.position());
		assertEquals(6, BinaryTimeseries.readScalingType(source));
		assertEquals(20, source.position());
		assertEquals(scalingOffset_D, BinaryTimeseries.readScalingOffset_double(source));
		assertEquals(28, source.position());
		assertEquals(scalingFactor_D, BinaryTimeseries.readScalingFactor_double(source));
		assertEquals(36, source.position());
		assertEquals(36, source.position());
		BinaryTimeseries.readReservedDummy(source);
		assertEquals(59, source.position());
		assertEquals(6, BinaryTimeseries.readDataType(source));
		assertEquals(60, source.position());
		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));
		assertEquals(64, source.position());
		final double[] rawData = new double[numSamples];
		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);
		assertEquals(fileSize, source.position());
		assertArrayEquals(values, rawData);
		// read and scale into given primitive array
		final   byte[] referenceData_byte   = new   byte[numSamples];
		final  short[] referenceData_short  = new  short[numSamples];
		final    int[] referenceData_int    = new    int[numSamples];
		final   long[] referenceData_long   = new   long[numSamples];
		final  float[] referenceData_float  = new  float[numSamples];
		final double[] referenceData_double = new double[numSamples];
		for (int i=0; i<numSamples; ++i) {
			final double referenceValue = (double) (scalingOffset_D + i*scalingFactor_D);
			referenceData_byte  [i] = (byte  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_short [i] = (short )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_int   [i] = (int   )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_long  [i] = (long  )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_float [i] = (float )(scalingOffset_D + referenceValue*scalingFactor_D);
			referenceData_double[i] = (double)(scalingOffset_D + referenceValue*scalingFactor_D);
		}
		source.position(19);
		final byte[] data_byte = BinaryTimeseries.readData_byte(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_byte, data_byte);
		source.position(19);
		final short[] data_short = BinaryTimeseries.readData_short(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_short, data_short);
		source.position(19);
		final int[] data_int = BinaryTimeseries.readData_int(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_int, data_int);
		source.position(19);
		final long[] data_long = BinaryTimeseries.readData_long(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_long, data_long);
		source.position(19);
		final float[] data_float = BinaryTimeseries.readData_float(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_float, data_float);
		source.position(19);
		final double[] data_double = BinaryTimeseries.readData_double(source);
		assertEquals(fileSize, source.position());
		assertArrayEquals(referenceData_double, data_double);
	}

} // class GeneratedApiTests
