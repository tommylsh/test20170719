package com.maxim.pos.common.logger;

import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.helpers.LogLog;

public class BranchDailyRollingFileAppender extends DailyRollingFileAppender {
	  public
	  synchronized
	  void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
	                                                            throws IOException {
	    LogLog.debug("setFile called: "+fileName+", "+append);
	    
	    
	    super.setFile(fileName, append, bufferedIO, bufferSize);
	    
	    
	  }

}
