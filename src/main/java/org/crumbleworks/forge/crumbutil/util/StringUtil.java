package org.crumbleworks.forge.crumbutil.util;

/**
 * @author Patrick Bächli
 * @since 0.4.0
 */
public class StringUtil {

    private static final char DEL_CHARACTER = '\u007f';
    
    private StringUtil() {}
    
    /**
     * Checks whether a string consists only of valid ASCII characters.
     * 
     * @see <a href="https://tools.ietf.org/html/rfc20">RFC20</a>
     * 
     * @param s String to check
     * 
     * @return <code>true</code> if all characters are valid ASCII characters<br>
     *         <code>false</code> if at least one character is not a valid ASCII character
     */
    public static final boolean isAscii(final String s) {
        for(char c : s.toCharArray()) {
            if(c > DEL_CHARACTER) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks that a string is neither <code>null</code> nor empty ("")
     * 
     * @param s
     * 
     * @return <code>true</code> if the string is neither null nor empty; <code>false</code> otherwise
     */
    public static final boolean neitherNullNorEmpty(final String s) {
        if(s != null && !"".equals(s)) {
            return true;
        }
        
        return false;
    }
}
