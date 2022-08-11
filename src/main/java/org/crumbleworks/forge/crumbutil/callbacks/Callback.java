package org.crumbleworks.forge.crumbutil.callbacks;

/**
 * Allows passing a method to be called.
 * 
 * @author Michael Stocker
 * @since 0.1.0
 */
@FunctionalInterface
public interface Callback {

    public void call();
}
