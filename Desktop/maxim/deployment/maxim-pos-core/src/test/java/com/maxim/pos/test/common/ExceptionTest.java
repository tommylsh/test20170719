package com.maxim.pos.test.common;

import org.junit.Test;

import com.maxim.pos.common.exception.DuplicatedBranchCodeException;
import com.maxim.pos.common.exception.ValidBranchCodeException;

public class ExceptionTest extends BaseTest {

    @Test
    public void test1() {
        DuplicatedBranchCodeException posException = new DuplicatedBranchCodeException(messageSource);
        logger.info("Message Source-> {}: {}", "DuplicatedBranchCodeException", posException.getMessage());
    }

    @Test
    public void test2() {

        ValidBranchCodeException posException = new ValidBranchCodeException(messageSource, new Object[] { "%$&*" });
        logger.info("Message Source-> {}: {}", "ValidBranchCodeException", posException.getMessage());
    }

}
