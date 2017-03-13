package it.discovery.marina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SampleApache {
	final private static Log LOGGER = LogFactory.getLog("it.discovery.marina.jsonlogger");
	
	public static void main(String[] args) throws SecurityException, IOException 
	{
		System.out.println(readFile());
		
	}

	
	private static boolean readFile() 
	{
		LOGGER.trace("start reading file");
		try {
			Files.readAllBytes(Paths.get("C:/base/0/2.txt"));
		} catch (IOException e) {
			LOGGER.error("Exception " +e.toString(), e);
			return false;
		}
		LOGGER.trace("end reading file");
		return true;
	}
}
