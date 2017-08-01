package com.maxim.pos.security.value;

import java.util.ArrayList;
import java.util.List;

import com.maxim.data.DTO;

public class FolderDTO implements DTO {

    private static final long serialVersionUID = -3859340952668869385L;

    private Long id;
    private String name;
    private boolean enabled;
    private List<LinkDTO> linkDtos = new ArrayList<LinkDTO>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<LinkDTO> getLinkDtos() {
        return linkDtos;
    }

    public void setLinkDtos(List<LinkDTO> linkDtos) {
        this.linkDtos = linkDtos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FolderDTO other = (FolderDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
