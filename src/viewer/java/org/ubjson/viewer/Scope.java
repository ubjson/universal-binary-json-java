package org.ubjson.viewer;

/**
 * A class that provides an over-loaded concept of tracking not only the current
 * JSON-esque scope we are in (Object or Array) but also the size of the scope
 * (container) we are in if it is known. For example an Object of length 2 or an
 * Array of length 15.
 * 
 * This is necessary so we can properly print values out and we know which
 * containers they belong to.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */
public class Scope {
	public enum Type {
		ARRAY, OBJECT;
	}

	private Type type;
	private int count;
	private int length;

	// Used to track name-value pairs in OBJECT scopes.
	public boolean toggle;

	public Scope(Type type) throws IllegalArgumentException {
		this(type, -1);
	}

	public Scope(Type type, int length) throws IllegalArgumentException {
		if (type == null)
			throw new IllegalArgumentException("type cannot be null");
		if (length < -1)
			throw new IllegalArgumentException("length [" + length
					+ "] must be >= -1");

		this.type = type;
		this.count = 0;
		this.length = length;
		this.toggle = false;
	}

	@Override
	public String toString() {
		return Scope.class.getName() + "@" + hashCode() + " [type=" + type
				+ ", count=" + count + ", length=" + length + ", toggle="
				+ toggle + "]";
	}

	public Type getType() {
		return type;
	}

	public boolean isFull() {
		return (count == length);
	}

	public int getCount() {
		return count;
	}

	public void incrementCount() throws IllegalStateException {
		if (count + 1 > length)
			throw new IllegalStateException(
					"An attempt was made to increment count [" + count
							+ "] to [" + (count + 1)
							+ "] which is beyond the scope's length [" + length
							+ "].");

		count++;
	}

	public int getLength() {
		return length;
	}
}