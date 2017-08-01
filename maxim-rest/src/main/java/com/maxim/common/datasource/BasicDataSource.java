package com.maxim.common.datasource;

import org.springframework.beans.factory.annotation.Autowired;

public class BasicDataSource extends org.apache.commons.dbcp2.BasicDataSource {

//    private static final long serialVersionUID = 20160117L;
//    
//    private static final String PASSPHRASE = "90206f7a4fc149b592a14b7629caad5e";
	
    @Autowired
	private SecurityConfig securityConfig ;

    /**
     * @param password encrypt password
     */
    @Override
    public void setPassword(String password) {
        try {
            super.setPassword(securityConfig.decrypt(password));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setUsername(String username) {
        try {
            super.setUsername(securityConfig.decrypt(username));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
