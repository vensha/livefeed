package vensha.utils;
 
public interface Logger {
 
public void init(String[] args) throws Exception;
public void log(String msg);
public void logException(Exception e);
public void logException(String msg, Exception e);
public void setShowBaseName(boolean show);
public void setShowTimestamps(boolean show);  
public void finalize() throws Throwable;
 
}  