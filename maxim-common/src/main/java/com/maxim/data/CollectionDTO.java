package com.maxim.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO containing a collection of DTOs
 * 
 * @author CPPPAA
 * 
 */
@XmlRootElement
public class CollectionDTO implements ICollectionDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130626L;

	public CollectionDTO() {}
	
	public CollectionDTO(Collection<? extends DTO> dtos) {
		this.dtos = dtos;
	}
	
	public CollectionDTO(List<Map<String,Object>> map){
	    this.map = map;
	}
	private Collection<? extends DTO> dtos;
	
	private  List<Map<String,Object>> map;
	
    public List<Map<String, Object>> getMap() {
        return map;
    }

    public void setMap(List<Map<String, Object>> map) {
        this.map = map;
    }

    public Collection<? extends DTO> getDtos() {
		return dtos;
	}

	public void setDtos(Collection<? extends DTO> dtos) {
		this.dtos = dtos;
	}	
}
