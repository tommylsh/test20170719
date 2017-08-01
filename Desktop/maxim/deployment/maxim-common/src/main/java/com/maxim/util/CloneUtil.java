package com.maxim.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CloneUtil {

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(T obj) throws Exception {
		// serialize
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(bout);
		objOut.writeObject(obj);

		// de-serialize
		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		ObjectInputStream objIn = new ObjectInputStream(bin);
		return (T) objIn.readObject();
	}

}
