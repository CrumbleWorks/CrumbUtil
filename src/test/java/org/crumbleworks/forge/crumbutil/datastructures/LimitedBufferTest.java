package org.crumbleworks.forge.crumbutil.datastructures;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author Michael Stocker
 * @since 0.6.17
 */
public class LimitedBufferTest {

    @Test
    public void testCapacityAfterCreation() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        assertEquals(5, buff.getCapacity());
    }
    
    /* **********************************************************************
     * SIZE
     */
    
    @Test
    public void testSizeAfterCreation() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        assertEquals(0, buff.getSize());
    }
    
    @Test
    public void testSizeAfterWriting() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        
        assertEquals(2, buff.getSize());
    }
    
    @Test
    public void testSizeAfterClear() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.clear();
        
        assertEquals(0, buff.getSize());
    }
    
    @Test
    public void testSizeAfterWriteAndClear() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        
        buff.clear();
        
        assertEquals(0, buff.getSize());
    }
    
    @Test
    public void testSizeAfterWriteAndRead() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        
        buff.read();
        
        assertEquals(2, buff.getSize());
    }
    
    @Test
    public void testSizeAfterCircularOverlap() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        buff.write(5);
        buff.write(667);
        buff.write(99);
        
        buff.write(0);
        buff.write(13);
        buff.write(43);
        
        assertEquals(5, buff.getSize());
    }
    
    /* **********************************************************************
     * READ & WRITE
     */
    
    //EMPTY
    @Test
    public void testRead_Empty() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        assertNull(buff.read());
    }
    
    @Test
    public void testReadMany_Empty() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        assertArrayEquals(new Integer[]{}, buff.read(2));
        assertArrayEquals(new Integer[]{}, buff.read(4));
        assertArrayEquals(new Integer[]{}, buff.read(11));
    }
    
    @Test
    public void testReadFrom0_Empty() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        assertThrows(IndexOutOfBoundsException.class, () -> buff.readFrom(0));
    }
    
    @Test
    public void testReadFrom1_Empty() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        assertThrows(IndexOutOfBoundsException.class, () -> buff.readFrom(1));
    }
    
    @Test
    public void testReadManyFrom0_Empty() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        assertThrows(IndexOutOfBoundsException.class, () -> buff.readFrom(0, 2));
    }
    
    @Test
    public void testReadManyFrom1_Empty() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        assertThrows(IndexOutOfBoundsException.class, () -> buff.readFrom(1, 2));
    }
    
    //WRITTEN
    @Test
    public void testReadAfterWrite_single() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(19);
        buff.write(14);
        buff.write(7);
        
        assertEquals(7, (int)buff.read());
    }
    
    @Test
    public void testReadAfterWrite_many() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(19);
        buff.write(14);
        buff.write(7);
        
        assertArrayEquals(new Integer[]{14, 7}, buff.read(2));
        assertArrayEquals(new Integer[]{19, 14, 7}, buff.read(3));
        assertArrayEquals(new Integer[]{19, 14, 7}, buff.read(7));
    }
    
    @Test
    public void testReadAfterWrite_singleFrom() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(19);
        buff.write(14);
        buff.write(7);
        
        assertEquals(14, (int)buff.readFrom(1));
        assertEquals(19, (int)buff.readFrom(0));
        assertEquals(7, (int)buff.readFrom(2));
    }
    
    @Test
    public void testReadAfterWrite_manyFrom() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(19);
        buff.write(14);
        buff.write(7);
        
        assertArrayEquals(new Integer[]{19, 14}, buff.readFrom(0, 2));
        assertArrayEquals(new Integer[]{14, 7}, buff.readFrom(1, 2));
        assertArrayEquals(new Integer[]{19, 14, 7}, buff.readFrom(0, 3));
        assertArrayEquals(new Integer[]{7}, buff.readFrom(2, 1));
    }
    
    //circular-overlap
    @Test
    public void testReadAfterWriteCircularOverlap_single() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        buff.write(5);
        
        buff.write(667);
        buff.write(99);
        
        buff.write(0);
        buff.write(13);
        buff.write(43);
        
        assertEquals(43, (int)buff.read());
    }
    @Test
    public void testReadAfterWriteCircularOverlap_single_specialCases() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        buff.write(5);
        buff.write(667);
        
        assertEquals(667, (int)buff.read());
        
        buff.write(88); //wraparound
        
        assertEquals(88, (int)buff.read());

        buff.write(866);
        
        assertEquals(866, (int)buff.read());
    }
    
    @Test
    public void testReadAfterWriteCircularOverlap_many() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        buff.write(5);
        
        buff.write(667);
        buff.write(99);
        
        buff.write(0);
        buff.write(13);
        buff.write(43);
        
        assertArrayEquals(new Integer[]{13, 43}, buff.read(2));
        assertArrayEquals(new Integer[]{99, 0, 13, 43}, buff.read(4));
        assertArrayEquals(new Integer[]{43}, buff.read(1));
        assertArrayEquals(new Integer[]{667, 99, 0, 13, 43}, buff.read(5));
        assertArrayEquals(new Integer[]{667, 99, 0, 13, 43}, buff.read(13));
    }
    
    @Test
    public void testReadAfterWriteCircularOverlap_singleFrom() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        buff.write(5);
        
        buff.write(667);
        buff.write(99);
        
        buff.write(0);
        buff.write(13);
        buff.write(43);
        
        assertEquals(99, (int)buff.readFrom(1));
        assertEquals(0, (int)buff.readFrom(2));
        assertEquals(43, (int)buff.readFrom(4));
    }
    
    @Test
    public void testReadAfterWriteCircularOverlap_manyFrom() {
        LimitedBuffer<Integer> buff = new LimitedBuffer<>(5, Integer.class, Integer[].class);
        
        buff.write(2);
        buff.write(77);
        buff.write(5);
        
        buff.write(667);
        buff.write(99);
        
        buff.write(0);
        buff.write(13);
        buff.write(43);
        
        assertArrayEquals(new Integer[]{667, 99}, buff.readFrom(0, 2));
        assertArrayEquals(new Integer[]{99, 0}, buff.readFrom(1, 2));
        assertArrayEquals(new Integer[]{667, 99, 0, 13, 43}, buff.readFrom(0, 5));
        assertArrayEquals(new Integer[]{0, 13, 43}, buff.readFrom(2, 3));
        
        assertArrayEquals(new Integer[]{0, 13, 43}, buff.readFrom(2, 5));
    }
}