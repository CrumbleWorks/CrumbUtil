package org.crumbleworks.forge.crumbutil.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

//TODO add failing tests for proper blackboxing
/**
 * @author Michael Stocker
 * @since 0.2.0
 */
public class MathUtilTest {
    
    @Test
    public void testMin() {
        long[][] in = {{1l, 2l},
                       {5l, 2l},
                       {1236586l, 67l},
                       {786l, 689162031l}};
        long[] out = {1l, 2l, 67l, 786l};
        
        for(int i = 0 ; i < in.length ; i++) {
            assertEquals(out[i], MathUtil.min(in[i][0], in[i][1]));
        }
    }
    
    @Test
    public void testMax() {
        long[][] in = {{1l, 2l},
                       {5l, 2l},
                       {1236586l, 67l},
                       {786l, 689162031l}};
        long[] out = {2l, 5l, 1236586l, 689162031l};
        
        for(int i = 0 ; i < in.length ; i++) {
            assertEquals(out[i], MathUtil.max(in[i][0], in[i][1]));
        }
    }
    
    @Test
    public void testGCD() {
        long[][] in = {{8l, 12l},
                       {8l, 16l},
                       {12l, 15l},
                       {12l, 18l},
                       {24l, 36l},
                       {6l, 15l},
                       {24l, 32l},
                       {30l, 20l},
                       {15l, 75l},
                       {81l, 36l},
                       {24l, 72l},
                       {60l, 36l},
                       {800, 600}
                       };
        long[] out = {4l, 8l, 3l, 6l, 12l, 3l, 8l, 10l, 15l, 9l, 24l, 12l, 200};
        
        for(int i = 0 ; i < in.length ; i++) {
            assertEquals(out[i], MathUtil.gcd(in[i][0], in[i][1]));
        }
    }
    
    @Test
    public void testLCM() {
        long[][] in = {{2l, 6l},
                       {18l, 3l},
                       {12l, 9l},
                       {6l, 10l},
                       {12l, 15l},
                       {4l, 30l},
                       {6l, 15l},
                       {18l, 12l}
                       };
        long[] out = {6l, 18l, 36l, 30l, 60l, 60l, 30l, 36l};
        
        for(int i = 0 ; i < in.length ; i++) {
            assertEquals(out[i], MathUtil.lcm(in[i][0], in[i][1]));
        }
    }
}
