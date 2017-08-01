package com.maxim.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import com.maxim.data.CollectionDTO;
import com.maxim.data.DTO;

/**
 * Transform DTO/Entity to DTO
 * 
 * @author SPISTEV
 */
public class EntityTransform {

	private static ModelMapper mapper;

	static {
		mapper = new ModelMapper();
	}

	public static <T> T transform(Serializable entity,
			Class<T> destinationType) {
		return mapper.map(entity, destinationType);
	}

	public static CollectionDTO transform(
			Collection<? extends Serializable> collection,
			Class<? extends DTO> tgtClass) {
		List<DTO> list = new ArrayList<DTO>();

		for (Iterator<? extends Serializable> itr = collection.iterator(); itr
				.hasNext();) {
			list.add(mapper.map(itr.next(), tgtClass));
		}

		return new CollectionDTO(list);
	}
	
	
	public static CollectionDTO transform(
            Collection<? extends Serializable> collection,
            Class<? extends DTO> tgtClass, PropertyMap propertyMap) {
        List<DTO> list = new ArrayList<DTO>();
        mapper.addMappings(propertyMap);
        
        for (Iterator<? extends Serializable> itr = collection.iterator(); itr
                .hasNext();) {
            list.add(mapper.map(itr.next(), tgtClass));
        }

        return new CollectionDTO(list);
    }
	
	public static <T> T transform(DTO dto,
			Class<T> tgtClass) {
		return mapper.map(dto, tgtClass);
	}
}
