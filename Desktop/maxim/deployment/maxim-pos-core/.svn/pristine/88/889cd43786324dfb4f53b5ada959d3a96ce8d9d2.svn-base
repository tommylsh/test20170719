package com.maxim.pos.test.sales.persistence;

import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.persistence.PollSchemeInfoDao;
import com.maxim.pos.test.common.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollSchemeInfoDaoTest extends BaseTest {

    @Autowired
    private PollSchemeInfoDao pollSchemeInfoDao;

    @Test
    @Transactional
    public void test() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pollSchemeType", PollSchemeType.SALES_REALTIME);
        paramMap.put("clientType", ClientType.ORACLE);
        paramMap.put("destination", "ORDERS");
        List<SchemeInfo> schemeInfoList = pollSchemeInfoDao.findSchemeInfo(paramMap);
        Assert.isTrue(schemeInfoList.size() == 1);
        List<SchemeTableColumn> tableColumns = schemeInfoList.get(0).getSchemeTableColumns();
        for (SchemeTableColumn tableColumn : tableColumns) {
            System.out.println(tableColumn.getFromColumn() + " >> " + tableColumn.getToColumn());
        }
    }

}
