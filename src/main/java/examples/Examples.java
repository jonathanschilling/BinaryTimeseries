package examples;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import de.labathome.BinaryTimeseries;

/**
 * This class is supposed to serve as an entry point into using BinaryTimeseries
 * to store your time series data.
 * 
 * @author Jonathan Schilling (jonathan.schilling@mail.de)
 * @version 1.0.0 first published version
 * @version 1.0.1 fixed wrong endianess value
 */
public class Examples {

	public static void main(String[] args) {
		Example1();
	}

	/**
	 * In this example, a synthetic sine signal is computed and saved to a BinaryTimeseries file, java.io.tmpdir/example1.bts .
	 * The signal is read back and compared against the original time series
	 */
	public static void Example1() {

		/**
		 * Generate a demo signal: 1s length, 1kHz sampling frequency
		 * Sine, 10 Hz, 0.77V amplitude, 33 deg phase
		 */
		
		// reference timestamp is at 0
		double t0 = 0.0;
		
		// sampling frequency: 1kHz --> sampling interval is 1ms
		double dt = 1.0e-3;
		
		// sine with 100 Hz, 0.77V amplitude, phase = 33 deg
		double f_Signal = 10.0;
		double phi_Signal = 33.0 * Math.PI/180.0;
		double U_Signal = 0.77;
		
		int nSamples = 1000;
		double[] signal = new double[nSamples];
		for (int i=0; i<nSamples; ++i) {
			double t = t0 + i*dt;
			double U = U_Signal * Math.sin(2.0*Math.PI*f_Signal*t + phi_Signal);

			signal[i] = U;
		}
		
		/**
		 * Write the signal to a BinaryTimeseries
		 */
		Path outputFile = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "example1.bts");
		System.out.println("using '" + outputFile + "' for example 1 of BinaryTimeseries");
	
		// compute the file size to know in advance how large of a buffer to allocate
		int filesize = BinaryTimeseries.fileOffset(Double.BYTES, nSamples);

		System.out.println("size of output file for example 1 is " + filesize/1024 + " kB");

		// write
		try (RandomAccessFile memoryFile = new RandomAccessFile(outputFile.toFile(), "rw")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
					filesize);

			long wrintingStart = -System.nanoTime();
			
			// This is where the actual writing happens
			BinaryTimeseries.write(mappedByteBuffer, t0, dt, signal);
			
			
			System.out.println("writing took " + (int) (Math.round((wrintingStart + System.nanoTime()) / 1e3)) + " us");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		/**
		 * Read the signal from the BinaryTimeseries
		 */
		byte dtype_time_readback = BinaryTimeseries.DTYPE_NONE;
		double t0_readback=Double.NaN, dt_readback=Double.NaN;
		double[] signal_readback = null;
		try (RandomAccessFile memoryFile = new RandomAccessFile(outputFile.toFile(), "r")) {
			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, memoryFile.length());

			/**
			 * Artificially swap the endianess of the buffer to trigger correction in reading routine.
			 */
			mappedByteBuffer.order(mappedByteBuffer.order()==ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
			
			long readingStart = -System.nanoTime();
			
			// This is where the actual reading takes place
			if (!BinaryTimeseries.readEndianessOk(mappedByteBuffer)) {
				// swap byte order if endianess was not read correctly in the first try
				mappedByteBuffer.order(mappedByteBuffer.order()==ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
				System.out.println("swapped endianess of read buffer");
			}
			// read time axis parameters
			dtype_time_readback = BinaryTimeseries.readTimeType(mappedByteBuffer);
			if (dtype_time_readback == BinaryTimeseries.DTYPE_DOUBLE) {
				t0_readback = BinaryTimeseries.readTimeT0_double(mappedByteBuffer);
				dt_readback = BinaryTimeseries.readTimeDt_double(mappedByteBuffer);
			}
			signal_readback = BinaryTimeseries.readData_double(mappedByteBuffer);

			System.out.println("reading took " + (int) (Math.round((readingStart + System.nanoTime()) / 1e3)) + " us");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// clean up in the end
		if (Files.exists(outputFile)) {
			try {
				Files.delete(outputFile);
				System.out.println("deleted temporary file for example 1: '"+outputFile+"'");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("output file for example 1 was not created");
		}
		
		// check that signal was read back correctly
		if (dtype_time_readback != BinaryTimeseries.DTYPE_DOUBLE) {
			throw new RuntimeException("readback data type of time is not DTYPE_DOUBLE but '"+BinaryTimeseries.dtypeStr(dtype_time_readback)+"'");
		}
		if (t0 != t0_readback) {
			throw new RuntimeException("readback t0 is not original t0");
		}
		if (dt != dt_readback) {
			throw new RuntimeException("readback dt is not original t0");
		}
		if (signal_readback == null) {
			throw new RuntimeException("readback signal is null");
		}
		if (nSamples != signal_readback.length) {
			throw new RuntimeException("readback signal does not have length nSamples (="+nSamples+") but "+signal_readback.length);
		}
		
		/**
		 * output to console for comparison (e.g. using gnuplot...)
		 */
		for (int i=0; i<nSamples; ++i) {
			double t = t0 + i*dt;
			System.out.println(String.format(Locale.ENGLISH, "%.6e\t%.6e\t%.6e", t, signal[i], signal_readback[i]));
			if (signal[i] != signal_readback[i]) {
				throw new RuntimeException("readback signal mismatch at t="+t+" signal(t)="+signal[i]+" signal_readback(t)="+signal_readback[i]);
			}
		}
	}
	
	

}
