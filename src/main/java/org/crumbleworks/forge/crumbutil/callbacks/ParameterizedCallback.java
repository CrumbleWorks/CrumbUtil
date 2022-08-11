package org.crumbleworks.forge.crumbutil.callbacks;

/**
 * Allows passing a method to be called with a parameter.
 * 
 * @author Michael Stocker
 * @since 0.1.0
 *
 * @param <T> the type of the passed parameter
 */
@FunctionalInterface
public interface ParameterizedCallback<T> {

    public void call(T param);
}
