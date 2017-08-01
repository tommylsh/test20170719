package com.maxim.pos.security.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Folder;
import com.maxim.util.BeanUtil;

@Repository("folderDao")
public class FolderDao extends HibernateDAO {

    public static final String SYSTEM_ALIAS = "systemAlias";
    public static final String HQL_findFoldersBySystemAlias = "findFoldersBySystemAlias";
    public static final String HQL_findFolderDetailById = "findFolderDetailById";

    public List<Folder> findFoldersBySystemAlias(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findFoldersBySystemAlias, paramMap);
        return getList(cmd, Folder.class);
    }
    
    public Folder findFolderDetailById(Long id) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", id);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findFolderDetailById, paramMap);
        return getSingle(cmd, Folder.class);
    }
    

}
