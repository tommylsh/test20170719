package com.maxim.web.faces.converter;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

import com.maxim.entity.AbstractEntity;

public class PrimefacesEntityConverter implements Converter, Serializable {

    private static final long serialVersionUID = -6348564494282388827L;

    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
        if (value == null || value.trim().equals("")) {
            return null;
        }
        Long id = Long.parseLong(value);
        Class<AbstractEntity> entityClass = null;

        if (component instanceof UISelectMany) {
            UISelectMany uiSelectMany = (UISelectMany) component;

            for (UIComponent item : uiSelectMany.getChildren()) {
                for (Object entity : (List) ((UISelectItems) item).getValue()) {
                    if (entity instanceof AbstractEntity) {
                        if (((AbstractEntity) entity).getId().equals(id)) {
                            return entity;
                        }
                    }
                }
            }
        } else if (component instanceof PickList) {
            Object dualList = ((PickList) component).getValue();
            DualListModel<AbstractEntity> listModel = (DualListModel<AbstractEntity>) dualList;
            for (Object obj : listModel.getSource()) {
                if (((AbstractEntity) obj).getId().equals(id)) {
                    return obj;
                }
            }
            for (Object obj : listModel.getTarget()) {
                if (((AbstractEntity) obj).getId().equals(id)) {
                    return obj;
                }
            }
        } else {
            entityClass = (Class<AbstractEntity>) component.getValueExpression("value").getType(context.getELContext());
            return entityManager.find(entityClass, id);
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object obj) throws ConverterException {
        if (obj == null) {
            return "";
        } else {
            if (obj instanceof AbstractEntity) {
                return ((AbstractEntity) obj).getId().toString();
            } else {
                return "";
            }
        }
    }
}
