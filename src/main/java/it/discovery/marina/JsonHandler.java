
package it.discovery.marina;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import java.util.logging.StreamHandler;

public class JsonHandler extends StreamHandler  {

	private void configure() {
        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();

        String level = manager.getProperty(cname +".level");
        if (level == null) {
        	setLevel(Level.INFO);
        }
        else
        {
        	try{
        		Level l = Level.parse((level.trim()));
        		setLevel(l);
        	}
        	catch(Exception e)
        	{
        		setLevel(Level.INFO);
        	}
        }
        
        String filter = manager.getProperty(cname +".filter");
        try {
            if (filter != null) {
                Class<Filter> clz = (Class<Filter>)ClassLoader.getSystemClassLoader().loadClass(filter);
                setFilter(clz.newInstance());
            }
            else
            {
            	setFilter(null);
            }
        } catch (Exception ex) {
         
        	setFilter(null);
        }
       
        setFormatter( new JSONFormatter());
        try {
            setEncoding(manager.getProperty(cname +".encoding").trim());
        } catch (Exception ex) {
            try {
                setEncoding(null);
            } catch (Exception ex2) {
          
            }
        }
    }

    public JsonHandler() {
        configure();
        setOutputStream(System.err);
    }

  
    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

   
    @Override
    public void close() {
        flush();
    }

}
