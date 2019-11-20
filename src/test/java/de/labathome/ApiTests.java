package de.labathome;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * The purpose of this test class is to check the full API of BinaryTimeseries for consistency.
 * The test code can be automagically generated by running the {@code main} method of this class.
 * 
 * @author Jonathan Schilling (jonathan.schilling@mail.de)
 */
public class ApiTests {

	/**
	 * Generate all test methods in this class and print the source code to the command line.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		
		final byte[]    time_dtypes = new byte[] {
				BinaryTimeseries.DTYPE_LONG,
				BinaryTimeseries.DTYPE_DOUBLE
		};
		
		final byte[] scaling_dtypes = new byte[] {
				BinaryTimeseries.DTYPE_NONE,
				BinaryTimeseries.DTYPE_BYTE,
				BinaryTimeseries.DTYPE_SHORT,
				BinaryTimeseries.DTYPE_INT,
				BinaryTimeseries.DTYPE_LONG,
				BinaryTimeseries.DTYPE_FLOAT,
				BinaryTimeseries.DTYPE_DOUBLE
		};
		
		final byte[]    data_dtypes = new byte[] {
				BinaryTimeseries.DTYPE_BYTE,
				BinaryTimeseries.DTYPE_SHORT,
				BinaryTimeseries.DTYPE_INT,
				BinaryTimeseries.DTYPE_LONG,
				BinaryTimeseries.DTYPE_FLOAT,
				BinaryTimeseries.DTYPE_DOUBLE
		};
		
		final int[] data_sizes = new int[] {
				Byte.BYTES,
				Short.BYTES,
				Integer.BYTES,
				Long.BYTES,
				Float.BYTES,
				Double.BYTES
		};
		
		int numTests = 0;
		for (int time_dtype_idx=0; time_dtype_idx<time_dtypes.length; ++time_dtype_idx) {
			final byte time_dtype = time_dtypes[time_dtype_idx];
			
			for (int scaling_dtype_idx=0; scaling_dtype_idx<scaling_dtypes.length; ++scaling_dtype_idx) {
				final byte scaling_dtype = scaling_dtypes[scaling_dtype_idx];
				
				for (int data_dtype_idx=0; data_dtype_idx<data_dtypes.length; ++data_dtype_idx) {
					final byte data_dtype = data_dtypes[data_dtype_idx];
					final int data_size = data_sizes[data_dtype_idx];
					
					System.out.println(String.format("// %2d ", numTests+1)+
							BinaryTimeseries.dtypeStr(   time_dtype)+" "+
							BinaryTimeseries.dtypeStr(scaling_dtype)+" "+
							BinaryTimeseries.dtypeStr(   data_dtype)
					);
					
					// header and ten samples
					final int filesize = 64 + data_size*10;
					
					
					
					
					
					// check static routines
					if (time_dtype_idx == 0 && scaling_dtype_idx == 0) {
						// this needs to be executed only once per data type
						System.out.println("@Test\n"+
								"public void testFileOffset_"+BinaryTimeseries.dtypeStr(data_dtype)+"() {\n"+
								"	assertEquals("+filesize+", BinaryTimeseries.fileOffset("+data_size+", 10));\n"+
								"}\n");
					}
					
					
					
					
					
					
					
					// check writing routines
					
					
					
					
					// check reading routines
					
					
					numTests++;
				}
			}
			
		}
		
		System.out.println("// total number of tests: "+numTests);
		
		
		
	}
	
	// block comment headers are:
	// - index of test
	// - data type of time expression
	// - data type of scaling parameters
	// - data type of raw data
	
	/***********************************
	 * AUTO-GENERATED CODE STARTS HERE *
	 ***********************************/
	
	//  1 L N B
	@Test
	public void testFileOffset_B() {
		assertEquals(74, BinaryTimeseries.fileOffset(1, 10));
	}

	//  2 L N S
	@Test
	public void testFileOffset_S() {
		assertEquals(84, BinaryTimeseries.fileOffset(2, 10));
	}

	//  3 L N I
	@Test
	public void testFileOffset_I() {
		assertEquals(104, BinaryTimeseries.fileOffset(4, 10));
	}

	//  4 L N L
	@Test
	public void testFileOffset_L() {
		assertEquals(144, BinaryTimeseries.fileOffset(8, 10));
	}

	//  5 L N F
	@Test
	public void testFileOffset_F() {
		assertEquals(104, BinaryTimeseries.fileOffset(4, 10));
	}

	//  6 L N D
	@Test
	public void testFileOffset_D() {
		assertEquals(144, BinaryTimeseries.fileOffset(8, 10));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***********************************
	 * AUTO-GENERATED CODE  ENDS  HERE *
	 ***********************************/
}
