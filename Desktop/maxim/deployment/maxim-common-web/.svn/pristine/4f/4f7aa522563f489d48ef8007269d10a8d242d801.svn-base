package com.maxim.ws;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//public class AbstractDatatableWS {
//
//	protected List<Attribute> convertSorts(AbstractDatatableQuery query) {
//
//		List<Attribute> orders = new ArrayList<Attribute>();
//
//		// Sorting
//		Integer sortCols = query.getiSortingCols();
//		if (sortCols != null && sortCols > 0) {
//			List<Integer> sortColIdxs = query.getiSortCol();
//			List<String> sortDirs = query.getsSortDir();
//			List<String> dataCols = query.getmDataProp();
//			for (int i = 0; i < sortCols; i++) {
//				String field = dataCols.get(sortColIdxs.get(i));
//				boolean isDesc = sortDirs.get(i).equals("asc") ? false : true;
//				// skip derived fields
//				orders.add(new Attribute(field, isDesc));
//			}
//		}
//		return orders;
//	}
//
//	protected Map<String, Object> convertFilters(AbstractDatatableQuery query) {
//		Map<String, Object> filters = new HashMap<String, Object>();
//		List<String> filterStrs = query.getsSearch();
//
//		for (mo.gov.datatable.DatatableFilterMapping filterMapping : query
//				.getFilterMappings()) {
//			String filterStr = filterStrs.get(filterMapping.getIndex());
//
//			// no value
//			if (filterStr == null || filterStr.isEmpty()) {
//				continue;
//			}
//
//			if (filterMapping.isMultiValue()) {
//				FilterValueParser<?> parser = filterMapping.getParser();
//				List<Object> valueList = new ArrayList<Object>();
//				for (String value : filterStr.split(",")) {
//					valueList.add(parser.parse(value));
//				}
//				filters.put(filterMapping.getFilterName(), valueList);
//			} else {
//				filters.put(filterMapping.getFilterName(), filterMapping
//						.getParser().parse(filterStr));
//			}
//		}
//
//		return filters;
//	}
//
//}
