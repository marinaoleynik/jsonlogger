package it.discovery.marina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sample {
	final private static Logger LOGGER = Logger.getLogger("it.discovery.marina.jsonlogger");
	
	public static void main(String[] args) throws SecurityException, IOException 
	{
		Handler handler = new JsonHandler();
		handler.setLevel(Level.FINEST);
		LOGGER.addHandler(handler);
		LOGGER.setLevel(Level.FINEST);
		System.out.println(readFile());
		
	}

	private static boolean readFile() 
	{
		LOGGER.finest("start reading file");
		try {
			Files.readAllBytes(Paths.get("C:/base/0/1.txt"));
		} catch (IOException e) {
			LOGGER.severe("Exception " +e.toString());
			return false;
		}
		LOGGER.finest("end reading file");
		return true;
	}
}
