package com.maxim.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.maxim.data.DTO;

/**
 * Function DTO
 * 
 * @author SPISTEV
 * 
 */
public class Function implements DTO {

	/**
		 * 
		 */
	private static final long serialVersionUID = 20130825L;

	/** Function name */
	private String func;

	/** City list */
	private Collection<String> subFuncs;

	public Function() {
		super();
	}

	public Function(String func, Collection<String> subFuncs) {
		super();
		this.func = func;

		if (subFuncs == null)
			this.subFuncs = Collections.emptySet();
		else
			this.subFuncs = Collections.unmodifiableCollection(subFuncs);
	}

	public String getFunc() {
		return func;
	}

	public Collection<String> getSubFuncs() {
		return subFuncs;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public void setSubFuncs(Set<String> subFuncs) {
		this.subFuncs = subFuncs;
	}

}
