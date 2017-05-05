package vensha.utils;
import java.io.PrintWriter;
import java.io.StringWriter;
/**
* A quick implementation of Log Manager. Maintains a list of loggers that are accessible by a name.
* If a logger by a name doesn't exist,it creates one and registers it. 
*/   
import java.util.Hashtable;
  
public class LogManager {
private static Hashtable logRegistry_; 
private static Logger defaultLogger_;
private static Logger emptyLogger_;
private static Logger currentLogger_;
 
static {
   logRegistry_ = new Hashtable();
   defaultLogger_ = new FileLogger();
   emptyLogger_ = new EmptyLogger();
   try { 
     defaultLogger_.init(null);  //"Null" will create a logger that writes everything to System.out. 
   }catch(Exception e) {
      //Will not happen since args are null; 
   }    
}
   
private LogManager() {
}
 
/**
 *No args method will return defaultLogger_.
 *This will Log everything to Console, as good as System.out.println
 */
public static Logger getLogger() {
    return defaultLogger_; 
}
public static void setCurrentLogger(Logger logger) {
   currentLogger_ = logger; 
}  
public static Logger getCurrentLogger() {
  if (currentLogger_ == null) {
     return defaultLogger_; 
  }
  return currentLogger_;  
}  
   
/**
 * This call will return a FileLogger , if one exists by the specified name,it simply returns the existing
 * one else it creates a new Logger ,registers it and returns it. 
 * Note that this is an expensive call with sync, so calling classes must maintain a local reference
 * (and call this only to get the initial reference)   
 */   
public synchronized static Logger getFileLogger(String name, String file, boolean append)
                                                                            throws Exception {
    Logger logger; 
    Object obj = logRegistry_.get(name);
    if (obj == null) {
       logger = new FileLogger();
       logger.init( new String[] {name,file,new Boolean(append).toString()} );  
       logRegistry_.put(name,logger);  
    }else{
       logger = (Logger)obj; 
    }  
    return logger; 
     
}

public static Logger createByteArrayLogger() throws Exception {
	return new ByteArrayLogger();
}
/* Call this method only if u r sure if the logger exists. This will return null in case
 * no logger by the name exists 
 */   
public static Logger getLogger(String logName) {
   return (Logger)logRegistry_.get(logName); 
}     
/**
 *Returns a do-nothing logger. Useful to turn off the whole logging
 */
public static Logger getEmptyLogger() {
    return emptyLogger_; 
}    

public static void registerLogger(String name, Logger logger) {
   logRegistry_.put(name,logger); 
}   

public static String exToString(Exception e) {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	e.printStackTrace(pw);
	return sw.toString(); 
}

//Convenient methods to log on current logger
public static void log(String msg) {
	getCurrentLogger().log(msg);
}
public static void log(Exception e) {
	getCurrentLogger().logException(e);
}
public static void log(String msg, Exception e) {
	getCurrentLogger().logException(msg,e);
}
 
 

}  