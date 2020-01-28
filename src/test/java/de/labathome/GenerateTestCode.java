package de.labathome;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static de.labathome.GenerateTestData.time_dtypes;
import static de.labathome.GenerateTestData.scaling_dtypes;
import static de.labathome.GenerateTestData.data_dtypes;
import static de.labathome.GenerateTestData.data_sizes;
import static de.labathome.GenerateTestData.javaName;
import static de.labathome.GenerateTestData.javaClassName;

import static de.labathome.GenerateTestData.numSamples;
import static de.labathome.GenerateTestData.numSamplesStr;
import static de.labathome.GenerateTestData.t0Str;
import static de.labathome.GenerateTestData.dtStr;
import static de.labathome.GenerateTestData.scalingOffsetStr;
import static de.labathome.GenerateTestData.scalingFactorStr;

import static de.labathome.GenerateTestData.numSamplesSubset;
import static de.labathome.GenerateTestData.sourceOffset;
import static de.labathome.GenerateTestData.targetOffset;
import static de.labathome.GenerateTestData.t_lStr;
import static de.labathome.GenerateTestData.expectedFirstIndexInside;
import static de.labathome.GenerateTestData.t_uStr;
import static de.labathome.GenerateTestData.expectedLastIndexInside;
/**
 * The purpose of this classof tests is to check the full API of BinaryTimeseries
 * for consistency. The test code can be automagically generated by running the
 * {@code main} method of this class.
 * 
 * @author Jonathan Schilling (jonathan.schilling@mail.de)
 * @version 1.0.0 first published version
 * @version 1.0.1 fixed wrong endianess value
 */
public class GenerateTestCode {
	
	public static void main(String[] args) {
		//generateJavaTestCode();
		generatePythonTestCode();
	}

	/**
	 * generate the tests in Java and write them to src/test/java/GeneratedApiTests.java
	 */
	public static void generateJavaTestCode() {

		try (FileWriter fw = new FileWriter("src/test/java/de/labathome/GeneratedApiTests.java")) {
			try (PrintWriter pw = new PrintWriter(fw)) {

				pw.println("package de.labathome;\n" + 
						"\n" + 
						"import static org.junit.jupiter.api.Assertions.assertArrayEquals;\n" + 
						"import static org.junit.jupiter.api.Assertions.assertEquals;\n" + 
						"\n" + 
						"import java.io.RandomAccessFile;\n" +
						"import java.nio.ByteBuffer;\n" +
						"import java.nio.MappedByteBuffer;\n" +
						"import java.nio.channels.FileChannel;\n" + 
						"\n" + 
						"import org.junit.jupiter.api.Test;\n" + 
						"\n" + 
						"class GeneratedApiTests {");
				
				for (int time_dtype_idx = 0; time_dtype_idx < time_dtypes.length; ++time_dtype_idx) {
					final byte time_dtype = time_dtypes[time_dtype_idx];
					final String tT = BinaryTimeseries.dtypeStr(time_dtype);
					String jtT = javaName.get(time_dtype);

					for (int scaling_dtype_idx = 0; scaling_dtype_idx < scaling_dtypes.length; ++scaling_dtype_idx) {
						final byte scaling_dtype = scaling_dtypes[scaling_dtype_idx];
						final String tS = BinaryTimeseries.dtypeStr(scaling_dtype);
						String jtS = javaName.get(scaling_dtype);

						for (int data_dtype_idx = 0; data_dtype_idx < data_dtypes.length; ++data_dtype_idx) {
							final byte data_dtype = data_dtypes[data_dtype_idx];
							final String tD = BinaryTimeseries.dtypeStr(data_dtype);
							String jtD = javaName.get(data_dtype);
							String jctD = javaClassName.get(data_dtype);
							final int data_size = data_sizes[data_dtype_idx];

							final String testId = tT + "_" + tS + "_" + tD;
							
							System.out.println(testId);
							pw.println("	// " + testId);

							// compute file size from reserved number of header bytes, sample size and
							// number of samples
							final int filesize = 64 + data_size * numSamples;

							// check static routines
							if (scaling_dtype_idx == 0 && data_dtype_idx == 0) {
								// these need to be executed only once per time type

								// buildTimebase
								pw.println("	@Test\n" +
										"	public void testBuiltTimebase_"+tT+"() {\n" +
										"		final int numSamples = "+numSamplesStr+";\n" +
										"		final "+jtT+" t0_"+tT+" = ("+jtT+") "+t0Str+";\n" +
										"		final "+jtT+" dt_"+tT+" = ("+jtT+") "+dtStr+";\n" +
										"		final int numSamplesSubset = "+numSamplesSubset+";\n" +
										"		final int sourceOffset = "+sourceOffset+";\n" +
										"		final int targetOffset = "+targetOffset+";\n" +
										"		\n" +
										"		// 'manually' build reference time stamps\n" +
										"		final "+jtT+"[] timebase = new "+jtT+"[numSamples];\n" +
										"		for (int i=0; i<numSamples; ++i) {\n" +
										"			timebase[i] = t0_"+tT+" + i*dt_"+tT+";\n" +
										"		}\n" +
										"		final "+jtT+"[] timebase_subset = new "+jtT+"[numSamplesSubset];\n" +
										"		System.arraycopy(timebase, sourceOffset, timebase_subset, targetOffset, numSamplesSubset);\n" +
										"		\n" +
										"		// no sourceOffset and targetOffset given\n" +
										"		final "+jtT+"[] targetTimebase = new "+jtT+"[numSamples];\n" +
										"		BinaryTimeseries.buildTimebase(targetTimebase, t0_"+tT+", dt_"+tT+");\n" +
										"		assertArrayEquals(timebase, targetTimebase);\n" +
										"		\n" +
										"		// sourceOffset and targetOffset are given\n" +
										"		final "+jtT+"[] targetTimebase_subset = new "+jtT+"[numSamplesSubset];\n" +
										"		BinaryTimeseries.buildTimebase(sourceOffset, targetTimebase_subset, targetOffset, numSamplesSubset, t0_"+tT+", dt_"+tT+");\n" +
										"		assertArrayEquals(timebase_subset, targetTimebase_subset);\n" +
										"	}\n");

								// testFirstIndexInside
								pw.println("	@Test\n" +
										"	public void testFirstIndexInside_"+tT+"() {\n" +
										"		final "+jtT+" t0_"+tT+"  = ("+jtT+") "+t0Str+";\n" +
										"		final "+jtT+" dt_"+tT+"  = ("+jtT+") "+dtStr+";\n" +
										"		final "+jtT+" t_l_"+tT+" = ("+jtT+") "+t_lStr+";\n" +
										"		assertEquals("+expectedFirstIndexInside+", BinaryTimeseries.firstIndexInside(t0_"+tT+", dt_"+tT+", t_l_"+tT+"));\n" +
										"	}\n");

								// testLastIndexInside
								pw.println("	@Test\n" +
										"	public void testLastIndexInside_"+tT+"() {\n" +
										"		final "+jtT+" t0_"+tT+"  = ("+jtT+") "+t0Str+";\n" +
										"		final "+jtT+" dt_"+tT+"  = ("+jtT+") "+dtStr+";\n" +
										"		final "+jtT+" t_u_"+tT+" = ("+jtT+") "+t_uStr+";\n" +
										"		assertEquals("+expectedLastIndexInside+", BinaryTimeseries.lastIndexInside(t0_"+tT+", dt_"+tT+", t_u_"+tT+"));\n" +
										"	}\n");
							}

							if (time_dtype_idx == 0 && data_dtype_idx == 0) {
								// these need to be executed only once per scaling type

							}

							if (time_dtype_idx == 0 && scaling_dtype_idx == 0) {
								// these need to be executed only once per data type

								// fileOffset
								pw.println("	@Test\n" +
										"	public void testFileOffset_"+tD+"() {\n" +
										"		assertEquals("+filesize+", BinaryTimeseries.fileOffset("+jctD+".BYTES, "+numSamplesStr+"));\n" +
										"	}\n");
							}

							// code for testing the API write and read methods
							String writeTestCode = "	@Test\n" +
									"	public void testReadWrite_"+testId+"() {\n";

							writeTestCode += "		final "+jtT+" t0_"+tT+" = ("+jtT+") "+t0Str+";\n";
							writeTestCode += "		final "+jtT+" dt_"+tT+" = ("+jtT+") "+dtStr+";\n";

							writeTestCode += "		final double scalingOffset = "+scalingOffsetStr+";\n";
							writeTestCode += "		final double scalingFactor = "+scalingFactorStr+";\n";
							
							// scaling offset
							if (scaling_dtype != BinaryTimeseries.DTYPE_NONE) {
								writeTestCode += "		final "+jtS+" scalingOffset_"+tS+" = ("+jtS+") scalingOffset;\n";
								writeTestCode += "		final "+jtS+" scalingFactor_"+tS+" = ("+jtS+") scalingFactor;\n";
							}

							writeTestCode += "		final int numSamples = "+numSamplesStr+";\n";

							writeTestCode += "		final "+jtD+"[] values = new "+jtD+"[numSamples];\n" +
									"		for (int i=0; i<numSamples; ++i) {\n";
							writeTestCode += "			values[i] = ("+jtD+") (scalingOffset + i*scalingFactor);\n";
							writeTestCode += "		}\n" +
									"		//writing\n" +
									"		int fileSize = BinaryTimeseries.fileOffset("+data_size+", numSamples);\n" +
									"		final byte[] targetArr = new byte[fileSize];\n" +
									"		final ByteBuffer target = ByteBuffer.wrap(targetArr);\n" +
									"		assertEquals(0, target.position());\n" +
									"		BinaryTimeseries.writeEndianessCheckValue(target);\n" +
									"		assertEquals(2, target.position());\n" +
									"		BinaryTimeseries.writeTimebase(target, t0_"+tT+", dt_"+tT+");\n" +
									"		assertEquals(19, target.position());\n";

							if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
								writeTestCode += "		BinaryTimeseries.writeScalingDisabled(target);\n";
							} else {
								writeTestCode += "		BinaryTimeseries.writeScaling(target, scalingOffset_"+tS+", scalingFactor_"+tS+");\n";
							}
							writeTestCode += "		assertEquals(36, target.position());\n";

							writeTestCode += "		BinaryTimeseries.writeReservedDummy(target);\n" +
									"		assertEquals(59, target.position());\n" +
									"		BinaryTimeseries.writeData(target, values);\n" +
									"		assertEquals(fileSize, target.position());\n";

							// read reference data from binary files in src/test/resources
							String referenceFilename = "src/test/resources/"+testId+".bts";
							writeTestCode += "		byte[] referenceBTS_"+testId+" = null;\n" + 
							"		try (RandomAccessFile memoryFile = new RandomAccessFile(\""+referenceFilename+"\", \"r\")) {\n" + 
							"			int fileLength = (int) memoryFile.length();\n" +
							"			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);\n" +
							"			referenceBTS_"+testId+" = new byte[fileLength];\n" +
							"			mappedByteBuffer.get(referenceBTS_"+testId+");\n" +
							"		} catch (Exception e) {\n" +
							"			e.printStackTrace();\n" +
							"		}\n";
							
							// finally actually check the array contents
							writeTestCode += "		assertArrayEquals(referenceBTS_"+testId+", targetArr);\n";

							if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
								// rewind and re-check using write() without scaling
								writeTestCode += "		target.position(0);\n" +
										"		BinaryTimeseries.write(target, t0_"+tT+", dt_"+tT+", values);\n" +
										"		assertEquals(fileSize, target.position());\n" +
										"		assertArrayEquals(referenceBTS_"+testId+", targetArr);\n";
							} else {
								// rewind and re-check using write() with scaling
								writeTestCode += "		target.position(0);\n" +
										"		BinaryTimeseries.write(target, t0_"+tT+", dt_"+tT+", values, scalingOffset_"+tS+", scalingFactor_"+tS+");\n" +
										"		assertEquals(fileSize, target.position());\n" +
										"		assertArrayEquals(referenceBTS_"+testId+", targetArr);\n";
							}

							// now that the writing routines are verified, check the reading routines

							writeTestCode += "		// reading\n" +
									"		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_"+testId+");\n" +
									"		assertEquals(0, source.position());\n" +
									"		assertEquals(true, BinaryTimeseries.readEndianessOk(source));\n" +
									"		assertEquals(2, source.position());\n" +
									"		assertEquals("+time_dtype+", BinaryTimeseries.readTimeType(source));\n" +
									"		assertEquals(3, source.position());\n" +
									"		assertEquals(t0_"+tT+", BinaryTimeseries.readTimeT0_"+jtT+"(source));\n" +
									"		assertEquals(11, source.position());\n" +
									"		assertEquals(dt_"+tT+", BinaryTimeseries.readTimeDt_"+jtT+"(source));\n" +
									"		assertEquals(19, source.position());\n" +
									"		assertEquals("+scaling_dtype+", BinaryTimeseries.readScalingType(source));\n" +
									"		assertEquals(20, source.position());\n";
							if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
								writeTestCode += "		BinaryTimeseries.readScalingDisabled(source);\n";
							} else {
								writeTestCode += "		assertEquals(scalingOffset_"+tS+", BinaryTimeseries.readScalingOffset_"+jtS+"(source));\n" +
										"		assertEquals(28, source.position());\n" +
										"		assertEquals(scalingFactor_"+tS+", BinaryTimeseries.readScalingFactor_"+jtS+"(source));\n" +
										"		assertEquals(36, source.position());\n";
							}
							writeTestCode += "		assertEquals(36, source.position());\n" +
									"		BinaryTimeseries.readReservedDummy(source);\n" +
									"		assertEquals(59, source.position());\n" +
									"		assertEquals("+data_dtype+", BinaryTimeseries.readDataType(source));\n" +
									"		assertEquals(60, source.position());\n" +
									"		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));\n" +
									"		assertEquals(64, source.position());\n" +
									"		final "+jtD+"[] rawData = new "+jtD+"[numSamples];\n" +
									"		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);\n" +
									"		assertEquals(fileSize, source.position());\n" +
									"		assertArrayEquals(values, rawData);\n";

							// test reading of data into a given array type --> readData_byte...
							writeTestCode += "		// read and scale into given primitive array\n" +
									"		final   byte[] referenceData_byte   = new   byte[numSamples];\n" +
									"		final  short[] referenceData_short  = new  short[numSamples];\n" +
									"		final    int[] referenceData_int    = new    int[numSamples];\n" +
									"		final   long[] referenceData_long   = new   long[numSamples];\n" +
									"		final  float[] referenceData_float  = new  float[numSamples];\n" +
									"		final double[] referenceData_double = new double[numSamples];\n" +
									"		for (int i=0; i<numSamples; ++i) {\n";

							writeTestCode += "			final "+jtD+" referenceValue = ("+jtD+") (scalingOffset + i*scalingFactor);\n";
							if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
								writeTestCode += "			referenceData_byte  [i] = (byte  ) referenceValue;\n" +
												"			referenceData_short [i] = (short ) referenceValue;\n" +
												"			referenceData_int   [i] = (int   ) referenceValue;\n" +
												"			referenceData_long  [i] = (long  ) referenceValue;\n" +
												"			referenceData_float [i] = (float ) referenceValue;\n" +
												"			referenceData_double[i] = (double) referenceValue;\n";
							} else {
								//writeTestCode += "			final "+jtD+" referenceValue = ("+jtD+") (scalingOffset + i*scalingFactor);\n" +
								writeTestCode += "			referenceData_byte  [i] = (byte  )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
										"			referenceData_short [i] = (short )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
										"			referenceData_int   [i] = (int   )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
										"			referenceData_long  [i] = (long  )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
										"			referenceData_float [i] = (float )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
										"			referenceData_double[i] = (double)(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n";
							}
							writeTestCode += "		}\n" +
									"		source.position(19);\n" +
									"		final byte[] data_byte = BinaryTimeseries.readData_byte(source);\n" +
									"		assertEquals(fileSize, source.position());\n" +
									"		assertArrayEquals(referenceData_byte, data_byte);\n" +
									"		source.position(19);\n" +
									"		final short[] data_short = BinaryTimeseries.readData_short(source);\n" +
									"		assertEquals(fileSize, source.position());\n" +
									"		assertArrayEquals(referenceData_short, data_short);\n" +
									"		source.position(19);\n" +
									"		final int[] data_int = BinaryTimeseries.readData_int(source);\n" +
									"		assertEquals(fileSize, source.position());\n" +
									"		assertArrayEquals(referenceData_int, data_int);\n" + "		source.position(19);\n" +
									"		final long[] data_long = BinaryTimeseries.readData_long(source);\n" +
									"		assertEquals(fileSize, source.position());\n" +
									"		assertArrayEquals(referenceData_long, data_long);\n" +
									"		source.position(19);\n" +
									"		final float[] data_float = BinaryTimeseries.readData_float(source);\n" +
									"		assertEquals(fileSize, source.position());\n" +
									"		assertArrayEquals(referenceData_float, data_float);\n" +
									"		source.position(19);\n" +
									"		final double[] data_double = BinaryTimeseries.readData_double(source);\n" +
									"		assertEquals(fileSize, source.position());\n" +
									"		assertArrayEquals(referenceData_double, data_double);\n";

							writeTestCode += "	}\n";
							pw.println(writeTestCode);
						}
					}
				}

				pw.println("} // class GeneratedApiTests");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * generate the tests in Python and write them to src/test/python/GeneratedApiTests.py
	 */
	public static void generatePythonTestCode() {

		try (FileWriter fw = new FileWriter("src/test/python/GeneratedApiTests.py")) {
			try (PrintWriter pw = new PrintWriter(fw)) {

				pw.println("# -*- coding: utf-8 -*-\n" +
						"import unittest\n" + 
						"import sys\n" + 
						"bts_path = '../../main/python'\n" + 
						"if not bts_path in sys.path:\n" + 
						"	sys.path.insert(0, bts_path)\n" + 
						"from BinaryTimeseries import BinaryTimeseries\n" + 
						"\n");
				
				for (int time_dtype_idx = 0; time_dtype_idx < time_dtypes.length; ++time_dtype_idx) {
					final byte time_dtype = time_dtypes[time_dtype_idx];
					final String tT = BinaryTimeseries.dtypeStr(time_dtype);
					String jtT = javaName.get(time_dtype);

					for (int scaling_dtype_idx = 0; scaling_dtype_idx < scaling_dtypes.length; ++scaling_dtype_idx) {
						final byte scaling_dtype = scaling_dtypes[scaling_dtype_idx];
						final String tS = BinaryTimeseries.dtypeStr(scaling_dtype);
						String jtS = javaName.get(scaling_dtype);

						for (int data_dtype_idx = 0; data_dtype_idx < data_dtypes.length; ++data_dtype_idx) {
							final byte data_dtype = data_dtypes[data_dtype_idx];
							final String tD = BinaryTimeseries.dtypeStr(data_dtype);
							String jtD = javaName.get(data_dtype);
							String jctD = javaClassName.get(data_dtype);
							final int data_size = data_sizes[data_dtype_idx];

							final String testId = tT + "_" + tS + "_" + tD;
							
							final String filename = "../resources/"+testId+".bts";
							
							System.out.println(testId);
							pw.println("# " + testId);
							
							pw.println("with open('"+filename+"', 'rb') as f:\n" + 
									"	bts = BinaryTimeseries(f.fileno())");

//							
//							
//							// compute file size from reserved number of header bytes, sample size and
//							// number of samples
//							final int filesize = 64 + data_size * numSamples;
//
//							// check static routines
//							if (scaling_dtype_idx == 0 && data_dtype_idx == 0) {
//								// these need to be executed only once per time type
//
//								// buildTimebase
//								pw.println("	@Test\n" +
//										"	public void testBuiltTimebase_"+tT+"() {\n" +
//										"		final int numSamples = "+numSamplesStr+";\n" +
//										"		final "+jtT+" t0_"+tT+" = ("+jtT+") "+t0Str+";\n" +
//										"		final "+jtT+" dt_"+tT+" = ("+jtT+") "+dtStr+";\n" +
//										"		final int numSamplesSubset = "+numSamplesSubset+";\n" +
//										"		final int sourceOffset = "+sourceOffset+";\n" +
//										"		final int targetOffset = "+targetOffset+";\n" +
//										"		\n" +
//										"		// 'manually' build reference time stamps\n" +
//										"		final "+jtT+"[] timebase = new "+jtT+"[numSamples];\n" +
//										"		for (int i=0; i<numSamples; ++i) {\n" +
//										"			timebase[i] = t0_"+tT+" + i*dt_"+tT+";\n" +
//										"		}\n" +
//										"		final "+jtT+"[] timebase_subset = new "+jtT+"[numSamplesSubset];\n" +
//										"		System.arraycopy(timebase, sourceOffset, timebase_subset, targetOffset, numSamplesSubset);\n" +
//										"		\n" +
//										"		// no sourceOffset and targetOffset given\n" +
//										"		final "+jtT+"[] targetTimebase = new "+jtT+"[numSamples];\n" +
//										"		BinaryTimeseries.buildTimebase(targetTimebase, t0_"+tT+", dt_"+tT+");\n" +
//										"		assertArrayEquals(timebase, targetTimebase);\n" +
//										"		\n" +
//										"		// sourceOffset and targetOffset are given\n" +
//										"		final "+jtT+"[] targetTimebase_subset = new "+jtT+"[numSamplesSubset];\n" +
//										"		BinaryTimeseries.buildTimebase(sourceOffset, targetTimebase_subset, targetOffset, numSamplesSubset, t0_"+tT+", dt_"+tT+");\n" +
//										"		assertArrayEquals(timebase_subset, targetTimebase_subset);\n" +
//										"	}\n");
//
//								// testFirstIndexInside
//								pw.println("	@Test\n" +
//										"	public void testFirstIndexInside_"+tT+"() {\n" +
//										"		final "+jtT+" t0_"+tT+"  = ("+jtT+") "+t0Str+";\n" +
//										"		final "+jtT+" dt_"+tT+"  = ("+jtT+") "+dtStr+";\n" +
//										"		final "+jtT+" t_l_"+tT+" = ("+jtT+") "+t_lStr+";\n" +
//										"		assertEquals("+expectedFirstIndexInside+", BinaryTimeseries.firstIndexInside(t0_"+tT+", dt_"+tT+", t_l_"+tT+"));\n" +
//										"	}\n");
//
//								// testLastIndexInside
//								pw.println("	@Test\n" +
//										"	public void testLastIndexInside_"+tT+"() {\n" +
//										"		final "+jtT+" t0_"+tT+"  = ("+jtT+") "+t0Str+";\n" +
//										"		final "+jtT+" dt_"+tT+"  = ("+jtT+") "+dtStr+";\n" +
//										"		final "+jtT+" t_u_"+tT+" = ("+jtT+") "+t_uStr+";\n" +
//										"		assertEquals("+expectedLastIndexInside+", BinaryTimeseries.lastIndexInside(t0_"+tT+", dt_"+tT+", t_u_"+tT+"));\n" +
//										"	}\n");
//							}
//
//							if (time_dtype_idx == 0 && data_dtype_idx == 0) {
//								// these need to be executed only once per scaling type
//
//							}
//
//							if (time_dtype_idx == 0 && scaling_dtype_idx == 0) {
//								// these need to be executed only once per data type
//
//								// fileOffset
//								pw.println("	@Test\n" +
//										"	public void testFileOffset_"+tD+"() {\n" +
//										"		assertEquals("+filesize+", BinaryTimeseries.fileOffset("+jctD+".BYTES, "+numSamplesStr+"));\n" +
//										"	}\n");
//							}
//
//							// code for testing the API write and read methods
//							String writeTestCode = "	@Test\n" +
//									"	public void testReadWrite_"+testId+"() {\n";
//
//							writeTestCode += "		final "+jtT+" t0_"+tT+" = ("+jtT+") "+t0Str+";\n";
//							writeTestCode += "		final "+jtT+" dt_"+tT+" = ("+jtT+") "+dtStr+";\n";
//
//							writeTestCode += "		final double scalingOffset = "+scalingOffsetStr+";\n";
//							writeTestCode += "		final double scalingFactor = "+scalingFactorStr+";\n";
//							
//							// scaling offset
//							if (scaling_dtype != BinaryTimeseries.DTYPE_NONE) {
//								writeTestCode += "		final "+jtS+" scalingOffset_"+tS+" = ("+jtS+") scalingOffset;\n";
//								writeTestCode += "		final "+jtS+" scalingFactor_"+tS+" = ("+jtS+") scalingFactor;\n";
//							}
//
//							writeTestCode += "		final int numSamples = "+numSamplesStr+";\n";
//
//							writeTestCode += "		final "+jtD+"[] values = new "+jtD+"[numSamples];\n" +
//									"		for (int i=0; i<numSamples; ++i) {\n";
//							writeTestCode += "			values[i] = ("+jtD+") (scalingOffset + i*scalingFactor);\n";
//							writeTestCode += "		}\n" +
//									"		//writing\n" +
//									"		int fileSize = BinaryTimeseries.fileOffset("+data_size+", numSamples);\n" +
//									"		final byte[] targetArr = new byte[fileSize];\n" +
//									"		final ByteBuffer target = ByteBuffer.wrap(targetArr);\n" +
//									"		assertEquals(0, target.position());\n" +
//									"		BinaryTimeseries.writeEndianessCheckValue(target);\n" +
//									"		assertEquals(2, target.position());\n" +
//									"		BinaryTimeseries.writeTimebase(target, t0_"+tT+", dt_"+tT+");\n" +
//									"		assertEquals(19, target.position());\n";
//
//							if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
//								writeTestCode += "		BinaryTimeseries.writeScalingDisabled(target);\n";
//							} else {
//								writeTestCode += "		BinaryTimeseries.writeScaling(target, scalingOffset_"+tS+", scalingFactor_"+tS+");\n";
//							}
//							writeTestCode += "		assertEquals(36, target.position());\n";
//
//							writeTestCode += "		BinaryTimeseries.writeReservedDummy(target);\n" +
//									"		assertEquals(59, target.position());\n" +
//									"		BinaryTimeseries.writeData(target, values);\n" +
//									"		assertEquals(fileSize, target.position());\n";
//
//							// read reference data from binary files in src/test/resources
//							String referenceFilename = "src/test/resources/"+testId+".bts";
//							writeTestCode += "		byte[] referenceBTS_"+testId+" = null;\n" + 
//							"		try (RandomAccessFile memoryFile = new RandomAccessFile(\""+referenceFilename+"\", \"r\")) {\n" + 
//							"			int fileLength = (int) memoryFile.length();\n" +
//							"			MappedByteBuffer mappedByteBuffer = memoryFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, fileLength);\n" +
//							"			referenceBTS_"+testId+" = new byte[fileLength];\n" +
//							"			mappedByteBuffer.get(referenceBTS_"+testId+");\n" +
//							"		} catch (Exception e) {\n" +
//							"			e.printStackTrace();\n" +
//							"		}\n";
//							
//							// finally actually check the array contents
//							writeTestCode += "		assertArrayEquals(referenceBTS_"+testId+", targetArr);\n";
//
//							if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
//								// rewind and re-check using write() without scaling
//								writeTestCode += "		target.position(0);\n" +
//										"		BinaryTimeseries.write(target, t0_"+tT+", dt_"+tT+", values);\n" +
//										"		assertEquals(fileSize, target.position());\n" +
//										"		assertArrayEquals(referenceBTS_"+testId+", targetArr);\n";
//							} else {
//								// rewind and re-check using write() with scaling
//								writeTestCode += "		target.position(0);\n" +
//										"		BinaryTimeseries.write(target, t0_"+tT+", dt_"+tT+", values, scalingOffset_"+tS+", scalingFactor_"+tS+");\n" +
//										"		assertEquals(fileSize, target.position());\n" +
//										"		assertArrayEquals(referenceBTS_"+testId+", targetArr);\n";
//							}
//
//							// now that the writing routines are verified, check the reading routines
//
//							writeTestCode += "		// reading\n" +
//									"		final ByteBuffer source = ByteBuffer.wrap(referenceBTS_"+testId+");\n" +
//									"		assertEquals(0, source.position());\n" +
//									"		assertEquals(true, BinaryTimeseries.readEndianessOk(source));\n" +
//									"		assertEquals(2, source.position());\n" +
//									"		assertEquals("+time_dtype+", BinaryTimeseries.readTimeType(source));\n" +
//									"		assertEquals(3, source.position());\n" +
//									"		assertEquals(t0_"+tT+", BinaryTimeseries.readTimeT0_"+jtT+"(source));\n" +
//									"		assertEquals(11, source.position());\n" +
//									"		assertEquals(dt_"+tT+", BinaryTimeseries.readTimeDt_"+jtT+"(source));\n" +
//									"		assertEquals(19, source.position());\n" +
//									"		assertEquals("+scaling_dtype+", BinaryTimeseries.readScalingType(source));\n" +
//									"		assertEquals(20, source.position());\n";
//							if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
//								writeTestCode += "		BinaryTimeseries.readScalingDisabled(source);\n";
//							} else {
//								writeTestCode += "		assertEquals(scalingOffset_"+tS+", BinaryTimeseries.readScalingOffset_"+jtS+"(source));\n" +
//										"		assertEquals(28, source.position());\n" +
//										"		assertEquals(scalingFactor_"+tS+", BinaryTimeseries.readScalingFactor_"+jtS+"(source));\n" +
//										"		assertEquals(36, source.position());\n";
//							}
//							writeTestCode += "		assertEquals(36, source.position());\n" +
//									"		BinaryTimeseries.readReservedDummy(source);\n" +
//									"		assertEquals(59, source.position());\n" +
//									"		assertEquals("+data_dtype+", BinaryTimeseries.readDataType(source));\n" +
//									"		assertEquals(60, source.position());\n" +
//									"		assertEquals(numSamples, BinaryTimeseries.readNumSamples(source));\n" +
//									"		assertEquals(64, source.position());\n" +
//									"		final "+jtD+"[] rawData = new "+jtD+"[numSamples];\n" +
//									"		BinaryTimeseries.readRawData(source, rawData, 0, numSamples);\n" +
//									"		assertEquals(fileSize, source.position());\n" +
//									"		assertArrayEquals(values, rawData);\n";
//
//							// test reading of data into a given array type --> readData_byte...
//							writeTestCode += "		// read and scale into given primitive array\n" +
//									"		final   byte[] referenceData_byte   = new   byte[numSamples];\n" +
//									"		final  short[] referenceData_short  = new  short[numSamples];\n" +
//									"		final    int[] referenceData_int    = new    int[numSamples];\n" +
//									"		final   long[] referenceData_long   = new   long[numSamples];\n" +
//									"		final  float[] referenceData_float  = new  float[numSamples];\n" +
//									"		final double[] referenceData_double = new double[numSamples];\n" +
//									"		for (int i=0; i<numSamples; ++i) {\n";
//
//							writeTestCode += "			final "+jtD+" referenceValue = ("+jtD+") (scalingOffset + i*scalingFactor);\n";
//							if (scaling_dtype == BinaryTimeseries.DTYPE_NONE) {
//								writeTestCode += "			referenceData_byte  [i] = (byte  ) referenceValue;\n" +
//												"			referenceData_short [i] = (short ) referenceValue;\n" +
//												"			referenceData_int   [i] = (int   ) referenceValue;\n" +
//												"			referenceData_long  [i] = (long  ) referenceValue;\n" +
//												"			referenceData_float [i] = (float ) referenceValue;\n" +
//												"			referenceData_double[i] = (double) referenceValue;\n";
//							} else {
//								//writeTestCode += "			final "+jtD+" referenceValue = ("+jtD+") (scalingOffset + i*scalingFactor);\n" +
//								writeTestCode += "			referenceData_byte  [i] = (byte  )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
//										"			referenceData_short [i] = (short )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
//										"			referenceData_int   [i] = (int   )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
//										"			referenceData_long  [i] = (long  )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
//										"			referenceData_float [i] = (float )(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n" +
//										"			referenceData_double[i] = (double)(scalingOffset_"+tS+" + referenceValue*scalingFactor_"+tS+");\n";
//							}
//							writeTestCode += "		}\n" +
//									"		source.position(19);\n" +
//									"		final byte[] data_byte = BinaryTimeseries.readData_byte(source);\n" +
//									"		assertEquals(fileSize, source.position());\n" +
//									"		assertArrayEquals(referenceData_byte, data_byte);\n" +
//									"		source.position(19);\n" +
//									"		final short[] data_short = BinaryTimeseries.readData_short(source);\n" +
//									"		assertEquals(fileSize, source.position());\n" +
//									"		assertArrayEquals(referenceData_short, data_short);\n" +
//									"		source.position(19);\n" +
//									"		final int[] data_int = BinaryTimeseries.readData_int(source);\n" +
//									"		assertEquals(fileSize, source.position());\n" +
//									"		assertArrayEquals(referenceData_int, data_int);\n" + "		source.position(19);\n" +
//									"		final long[] data_long = BinaryTimeseries.readData_long(source);\n" +
//									"		assertEquals(fileSize, source.position());\n" +
//									"		assertArrayEquals(referenceData_long, data_long);\n" +
//									"		source.position(19);\n" +
//									"		final float[] data_float = BinaryTimeseries.readData_float(source);\n" +
//									"		assertEquals(fileSize, source.position());\n" +
//									"		assertArrayEquals(referenceData_float, data_float);\n" +
//									"		source.position(19);\n" +
//									"		final double[] data_double = BinaryTimeseries.readData_double(source);\n" +
//									"		assertEquals(fileSize, source.position());\n" +
//									"		assertArrayEquals(referenceData_double, data_double);\n";
//
//							writeTestCode += "	}\n";
//							pw.println(writeTestCode);
						}
					}
				}
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
