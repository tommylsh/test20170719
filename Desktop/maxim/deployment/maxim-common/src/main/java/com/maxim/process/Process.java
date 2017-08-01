package com.maxim.process;

import com.maxim.data.DTO;
import com.maxim.data.Query;

/**
 * Interface for business process 
 * @author Steven
 *
 */
public interface Process {

	public DTO process(Query query);
	
}
