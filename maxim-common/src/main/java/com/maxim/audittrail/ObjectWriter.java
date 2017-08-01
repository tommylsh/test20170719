package com.maxim.audittrail;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectWriter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ObjectWriter.class);

	public static String write(Object obj) {

		@SuppressWarnings("rawtypes")
		Class objClass = obj.getClass();

		StringBuilder sb = new StringBuilder();
		sb.append(objClass.getSimpleName() + " {");

		// scan for attribute
		for (Method method : objClass.getMethods()) {

			String methodName = method.getName();

			// skip if no LogAttribute
			if (!method.isAnnotationPresent(LogAttribute.class)) {
				continue;
			}

			try {
				sb.append(methodName + ":" + method.invoke(obj) + " ");
			} catch (Exception e) {
				LOGGER.warn("Unable to write log [" + objClass.getSimpleName()
						+ " " + methodName + "]");
			}
		}

		return sb.append("}").toString();
	}
}
