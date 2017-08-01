package com.maxim.pos.security.service;

import java.util.Collection;
import java.util.List;

import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.entity.User;

public interface UserService {
    
    public List<User> findUsers();
    
    public User findUserByUserId(String userId);
    
    public User findUserDetailByUserId(String userId);
    
    public User findUserById(Long id);

    public List<User> findUserByCriteria(CommonCriteria criteria);
    
    public Long getUserCountByCriteria(CommonCriteria criteria);
    
    public User saveUser(User user);
    
    public User updateUserWithRoles(User user, Collection<Role> roles);
    
    public void deleteUserById(Long id);
    
}
