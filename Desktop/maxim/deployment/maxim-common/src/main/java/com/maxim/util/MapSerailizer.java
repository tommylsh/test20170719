package com.maxim.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Note that the structure does not support escape character (e.g. key and value
 * must not contain ':' and ',')
 * 
 * @author Steven
 * 
 */
public abstract class MapSerailizer<T> {

	public String serialize(Map<String, T> map) {
		StringBuilder sb = new StringBuilder();

		Set<Entry<String, T>> entries = map.entrySet();

		for (Iterator<Entry<String, T>> itr = entries.iterator(); itr.hasNext();) {
			Entry<String, T> entry = itr.next();
			sb.append(entry.getKey()).append(":").append(entry.getValue());
			if (itr.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public Map<String, T> deserialize(String mapString) {
		Map<String, T> map = new HashMap<String, T>();

		if (mapString == null || mapString.isEmpty()) {
			return map;
		}

		String[] pairs = mapString.split(",");
		for (String pair : pairs) {
			String[] entry = pair.split(":");
			if (entry.length != 2) {
				continue;
			}
			map.put(entry[0], parse((String) entry[1]));
		}
		return map;
	}

	/**
	 * To be overrided, convert string to object
	 * 
	 * @param objStr
	 * @return
	 */
	protected abstract T parse(String objStr);

}
