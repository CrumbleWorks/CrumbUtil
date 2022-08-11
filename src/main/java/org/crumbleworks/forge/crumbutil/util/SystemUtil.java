package org.crumbleworks.forge.crumbutil.util;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
public final class SystemUtil {
    
    private SystemUtil() {}

    /**
     * Returns the default hashcode of this object in form of a hex String
     * 
     * @param obj the object for which to get the default hashcode
     * 
     * @return the system default hashcode of the passed object
     */
    public static final String getDefaultHash(final Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }
}
