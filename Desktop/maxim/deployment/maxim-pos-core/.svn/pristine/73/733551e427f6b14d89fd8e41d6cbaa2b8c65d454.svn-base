package com.maxim.pos.test.security.service;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.util.EncryptPasswordUtils;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.entity.User;
import com.maxim.pos.security.value.UserQueryCriteria;
import com.maxim.pos.test.common.BaseTest;
import com.maxim.util.DateUtil;

public class UserServiceTest extends BaseTest {

    @Transactional
    @Test
    public void test1() {
        User user = new User();
        String userId = "user" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");

        user.setUserId(userId);
        user.setUserName(userId);
        String password = EncryptPasswordUtils.customizeMd5Encrypt("admin");
        user.setPassword(password);
        Auditer.audit(user, null);

        user = userService.saveUser(user);
        Assert.assertTrue(String.format("the created user's userId[%s] should be equal to the variable userId[%s]",
                user.getUserId(), userId), userId.equals(user.getUserId()));

        user = userService.findUserByUserId(userId);
        Assert.assertNotNull(user);
        Assert.assertTrue(userId.equals(user.getUserName()));
        Assert.assertTrue(password.equals(user.getPassword()));
        Assert.assertTrue(Auditer.SYSTEM_RESERVED_USER_ID.equals(user.getCreateUser()));
        Assert.assertNotNull(user.getCreateTime());
        Assert.assertTrue(Auditer.SYSTEM_RESERVED_USER_ID.equals(user.getLastUpdateUser()));
        Assert.assertNotNull(user.getLastUpdateTime());
    }

    @Transactional
    @Test
    public void test2() {
        User user = new User();
        String userId = "user" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        String password = EncryptPasswordUtils.customizeMd5Encrypt("admin");

        user.setUserId(userId);
        user.setUserName(userId);
        user.setPassword(password);
        Auditer.audit(user, null);

        List<Role> roles = roleService.findRolesByDefaultSystemAlias();

        user = userService.updateUserWithRoles(user, roles);

        Assert.assertNotNull(user);
        Assert.assertTrue(userId.equals(user.getUserName()));
        Assert.assertTrue(password.equals(user.getPassword()));
        Assert.assertTrue(Auditer.SYSTEM_RESERVED_USER_ID.equals(user.getCreateUser()));
        Assert.assertNotNull(user.getCreateTime());
        Assert.assertTrue(Auditer.SYSTEM_RESERVED_USER_ID.equals(user.getLastUpdateUser()));
        Assert.assertNotNull(user.getLastUpdateTime());

        Assert.assertTrue(user.getRoles().size() == roles.size());
    }

    @Transactional
    @Test
    public void test3() {
        int maxResult = 5;

        for (int i = 0; i < maxResult; i++) {
            User user = new User();
            String userId = "dummyuser" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");

            logger.info("userId : {}", userId);

            user.setUserId(userId);
            user.setUserName(userId);
            user.setPassword(EncryptPasswordUtils.customizeMd5Encrypt("admin"));
            Auditer.audit(user, null);
            user = userService.saveUser(user);
        }

        UserQueryCriteria criteria = new UserQueryCriteria();
        criteria.setUserIdKeyword("dummyuser");
        // criteria.setUserNameKeyword("8");

        criteria.setMaxResult(maxResult);
        List<User> users = userService.findUserByCriteria(criteria);
        logger.info("user size : {}", users.size());

        Assert.assertTrue(users.size() == maxResult);

        Long count = userService.getUserCountByCriteria(criteria);
        logger.info("count : {}", count);

        Assert.assertTrue(count.intValue() == maxResult);
    }

}
