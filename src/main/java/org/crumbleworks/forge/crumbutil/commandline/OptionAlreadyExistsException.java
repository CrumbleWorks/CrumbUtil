package org.crumbleworks.forge.crumbutil.commandline;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
public final class OptionAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 7255869440385385783L;

    public OptionAlreadyExistsException() {
        super();
    }
    
    public OptionAlreadyExistsException(final String message) {
        super(message);
    }
    
    public OptionAlreadyExistsException(final Throwable cause) {
        super(cause);
    }
    
    public OptionAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
