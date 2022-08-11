
package org.crumbleworks.forge.crumbutil.datastructures;

import static org.crumbleworks.forge.crumbutil.validation.Parameters.greaterOrEqual;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

/**
 * Creates a fixed size, circular {@link List}. New elements are added at the beginning, the oldest element gets dropped.
 * <p>Order of the list is youngest to oldest, <code>0</code> to <code>{@linkplain #size()} -1</code>
 * <p>This class offers:
 * <ul>
 *     <li>add(E)</li>
 *     <li>get(int)</li>
 *     <li>size()</li>
 * </ul>
 * 
 * @see TailInRingBuffer
 * 
 * @author Michael Stocker
 * @since 0.1.0
 *
 * @param <E> the type of elements in this list
 */
public class HeadInRingBuffer<E> extends AbstractList<E> {

    private final int capacity;
    private int size;
    
    /*
     * The current offset for the indexes
     */
    int offset = 0;

    private transient final Object[] data;
    
    /**
     * Constructs an empty ringbuffer with the specified capacity
     * 
     * @param capacity the capacity of the ringbuffer
     * 
     * @throws IllegalArgumentException if the specified capacity is negative or zero
     */
    public HeadInRingBuffer(int capacity) {
        if(capacity > 0) {
            this.capacity = capacity;
            this.data = new Object[capacity];
            this.size = 0;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + capacity);
        }
    }
    
    /**
     * Constructs an ringbuffer containing the elements of the specified collection, in the order they are returned by the collection's iterator.
     * <p>And with a capacity equal to the size of the specified collection.
     * 
     * @param c the collection whose elements are to be placed in this ringbuffer
     * @throws IllegalArgumentException if the specified collection is empty
     * @throws NullPointerException if the specified collection is <code>null</code>
     */
    public HeadInRingBuffer(Collection<E> c) {
        Object[] cArr = c.toArray();
        
        if((size = cArr.length) > 0) {
            this.capacity = size;
            data = new Object[capacity];
            
            int dataCursor = 0;
            int cArrCursor = size - 1;
            for( ; dataCursor < capacity ; dataCursor++, cArrCursor--) {
                data[dataCursor] = cArr[cArrCursor];
            }
            System.out.println();
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + size);
        }
    }
    
    @Override
    public int size() {
        return size;
    }
    
    /**
     * Returns the capacity of this ringbuffer
     * 
     * @return the capacity of this ringbuffer
     */
    public int capacity() {
        return capacity;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E get(int index) {
        return (E)data[(internalIndex(-(indexInsideBounds(greaterOrEqual(index, 0)) - (size - 1))))];
    }
    
    @Override
    public boolean add(E element) {
        if(size < capacity) {
            data[internalIndex(++size - 1)] = element;
            return true;
        }
        
        offset++;
        data[internalIndex(size -1)] = element;
        return true;
    }

    /*
     * conversion method to map an index to the index of the internal array
     */
    private final int internalIndex(int i) {
        int res;
        return (res = i + offset) >= capacity ? res - capacity : res;
    }
    
    /*
     * check index inside bounds
     */
    private final int indexInsideBounds(int i) {
        if(i < size) {
            return i;
        }
        
        throw new IllegalArgumentException("Index " + i + " is outside bounds.");
    }
}
