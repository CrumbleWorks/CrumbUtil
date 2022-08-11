package org.crumbleworks.forge.crumbutil.datastructures;

/**
 * A <code>WaitBuffer</code> allows waiting for an object to be received. Using the {@link #set(Object)} method the object will release the lock on the {@link #get()} method.
 * <p>This class can be reused as after finishing a cycle.
 * 
 * @author Michael Stocker
 * @since 0.6.4
 *
 * @param <T> the type of the object to be waited for
 */
public class WaitBuffer<T> {
    private T object;
    private boolean isWaiting = false;
    
    public synchronized void set(T object) {
        notifyAll();
        isWaiting = false;
        this.object = object;
    }
    
    public synchronized T get() throws InterruptedException {
        isWaiting = true;
        wait();
        return object;
    }
    
    public boolean isWaiting() {
        return isWaiting;
    }
}
