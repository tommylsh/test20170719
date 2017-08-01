package com.maxim.pos.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.data.DTO;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.persistence.SchemeTableColumnDao;
import com.maxim.pos.common.value.CommonCriteria;

@Transactional
@Service(SchemeTableColumnService.BEAN_NAME)
public class SchemeTableColumnServiceImpl implements SchemeTableColumnService{

	@Autowired
	private SchemeTableColumnDao schemeTableColumnDao;
	
	@Transactional(readOnly = true)
	@Override
	public SchemeTableColumn getSchemeTableColumnByCriteria(CommonCriteria criteria) {
		return null;
	}
	
	@Override
	@Transactional
	public SchemeTableColumn saveSchemeTableColumn(SchemeTableColumn schemeTableColumn) {
		schemeTableColumnDao.save(schemeTableColumn);
		return null;
	}
	
	@Override
	@Transactional
	public DTO saveSchemeTableColumns(List<SchemeTableColumn> schemeTableColumns) {
		
		long id = schemeTableColumns.iterator().next().getSchemeInfo().getId();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("schemeInfoId",id);
		paramMap.put("queryRecord",true);

		List<SchemeTableColumn> oldSchemeTableColumnList = schemeTableColumnDao.findSchemeTableColumnByCriteria(paramMap);
    	System.out.println("Scheme Table Column List Size = " + oldSchemeTableColumnList.size());
    	
		for (SchemeTableColumn col : oldSchemeTableColumnList)
		{
			schemeTableColumnDao.delete(col);
		}

		for(SchemeTableColumn schemeTableColumn: schemeTableColumns){
			schemeTableColumnDao.save(schemeTableColumn);
		}
		return null;
	}

}
