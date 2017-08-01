package com.maxim.pos.common.service;

import java.util.List;

import com.maxim.data.DTO;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.value.CommonCriteria;

public interface SchemeTableColumnService {
	
	public static final String BEAN_NAME = "schemeTableColumnService";

	public SchemeTableColumn getSchemeTableColumnByCriteria(CommonCriteria criteria);
	
	public SchemeTableColumn saveSchemeTableColumn(SchemeTableColumn schemeTableColumn);

	public DTO saveSchemeTableColumns(List<SchemeTableColumn> schemeTableColumns);

}
