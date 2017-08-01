package com.maxim.pos.common.data;

import com.maxim.data.DTO;
import com.maxim.exception.BaseException;

public class ResultDTO implements DTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 20170302L;
	
	private String process;
	private String returnMsg;
	
	private int recordProcessed;
	private int recordInsert;
	private int recordUpdate;
	private int recordDelete;
	
	private BaseException exception;
	
	
	ResultDTO(String process, String returnMsg, BaseException e){
		this.process = process;
		this.returnMsg = returnMsg;
		this.exception = e;
	}


	public String getProcess() {
		return process;
	}


	public void setProcess(String process) {
		this.process = process;
	}


	public String getReturnMsg() {
		return returnMsg;
	}


	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}


	public int getRecordProcessed() {
		return recordProcessed;
	}


	public void setRecordProcessed(int recordProcessed) {
		this.recordProcessed = recordProcessed;
	}


	public int getRecordInsert() {
		return recordInsert;
	}


	public void setRecordInsert(int recordInsert) {
		this.recordInsert = recordInsert;
	}


	public int getRecordUpdate() {
		return recordUpdate;
	}


	public void setRecordUpdate(int recordUpdate) {
		this.recordUpdate = recordUpdate;
	}


	public int getRecordDelete() {
		return recordDelete;
	}


	public void setRecordDelete(int recordDelete) {
		this.recordDelete = recordDelete;
	}


	public BaseException getException() {
		return exception;
	}


	public void setException(BaseException exception) {
		this.exception = exception;
	}
	
	

	
}
