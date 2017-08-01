package com.maxim.pos.common.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.util.JsonUtils;

/**
 * Created by Lotic on 2017-05-27.
 */
@Service
public class ChineseConverionServiceImpl implements ChineseConverionService {
    private static final Logger logger = LoggerFactory.getLogger(ChineseConverionServiceImpl.class);
    
    @Value(value = "classpath:chinese_conversion_table.json")
    private Resource defaultConfigrationFile;

    @Value("${system.conversion.configrationFile}")
	protected String localConfigrationFileName ;

    @SuppressWarnings("unchecked")
	@PostConstruct
    public void init() {
        try {
        	
            InputStream in = null;

            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource localResource = loader.getResource(localConfigrationFileName);
            if (!localResource.exists())
            {
            	in = defaultConfigrationFile.getInputStream();
            }
            else
            {
            	LogUtils.printLog("localConfigrationFileName :"+localResource.getDescription());            
            	in = localResource.getInputStream();
            }
            Reader reader = new InputStreamReader(in);
            
			Map<String, Object> map			= JsonUtils.fromJson(reader, Map.class);
        	JDBCUtils.CONV_CHI_TABLE_MAP	= (Map<String, List<String>>) map.get("TABLE_LIST");            		
        	JDBCUtils.CONV_CHI_BRANCH_LIST	= (List<String>) map.get("BRANCH_LIST");
        	reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.printException(logger, "convGBTable exception {}", e.getMessage());
        }
    }

}
