package vensha.livefeed.utils;
/*
* An implementaion of Logger that does no logging!! . To turn off all logging, useful for debugging etc
*/   
import java.io.*; 
import java.util.Date; 
 
 
public class EmptyLogger implements Logger {
 
public EmptyLogger() {
}
 
public void init(String[] args) throws Exception {
}
    
public void setShowBaseName(boolean show) {
}
 
public void setShowTimestamps(boolean show) {
}    

public void log(String msg) {
}
   
public void logException(Exception e) {
}
   
public void logException(String msg, Exception e) {
}
public String toString() {
	return "EmptyLogger-NOP";
}
 
/*
* Important - close all file handlers
*/      
public void finalize() throws Throwable {
}
   
} 