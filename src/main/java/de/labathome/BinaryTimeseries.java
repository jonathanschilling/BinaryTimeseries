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

}
