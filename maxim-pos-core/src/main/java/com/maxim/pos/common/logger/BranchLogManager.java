package com.maxim.pos.common.logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;

public class BranchLogManager extends LogManager {

	  static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";  

	  static private Map<String,RepositorySelector> repositorySelectorMap;
	  
	  static {

		  repositorySelectorMap = new HashMap<String,RepositorySelector>();
	  }
	  
	  protected static RepositorySelector newRepositorySelector()
	  {
		    Hierarchy h = new Hierarchy(new RootLogger((Level) Level.DEBUG));
		    RepositorySelector repositorySelector = new DefaultRepositorySelector(h);

		    /** Search for the properties file log4j.properties in the CLASSPATH.  */
		    String override =OptionConverter.getSystemProperty(DEFAULT_INIT_OVERRIDE_KEY,
								       null);

		    // if there is no default init override, then get the resource
		    // specified by the user or the default config file.
		    if(override == null || "false".equalsIgnoreCase(override)) {

		      String configurationOptionStr = OptionConverter.getSystemProperty(
									  DEFAULT_CONFIGURATION_KEY, 
									  null);

		      String configuratorClassName = OptionConverter.getSystemProperty(
		                                                   CONFIGURATOR_CLASS_KEY, 
								   null);

		      URL url = null;

		      // if the user has not specified the log4j.configuration
		      // property, we search first for the file "log4j.xml" and then
		      // "log4j.properties"
		      if(configurationOptionStr == null) {	
			url = Loader.getResource(DEFAULT_XML_CONFIGURATION_FILE);
			if(url == null) {
			  url = Loader.getResource(DEFAULT_CONFIGURATION_FILE);
			}
		      } else {
			try {
			  url = new URL(configurationOptionStr);
			} catch (MalformedURLException ex) {
			  // so, resource is not a URL:
			  // attempt to get the resource from the class path
			  url = Loader.getResource(configurationOptionStr); 
			}	
		      }
		      
		      // If we have a non-null url, then delegate the rest of the
		      // configuration to the OptionConverter.selectAndConfigure
		      // method.
		      if(url != null) {
			    LogLog.debug("Using URL ["+url+"] for automatic log4j configuration.");
		        try {
		            OptionConverter.selectAndConfigure(url, configuratorClassName,
							   LogManager.getLoggerRepository());
		        } catch (NoClassDefFoundError e) {
		            LogLog.warn("Error during default initialization", e);
		        }
		      } else {
			    LogLog.debug("Could not find resource: ["+configurationOptionStr+"].");
		      }
		    } else {
		        LogLog.debug("Default initialization of overridden by " + 
		            DEFAULT_INIT_OVERRIDE_KEY + "property."); 
		    }  
		    
		    return repositorySelector;
	  }
	  
	  
	  static
	  public
	  LoggerRepository getBranchLoggerRepository(String branchCode) {
		  
		RepositorySelector repositorySelector = repositorySelectorMap.get(branchCode);
		
	    if (repositorySelector == null) {
	    	repositorySelector = newRepositorySelector();
	    	repositorySelectorMap.put(branchCode,repositorySelector);
	    }
//	    if (repositorySelector == null) {
//	        repositorySelector = new DefaultRepositorySelector(new NOPLoggerRepository());
//	        Exception ex = new IllegalStateException("Class invariant violation");
//	        String msg =
//	                "log4j called after unloading, see http://logging.apache.org/log4j/1.2/faq.html#unload.";
//	        if (isLikelySafeScenario(ex)) {
//	            LogLog.debug(msg, ex);
//	        } else {
//	            LogLog.error(msg, ex);
//	        }
//	    }
	    return repositorySelector.getLoggerRepository();
	  }

	  
	  /**
	     Retrieve the appropriate root logger.
	   */
	  public
	  static 
	  Logger getRootLogger(final String branchCode) {
	     // Delegate the actual manufacturing of the logger to the logger repository.
	    return getBranchLoggerRepository(branchCode).getRootLogger();
	  }

	  /**
	     Retrieve the appropriate {@link Logger} instance.  
	  */
	  public
	  static 
	  Logger getLogger(final String branchCode, final String name) {
	     // Delegate the actual manufacturing of the logger to the logger repository.
	    return getBranchLoggerRepository(branchCode).getLogger(name);
	  }

	 /**
	     Retrieve the appropriate {@link Logger} instance.  
	  */
	  public
	  static 
	  Logger getLogger(final String branchCode, final Class clazz) {
	     // Delegate the actual manufacturing of the logger to the logger repository.
	    return getBranchLoggerRepository(branchCode).getLogger(clazz.getName());
	  }


	  /**
	     Retrieve the appropriate {@link Logger} instance.  
	  */
	  public
	  static 
	  Logger getLogger(final String branchCode, final String name, final LoggerFactory factory) {
	     // Delegate the actual manufacturing of the logger to the logger repository.
	    return getBranchLoggerRepository(branchCode).getLogger(name, factory);
	  }  

	  public
	  static
	  Logger exists(final String branchCode, final String name) {
	    return getBranchLoggerRepository(branchCode).exists(name);
	  }

	  public
	  static
	  Enumeration getCurrentLoggers(final String branchCode) {
	    return getBranchLoggerRepository(branchCode).getCurrentLoggers();
	  }

	  public
	  static
	  void shutdown() {
		  for (RepositorySelector selector : repositorySelectorMap.values())
		  {
			  selector.getLoggerRepository().shutdown();
		  }
		  LogManager.shutdown();
	  }

	  public
	  static
	  void resetConfiguration() {
		  for (RepositorySelector selector : repositorySelectorMap.values())
		  {
			  selector.getLoggerRepository().resetConfiguration();
		  }
		  LogManager.resetConfiguration();
	  }

//	  private static boolean isLikelySafeScenario(final Exception ex) {
//	      StringWriter stringWriter = new StringWriter();
//	      ex.printStackTrace(new PrintWriter(stringWriter));
//	      String msg = stringWriter.toString();
//	      return msg.indexOf("org.apache.catalina.loader.WebappClassLoader.stop") != -1;
//	  }

}
