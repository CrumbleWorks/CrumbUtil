package org.crumbleworks.forge.crumbutil.datastructures;

import static org.crumbleworks.forge.crumbutil.datastructures.ListHelpers.createCharacterArrayListWithLoad;
import static org.crumbleworks.forge.crumbutil.datastructures.ListHelpers.createStringOfNElements;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.crumbleworks.forge.crumbutil.datastructures.TailInRingBuffer;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public class TailInRingBufferTest {
    
    @Test
    public void constructWithCapacityZero() {
    	assertThrows(IllegalArgumentException.class, () -> new TailInRingBuffer<Integer>(0));
    }

    @Test
    public void constructWithCapacityNegative() {
    	assertThrows(IllegalArgumentException.class, () -> new TailInRingBuffer<Integer>(-7));
    }
    
    @Test
    public void constructWithCapacityPositive() {
        int[] capacities = {1, 2, 4, 64, 23423, 324567543}; //ultimatively these numbers depend on the underlying VMs allocated memory, so no point in going up to Integer.MAX_VALUE
        
        for(int i : capacities) {
            assertTrue(null != new TailInRingBuffer<Object>(i));
        }
    }
    
    @Test
    public void constructFromOtherCollection() {
        int load = 15;
        List<Character> l = createCharacterArrayListWithLoad(createStringOfNElements(load));
        List<Character> ringBuffer = new TailInRingBuffer<Character>(l);
        
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
            
            TailInRingBuffer<Character> ring = new TailInRingBuffer<Character>(createCharacterArrayListWithLoad(elements));
            
            String beforeAdding = elements;
            StringBuilder contents = new StringBuilder();
            
            ring.add('F');
            
            for(Character c : ring) {
                contents.append(c);
            }
            
            assertNotEquals(beforeAdding, contents.toString());
            
            StringBuilder afterAdding = new StringBuilder(beforeAdding.substring(1, elements.length())).append("F");
            
            assertEquals(afterAdding.toString(), contents.toString());
        }
    }
    
    /* ************************************************************************
     * GET
     */
    
    @Test
    public void getRandomElement() {
        String elements = createStringOfNElements(5);
        TailInRingBuffer<Character> ring = new TailInRingBuffer<Character>(createCharacterArrayListWithLoad(elements));
        
        assertEquals((Character)elements.charAt(2) ,ring.get(2));
    }
    
    @Test
    public void getOutsideRange() {
        String elements = createStringOfNElements(5);
        TailInRingBuffer<Character> ring = new TailInRingBuffer<Character>(createCharacterArrayListWithLoad(elements));
        
        assertThrows(IllegalArgumentException.class, () -> ring.get(5)); //size 5; greates index > 4
    }
}
