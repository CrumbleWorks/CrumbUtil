package org.crumbleworks.forge.crumbutil.util;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
public final class MathUtil {

    private MathUtil() {}
    
    /* ************************************************************************
     * NUMBERS
     */
    
    /**
     * @param x a number
     * @param y a number
     * 
     * @return the smaller of both <code>x</code> &amp; <code>y</code>
     */
    public static final long min(final long x, final long y) {
        return x < y ? x : y;
    }
    
    public static final int min(final int x, final int y) {
        return (int)min((long)x, (long)y);
    }
    
    /**
     * @param x a number
     * @param y a number
     * 
     * @return the bigger of both <code>x</code> &amp; <code>y</code>
     */
    public static final long max(final long x, final long y) {
        return x > y ? x : y;
    }
    
    public static final int max(final int x, final int y) {
        return (int)max((long)x, (long)y);
    }
    
    //Euclid
    /**
     * @param a a number
     * @param b a number
     * 
     * @return the <code>greatest common denominator</code> of both <code>a</code> &amp; <code>b</code>
     */
    public static final long gcd(final long a, final long b) {
        if(b == 0) {
            return a;
        }
        
        return gcd(b, a % b);
    }
    
    public static final int gcd(final int a, final int b) {
        return (int)gcd((long)a, (long)b);
    }
    
    //Euclid reduction
    /**
     * @param a a number
     * @param b a number
     * 
     * @return the <code>least common multiple</code> of both <code>a</code> &amp; <code>b</code>
     */
    public static final long lcm(final long a, final long b) {
        if(a != 0 || b != 0) {
            return a * (b / gcd(a, b));
        }
        
        //if both are 0 we have a special case
        return 0;
    }
    
    public static final int lcm(final int a, final int b) {
        return (int)lcm((long)a, (long)b);
    }
    
    /* ************************************************************************
     * GEOMETRY
     */
    
    //Heron's formula
    /**
     * @param a the length of side <code>a</code>
     * @param b the length of side <code>b</code>
     * @param c the length of side <code>c</code>
     * 
     * @return the <code>area</code> of the triangle with the sides <code>a</code>, <code>b</code> &amp; <code>c</code>
     */
    public static final double triangleArea(final double a, final double b, final double c) {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}
