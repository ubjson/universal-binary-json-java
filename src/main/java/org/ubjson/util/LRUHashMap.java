package org.ubjson.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = -4615724930741862324L;

	public static final int DEFAULT_CAPACITY = 256;

	private int capacity;

	public LRUHashMap() {
		this(DEFAULT_CAPACITY);
	}

	public LRUHashMap(int capacity) {
		super(capacity);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return (size() > capacity);
	}
}