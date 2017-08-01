package com.maxim.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SysParamsInit implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysParamsInit.class);

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.debug("Start init system parameter");
//        init();
        LOGGER.debug("End init system parameter");
    }

    public void init() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT CODE,CODE_VALUE FROM CG_APPLICATION_SETTING";
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            boolean next = rs.next();
            if (next) {
                Map<String, String> params = new HashMap<String, String>();
                while (next) {
                    String key = rs.getString("PARAM_NAME");
                    String value = rs.getString("PARAM_VALUE");
                    params.put(key, value == null ? "" : value);
                    next = rs.next();
                }
                SysParamsConstant.setParams(params);
            } else {
                LOGGER.warn("System parameter is empty.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("init system parameter error!",e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) {
                // ignored.
            }
        }
    }

}
