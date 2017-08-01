package com.maxim.core.service;

import com.maxim.api.model.Trans;
import com.maxim.api.service.TransService;
import com.maxim.common.exception.DatabaseException;
import com.maxim.common.exception.ValidationException;
import com.maxim.common.util.ValidationUtils;
import com.maxim.common.validation.Group;
import com.maxim.core.mapper.ext.TransExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by Lotic on 2017-04-27.
 */
@Transactional
@Service("transService")
public class TransServiceImpl implements TransService{
    private static final  Logger LOGGER = LoggerFactory.getLogger(TransServiceImpl.class);

    @Resource(name = "transExtMapper")
    private TransExtMapper transExtMapper;
    @Override
    public int add(Trans trans) {
        return 0;
    }


    public int add(List<Trans> transransList) {
        if (transransList == null || transransList.isEmpty()) {
            throw new ValidationException("[Validation failed] - this argument [transransList] is required; it must not be null");
        }
        List<String> violations = ValidationUtils.validateCollection(transransList, Group.Add.class);
        if (!violations.isEmpty()) {
            throw new ValidationException("[Validation failed]:" + violations);
        }
        try {
//            transExtMapper.deleteBatch(transransList);
            return transExtMapper.insertBatch(transransList);
        } catch (Exception e) {
            LOGGER.error("Batch Insert Or Delete Error", e);
            throw new DatabaseException(e.getMessage());
        }
    }


}
