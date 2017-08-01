package com.maxim.util;

import java.io.IOException;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author Steven
 */
public final class CommonUtil {
    /**
     * Stores the class name for debugging purpose.
     */
    private static final String CLASS_NAME = CommonUtil.class.getName();


    /**
     * This method provides to get the configuration value from crms
     * configuration properties file.
     */
    public static String getConfigurationValue(final String keyName) {
        final String methodName = "getConfigurationValue";
        String properitesValue = "";
        try {
            Properties prop = new Properties();
            prop.load(CommonUtil.getResourceAsStream(CommonUtil.class, "config.properties"));
            properitesValue = prop.getProperty(keyName);
        }
        catch (Exception e) {
        }

        return properitesValue;
    }
    
    /**
     * This method provides to get the AllowedRoleID(currently) /DefinedFunctionID from properties
     *  properties file
     */
    public static String getFunctionID(final String keyName) {
        final String methodName = "getFunctionID";
        String properitesValue = "";
        try {
            Properties prop = new Properties();
            prop.load(CommonUtil.getResourceAsStream(CommonUtil.class, "function.properties"));
            properitesValue = prop.getProperty(keyName);
        }
        catch (IOException ioe) {
            //CommonDebugLogger.error(CLASS_NAME, methodName, "Could not find the brlconfig.properties", ioe);
        }
        catch (NullPointerException npe) {

            //CommonDebugLogger.error(CLASS_NAME, methodName, "Could not find the brlconfig.properties", npe);
        }
        return properitesValue;
    }
    
    public static void checkValidParam(final String str, final String[] validParam) {
        if (!containsString(str, validParam)) {
            throw new IllegalArgumentException("Parameter " + str + " is invalid!");
        }
    }

    public static boolean containsString(final String s, final String[] arr) {
        boolean result = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(s)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    
    public static InputStream getResourceAsStream(final Class clazz, final String resourceName) {
        if (isBlank(resourceName)) {
            throw new IllegalArgumentException("resourceName is blank!");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null!");
        }
        InputStream result =  clazz.getClassLoader().getResourceAsStream(resourceName);
        return result;
    }
    
    
    public static List stringToList(String data, String separator){

        List list = new ArrayList();
        if (data!=null && data.length()>0 && separator!=null){
            StringTokenizer strTok = new StringTokenizer(data, separator);
            while (strTok.hasMoreTokens()) {
                list.add(((String) strTok.nextToken()).trim());
            }
        }
        return list;
    }
    
    public static boolean isBlank(final String str) {
        boolean result = true;
        int strLen = 0;
        if (str == null || str.length() == 0) {
            result = true;
        }
        else {
	        strLen = str.length();
	        for (int i = 0; i < strLen; i++) {
	            if (!Character.isWhitespace(str.charAt(i))) {
	                result = false;
	                break;
	            }
	        }
        }
        return result;
    }
    
    public static String getClassNameFromFullPackage(String fullName) {
        final String methodName = "getClassNameFromFullPackage";
        String className = "";
        
        int lastdotIdx = fullName.lastIndexOf(".");
        
        if(lastdotIdx == -1){
        	className = fullName;
        }else{
        	className = fullName.substring(lastdotIdx+1);
        }
        
        return className;
    }
    
	public static String getStackTrace(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	public static String nvl(String s){
		if (isBlank(s)){
			return "";
		}else{
			return s.trim();
		}
	}
	
	public static boolean isInteger(String s){
		
		try{
			int i = Integer.parseInt(s);
		}catch(NumberFormatException e){

			return false;
		}
		
		return true;
	}
	
	public static boolean isDouble(String s){
		try{
			double d = Double.parseDouble(s);
		}catch(NumberFormatException e){

			return false;
		}
		
		return true;
	}
	

}