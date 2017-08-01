package com.maxim.data;

import com.maxim.util.CloneUtil;


public abstract class CloneableDTO implements DTO, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 201404023L;

	public Object clone() throws CloneNotSupportedException {
		try {
			return CloneUtil.clone(this);
		} catch (Exception e) {
			throw new CloneNotSupportedException();
		}
	}
}
