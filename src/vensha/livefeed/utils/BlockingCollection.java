package vensha.livefeed.utils;

public interface BlockingCollection<T> {
public T getNext() throws InterruptedException;
public void done();   
public boolean isEmpty();
public void add(T obj);   
} 