package org.crumbleworks.forge.crumbutil.validation;

/**
 * @author Michael Stocker
 * @since 0.6.8
 */
public final class InstanceLockException extends Exception {
    private static final long serialVersionUID = -5075436244317307826L;

    public InstanceLockException() {
        super();
    }
    
    public InstanceLockException(final String message) {
        super(message);
    }
    
    public InstanceLockException(final Throwable cause) {
        super(cause);
    }
    
    public InstanceLockException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
