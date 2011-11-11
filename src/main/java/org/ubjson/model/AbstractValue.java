package org.ubjson.model;

public abstract class AbstractValue<T> implements IValue<T> {
	protected T value;

	public AbstractValue() {
		value = null;
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + hashCode() + " [value="
				+ (value == null ? "" : value.toString()) + "]";
	}

	@Override
	public T getValue() {
		return value;
	}
}