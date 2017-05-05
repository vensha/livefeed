package vensha.utils;
import java.io.*; 
 
public class ByteArrayLogger extends FileLogger {
protected ByteArrayOutputStream baos_; 

public ByteArrayLogger() throws Exception {
	init(null);
}
  
public void init(String[] args) throws Exception {
    baos_ = new ByteArrayOutputStream(); 
    logWriter_ = new PrintStream(baos_);   
}
public byte[] getBytes() {
  return baos_.toByteArray();  
}       
public String getAsString() {
   return baos_.toString(); 
}  
public ByteArrayOutputStream getStream() {
   return baos_; 
}  
public String toString() {
	return baos_.toString();
}
}  