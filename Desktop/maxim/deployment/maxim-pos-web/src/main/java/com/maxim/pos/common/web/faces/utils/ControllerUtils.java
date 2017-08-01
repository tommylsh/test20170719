package com.maxim.pos.common.web.faces.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maxim.entity.AbstractEntity;

public class ControllerUtils {

	public static List<Long> getSelectedIds(Map<Long, Boolean> selections) {
		List<Long> ids = new ArrayList<Long>();
		for (Entry<Long, Boolean> entry : selections.entrySet()) {
			if (entry.getValue()) {
				ids.add(entry.getKey());
			}
		}
		return ids;
	}

	public static List<Long> retrieveIdsFromEntities(Collection<? extends AbstractEntity> entities) {
		List<Long> ids = new ArrayList<Long>();
		for (AbstractEntity entity : entities) {
			ids.add(entity.getId());
		}

		return ids;
	}

}
