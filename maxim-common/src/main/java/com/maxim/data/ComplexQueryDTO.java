package com.maxim.data;

import java.util.Collection;

public class ComplexQueryDTO extends CollectionDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 20130923L;

	private Integer filteredRecordCount;

	private Integer totalRecordCount;

	public ComplexQueryDTO(Collection<? extends DTO> dtos,
			Integer filteredRecordCount, Integer totalRecordCount) {
		super();
		setDtos(dtos);
		this.filteredRecordCount = filteredRecordCount;
		this.totalRecordCount = totalRecordCount;
	}

	public Integer getFilteredRecordCount() {
		return filteredRecordCount;
	}

	public void setFilteredRecordCount(Integer filteredRecordCount) {
		this.filteredRecordCount = filteredRecordCount;
	}

	public Integer getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(Integer totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}

}