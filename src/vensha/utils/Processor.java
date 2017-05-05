/*
 * @(#)Java Source File	
 * 
 * Copyright by Vensha., 
 * vensha@hotmail.com  
 * All rights reserved. 
 * 
 * This software is the confidential and proprietary information of 
 * Vensha. ("Confidential Information").  You shall not disclose such 
 * Confidential Information and shall use it only in accordance with 
 * the terms of the license agreement you entered into with Vensha. 
 */

package vensha.utils;

import java.io.*;
import java.util.Vector; 

public class Processor extends Thread {
 
private String executableprogram="hhh";
private String flags = "";
private Vector listeners_;
private String filepath="";
private String arguments="";
private String msg;
private boolean internalerror;
private Process ps;
private static  int count;    
private int pid, processflag;
private static final int JAVA_PROCESS  = 0; 
private static final int JAVAC_PROCESS = 1;  
private String[] parms_; 
private String[] env_; 
private File wrkDir_;
private int format_ = -1; 
private boolean showCompleteMsg_ = false; 
                                                                                                       
public Processor(String exepath,ProcessListener listener) {
  this(exepath,"");
  if (listener != null) 
      listeners_.add(listener); 
}
public Processor(String[] args, String[] env, File wrkDir) {
   listeners_ = new Vector(1);
   parms_ = args;
   env_ = env;
   wrkDir_ = wrkDir;   
   format_ = 0; 
    
}  
public Processor(String cmd, String[] env, File wrkDir) {
   this(new String[] {cmd},env,wrkDir);
}   
 
public Processor(String exepath,String flagstream) {
  executableprogram = exepath; 
  flags = flagstream;
  count++;    
  pid = count;    
  listeners_ = new Vector(1); 
}
public void setShowAdditonalMessages(boolean show) {
   showCompleteMsg_ = show; 
}  
 
public void setFlags(String switches) {
  flags = switches;
}
 
public void setProgram(String  program) {
   executableprogram = program;         
}
public void process() {
   startProcessor(); 
} 
public void process(File f,ProcessListener pl,String args) {
    process(f.getAbsolutePath(),pl,args);    
}

public void process(String path,ProcessListener pl,String args) {
  filepath  = path;
  listeners_.add(pl);
  arguments = args;  
  startProcessor();
}
 
public void process(File f,String args) {
  process(f.getAbsolutePath(),args);
}

public void process(String path,String args) {
  filepath = path;
  arguments =  args;    
  startProcessor();
}
//setListener is chnaged to addListener
public void addListener(ProcessListener pl) {
  listeners_.add(pl);
}
 
public void removeListener(ProcessListener pl) {
  listeners_.remove(pl); 
}  

private void startProcessor() {
   internalerror = false;
   //System.out.println(toString()); 
   start();
}
public Process getProcess() {
   return ps; 
}  
    
public void run() {
 InputStream is,errs;
 errs=null; 
 if (arguments == null)  arguments = "";   
 try  {
      if (format_ == -1) {
         ps = Runtime.getRuntime ().exec (
             executableprogram + " " + flags + " " + filepath + " " + arguments);
      }else{
        ps = Runtime.getRuntime().exec (parms_,env_,wrkDir_);
      }     

       fireProcessStarted();     
  
      //input stream doesnt work,only error stream works for 'javac'
   if (executableprogram.indexOf("javac")  >= 0 )  {      
      is = ps.getErrorStream();  //for javac
      msg =  "Compilation successful!\nNo Errors" ;   
      processflag = JAVAC_PROCESS; 
   }else{
      is = ps.getInputStream();    // for java
      errs = ps.getErrorStream(); 
      spinOffGobblerThread(errs); 
      msg = "#" + pid +  " - Process complete!\nNo Errors";       
      processflag = JAVA_PROCESS; 
   }               

   BufferedReader ds = new BufferedReader (new InputStreamReader (is));
   String str = ds.readLine ();
   while (str != null) {
     handleOutput(str); 
     str = ds.readLine ();
 }
 ds.close ();
 internalerror = false;
}catch (Exception err) {
     System.out.println("CError " + err);
     handleOutput("Could not Start Process - \n" + err.getMessage()); 
     internalerror = true;
     err.printStackTrace(); 
}
fireProcessComplete(); 
}
 
private void spinOffGobblerThread(final InputStream err){
    final Processor copy = this; 
    Thread t = new Thread()
    {
      public void run(){   
        try { 
        BufferedReader ds  = new BufferedReader (new InputStreamReader (err));
        String str = ds.readLine ();  
        while (str != null) {
           handleOutput(str);  
           str = ds.readLine ();
       }
       }catch(IOException e) {System.out.println(e);} 
       if (showCompleteMsg_ == true)  {
            msg = "[" + copy.toString() + "] - Process complete, Exit Code: " + ps.exitValue();
            handleOutput(msg);  
       }      
     }   
   };
   t.start();  
}  
 
public void kill() {
   ps.destroy();
   //now try to gobble up any error/input streams ,some procees write o/p after destroy
     
   String retValue = "unknown"; 
   try {
      //spinOffGobblerThread(ps.getErrorStream());       
      //spinOffGobblerThread(ps.getInputStream());       
      retValue = (new Integer(ps.waitFor())).toString();
   }catch(Exception e){
     e.printStackTrace(); 
   }   
   msg = "Process: " + pid + " , is killed\nTerminated Abnormally , Exit Code: " + retValue;
}
 
public String toString() {
    return pid + ":" + executableprogram  + " " + flags + " " + filepath + "  " + arguments; 
}       
 
public boolean internalError(){
   return internalerror; 
}
/*
 *This will return the output stream of the process. Note that this stream
 *is the 'input' for the process...if the process is expecting any input
 *then you write to this stream  
 */    
public OutputStream getOutputStream() {
   if (ps == null) return null;
   return ps.getOutputStream(); 
} 
 
private void handleOutput(String msg) {
  //msg = "[#" + pid + "] " +  msg;
  listeners_.trimToSize(); 
  int lCount = listeners_.size(); 
  if (lCount <= 0)  System.out.println("P: " + msg);
  else {
    for (int i=0;i<lCount;i++) ((ProcessListener)listeners_.elementAt(i)).processMessage(msg); 
  }  
} 
 
private void fireProcessStarted() {
  listeners_.trimToSize(); 
  int count = listeners_.size(); 
  if (count == 0) {
    System.out.println("P:  processStarted");
   }else {
    for (int i=0;i<count;i++) {
      ((ProcessListener)listeners_.elementAt(i)).processStarted(this); 
    } 
  }   
  
}     
 
private void fireProcessComplete() {
  listeners_.trimToSize(); 
  int count = listeners_.size(); 
  if (count == 0)  System.out.println("P: " + msg);
  else {
    for (int i=0;i<count;i++) ((ProcessListener)listeners_.elementAt(i)).processComplete(this); 
  }  
}      
 
}
