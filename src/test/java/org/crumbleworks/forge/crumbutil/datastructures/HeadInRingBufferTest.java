package org.crumbleworks.forge.crumbutil.datastructures;

import static org.crumbleworks.forge.crumbutil.datastructures.ListHelpers.createCharacterArrayListWithLoad;
import static org.crumbleworks.forge.crumbutil.datastructures.ListHelpers.createStringOfNElements;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.crumbleworks.forge.crumbutil.datastructures.HeadInRingBuffer;
import org.junit.Test;

@SuppressWarnings("unused")
public class HeadInRingBufferTest {
    
    @Test(expected = IllegalArgumentException.class)
    public void constructWithCapacityZero() {
        List<Integer> ringBuffer = new HeadInRingBuffer<Integer>(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithCapacityNegative() {
        List<Integer> ringBuffer = new HeadInRingBuffer<Integer>(-7);
    }
    
    @Test
    public void constructWithCapacityPositive() {
        int[] capacities = {1, 2, 4, 64, 23423, 324567543}; //ultimatively these numbers depend on the underlying VMs allocated memory, so no point in going up to Integer.MAX_VALUE
        
        for(int i : capacities) {
            assertTrue(null != new HeadInRingBuffer<Integer>(i));
        }
    }
    
    @Test
    public void constructFromOtherCollection() {
        int load = 15;
        List<Character> l = createCharacterArrayListWithLoad(createStringOfNElements(load));
        List<Character> ringBuffer = new HeadInRingBuffer<Character>(l);
        
        assertEquals(load, ringBuffer.size());
        assertEquals(l, ringBuffer);
    }
    
    /* ************************************************************************
     * ADD
     */
    @Test
    public void addElement() {
        int[] numElements = {1, 2, 3, 5, 6, 34, 234, 66354, 2345764};
        
        for(int i : numElements) {
            String elements = createStringOfNElements(i);
            
            HeadInRingBuffer<Character> ring = new HeadInRingBuffer<Character>(createCharacterArrayListWithLoad(elements));
            
            String beforeAdding = elements;
            StringBuilder contents = new StringBuilder();
            
            ring.add('F');
            
            for(Character c : ring) {
                contents.append(c);
            }
            
            assertNotEquals(beforeAdding, contents.toString());
            
            StringBuilder afterAdding = new StringBuilder(beforeAdding.substring(0, (elements.length() - 1))).reverse().append("F").reverse();
            
            assertEquals(afterAdding.toString(), contents.toString());
        }
    }
    
    /* ************************************************************************
     * GET
     */
    
    @Test
    public void getRandomElement() {
        String elements = createStringOfNElements(7);
        HeadInRingBuffer<Character> ring = new HeadInRingBuffer<Character>(createCharacterArrayListWithLoad(elements));
        
        assertEquals((Character)elements.charAt(4) ,ring.get(4));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void getOutsideRange() {
        String elements = createStringOfNElements(5);
        HeadInRingBuffer<Character> ring = new HeadInRingBuffer<Character>(createCharacterArrayListWithLoad(elements));
        
        ring.get(5); //size 5; greates index > 4
    }
}
