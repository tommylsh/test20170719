package com.maxim.pos.security.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.IndexColumn;

import com.maxim.pos.security.enumeration.ResourceType;

@Entity
@Table(name = "SECURITY_FOLDER")
public class Folder extends Resource {

    private static final long serialVersionUID = -1514171977484704882L;

    private List<Link> links = new ArrayList<Link>();

    public Folder() {
    }

    public Folder(SystemModule systemModule) {
        setSystemModule(systemModule);
        setType(ResourceType.FOLDER);
    }

    public void resetLinkIndices() {
        int index = 0;
        for (Link link : links) {
            link.setIndex(index);

            index += 1;
        }
    }

    @Override
    @Id
    @Column(name = "SECURITY_FOLDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "folder", orphanRemoval = true)
    @IndexColumn(name = "INDEX1")
    @Fetch(value = FetchMode.SUBSELECT)
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

}
