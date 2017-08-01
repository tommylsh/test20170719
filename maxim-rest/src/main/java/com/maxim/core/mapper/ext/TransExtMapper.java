package com.maxim.core.mapper.ext;

import com.maxim.api.model.Trans;

import java.util.List;

/**
 * Created by Lotic on 2017-05-02.
 */
public interface TransExtMapper {
    int insertBatch(List<Trans> records);

    int deleteBatch(List<Trans> records);
}
