package com.maxim.util.pgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GPGCmdLineHelper {
	
//	public static void main(String[] args) {
//		decryptFile("c:\\test\\pgp\\NBIS000051.pgp", "c:\\test\\pgp\\2345.txt", "123456");
//	}
    
	private static final Logger LOGGER = LoggerFactory.getLogger(GPGCmdLineHelper.class);
	
	private static final String ECHO_CMD = "cmd.exe /C echo ";
	private static final String DELIMITER = "|";
	private static final String PGP_CMD = "gpg --batch --passphrase-fd 0 ";
	private static final String SPACE = " ";
	private static final String PARA_OUTPUT = "--output";
	private static final String PARA_DECRYPT = "--decrypt";
	
	public static void decryptFile(String inputFile, String outputFile, String passphrase){
		
		try{

	    	StringBuilder sb = new StringBuilder();
	    	sb.append(ECHO_CMD);
	    	sb.append(passphrase);
	    	sb.append(DELIMITER);
	    	sb.append(PGP_CMD);
	    	sb.append(PARA_OUTPUT);
	    	sb.append(SPACE);
	    	sb.append(outputFile);
	    	sb.append(SPACE);
	    	sb.append(PARA_DECRYPT);
	    	sb.append(SPACE);
	    	sb.append(inputFile);
	    	
	    	String command = sb.toString();
	    	LOGGER.info("decryptFile command: " +command);
	    	Runtime rt = Runtime.getRuntime();
	    	Process process = rt.exec(command);
	    	if(process != null){

		    	BufferedReader stdInput = new BufferedReader(new 
			    	     InputStreamReader(process.getInputStream()));
		    	BufferedReader stdError = new BufferedReader(new 
		    	     InputStreamReader(process.getErrorStream()));
		
		    	// read the output from the command
		    	LOGGER.info("File Descryption on File" + inputFile + " Command Info Output:");
		    	String s = null;
		    	while ((s = stdInput.readLine()) != null) {
		    	    LOGGER.info(s);
		    	}
		
		    	// read any errors from the attempted command
		    	LOGGER.error("File Descryption on File" + inputFile + " Command Error Output:");
		    	while ((s = stdError.readLine()) != null) {
		    		LOGGER.info(s);
		    	}
		    	
		    	process.destroy();    				
	    				
	    	}
	    	
		}
    	catch(Exception e){
    		LOGGER.error("File Descryption Command Line Error.");
    		throw new RuntimeException("File Descryption Command Line Error.");
    	}
	}
	
	
	public static File decryptFileReturn(String inputFile, String outputFile, String passphrase)
			throws RuntimeException{
		
		File out = null;
		try{

	    	StringBuilder sb = new StringBuilder();
	    	sb.append(ECHO_CMD);
	    	sb.append(passphrase);
	    	sb.append(DELIMITER);
	    	sb.append(PGP_CMD);
	    	sb.append(PARA_OUTPUT);
	    	sb.append(SPACE);
	    	sb.append(outputFile);
	    	sb.append(SPACE);
	    	sb.append(PARA_DECRYPT);
	    	sb.append(SPACE);
	    	sb.append(inputFile);
	    	
	    	String command = sb.toString();
	    	
	    	Runtime rt = Runtime.getRuntime();
	    	Process process = rt.exec(command);
	    	if(process != null){
	
	    				
			    	BufferedReader stdInput = new BufferedReader(new 
				    	     InputStreamReader(process.getInputStream()));
				
			    	BufferedReader stdError = new BufferedReader(new 
			    	     InputStreamReader(process.getErrorStream()));
			
			    	// read the output from the command
			    	LOGGER.info("File Descryption on File" + inputFile + "Command Info Output:");
			    	String s = null;
			    	while ((s = stdInput.readLine()) != null) {
			    	    LOGGER.info(s);
			    	}
			
			    	// read any errors from the attempted command
			    	LOGGER.error("File Descryption on File" + inputFile + "Command Error Output:");
			    	while ((s = stdError.readLine()) != null) {
			    		LOGGER.info(s);
			    	
				    }

	    			
	    		process.destroy();
	    	}
	    	    				
	    		
		    	out = new File(outputFile);
	    	}
	    	
    	catch(Exception e){
    		LOGGER.error("File Descryption Command Line Error.");
    		throw new RuntimeException("File Descryption Command Line Error.");
    	}
		return out;
	}
}
