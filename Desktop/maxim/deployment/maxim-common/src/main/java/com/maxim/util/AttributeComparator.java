package com.maxim.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Compare the attributes among two objects, the attribute must implement
 * comparable
 * 
 * @author Steven
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class AttributeComparator implements Comparator<Serializable> {

	private Collection<Attribute> attributes;

	public AttributeComparator(Collection<Attribute> orders) {
		super();
		this.attributes = orders;
	}

	private static Map<String, Object> toMap(Object object)
			throws JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		StringWriter sw = new StringWriter();
		om.writeValue(sw, object);
		return om.readValue(sw.toString(), Map.class);
	}

	public int compare(Serializable o1, Serializable o2) {
		try {
			// BeanMap obj1 = new BeanMap(o1);
			// BeanMap obj2 = new BeanMap(o2);
			Map obj1 = toMap(o1);
			Map obj2 = toMap(o2);

			// compare ordering fields
			if (attributes != null && !attributes.isEmpty()) {
				for (Attribute attribute : attributes) {
					String field = attribute.getField();

					Comparable value1 = (Comparable) obj1.get(field);
					Comparable value2 = (Comparable) obj2.get(field);

					if (value1 == null && value2 != null) {
						return -1;
					} else if (value1 != null && value2 == null) {
						return 1;
					} else {
						int result = attribute.isDesc() ? value2.compareTo(value1)
								: value1.compareTo(value2);
						if (result != 0) {
							return result;
						}
					}
				}
			}
			return 0;
		} catch (IOException ioe) {
			return 0;
		}
	}
}
