package org.ubjson.spec;

import static org.ubjson.io.IMarkerType.ARRAY;
import static org.ubjson.io.IMarkerType.ARRAY_COMPACT;
import static org.ubjson.io.IMarkerType.BYTE;
import static org.ubjson.io.IMarkerType.DOUBLE;
import static org.ubjson.io.IMarkerType.END;
import static org.ubjson.io.IMarkerType.FALSE;
import static org.ubjson.io.IMarkerType.FLOAT;
import static org.ubjson.io.IMarkerType.HUGE;
import static org.ubjson.io.IMarkerType.HUGE_COMPACT;
import static org.ubjson.io.IMarkerType.INT16;
import static org.ubjson.io.IMarkerType.INT32;
import static org.ubjson.io.IMarkerType.INT64;
import static org.ubjson.io.IMarkerType.NULL;
import static org.ubjson.io.IMarkerType.OBJECT;
import static org.ubjson.io.IMarkerType.OBJECT_COMPACT;
import static org.ubjson.io.IMarkerType.STRING;
import static org.ubjson.io.IMarkerType.STRING_COMPACT;
import static org.ubjson.io.IMarkerType.TRUE;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;
import org.ubjson.spec.value.BooleanValue;
import org.ubjson.spec.value.ByteValue;
import org.ubjson.spec.value.DoubleValue;
import org.ubjson.spec.value.EndValue;
import org.ubjson.spec.value.FloatValue;
import org.ubjson.spec.value.BigDecimalHugeValue;
import org.ubjson.spec.value.BigIntegerHugeValue;
import org.ubjson.spec.value.IValue;
import org.ubjson.spec.value.Int16Value;
import org.ubjson.spec.value.Int32Value;
import org.ubjson.spec.value.Int64Value;
import org.ubjson.spec.value.NullValue;
import org.ubjson.spec.value.StringValue;

public class ObjectMapValue extends
		AbstractCollectionValue<Map<String, IValue<?>>> {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ObjectMapValue() {
		value = new HashMap();
	}

	public ObjectMapValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
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
			DataFormatException {
		int size = in.readObjectLength();
		value = new HashMap<String, IValue<?>>(size * 3);

		// TODO: add support for streaming.
		if (size == -1)
			throw new IOException(
					"Deserializing an unbounded OBJECT (length 255) container is not supported by this class.");

		int read = 0;
		int type = -1;

		/*
		 * TODO: The logic below isn't right, the "type" we are getting is the
		 * type for the name of the name-value pair, we have no idea what the
		 * value's type is.
		 */

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
				char[] chars = in.readHugeAsChars();

				for (int i = 0; !isDecimal && i < chars.length; i++) {
					if (chars[i] == '.')
						isDecimal = true;
				}

				if (isDecimal)
					value.put(name, new BigDecimalHugeValue(new BigDecimal(
							chars)));
				else
					value.put(name, new BigIntegerHugeValue(new BigInteger(
							new String(chars))));
				break;

			case STRING:
			case STRING_COMPACT:
				value.put(name, new StringValue(in));
				break;

			case ARRAY:
			case ARRAY_COMPACT:
				value.put(name, new ArrayListValue(in));
				break;

			case OBJECT:
			case OBJECT_COMPACT:
				value.put(name, new ObjectMapValue(in));
				break;

			default:
				throw new DataFormatException("Unknown type marker value "
						+ type + " (char='" + ((char) type) + "') encountered.");
			}

			// Keep track of how many name-value pairs we've read.
			read++;
		}
	}
}