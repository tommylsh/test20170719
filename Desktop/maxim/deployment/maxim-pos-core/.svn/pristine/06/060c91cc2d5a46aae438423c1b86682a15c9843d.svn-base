package com.maxim.pos.test.common.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.value.ApplicationSettingQueryCriteria;
import com.maxim.pos.test.common.BaseTest;

public class ApplicationSettingServiceTest extends BaseTest {

    @Transactional
    @Test
    public void test1() {
        String code = "DUMMY_CODE";
        String codeValue = "DUMMY_VALUE";
        String codeDescription = "DUMY_DESC";
        
        ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode(code);

        Assert.assertTrue((applicationSetting == null));

        applicationSetting = new ApplicationSetting();
        applicationSetting.setCode(code);
        applicationSetting.setCodeValue(codeValue);
        applicationSetting.setCodeDescription(codeDescription);
        Auditer.audit(applicationSetting);

        applicationSetting = applicationSettingService.saveApplicationSetting(applicationSetting);

        Assert.assertTrue((applicationSetting != null));
        Assert.assertTrue(code.equals(applicationSetting.getCode()));
        Assert.assertTrue(codeValue.equals(applicationSetting.getCodeValue()));
        Assert.assertTrue(codeDescription.equals(applicationSetting.getCodeDescription()));
    }

    @Transactional
    @Test
    public void test2() {
        ApplicationSetting applicationSetting1 = new ApplicationSetting();
        applicationSetting1.setCode("DUMMY_SETTING_CODE_01");
        applicationSetting1.setCodeValue("DUMMY_SETTING_CODE_01");
        applicationSetting1.setCodeDescription("DUMMY_SETTING_CODE_01");
        Auditer.audit(applicationSetting1);
        applicationSettingService.saveApplicationSetting(applicationSetting1);

        ApplicationSetting applicationSetting2 = new ApplicationSetting();
        applicationSetting2.setCode("DUMMY_SETTING_CODE_02");
        applicationSetting2.setCodeValue("DUMMY_SETTING_CODE_02");
        applicationSetting2.setCodeDescription("DUMMY_SETTING_CODE_02");
        Auditer.audit(applicationSetting2);
        applicationSettingService.saveApplicationSetting(applicationSetting2);

        ApplicationSettingQueryCriteria criteria = new ApplicationSettingQueryCriteria();
        criteria.setMaxResult(10);
        criteria.setCodeKeyword("DUMMY_SETTING");
        List<ApplicationSetting> settings = applicationSettingService.findApplicationSettingByCriteria(criteria);

        logger.info("settings.size: {}", settings.size());

        Assert.assertTrue(settings.size() == 2);

        criteria.setCodeKeyword("CODE_01");
        List<ApplicationSetting> settings2 = applicationSettingService.findApplicationSettingByCriteria(criteria);

        logger.info("settings.size: {}", settings2.size());

        Assert.assertTrue(settings2.size() == 1);

        Long count = applicationSettingService.getApplicationSettingCountByCriteria(criteria);

        logger.info("count: {}", count);

        Assert.assertTrue(settings2.size() == count.intValue());

    }

    @Transactional
    @Test
    public void test3() {
        ApplicationSetting applicationSetting = new ApplicationSetting();
        applicationSetting.setCode("DUMMY_CODE");
        applicationSetting.setCodeValue("DUMMY_VALUE");
        applicationSetting.setCodeDescription("DUMMY_VALUE");
        Auditer.audit(applicationSetting);
        applicationSettingService.saveApplicationSetting(applicationSetting);

        applicationSetting = applicationSettingService.findApplicationSettingByCode("DUMMY_CODE");
        Assert.assertTrue((applicationSetting != null));

    }

}
