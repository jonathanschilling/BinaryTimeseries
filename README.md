# BinaryTimeseries
A binary timeseries storage format, where the time axis is given via an expression.

## Scope
This is the specification for a really simple binary file format for storing a regularly-spaced sequence of
single-channel measurement data in an efficiently writeable and readable format. The basic assumption
is that the time axis `t_i` of a series of `N` measurements can be computed on the fly from the array indices:

```java
for (int i=0; i<N; i++) {
	t_i = t_0 + i * Delta_t;
}
```

where `t_0` is the (reference) timestamp of the first sample and `Delta_t` is the sampling interval.

The data values `y_i` are stored as raw values `y_i_raw`, optionally with an offset `scalingOffset`
and a scaling factor `scalingFactor`:

```java
for (int i=0; i<N; i++) {
	if (hasScaling) {
		y_i = y_i_raw;
	} else {
		y_i = scalingOffset + y_i_raw * scalingFactor;
	}
}
```

The maximum number of samples that can be stored inside this file format is limited by the maximum value of the (signed) `int` type,
which is

```Java
Integer.MAX_VALUE == 2^31 - 1 == 2_147_483_647 \approx 2.1e9
```

This corresponds to a total duration of `T_max = (2^31-1) * Delta_t`.

In the case of raw `double` values as `y_i_raw`, the corresponding maximum file size that can occur is

```Java
(64 + Double.BYTES * 2_147_483_647) == (64+8*(2^31-1)) \approx 16 GB
```

where 64 bytes are reserved for the file header information.

Suppose an ADC samples at a frequency `f = 1 MHz`. Then, the sampling interval is `Delta_t = 1/f = 1 µs`
and the maximum time series length that can be stored in one file in this file format is `T_max \approx 2147 s`.

The recommended file name extension for this file format is `*.bts` for **B**inary **T**ime **S**eries.

## Fast subset reading

The main goal of this file format is to allow easy and fast reading of subsets of the whole time series
data. Having an equally spaced time axis allows to compute the data indices inside a given time interval
and using the definitions in Sec. 3 of the documentation (see below), the offsets in the file can be computed for seeking to the computed
position in the file and reading only the required data from there on.

## Documentation
The specification of this file format is available as a PDF in this repository:
[Binary Timeseries File Format Specification](https://github.com/jonathanschilling/BinaryTimeseries/blob/master/doc/BinaryTimeseries.pdf).

The LaTeX source code and the compiled PDF of this specification are also embedded (as resources) in the `jar` of the Java implementation on Maven Central.

## Implementation
A Java implementation of this file format using a `ByteBuffer` as the file abstraction layer is available in this repository.

The latest release is available on Maven Central:

```
<dependency>
	<groupId>de.labathome</groupId>
	<artifactId>BinaryTimeseries</artifactId>
	<version>1.0.3</version>
</dependency>
```

A (currently read-only) Python implementation of this file format is available on [PyPI](https://pypi.org/project/BinaryTimeseries/):

```
pip install BinaryTimeseries
```

## Useage
A starting point on how to use these classes is given in the following example files:

[src/main/java/examples/Examples.java](https://github.com/jonathanschilling/BinaryTimeseries/blob/master/src/main/java/examples/Examples.java)

[BinaryTimeseries/Examples.py](https://github.com/jonathanschilling/BinaryTimeseries/blob/master/BinaryTimeseries/Examples.py)

