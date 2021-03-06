package de.labathome;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Some tests resembling real-world use cases of BinaryTimeseries.
 * 
 * @author Jonathan Schilling (jonathan.schilling@mail.de)
 * @version 1.0.0 first published version
 * @version 1.0.1 fixed wrong endianess value
 * @version 1.0.2 first official Python implementation
 * @version 1.0.3 add explicit close() method in Python implementation
 * @version 1.0.4 use de-labathome-parent in pom.xml
 */
public class RealWorldTests {

	/**
	 * Test the writing and reading of a whole timeseries with {@code long}
	 * timestamps and {@code short} raw data values.
	 */
	@Test
	public void testReadWrite_tL_dS_all() {

		Path tmpFile;
		int numValues;
		short[] values;
		long f, t0, dt;
		int filesize;

		tmpFile = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "tmp.bts");
		System.out.println("using '" + tmpFile + "' for testing BinaryTimeseries");

		// generate test data
		t0 = -99_000_000L; // -99 ms
		f = 2_000_000L; // 2 MHz
		numValues = (int) (10 * f); // 10 periods of data in total
		values = new short[numValues];
		dt = 1_000_000_000L / f;

		for (int i = 0; i < numValues; ++i) {
			values[i] = (short) (i % Short.MAX_VALUE);
		}

		filesize = 64 + numValues * 2;
		System.out.println("size of temporary file is " + filesize / (1024 * 1024) + " MB");

		assertEquals(filesize, BinaryTimeseries.fileOffset(Short.BYTES, numValues));

		// write
		try (RandomAccessFile memoryFile = new RandomAccessFile(tmpFile.toFile(), "rw")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
					filesize);

			long start = -System.nanoTime();
			BinaryTimeseries.write(mappedByteBuffer, t0, dt, values);
			System.out.println("writing took " + (int) (Math.round((start + System.nanoTime()) / 1e3)) + " us");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// read
		try (RandomAccessFile memoryFile = new RandomAccessFile(tmpFile.toFile(), "r")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, memoryFile.length());

			long[] _t0_dt = new long[2];

			// test that explanation of header values works
			byte[] header = new byte[64];
			mappedByteBuffer.get(header);
			System.out.println(BinaryTimeseries.explainHeader(header));
			mappedByteBuffer.position(0);

			long start = -System.nanoTime();
			assertEquals(0, mappedByteBuffer.position());
			assertEquals(true, BinaryTimeseries.readEndianessOk(mappedByteBuffer));
			assertEquals(2, mappedByteBuffer.position());
			assertEquals(BinaryTimeseries.DTYPE_LONG, BinaryTimeseries.readTimeType(mappedByteBuffer));
			_t0_dt[0] = BinaryTimeseries.readTimeT0_long(mappedByteBuffer);
			_t0_dt[1] = BinaryTimeseries.readTimeDt_long(mappedByteBuffer);
			assertEquals(19, mappedByteBuffer.position());
			final short[] _values = BinaryTimeseries.readData_short(mappedByteBuffer);
			System.out.println("reading took " + (int) (Math.round((start + System.nanoTime()) / 1e3)) + " us");

			// check
			assertEquals(t0, _t0_dt[0]);
			assertEquals(dt, _t0_dt[1]);
			assertArrayEquals(values, _values);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// clean up in the end
		if (Files.exists(tmpFile)) {
			try {
				Files.delete(tmpFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("temporary file was not created");
		}
	}

	/**
	 * Test the writing of a whole and reading of part of a timeseries with
	 * {@code long} timestamps and {@code short} raw data values.
	 */
	@Test
	public void testReadWrite_tL_dS_range() {

		Path tmpFile;
		int numValues;
		short[] values;
		long f, t0, dt;
		int filesize;

		tmpFile = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "tmp.bts");
		System.out.println("using '" + tmpFile + "' for testing BinaryTimeseries");

		// generate test data
		t0 = -99_000_000L; // -99 ms
		f = 2_000_000L; // 2 MHz
		numValues = (int) (10 * f); // 10 periods of data in total
		values = new short[numValues];
		dt = 1_000_000_000L / f;

		for (int i = 0; i < numValues; ++i) {
			values[i] = (short) (i % Short.MAX_VALUE);
		}

		filesize = 64 + numValues * 2;
		System.out.println("size of temporary file is " + filesize / (1024 * 1024) + " MB");

		assertEquals(filesize, BinaryTimeseries.fileOffset(Short.BYTES, numValues));

		// write
		try (RandomAccessFile memoryFile = new RandomAccessFile(tmpFile.toFile(), "rw")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
					filesize);

			long start = -System.nanoTime();
			BinaryTimeseries.write(mappedByteBuffer, t0, dt, values);
			System.out.println("writing took " + (int) (Math.round((start + System.nanoTime()) / 1e3)) + " us");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// subset of data to read is from 7th to 8th period
		long from = 7 * f;
		long upto = 8 * f;

		// read
		try (RandomAccessFile memoryFile = new RandomAccessFile(tmpFile.toFile(), "r")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, memoryFile.length());

			// test that explanation of header values works
			byte[] header = new byte[64];
			mappedByteBuffer.get(header);
			System.out.println(BinaryTimeseries.explainHeader(header));
			mappedByteBuffer.position(0);

			long start = -System.nanoTime();

			// target for reading
			assertEquals(0, mappedByteBuffer.position());
			assertEquals(true, BinaryTimeseries.readEndianessOk(mappedByteBuffer));
			assertEquals(2, mappedByteBuffer.position());
			assertEquals(BinaryTimeseries.DTYPE_LONG, BinaryTimeseries.readTimeType(mappedByteBuffer));
			assertEquals(3, mappedByteBuffer.position());
			final long _t0 = BinaryTimeseries.readTimeT0_long(mappedByteBuffer);
			assertEquals(11, mappedByteBuffer.position());
			assertEquals(t0, _t0);
			final long _dt = BinaryTimeseries.readTimeDt_long(mappedByteBuffer);
			assertEquals(19, mappedByteBuffer.position());
			assertEquals(dt, _dt);

			int fromIdx = BinaryTimeseries.firstIndexInside(_t0, _dt, from);
			int uptoIdx = BinaryTimeseries.lastIndexInside(_t0, _dt, upto);

			short[] _values = new short[uptoIdx - fromIdx + 1];

			assertEquals(BinaryTimeseries.DTYPE_NONE, BinaryTimeseries.readScalingType(mappedByteBuffer));
			assertEquals(20, mappedByteBuffer.position());
			BinaryTimeseries.readScalingDisabled(mappedByteBuffer);
			assertEquals(36, mappedByteBuffer.position());
			BinaryTimeseries.readReservedDummy(mappedByteBuffer);
			assertEquals(59, mappedByteBuffer.position());
			assertEquals(BinaryTimeseries.DTYPE_SHORT, BinaryTimeseries.readDataType(mappedByteBuffer));
			assertEquals(60, mappedByteBuffer.position());
			assertEquals(numValues, BinaryTimeseries.readNumSamples(mappedByteBuffer));
			assertEquals(64, mappedByteBuffer.position());
			mappedByteBuffer.position(BinaryTimeseries.fileOffset(Short.BYTES, fromIdx));
			BinaryTimeseries.readRawData(mappedByteBuffer, _values, 0, uptoIdx - fromIdx + 1);
			System.out
			.println("reading of subset took " + (int) (Math.round((start + System.nanoTime()) / 1e3)) + " us");

			// check
			short[] testSubsetValues = new short[uptoIdx - fromIdx + 1];
			System.arraycopy(values, fromIdx, testSubsetValues, 0, uptoIdx - fromIdx + 1);
			assertArrayEquals(testSubsetValues, _values);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// clean up in the end
		if (Files.exists(tmpFile)) {
			try {
				Files.delete(tmpFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("temporary file was not created");
		}
	}
}
