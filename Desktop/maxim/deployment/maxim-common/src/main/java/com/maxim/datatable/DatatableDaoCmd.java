package com.maxim.datatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.maxim.dao.DaoCmd;
import com.maxim.util.BeanUtil;

public class DatatableDaoCmd extends DaoCmd {

    private AbstractDatatableDTO datatableDto;

    public DatatableDaoCmd(String queryKey, AbstractDatatableDTO dto) {
        super(queryKey, dtoToMap(dto));
        setOrderString(getOrderString(dto));
        this.datatableDto = dto;
    }

    public AbstractDatatableDTO getDatatableDto() {
        return datatableDto;
    }

    public void setDatatableDto(AbstractDatatableDTO datatableDto) {
        this.datatableDto = datatableDto;
    }

    private static Map<String, Object> dtoToMap(AbstractDatatableDTO dto) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.putAll(BeanUtil.transBeanToMap(dto));

        List<Columns> columns = dto.getColumns();
        if (columns != null && !columns.isEmpty()) {
            for (Columns cls : columns) {
                if (cls.isSerachable()) {
                    com.maxim.datatable.Columns.Search search = cls.getSearch();
                    if (search != null && StringUtils.isNotBlank(search.getValue())) {
                        map.put(cls.getData(), search.getValue());
                    } else {
                        map.put(cls.getData(), dto.getSearch().getValue());
                    }
                }
            }
        }
        return map;
    }

    private static String getOrderString(AbstractDatatableDTO dto) {
        if (dto.getOrder() != null && !dto.getOrder().isEmpty()) {
            List<String> orderList = new ArrayList<String>();
            List<Order> orders = dto.getOrder();
            for (Order order : orders) {
                if (order.getColumn() != null) {
                    orderList.add(dto.getColumns().get(order.getColumn()).getData() + " " + order.getDir());
                }
            }
            return StringUtils.join(orderList, ",");
        }
        return null;
    }
}
