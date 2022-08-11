package org.crumbleworks.forge.crumbutil.copyable;

/**
 * Marks the class as <code>Copyable</code>.
 * 
 * @author Michael Stocker
 * @since 0.1.0
 */
public interface Copyable<T> {
    
    /**
     * Creates a <b>copy</b> of the current object instance. This means it has to return a <b>new instance</b>.
     * 
     * @return a <b>copy</b> of the current object instance.
     */
    public T createCopy();
}