package com.maxim.pos.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.persistence.LinkDao;

@Service("linkService")
@Transactional
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkDao linkDao;
    
    @Override
    @Transactional(readOnly = true)
    public Link findLinkByUrl(String url) {
        return linkDao.findLinkByUrl(url);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Link> findLinksByFolderId(Long folderId) {
        return linkDao.findLinksByFolderId(folderId);
    }

    @Override
    public Link saveLink(Link link) {
        return (Link) linkDao.save(link);
    }

    @Override
    public void deleteLink(Long id) {
        linkDao.delete(linkDao.getSingle(Link.class, id));        
    }

    @Override
    @Transactional(readOnly = true)
    public Long getLinkCountByUserIdAndUrl(String userId, String url) {
        return linkDao.getLinkCountByUserIdAndUrl(userId, url);
    }

}
