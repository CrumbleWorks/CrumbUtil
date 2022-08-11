package org.crumbleworks.forge.crumbutil.validation;

import java.util.Collection;

/**
 * Offers methods to validate parameters against certain criteria. Methods of this class return the value if it checks out.
 * 
 * @author Michael Stocker
 * @since 0.1.0
 */
public final class Parameters {

    private Parameters() {}
    
    /**
     * Checks if a value is <code>null</code>
     * 
     * @param value
     * @param errorMessage
     * 
     * @return the value
     * @throws IllegalArgumentException if the value is <code>null</code>
     */
    public static final <T> T notNull(final T value, final String errorMessage) {
        if(null != value) {
            return value;
        }
        
        throw new IllegalArgumentException(errorMessage);
    }
    
    public static final <T> T notNull(final T value) {
        return notNull(value, "Parameter may not be null!");
    }
    
    
    
    /**
     * Checks that a string is not empty (<code>!"".equals(String)</code>)
     * 
     * @param s
     * @param errorMessage
     * 
     * @return the passed in string
     * @throws IllegalArgumentException if the string was empty
     */
    public static final String stringNotEmpty(final String s, final String errorMessage) {
        if(!s.equals("")) { //to force throwing a nullpointer
            return s;
        }
        
        throw new IllegalArgumentException(notNull(errorMessage));
    }
    
    public static final String stringNotEmpty(final String s) {
        return stringNotEmpty(s, "String may not be empty!");
    }
    
    /**
     * Verifies a String against a regex
     * 
     * @param s
     * @param r
     * 
     * @return the verified string
     * @throws IllegalArgumentException if the string does not match
     */
    public static final String verifyAgainstRegex(final String s, final String r) {
        if(s.matches(r)) {
            return s;
        }
        
        throw new IllegalArgumentException("String '" + s + "' does not match regex '" + r + "'");
    }
    
    /**
     * Checks if a value is greater or equal a limit.
     * <p><code>&nbsp;value &gt;= limit</code>
     * 
     * @param value
     * @param limit
     * 
     * @return the value
     * @throws IllegalArgumentException if the value is smaller
     */
    public static final long greaterOrEqual(final long value, final long limit) {
        if(value >= limit) {
            return value;
        }
        
        throw new IllegalArgumentException("Value must be equal or greater than '" + limit + "' but was: " + value);
    }
    
    /**
     * Checks if a value is greater or equal a limit.
     * <p><code>&nbsp;value &gt;= limit</code>
     * 
     * @param value
     * @param limit
     * 
     * @return the value
     * @throws IllegalArgumentException if the value is smaller
     */
    public static final int greaterOrEqual(final int value, final int limit) {
        return (int)greaterOrEqual((long)value, (long)limit);
    }
    
    /**
     * Checks if a value is smaller or equal a limit.
     * <p><code>&nbsp;value &lt;= limit</code>
     * 
     * @param value
     * @param limit
     * 
     * @return the value
     * @throws IllegalArgumentException if the value is greater
     */
    public static final long smallerOrEqual(final long value, final long limit) {
        if(value <= limit) {
            return value;
        }
        
        throw new IllegalArgumentException("Value must be equal or smaller than '" + limit + "' but was: " + value);
    }
    
    /**
     * Checks if a value is smaller or equal a limit.
     * <p><code>&nbsp;value &lt;= limit</code>
     * 
     * @param value
     * @param limit
     * 
     * @return the value
     * @throws IllegalArgumentException if the value is greater
     */
    public static final int smallerOrEqual(final int value, final int limit) {
        return (int)smallerOrEqual((long)value, (long)limit);
    }
    
    /**
     * Checks if a value equals a given number.
     * <p><code>&nbsp;value =/= limit</code>
     * 
     * @param value
     * @param number
     * 
     * @return the value
     * @throws IllegalArgumentException if the value is equal to the number
     */
    public static final long mustNotBeEqual(final long value, final long number) {
        if(value != number) {
            return value;
        }
        
        throw new IllegalArgumentException("Value must not be equal to '" + number + "' but was: " + value);
    }
    
    /**
     * Checks if a value equals a given number.
     * <p><code>&nbsp;value =/= limit</code>
     * 
     * @param value
     * @param number
     * 
     * @return the value
     * @throws IllegalArgumentException if the value is equal to the number
     */
    public static final int mustNotBeEqual(final int value, final int number) {
        return (int)greaterOrEqual((long)value, (long)number);
    }
    
    /**
     * Checks if the given array contains <code>null</code> values.
     * 
     * @param array
     * @param errorMessage
     * 
     * @return the array
     * @throws IllegalArgumentException if the array contains
     *         <code>null</code> values
     */
    public static final <T> T[] checkForNullValues(final T[] array, final String errorMessage) {
        for (T t : array) {
            if(t == null) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
        
        return array;
    }
    
    public static final <T> T[] checkForNullValues(final T[] array) {
        return checkForNullValues(array, "Array may not contain null values!");
    }
}
