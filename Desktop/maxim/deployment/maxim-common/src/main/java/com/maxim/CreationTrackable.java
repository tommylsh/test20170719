/*
 * Created on Jun 17, 2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maxim;

import java.util.Date;

/**
 * Interface for tracking object creation
 */
public interface CreationTrackable {

	public String getCreatedBy();
	
	public Date getCreationTime();
}
