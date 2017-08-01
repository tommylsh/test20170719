package com.maxim.web.faces.scope;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import com.maxim.util.LoggerHelper;
import com.maxim.web.faces.utils.FacesUtils;

public class ViewScope implements Scope {

    public static final Logger logger = LoggerFactory.getLogger(ViewScope.class);

    @SuppressWarnings("rawtypes")
    public Object get(String name, ObjectFactory objectFactory) {

        Map<String, Object> viewMap = FacesUtils.getInstance().getViewRoot().getViewMap();

        // LoggerHelper.logInfo(logger, "ViewScope getting view with name: %s",
        // name);

        if (viewMap.containsKey(name)) {
            Object object = viewMap.get(name);

            // LoggerHelper.logInfo(logger, "ViewMap contains the object[%s],
            // then getting it from viewMap.get(name)", object);

            return object;
        } else {
            Object webFacesController = objectFactory.getObject();

            // LoggerHelper.logInfo(logger,
            // "ViewMap does not contain the object[%s], then getting it from
            // objectFactory.getObject()",
            // webFacesController);

            viewMap.put(name, webFacesController);
            return webFacesController;
        }
    }

    public Object remove(String name) {
        LoggerHelper.logInfo(logger, "ViewScope removing view with name: %s", name);
        return FacesUtils.getInstance().getViewRoot().getViewMap().remove(name);
    }

    public String getConversationId() {
        return null;
    }

    public void registerDestructionCallback(String name, Runnable callback) {

    }

    public Object resolveContextualObject(String key) {
        return null;
    }
}
