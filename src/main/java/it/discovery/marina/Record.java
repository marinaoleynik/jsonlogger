package it.discovery.marina;

public class Record
{
	private String errorLevel;
	private String message;
	private String trace;
	private String date;
	private String logName;
	
	public String getErrorLevel() {
		return errorLevel;
	}
	public void setErrorLevel(String errorLevel) {
		this.errorLevel = errorLevel;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTrace() {
		return trace;
	}
	public void setTrace(String trace) {
		this.trace = trace;
	}
	public String getDate()
	{
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getLogName()
	{
		return logName;
	}
	public void setLogName(String logName) {
		this.logName = logName;
		
	}
	
}