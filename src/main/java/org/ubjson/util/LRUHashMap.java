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