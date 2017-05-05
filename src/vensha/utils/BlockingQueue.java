package vensha.utils;
import java.util.*;  
  
/**
 * BlockingQueue implements a Work Queue where in getNext() is blocked if there are no more
 * objects in the Q. This class should be used along with Threads when  you want 
 * a thread to process the objects in the Q sequentially. See Main for Example usage 
 */   

public class BlockingQueue<T> implements BlockingCollection<T> {
protected LinkedList<T> queue_; 
private boolean done_ = false; 
 
public BlockingQueue() {
   queue_ = new LinkedList<T>(); 
}
/**
 * Adds a new object to the Q. Null objects are not allowed
 * @throws NullPointerException if <tt> obj </tt> is null 
 */ 
@Override
public synchronized void add(T obj) {
  if (obj == null) throw new NullPointerException("Nulls are not allowed");
  queue_.addLast(obj);
  notify();  
}
/**
 * Fetchs the next object from the Q.
 * This method will <bold>block</bold> until a new element is added if the Q is empty, unless
 * done() has been called to signal no more additions ,in which case this method will return NULL 
 * @throws InterruptedException 
 * @see #done()
 */      
public synchronized T getNext() throws InterruptedException {
   while (queue_.isEmpty() && !done_) {
      wait(); 
   }  
   // When done() is called, this will wakeup even if  queue is empty
   if (queue_.isEmpty()) return null; 
   return queue_.removeFirst();

    
}
 
/**
 * Returns true if Q is empty else false
 */   
public boolean isEmpty() {
   return queue_.isEmpty(); 
}  
 
/**
 * This method should be called to signal that no more additions will be done to this Q.
 * If this method has been called and the Q has been consumed,then getNext() will return null
 * instead of blocking when the Q is empty  
 * @see #getNext() 
 */   
public synchronized void done() {
   done_ = true; 
   notify(); 
} 
}

 

 