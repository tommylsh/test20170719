package com.maxim.pos.security.service;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.util.EncryptPasswordUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.entity.User;
import com.maxim.pos.security.persistence.UserDao;
import com.maxim.pos.security.value.UserQueryCriteria;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsers() {
        return userDao.getAllList(User.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUserByCriteria(CommonCriteria criteria) {
        List<User> users = userDao.findUserByCriteria(criteria);

        UserQueryCriteria userQueryCriteria = (UserQueryCriteria) criteria;
        logger.info("user join roles: {}", userQueryCriteria.isJoinRoles());

        if (userQueryCriteria.isJoinRoles()) {
            for (User user : users) {
                Hibernate.initialize(user.getRoles());
            }
        }

        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserCountByCriteria(CommonCriteria criteria) {
        return userDao.getUserCountByCriteria(criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUserId(String userId) {
        return userDao.findByUserId(userId);
    }

    @Override
    public User findUserDetailByUserId(String userId) {
        return userDao.findUserDetailByUserId(userId);
    }

    @Override
    public User findUserById(Long id) {
        return userDao.getSingle(User.class, id);
    }

    @Override
    public User saveUser(User user) {
        if (StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(EncryptPasswordUtils.customizeMd5Encrypt(user.getUserId()));
        }
        return (User) userDao.save(user);
    }

    @Override
    public User updateUserWithRoles(User user, Collection<Role> roles) {
        user.getRoles().clear();
        user.getRoles().addAll(roles);
        return saveUser(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userDao.delete(userDao.getSingle(User.class, id));
    }

}
