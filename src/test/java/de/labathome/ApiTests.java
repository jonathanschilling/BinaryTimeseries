package de.labathome;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * The purpose of this test class is to check the full API of BinaryTimeseries
 * for consistency.
 * 
 * @author Jonathan Schilling (jonathan.schilling@mail.de)
 * @version 1.0.0 first published version
 * @version 1.0.1 fixed wrong endianess value
 * @version 1.0.2 first official Python implementation
 * @version 1.0.3 add explicit close() method in Python implementation
 */
public class ApiTests {

	/**
	 * Test that the dtypeStr() method works as expected.
	 */
	@Test
	public void testDtypes() {
		assertEquals("N", BinaryTimeseries.dtypeStr(BinaryTimeseries.DTYPE_NONE));
		assertEquals("B", BinaryTimeseries.dtypeStr(BinaryTimeseries.DTYPE_BYTE));
		assertEquals("S", BinaryTimeseries.dtypeStr(BinaryTimeseries.DTYPE_SHORT));
		assertEquals("I", BinaryTimeseries.dtypeStr(BinaryTimeseries.DTYPE_INT));
		assertEquals("L", BinaryTimeseries.dtypeStr(BinaryTimeseries.DTYPE_LONG));
		assertEquals("F", BinaryTimeseries.dtypeStr(BinaryTimeseries.DTYPE_FLOAT));
		assertEquals("D", BinaryTimeseries.dtypeStr(BinaryTimeseries.DTYPE_DOUBLE));
		assertEquals("?", BinaryTimeseries.dtypeStr((byte) -1));

		assertEquals(false, BinaryTimeseries.hasScaling(BinaryTimeseries.DTYPE_NONE));
		assertEquals(true, BinaryTimeseries.hasScaling(BinaryTimeseries.DTYPE_BYTE));
		assertEquals(true, BinaryTimeseries.hasScaling(BinaryTimeseries.DTYPE_SHORT));
		assertEquals(true, BinaryTimeseries.hasScaling(BinaryTimeseries.DTYPE_INT));
		assertEquals(true, BinaryTimeseries.hasScaling(BinaryTimeseries.DTYPE_LONG));
		assertEquals(true, BinaryTimeseries.hasScaling(BinaryTimeseries.DTYPE_FLOAT));
		assertEquals(true, BinaryTimeseries.hasScaling(BinaryTimeseries.DTYPE_DOUBLE));
	}

	/**
	 * Test the header data explanation method.
	 */
	@Test
	public void testExplainHeader() {
		String explanation = "";

		byte[] header = null;
		explanation = BinaryTimeseries.explainHeader(header);
		assertEquals("header should not be null and have a length of 64 bytes", explanation);

		header = new byte[1];
		explanation = BinaryTimeseries.explainHeader(header);
		assertEquals("header should not be null and have a length of 64 bytes", explanation);

		header = new byte[64];
		explanation = BinaryTimeseries.explainHeader(header);
		assertEquals("  0   2 reads 0x00 => invalid endianess check value: 0", explanation);
	}

}
