package org.ubjson.model;

import static org.ubjson.io.IUBJTypeMarker.ARRAY;
import static org.ubjson.io.IUBJTypeMarker.ARRAY_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.BYTE;
import static org.ubjson.io.IUBJTypeMarker.DOUBLE;
import static org.ubjson.io.IUBJTypeMarker.END;
import static org.ubjson.io.IUBJTypeMarker.FALSE;
import static org.ubjson.io.IUBJTypeMarker.FLOAT;
import static org.ubjson.io.IUBJTypeMarker.HUGE;
import static org.ubjson.io.IUBJTypeMarker.HUGE_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.INT16;
import static org.ubjson.io.IUBJTypeMarker.INT32;
import static org.ubjson.io.IUBJTypeMarker.INT64;
import static org.ubjson.io.IUBJTypeMarker.NULL;
import static org.ubjson.io.IUBJTypeMarker.OBJECT;
import static org.ubjson.io.IUBJTypeMarker.OBJECT_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.STRING;
import static org.ubjson.io.IUBJTypeMarker.STRING_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.TRUE;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import org.ubjson.io.UBJFormatException;
import org.ubjson.io.UBJInputStreamParser;
import org.ubjson.io.UBJOutputStream;

public class ObjectValue extends
		AbstractCollectionValue<Map<String, IValue<?>>> {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ObjectValue() {
		value = new HashMap();
	}

	public ObjectValue(UBJInputStreamParser in) throws IOException,
			UBJFormatException {
		deserialize(in);
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + hashCode() + " [size="
				+ (value == null ? "" : value.size()) + ", elements="
				+ (value == null ? "" : value.toString()) + "]";
	}

	@Override
	public byte getType() {
		return (value.size() < 256 ? OBJECT_COMPACT : OBJECT);
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		writeValue(out, this);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			UBJFormatException {
		int size = in.readObjectLength();
		value = new HashMap<String, IValue<?>>(size * 3);

		// TODO: add support for streaming.
		if (size == -1)
			throw new IOException(
					"Deserializing an unbounded OBJECT (length 255) container is not supported by this class.");

		int read = 0;
		int type = -1;

		// Loop until we read size items or hit EOS.
		while ((read < size) && (type = in.nextType()) != -1) {
			String name = in.readString();

			// Switch on the value type.
			switch ((type = in.nextType())) {
			case END:
				value.put(name, new EndValue(in));
				break;

			case NULL:
				value.put(name, new NullValue(in));
				break;

			case TRUE:
			case FALSE:
				value.put(name, new BooleanValue(in));
				break;

			case BYTE:
				value.put(name, new ByteValue(in));
				break;

			case INT16:
				value.put(name, new Int16Value(in));
				break;

			case INT32:
				value.put(name, new Int32Value(in));
				break;

			case INT64:
				value.put(name, new Int64Value(in));
				break;

			case FLOAT:
				value.put(name, new FloatValue(in));
				break;

			case DOUBLE:
				value.put(name, new DoubleValue(in));
				break;

			case HUGE:
			case HUGE_COMPACT:
				boolean isDecimal = false;
				CharBuffer buffer = in.readHugeAsChars();
				char[] array = buffer.array();

				for (int i = buffer.position(), length = buffer.remaining(); !isDecimal
						&& i < length; i++) {
					if (array[i] == '.')
						isDecimal = true;
				}

				if (isDecimal)
					value.put(name, new BigDecimalHugeValue(new BigDecimal(
							array, buffer.position(), buffer.remaining())));
				else
					value.put(
							name,
							new BigIntegerHugeValue(new BigInteger(new String(
									array, buffer.position(), buffer
											.remaining()))));
				break;

			case STRING:
			case STRING_COMPACT:
				value.put(name, new StringValue(in));
				break;

			case ARRAY:
			case ARRAY_COMPACT:
				value.put(name, new ArrayValue(in));
				break;

			case OBJECT:
			case OBJECT_COMPACT:
				value.put(name, new ObjectValue(in));
				break;

			default:
				throw new UBJFormatException(in.getPosition(),
						"Unknown type marker value " + type + " (char='"
								+ ((char) type)
								+ "') encountered at stream position "
								+ in.getPosition() + ".");
			}

			// Keep track of how many name-value pairs we've read.
			read++;
		}
	}
}