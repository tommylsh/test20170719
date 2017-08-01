package com.maxim.pos.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.maxim.util.EncryptionUtil;

@Component("securityConfig")
public class SecurityConfig {

	public static final String AES_PREFIX = "{AES}";


	protected @Value("${security.encryptUsername}") boolean encryptUsername ;
    protected @Value("${security.encryptPassword}") boolean encryptPassword ;
    protected @Value("${aesKey}") String aseKey ;

    public String aesEncrypt(String content) throws Exception {
    	if (!content.startsWith(AES_PREFIX))
    		return AES_PREFIX + EncryptionUtil.base64Encode(EncryptionUtil.aesEncryptToBytes(content, aseKey));
    	return content ;
    }
    
    public String decrypt(String content) throws Exception {
    	if (content.startsWith(AES_PREFIX))
    		return EncryptionUtil.aesDecrypt(content.substring(AES_PREFIX.length()), aseKey);
    	return content;
    }

    public boolean isEncryptUsername() {
		return encryptUsername;
	}
	public boolean isEncryptPassword() {
		return encryptPassword;
	}
}