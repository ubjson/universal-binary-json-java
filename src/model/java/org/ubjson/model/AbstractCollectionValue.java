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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ubjson.io.UBJOutputStream;

public abstract class AbstractCollectionValue<T> extends AbstractValue<T> {
	protected void writeValue(UBJOutputStream out, IValue<?> value)
			throws IOException, IllegalArgumentException {
		switch (value.getType()) {
		case END:
			out.writeEnd();
			break;

		case NULL:
			out.writeNull();
			break;

		case TRUE:
		case FALSE:
			out.writeBoolean((Boolean) value.getValue());
			break;

		case BYTE:
			out.writeByte((Byte) value.getValue());
			break;

		case INT16:
			out.writeInt16((Short) value.getValue());
			break;

		case INT32:
			out.writeInt32((Integer) value.getValue());
			break;

		case INT64:
			out.writeInt64((Long) value.getValue());
			break;

		case FLOAT:
			out.writeFloat((Float) value.getValue());
			break;

		case DOUBLE:
			out.writeDouble((Double) value.getValue());
			break;

		case HUGE:
		case HUGE_COMPACT:
			Object o = value.getValue();

			if (o instanceof BigInteger)
				out.writeHuge((BigInteger) o);
			else if (o instanceof BigDecimal)
				out.writeHuge((BigDecimal) o);
			else
				throw new IllegalArgumentException(
						"Writing HUGE values of class type ["
								+ o.getClass()
								+ "] is not supported by this class; only BigInteger and BigDecimal are supported.");
			break;

		case STRING:
		case STRING_COMPACT:
			out.writeString((String) value.getValue());
			break;

		case ARRAY:
		case ARRAY_COMPACT:
			@SuppressWarnings("unchecked")
			List<? extends IValue<?>> list = (List<? extends IValue<?>>) value
					.getValue();
			int size = list.size();

			// write header
			out.writeArrayHeader(size);

			// delegate element write recursively
			for (int i = 0; i < size; i++)
				writeValue(out, list.get(i));
			break;

		case OBJECT:
		case OBJECT_COMPACT:
			@SuppressWarnings("unchecked")
			Map<String, ? extends IValue<?>> map = (Map<String, ? extends IValue<?>>) value
					.getValue();
			size = map.size();

			// write header
			out.writeObjectHeader(size);

			Iterator<?> entries = map.entrySet().iterator();

			while (entries.hasNext()) {
				@SuppressWarnings("unchecked")
				Entry<String, ? extends IValue<?>> item = (Entry<String, ? extends IValue<?>>) entries
						.next();

				// write name
				out.writeString(item.getKey());

				// delegate value-write recursively
				writeValue(out, item.getValue());
			}
			break;
		}
	}
}