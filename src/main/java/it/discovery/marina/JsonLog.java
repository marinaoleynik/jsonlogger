package it.discovery.marina;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonLog implements Log, Serializable{
	
	static protected final String systemPrefix = "it.discovery.marina.jsonlog.";
	/** Properties loaded from simplelog.properties */
    static protected final Properties simpleLogProps = new Properties();

    /** The default format to use when formating dates */
    static protected final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";

    /** Include the instance name in the log message? */
    static volatile protected boolean showLogName = false;

    /** Include the short name ( last component ) of the logger in the log
     *  message. Defaults to true - otherwise we'll be lost in a flood of
     *  messages without knowing who sends them.
     */
    static volatile protected boolean showShortName = true;

    /** Include the current time in the log message */
    static volatile protected boolean showDateTime = false;

    /** The date and time format to use in the log message */
    static volatile protected String dateTimeFormat = DEFAULT_DATE_TIME_FORMAT;
    
    static protected DateFormat dateFormatter = null;
    
	// ---------------------------------------------------- Log Level Constants
    /** "Trace" level logging. */
    public static final int LOG_LEVEL_TRACE  = 1;
    /** "Debug" level logging. */
    public static final int LOG_LEVEL_DEBUG  = 2;
    /** "Info" level logging. */
    public static final int LOG_LEVEL_INFO   = 3;
    /** "Warn" level logging. */
    public static final int LOG_LEVEL_WARN   = 4;
    /** "Error" level logging. */
    public static final int LOG_LEVEL_ERROR  = 5;
    /** "Fatal" level logging. */
    public static final int LOG_LEVEL_FATAL  = 6;

    /** Enable all logging levels */
    public static final int LOG_LEVEL_ALL    = LOG_LEVEL_TRACE - 1;

    /** Enable no logging levels */
    public static final int LOG_LEVEL_OFF    = LOG_LEVEL_FATAL + 1;

    private static String getStringProperty(String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (SecurityException e) {
            // Ignore
        }
        return prop == null ? simpleLogProps.getProperty(name) : prop;
    }

    private static String getStringProperty(String name, String dephault) {
        String prop = getStringProperty(name);
        return prop == null ? dephault : prop;
    }

    private static boolean getBooleanProperty(String name, boolean dephault) {
        String prop = getStringProperty(name);
        return prop == null ? dephault : "true".equalsIgnoreCase(prop);
    }
   
    static {
        // Add props from the resource simplelog.properties
        InputStream in = getResourceAsStream("jsonlog.properties");
        if(null != in) {
            try {
                simpleLogProps.load(in);
                in.close();
            } catch(java.io.IOException e) {
                // ignored
            }
        }

        showLogName = getBooleanProperty(systemPrefix + "showlogname", showLogName);
        showShortName = getBooleanProperty(systemPrefix + "showShortLogname", showShortName);
        showDateTime = getBooleanProperty(systemPrefix + "showdatetime", showDateTime);

        if(showDateTime) {
            dateTimeFormat = getStringProperty(systemPrefix + "dateTimeFormat",
                                               dateTimeFormat);
            try {
                dateFormatter = new SimpleDateFormat(dateTimeFormat);
            } catch(IllegalArgumentException e) {
                // If the format pattern is invalid - use the default format
                dateTimeFormat = DEFAULT_DATE_TIME_FORMAT;
                dateFormatter = new SimpleDateFormat(dateTimeFormat);
            }
        }
    }
    
    protected volatile int currentLogLevel;
    protected volatile String logName = null;
    private volatile String shortLogName = null;
    
    public JsonLog(String name) {
        logName = name;

        setCurrentLogLevel(JsonLog.LOG_LEVEL_INFO);

        // Set log level from properties
        String lvl = getStringProperty(systemPrefix + "log." + logName);
        int i = String.valueOf(name).lastIndexOf(".");
        while(null == lvl && i > -1) {
            name = name.substring(0,i);
            lvl = getStringProperty(systemPrefix + "log." + name);
            i = String.valueOf(name).lastIndexOf(".");
        }

        if(null == lvl) {
            lvl =  getStringProperty(systemPrefix + "defaultlog");
        }

        if("all".equalsIgnoreCase(lvl)) {
        	setCurrentLogLevel(JsonLog.LOG_LEVEL_ALL);
        } else if("trace".equalsIgnoreCase(lvl)) {
        	setCurrentLogLevel(JsonLog.LOG_LEVEL_TRACE);
        } else if("debug".equalsIgnoreCase(lvl)) {
        	setCurrentLogLevel(JsonLog.LOG_LEVEL_DEBUG);
        } else if("info".equalsIgnoreCase(lvl)) {
        	setCurrentLogLevel(JsonLog.LOG_LEVEL_INFO);
        } else if("warn".equalsIgnoreCase(lvl)) {
        	setCurrentLogLevel(JsonLog.LOG_LEVEL_WARN);
        } else if("error".equalsIgnoreCase(lvl)) {
        	setCurrentLogLevel(JsonLog.LOG_LEVEL_ERROR);
        } else if("fatal".equalsIgnoreCase(lvl)) {
        	setCurrentLogLevel(JsonLog.LOG_LEVEL_FATAL);
        } else if("off".equalsIgnoreCase(lvl)) {
        	setCurrentLogLevel(JsonLog.LOG_LEVEL_OFF);
        }
    }
    
    public int getCurrentLogLevel() {
		return currentLogLevel;
	}

	public void setCurrentLogLevel(int currentLogLevel) {
		this.currentLogLevel = currentLogLevel;
	}

	protected boolean isLevelEnabled(int logLevel) {
        return logLevel >= currentLogLevel;
    }
    
	@Override
	public void debug(Object message) {
		 if (isLevelEnabled(JsonLog.LOG_LEVEL_DEBUG)) {
	            log(JsonLog.LOG_LEVEL_DEBUG, message, null);
	        }
		
	}

	@Override
	public void debug(Object message, Throwable t) {
		 if (isLevelEnabled(JsonLog.LOG_LEVEL_DEBUG)) {
	            log(JsonLog.LOG_LEVEL_DEBUG, message, t);
	        }
		
	}

	@Override
	public void error(Object message) {
		if (isLevelEnabled(JsonLog.LOG_LEVEL_ERROR)) {
            log(JsonLog.LOG_LEVEL_ERROR, message, null);
        }
		
	}

	@Override
	public void error(Object message, Throwable t) {
		 if (isLevelEnabled(JsonLog.LOG_LEVEL_ERROR)) {
	            log(JsonLog.LOG_LEVEL_ERROR, message, t);
	        }
		
	}

	@Override
	public void fatal(Object message) {
		  if (isLevelEnabled(JsonLog.LOG_LEVEL_FATAL)) {
	            log(JsonLog.LOG_LEVEL_FATAL, message, null);
	        }
		
	}

	@Override
	public void fatal(Object message, Throwable t) {
		 if (isLevelEnabled(JsonLog.LOG_LEVEL_FATAL)) {
	            log(JsonLog.LOG_LEVEL_FATAL, message, t);
	        }
		
	}

	@Override
	public void info(Object message) {
		  if (isLevelEnabled(JsonLog.LOG_LEVEL_INFO)) {
	            log(JsonLog.LOG_LEVEL_INFO,message,null);
	        }
		
	}

	@Override
	public void info(Object message, Throwable t) {
		 if (isLevelEnabled(JsonLog.LOG_LEVEL_INFO)) {
	            log(JsonLog.LOG_LEVEL_INFO, message, t);
	        }
		
	}

	@Override
	public boolean isDebugEnabled() {
		return isLevelEnabled(JsonLog.LOG_LEVEL_DEBUG);
	}

	@Override
	public boolean isErrorEnabled() {
		 return isLevelEnabled(JsonLog.LOG_LEVEL_ERROR);
	}

	@Override
	public boolean isFatalEnabled() {
		 return isLevelEnabled(JsonLog.LOG_LEVEL_FATAL);
	}

	@Override
	public boolean isInfoEnabled() {
		 return isLevelEnabled(JsonLog.LOG_LEVEL_INFO);
	}

	@Override
	public boolean isTraceEnabled() {
		return isLevelEnabled(JsonLog.LOG_LEVEL_TRACE);
	}

	@Override
	public boolean isWarnEnabled() {
		 return isLevelEnabled(JsonLog.LOG_LEVEL_WARN);
	}

	@Override
	public void trace(Object message) {
		if (isLevelEnabled(JsonLog.LOG_LEVEL_TRACE)) {
            log(JsonLog.LOG_LEVEL_TRACE, message, null);
        }
		
	}

	@Override
	public void trace(Object message, Throwable t) {
		if (isLevelEnabled(JsonLog.LOG_LEVEL_TRACE)) {
            log(JsonLog.LOG_LEVEL_TRACE, message, t);
        }
		
	}

	@Override
	public void warn(Object message) {
		 if (isLevelEnabled(JsonLog.LOG_LEVEL_WARN)) {
	            log(JsonLog.LOG_LEVEL_WARN, message, null);
	        }
		
	}

	@Override
	public void warn(Object message, Throwable t) {
		 if (isLevelEnabled(JsonLog.LOG_LEVEL_WARN)) {
	            log(JsonLog.LOG_LEVEL_WARN, message, t);
	        }
		
	}
	
	protected void log(int type, Object message, Throwable t) {
		
		Record record = new Record();
        // Use a string buffer for better performance
        final StringBuffer buf = new StringBuffer();

        // Append date-time if so configured
       if(showDateTime) {
            final Date now = new Date();
            String dateText;
            synchronized(dateFormatter) {
                dateText = dateFormatter.format(now);
            }
           /* buf.append(dateText);
            buf.append(" ");*/
            record.setDate(dateText);
        }

        // Append a readable representation of the log level
        switch(type) {
            case JsonLog.LOG_LEVEL_TRACE: record.setErrorLevel("TRACE"); break;
            case JsonLog.LOG_LEVEL_DEBUG: record.setErrorLevel("DEBUG"); break;
            case JsonLog.LOG_LEVEL_INFO:  record.setErrorLevel("INFO");  break;
            case JsonLog.LOG_LEVEL_WARN:  record.setErrorLevel("WARN");  break;
            case JsonLog.LOG_LEVEL_ERROR: record.setErrorLevel("ERROR"); break;
            case JsonLog.LOG_LEVEL_FATAL: record.setErrorLevel("FATAL"); break;
        }

        // Append the name of the log instance if so configured
        if(showShortName) {
            if(shortLogName == null) {
                // Cut all but the last component of the name for both styles
                final String slName = logName.substring(logName.lastIndexOf(".") + 1);
                shortLogName = slName.substring(slName.lastIndexOf("/") + 1);
            }
            //buf.append(String.valueOf(shortLogName)).append(" - ");
            record.setLogName(String.valueOf(shortLogName));
        } else if(showLogName) {
            //buf.append(String.valueOf(logName)).append(" - ");
        	record.setLogName(String.valueOf(logName));
        }

        // Append the message
        record.setMessage((String.valueOf(message)));
        //buf.append(String.valueOf(message));

        // Append stack trace if not null
        if(t != null) {
           /* buf.append(" <");
            buf.append(t.toString());
            buf.append(">");*/

            final java.io.StringWriter sw = new java.io.StringWriter(1024);
            final java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
           // buf.append(sw.toString());
            record.setTrace(sw.toString());
        }

       
        GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.create();
	    buf.append(gson.toJson(record));
        // Print to the appropriate destination
        write(buf);
    }

	protected void write(StringBuffer buffer) {
        System.err.println(buffer.toString());
    }
	
	 private static ClassLoader getContextClassLoader() {
	        ClassLoader classLoader = null;

	        try {
	            // Are we running on a JDK 1.2 or later system?
	            final Method method = Thread.class.getMethod("getContextClassLoader", (Class[]) null);

	            // Get the thread context class loader (if there is one)
	            try {
	                classLoader = (ClassLoader)method.invoke(Thread.currentThread(), (Class[]) null);
	            } catch (IllegalAccessException e) {
	                // ignore
	            } catch (InvocationTargetException e) {
	                /**
	                 * InvocationTargetException is thrown by 'invoke' when
	                 * the method being invoked (getContextClassLoader) throws
	                 * an exception.
	                 *
	                 * getContextClassLoader() throws SecurityException when
	                 * the context class loader isn't an ancestor of the
	                 * calling class's class loader, or if security
	                 * permissions are restricted.
	                 *
	                 * In the first case (not related), we want to ignore and
	                 * keep going.  We cannot help but also ignore the second
	                 * with the logic below, but other calls elsewhere (to
	                 * obtain a class loader) will trigger this exception where
	                 * we can make a distinction.
	                 */
	                if (e.getTargetException() instanceof SecurityException) {
	                    // ignore
	                } else {
	                    // Capture 'e.getTargetException()' exception for details
	                    // alternate: log 'e.getTargetException()', and pass back 'e'.
	                    throw new LogConfigurationException
	                        ("Unexpected InvocationTargetException", e.getTargetException());
	                }
	            }
	        } catch (NoSuchMethodException e) {
	            // Assume we are running on JDK 1.1
	            // ignore
	        }

	        if (classLoader == null) {
	            classLoader = JsonLog.class.getClassLoader();
	        }

	        // Return the selected class loader
	        return classLoader;
	    }

	    private static InputStream getResourceAsStream(final String name) {
	        return (InputStream)AccessController.doPrivileged(
	            new PrivilegedAction() {
	                public Object run() {
	                    ClassLoader threadCL = getContextClassLoader();

	                    if (threadCL != null) {
	                        return threadCL.getResourceAsStream(name);
	                    } else {
	                        return ClassLoader.getSystemResourceAsStream(name);
	                    }
	                }
	            });
	    }
}