package com.maxim.pos.security.service;

import java.util.List;

import com.maxim.pos.security.entity.Link;

public interface LinkService {

    public Link findLinkByUrl(String url);
    
    public Long getLinkCountByUserIdAndUrl(String userId, String url);

    public List<Link> findLinksByFolderId(Long folderId);

    public Link saveLink(Link link);

    public void deleteLink(Long id);

}
