package org.crumbleworks.forge.crumbutil.validation;

import static org.junit.Assert.*;

import org.crumbleworks.forge.crumbutil.validation.Parameters;
import org.junit.Test;

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
    
    @Test(expected = IllegalArgumentException.class)
    public void checkNotNullWithNullValue() {
        Parameters.notNull(null);
    }
    
    //numberology (only testing longs as ints internally call long impl)
    @Test
    public void checkGreaterOrEqualWithValidChecks() {
        long[][] longs = {{1L, 0L}, {1L, 1L}, {2L, 1L}, {17L, 2L}, {13265487L, 2354245L}, {445453632343245L, 12343245677L}};
        
        for(long[] ls : longs) {
            assertEquals(ls[0], Parameters.greaterOrEqual(ls[0], ls[1]));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void checkGreaterOrEqualWithInvalidCheck() {
        Parameters.greaterOrEqual(1234234L, 1234782456925L);
    }
    
    @Test
    public void checkSmallerOrEqualWithValidChecks() {
        long[][] longs = {{1L, 0L}, {1L, 1L}, {2L, 1L}, {17L, 2L}, {13265487L, 2354245L}, {445453632343245L, 12343245677L}};
        
        for(long[] ls : longs) {
            assertEquals(ls[1], Parameters.smallerOrEqual(ls[1], ls[0]));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void checkSmallerOrEqualWithInvalidCheck() {
        Parameters.smallerOrEqual(1234782456925L, 1234234L);
    }
    
    @Test
    public void checkMustNotBeSameWithValidChecks() {
        long[][] longs = {{1L, 0L}, {1L, 7L}, {2L, 1L}, {17L, 2L}, {13265487L, 2354245L}, {445453632343245L, 12343245677L}};
        
        for(long[] ls : longs) {
            assertEquals(ls[0], Parameters.mustNotBeEqual(ls[0], ls[1]));
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void checkMustNotBeSameWithInvalidCheck() {
        Parameters.mustNotBeEqual(1234234L, 1234234L);
    }
    
    //String not empty
    @Test(expected = NullPointerException.class)
    public void checkStringNotEmptyWithNullString() {
        Parameters.stringNotEmpty(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void checkStringNotEmptyWithEmptyString() {
        Parameters.stringNotEmpty("");
    }
    
    @Test
    public void checkStringNotEmptyWithValidString() {
        assertEquals("TestString", Parameters.stringNotEmpty("TestString"));
    }
    
    //Verify against regex
    @Test(expected = NullPointerException.class)
    public void checkVARWithNullString() {
        Parameters.verifyAgainstRegex(null, "");
    }
    
    @Test(expected = NullPointerException.class)
    public void checkVARWithNullRegex() {
        Parameters.verifyAgainstRegex("", null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void checkVARWithNonMatching() {
        assertEquals("Hello", Parameters.verifyAgainstRegex("99 Red Balloons", "^[A-Za-z]$"));
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
    
    @Test(expected = IllegalArgumentException.class)
    public void checkArrayNoNullWIthNullValue() {
        Parameters.checkForNullValues(new Object[] {null});
    }
}
