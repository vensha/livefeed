package vensha.livefeed.utils;
public interface ProcessListener {
  public void processStarted(Processor p);
  public void processMessage(String line); 
  public void processComplete(Processor p);  
}