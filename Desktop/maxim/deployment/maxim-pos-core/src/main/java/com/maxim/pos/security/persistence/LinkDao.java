package com.maxim.pos.security.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Link;
import com.maxim.util.BeanUtil;

@Repository("linkDao")
public class LinkDao extends HibernateDAO {

    public static final String HQL_findLinkByUrl = "findLinkByUrl";
    public static final String HQL_findLinksByFolderId = "findLinksByFolderId";
    public static final String HQL_findLinksBySystemAlias = "findLinksBySystemAlias";
    public static final String HQL_getLinkCountByUserIdAndUrl = "getLinkCountByUserIdAndUrl";

    public Link findLinkByUrl(String url) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("url", url);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findLinkByUrl, paramMap);

        return getSingle(cmd, Link.class);
    }

    public List<Link> findLinksByFolderId(Long folderId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("folderId", folderId);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findLinksByFolderId, paramMap);
        return getList(cmd, Link.class);
    }

    public List<Link> findLinksBySystemAlias(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findLinksBySystemAlias, paramMap);
        return getList(cmd, Link.class);
    }

    public Long getLinkCountByUserIdAndUrl(String userId, String url) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        paramMap.put("url", url);
        PosDaoCmd cmd = new PosDaoCmd(HQL_getLinkCountByUserIdAndUrl, paramMap);

        return getSingle(cmd, Long.class);
    }

}
