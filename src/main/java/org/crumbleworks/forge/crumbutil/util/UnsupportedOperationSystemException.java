package org.crumbleworks.forge.crumbutil.util;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
public final class UnsupportedOperationSystemException extends RuntimeException {
    private static final long serialVersionUID = -1309054880578078631L;

    public UnsupportedOperationSystemException() {
        super();
    }
    
    public UnsupportedOperationSystemException(final String message) {
        super(message);
    }
    
    public UnsupportedOperationSystemException(final Throwable cause) {
        super(cause);
    }
    
    public UnsupportedOperationSystemException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
