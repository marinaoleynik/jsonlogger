package it.discovery.marina;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		GsonBuilder builder = new GsonBuilder();
	    Gson gson = builder.create();
	    return gson.toJson(record) + "\n";
	}

}
