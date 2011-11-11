package org.ubjson.model;

import static org.ubjson.io.IMarkerType.HUGE;
import static org.ubjson.io.IMarkerType.HUGE_COMPACT;

import java.io.IOException;
import java.math.BigInteger;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class HugeBigIntegerValue extends AbstractValue<BigInteger> {
	public HugeBigIntegerValue() {
		// default
	}

	public HugeBigIntegerValue(String chars) {
		value = new BigInteger(chars);
	}

	public HugeBigIntegerValue(BigInteger bi) {
		value = bi;
	}

	public HugeBigIntegerValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return (value.bitLength() < 256 ? HUGE_COMPACT : HUGE);
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeHuge(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readHugeAsBigInteger();
	}
}