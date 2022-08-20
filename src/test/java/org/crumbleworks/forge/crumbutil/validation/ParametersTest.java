package org.crumbleworks.forge.crumbutil.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
public class ParametersTest {

    //notNull
    @Test
    public void checkNotNullWithNonNullValue() {
        assertTrue(null != Parameters.notNull("NOT NULL"));
    }
    
    @Test
    public void checkNotNullWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> Parameters.notNull(null));
    }
    
    //numberology (only testing longs as ints internally call long impl)
    @Test
    public void checkGreaterOrEqualWithValidChecks() {
        long[][] longs = {{1L, 0L}, {1L, 1L}, {2L, 1L}, {17L, 2L}, {13265487L, 2354245L}, {445453632343245L, 12343245677L}};
        
        for(long[] ls : longs) {
            assertEquals(ls[0], Parameters.greaterOrEqual(ls[0], ls[1]));
        }
    }
    
    @Test
    public void checkGreaterOrEqualWithInvalidCheck() {
        assertThrows(IllegalArgumentException.class, () -> Parameters.greaterOrEqual(1234234L, 1234782456925L));
    }
    
    @Test
    public void checkSmallerOrEqualWithValidChecks() {
        long[][] longs = {{1L, 0L}, {1L, 1L}, {2L, 1L}, {17L, 2L}, {13265487L, 2354245L}, {445453632343245L, 12343245677L}};
        
        for(long[] ls : longs) {
            assertEquals(ls[1], Parameters.smallerOrEqual(ls[1], ls[0]));
        }
    }
    
    @Test
    public void checkSmallerOrEqualWithInvalidCheck() {
    	assertThrows(IllegalArgumentException.class, () -> Parameters.smallerOrEqual(1234782456925L, 1234234L));
    }
    
    @Test
    public void checkMustNotBeSameWithValidChecks() {
        long[][] longs = {{1L, 0L}, {1L, 7L}, {2L, 1L}, {17L, 2L}, {13265487L, 2354245L}, {445453632343245L, 12343245677L}};
        
        for(long[] ls : longs) {
            assertEquals(ls[0], Parameters.mustNotBeEqual(ls[0], ls[1]));
        }
    }
    
    @Test
    public void checkMustNotBeSameWithInvalidCheck() {
    	assertThrows(IllegalArgumentException.class, () -> Parameters.mustNotBeEqual(1234234L, 1234234L));
    }
    
    //String not empty
    @Test
    public void checkStringNotEmptyWithNullString() {
    	assertThrows(NullPointerException.class, () -> Parameters.stringNotEmpty(null));
    }
    
    @Test
    public void checkStringNotEmptyWithEmptyString() {
    	assertThrows(IllegalArgumentException.class, () -> Parameters.stringNotEmpty(""));
    }
    
    @Test
    public void checkStringNotEmptyWithValidString() {
        assertEquals("TestString", Parameters.stringNotEmpty("TestString"));
    }
    
    //Verify against regex
    @Test
    public void checkVARWithNullString() {
        assertThrows(NullPointerException.class, () -> Parameters.verifyAgainstRegex(null, ""));
    }
    
    @Test
    public void checkVARWithNullRegex() {
        assertThrows(NullPointerException.class, () -> Parameters.verifyAgainstRegex("", null));
    }
    
    @Test
    public void checkVARWithNonMatching() {
    	assertThrows(IllegalArgumentException.class, () -> Parameters.verifyAgainstRegex("99 Red Balloons", "^[A-Za-z]$"));
    }
    
    @Test
    public void checkVARWithMatching() {
        assertEquals("Hello", Parameters.verifyAgainstRegex("Hello", "^[A-Za-z]+$"));
    }
    
    //arrays
    @Test
    public void checkArrayNoNullWithNoNullValues() {
        Object[] o = {new Object(), new Object(), new Object()};
        Parameters.checkForNullValues(o);
        assertTrue(true);
    }
    
    @Test
    public void checkArrayNoNullWithEmptyArray() {
        Object[] o = {};
        Parameters.checkForNullValues(o);
        assertTrue(true);
    }
    
    @Test
    public void checkArrayNoNullWIthNullValue() {
    	assertThrows(IllegalArgumentException.class, () -> Parameters.checkForNullValues(new Object[] {null}));
    }
}
