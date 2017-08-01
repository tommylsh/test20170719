package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.value.CommonCriteria;

import java.util.List;

public interface PollSchemeTableColumnService {

    String BEAN_NAME = "pollSchemeTableColumnService";

    void save(SchemeTableColumn schemeTableColumn);

    void delete(Long schemeTableColumnId);

    List<SchemeTableColumn> findSchemeTableColumnByCriteria(CommonCriteria criteria);

    Long getSchemeTableColumnCountByCriteria(CommonCriteria criteria);

}
