package org.crumbleworks.forge.crumbutil.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author Patrick Bächli
 * @since 0.4.0
 */
public class StringUtilTest {

    // is ASCII
    @Test
    public void testEmptyString() {
        assertTrue(StringUtil.isAscii(""));
    }
    
    @Test
    public void testNullString() {
        assertThrows(NullPointerException.class, () -> StringUtil.isAscii(null));
    }
    
    @Test
    public void testASCIIString() {
        assertTrue(StringUtil.isAscii("test"));
    }
    
    @Test
    public void testASCIIStringWithSpecialCharacters() {
        assertTrue(StringUtil.isAscii("test"));
    }
    
    @Test
    public void testASCIIStringWithWhiteSpaces() {
        assertTrue(StringUtil.isAscii("this is a test"));
    }
    
    @Test
    public void testASCIIStringWithFormattingCharacters() {
        assertTrue(StringUtil.isAscii("thiss\b\tis\f a\ntest"));
    }
    
    @Test
    public void testNonASCIIString() {
        assertFalse(StringUtil.isAscii("täst"));
    }
    
    // not null nor empty
    @Test
    public void checkNotNullNorEmptyWithNonNullValue() {
        assertTrue(StringUtil.neitherNullNorEmpty("Hello"));
        assertFalse(StringUtil.neitherNullNorEmpty(""));
    }

    @Test
    public void checkNotNullNorEmptyWithNullValue() {
        assertFalse(StringUtil.neitherNullNorEmpty(null));
    }
    
}
