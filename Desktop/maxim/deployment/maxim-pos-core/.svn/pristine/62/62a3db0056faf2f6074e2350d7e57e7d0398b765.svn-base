package com.maxim.pos.security.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.User;
import com.maxim.util.BeanUtil;

@Repository("userDao")
public class UserDao extends HibernateDAO {

    public static final String HQL_findUserByUserId = "findUserByUserId";
    public static final String HQL_findUserDetailByUserId = "findUserDetailByUserId";
    public static final String HQL_findUsersByCriteria = "findUsersByCriteria";

    public User findByUserId(String userId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findUserByUserId, paramMap);

        return getSingle(cmd, User.class);
    }

    @SuppressWarnings("unchecked")
    public List<User> findUserByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findUsersByCriteria, paramMap);

        return (List<User>) getPaginatedListByCriteriaAndType(cmd, paramMap, User.class);
    }

    public Long getUserCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findUsersByCriteria, paramMap);

        return getSingle(cmd, Long.class);
    }

    public User findUserDetailByUserId(String userId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findUserDetailByUserId, paramMap);

        return getSingle(cmd, User.class);
    }
    
}
