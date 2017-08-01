package com.maxim.pos.security.value;

import java.util.Collection;

import com.maxim.pos.security.entity.Folder;
import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.entity.Permission;

public class UpdateRoleDTO {

    private Collection<Folder> folders;
    private Collection<Link> links;
    private Collection<Permission> permissions;

    public UpdateRoleDTO() {
    }

    public UpdateRoleDTO(Collection<Folder> folders, Collection<Link> links, Collection<Permission> permissions) {
        this.folders = folders;
        this.links = links;
        this.permissions = permissions;
    }

    public Collection<Folder> getFolders() {
        return folders;
    }

    public void setFolders(Collection<Folder> folders) {
        this.folders = folders;
    }

    public Collection<Link> getLinks() {
        return links;
    }

    public void setLinks(Collection<Link> links) {
        this.links = links;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

}
