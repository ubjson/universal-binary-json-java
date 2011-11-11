package org.ubjson.model;

import static org.ubjson.io.IMarkerType.HUGE;
import static org.ubjson.io.IMarkerType.HUGE_COMPACT;

import java.io.IOException;
import java.math.BigDecimal;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class HugeBigDecimalValue extends AbstractValue<BigDecimal> {
	public HugeBigDecimalValue() {
		// default
	}

	public HugeBigDecimalValue(char[] chars) {
		value = new BigDecimal(chars);
	}
	
	public HugeBigDecimalValue(BigDecimal db) {
		value = db;
	}

	public HugeBigDecimalValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return (value.toString().length() < 256 ? HUGE_COMPACT : HUGE);
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeHuge(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readHugeAsBigDecimal();
	}
}