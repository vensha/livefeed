package vensha.livefeed.utils;
/*
* An implementaion of Logger that logs to a File
*/
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;


public class FileLogger implements Logger {
protected PrintStream logWriter_;
private String baseName_ ;
private String file_ ;
private boolean append_ = true;
private boolean printBase_;
private boolean printTimeStamps_ = true;
private SimpleDateFormat formatter_ = new SimpleDateFormat("MMM dd yyyy hh:mm:ss");

public FileLogger() {
}

public void init(String[] args) throws Exception {
   if (args==null || args.length < 3) {
      baseName_ = "";
      logWriter_ = System.out;
   }else{
       baseName_ = args[0];
       file_ = args[1];
       append_ = args[2].equalsIgnoreCase("true");
       logWriter_ = new PrintStream(new FileOutputStream(file_,append_));
   }
   //test

}

public void setShowBaseName(boolean show) {
    printBase_= show;
}

public void setShowTimestamps(boolean show) {
     printTimeStamps_ = show;
}

public void log(String msg) {
   timeStamp();
   if (printBase_ )  logWriter_.print(baseName_);
   StackTraceElement[] ste = Thread.currentThread().getStackTrace();
   logWriter_.println("["+ Thread.currentThread().getName() + "] (" + ste[2].getFileName() + ":" + ste[2].getLineNumber() + ") " +  msg);
}

public void logException(Exception e) {
  timeStamp();
  if (printBase_ )  logWriter_.print(baseName_);
  e.printStackTrace(logWriter_);
}

public void logException(String msg, Exception e) {
   timeStamp();
   if (printBase_ )  logWriter_.print(baseName_);
   logWriter_.println(msg);
   e.printStackTrace(logWriter_);
}

public void timeStamp() {
    if (printTimeStamps_) {
       logWriter_.print( "[" + formatter_.format(new Date()) + "]" );
    }
}

/*
* Important - close all file handlers
*/
public void finalize() throws Throwable {
   logWriter_.flush();
   logWriter_.close();
}

public String toString() {
	return baseName_ + "::" + file_;
}

}