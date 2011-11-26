/**   
 * Copyright 2011 The Buzz Media, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ubjson.io.reflect;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class used to implement a LRU cache used by the reflection implementation to
 * cache reflected values from classes for performance reasons.
 * <p/>
 * This class is intentionally package-private as (for the time being) it is a
 * hidden implementation detail of the default reflection-based IO classes.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 * 
 * @param <K>
 *            key type for this map.
 * @param <V>
 *            value type for this map.
 */
class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 6873519493912239225L;

	public static final String CACHE_SIZE_PROPERTY_NAME = "ubjson.io.reflect.cacheSize";

	public static final int CACHE_SIZE = Integer.getInteger(
			CACHE_SIZE_PROPERTY_NAME, 256);

	static {
		if (CACHE_SIZE < 1)
			throw new RuntimeException("Invalid CACHE_SIZE [" + CACHE_SIZE
					+ "], system property [" + CACHE_SIZE_PROPERTY_NAME
					+ "] must be set to an integer value >= 1.");
	}

	private int cacheSize;

	public LRUHashMap() {
		this(CACHE_SIZE);
	}

	public LRUHashMap(int cacheSize) {
		super(cacheSize);
		this.cacheSize = cacheSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return (size() > cacheSize);
	}
}