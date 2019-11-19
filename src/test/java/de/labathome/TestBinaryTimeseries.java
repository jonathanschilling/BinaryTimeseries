package de.labathome;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class TestBinaryTimeseries {

	/**
	 * Test the writing and reading of a whole timeseries with {@code long} timestamps and {@code short} raw data values.
	 */
	@Test
	public void testReadWrite_tL_dS_all() {

		Path tmpFile;
		int numValues;
		short[] values;
		long f, t0, dt;
		int filesize;

		tmpFile = Paths.get(System.getProperty("java.io.tmpdir")+File.separator+"tmp.ts");
		System.out.println("using '"+tmpFile+"' for testing BinaryTimeseries");

		// generate test data
		t0 = -99_000_000L;       // -99 ms
		f  =   2_000_000L;       //   2 MHz
		numValues = (int) (10*f);
		values = new short[numValues];
		dt = 1_000_000_000L / f;

		for (int i=0; i<numValues; ++i) {
			values[i] = (short) (i%Short.MAX_VALUE);
		}

		filesize = 24+numValues*2;
		System.out.println("size of temporary file is " + filesize/(1024*1024)+" MB");

		assertEquals(filesize, BinaryTimeseries.filesize(Short.BYTES, numValues));
		
		// write
		try (RandomAccessFile memoryFile = new RandomAccessFile(tmpFile.toFile(), "rw")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, filesize);

			long start = -System.nanoTime();
			BinaryTimeseries.write((ByteBuffer)mappedByteBuffer, t0, dt, values);
			System.out.println("writing took " + (int)(Math.round((start+System.nanoTime())/1e6))+"ms");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// read
		try (RandomAccessFile memoryFile = new RandomAccessFile(tmpFile.toFile(), "r")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, filesize);

			long[] t0_dt = new long[2];
			short[] _values = new short[numValues];

			Object[] target = new Object[] { t0_dt, _values };

			long start = -System.nanoTime();
			BinaryTimeseries.read_tL_dS(mappedByteBuffer, target);
			System.out.println("reading took " + (int)(Math.round((start+System.nanoTime())/1e6))+"ms");

			// check
			assertEquals(t0, t0_dt[0]);
			assertEquals(dt, t0_dt[1]);
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
	 * Test the writing of a whole and reading of part of a timeseries with {@code long} timestamps and {@code short} raw data values.
	 */
	@Test
	public void testReadWrite_tL_dS_range() {

		Path tmpFile;
		int numValues;
		short[] values;
		long f, t0, dt;
		int filesize;

		tmpFile = Paths.get(System.getProperty("java.io.tmpdir")+File.separator+"tmp.ts");
		System.out.println("using '"+tmpFile+"' for testing BinaryTimeseries");

		// generate test data
		t0 = -99_000_000L;       // -99 ms
		f  =   2_000_000L;       //   2 MHz
		numValues = (int) (10*f);
		values = new short[numValues];
		dt = 1_000_000_000L / f;

		for (int i=0; i<numValues; ++i) {
			values[i] = (short) (i%Short.MAX_VALUE);
		}

		filesize = 24+numValues*2;
		System.out.println("size of temporary file is " + filesize/(1024*1024)+" MB");

		assertEquals(filesize, BinaryTimeseries.filesize(Short.BYTES, numValues));
		
		// write
		try (RandomAccessFile memoryFile = new RandomAccessFile(tmpFile.toFile(), "rw")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, filesize);

			long start = -System.nanoTime();
			BinaryTimeseries.write((ByteBuffer)mappedByteBuffer, t0, dt, values);
			System.out.println("writing took " + (int)(Math.round((start+System.nanoTime())/1e6))+"ms");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// subset of data to read
		int readStart = (int) (7*f);
		int readEnd = (int) (8*f);

		// equivalent timestamps
		long from = t0+readStart*dt;
		long upto = t0+readEnd*dt;

		// read
		try (RandomAccessFile memoryFile = new RandomAccessFile(tmpFile.toFile(), "r")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, filesize);

			// target for reading
			short[][] _values = new short[1][];

			long start = -System.nanoTime();
			BinaryTimeseries.read_timeRange(mappedByteBuffer, _values, 0, from , upto);
			System.out.println("reading of subset took " + (int)(Math.round((start+System.nanoTime())/1e6))+"ms");

			// check
			short[] testSubsetValues = new short[readEnd-readStart+1];
			System.arraycopy(values, readStart, testSubsetValues, 0, readEnd-readStart+1);
			assertArrayEquals(testSubsetValues, _values[0]);
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
