package org.crumbleworks.forge.crumbutil.datastructures;

import static org.crumbleworks.forge.crumbutil.validation.Parameters.notNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A limited buffer, that will evict the oldest element once it reaches capacity.
 * 
 * @author Michael Stocker
 * @since 0.6.10
 * 
 * @param <E> the element held by this buffer
 */
public class LimitedBuffer<E> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    
    private final Class<E> type;
    private final Class<E[]> arrtype;
    
    private final Object[] buffer;
    /**
     * The oldest element in this buffer
     */
    private int head = 0;
    /**
     * The position at which to write the next element
     */
    private int tail = head;
    /**
     * The amount of elements currently in this buffer
     */
    private int size = 0;
    
    /**
     * Initializes a buffer with the given capacity.
     * 
     * @param capacity the capacity in elements
     * @param type the type of elements stored in this buffer
     */
    public LimitedBuffer(int capacity, Class<E> type, Class<E[]> arrtype) {
        this.type = notNull(type);
        this.arrtype = notNull(arrtype);
        buffer = new Object[capacity];
    }
    
    /**
     * Writes the given element to this buffer.
     * 
     * @param element the element to be written to this buffer
     */
    public void write(final E element) {
        try {
            lock.writeLock().lock();
            
            notNull(element);
            
            if(size == buffer.length
            && head == tail) { //we overlap, shift head ahead
                head = (head + 1) % buffer.length;
                
                size--;
            }
            
            buffer[tail] = element;
            
            //shift tail for next write, break at buffer.length
            tail = (tail + 1) % buffer.length;
            
            size++;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Clears the buffer, effectively emptying it.
     */
    public void clear() {
        try {
            lock.writeLock().lock();
            
            Arrays.fill(buffer, null);
            
            head = 0;
            tail = head;
            
            size = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Reads the last entry from the buffer, if there is anything to read.
     * 
     * @return the stored element or <code>null</code>
     */
    public E read() {
        try {
            lock.readLock().lock();
            
            if(size == 0) {
                return null;
            }
            
            int index = tail - 1;
            if(index < 0) {
                index += size;
            }
            
            return (E) buffer[index];
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Reads up to the last <i>n</i> entries from the buffer.
     * 
     * @param entries the number of entries to read
     * 
     * @return an array of elements with a length between <code>0</code> and
     *         <code>entries</code>
     */
    public E[] read(int entries) {
        try {
            lock.readLock().lock();
            
            if(size == 0) { //empty
                return (E[]) Array.newInstance(type, 0);
            }
            
            int index = tail - entries;
            if(index < 0) {
                index += size;
            }
            
            return (E[]) readFrom(entries,
                                  0,
                                  index, 
                                  tail,
                                  0);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Reads the entry at the supplied index from the buffer.
     * 
     * @param entryIndex the index from which to read
     * 
     * @return the stored element
     * 
     * @throws IndexOutOfBoundsException if the entryIndex is higher than
     *         {@link #getSize() size - 1}
     */
    public E readFrom(int entryIndex) {
        if(entryIndex > (size - 1)) {
            throw new IndexOutOfBoundsException("Cannot read from index "
                                              + entryIndex + ", highest "
                                              + "possible index is "
                                              + (size - 1));
        }
        
        try {
            lock.readLock().lock();
            
            if(size < buffer.length) { //not yet wrapping
                return (E) buffer[entryIndex];
            }
            
            //wrapping
            return (E) buffer[(head + entryIndex) % size];
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Reads up to <i>n</i> entries, starting with the supplied index, from
     * the buffer.
     * 
     * @param entryIndex the index from which to start reading
     * @param entries the number of entries to read
     * 
     * @return an array of elements with a length between <code>0</code> and
     *         <code>entries</code>
     * 
     * @throws IndexOutOfBoundsException if the entryIndex is higher than
     *         {@link #getSize() size - 1}
     */
    public E[] readFrom(int entryIndex, int entries) {
        if(entryIndex > (size - 1)) {
            throw new IndexOutOfBoundsException("Cannot read from index "
                                              + entryIndex + ", highest "
                                              + "possible index is "
                                              + (size - 1));
        }
        
        try{
            lock.readLock().lock();
            
            int endIndex = entryIndex + entries;
            if(endIndex > size) {
                endIndex = size;
            }
            
            return (E[]) readFrom(entries,
                                  head,
                                  entryIndex,
                                  endIndex,
                                  entryIndex);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Retrieves entries from the backing buffer based on the provided
     * parameters.
     * 
     * <p>There are two common prefixes for parameters: <code>top</code>,
     * which denotes parameters that modify the behaviour for retrieving less
     * entries than there are in the buffer, and <code>bot</code>, which
     * denotes parameters that modify the behaviour for retrieving the whole
     * buffer.
     * 
     * @param entries the amount of entries that are requested
     * 
     * @param topIndexOffset offsets the topIndex when less entries are
     *        requested than the backing buffer holds
     * @param topIndex the index from which to start when retrieving less
     *        elements than the buffer contains
     * @param topRange the exclusive range to which elements should be copied
     *        if the buffer is not yet wrapping
     * 
     * @param botIndex the index from which to start when requesting items
     *        equal to the buffer-size or even more; this index is adjusted
     *        by the position of the <code>head</code> internally, so no
     *        additional attention is needed
     * 
     * @return an array of elements with a length between <code>0</code> and
     *         <code>entries</code>
     */
    @SuppressWarnings("unchecked")
    private final E[] readFrom(int entries, int topIndexOffset, int topIndex, int topRange, int botIndex) {
        if(entries < size) { //less entries than size
            if(size < buffer.length) { //buffer not yet full
                return (E[]) Arrays.copyOfRange(buffer, topIndex, topRange, arrtype);
            }
            
            int resultSize = topRange - topIndex;
            if(resultSize < 0) {
                resultSize += size;
            }
            
            E[] res = (E[]) Array.newInstance(type, resultSize);
            for(int i = 0 ; i < res.length ; i++) {
                res[i] = (E) buffer[(topIndexOffset + topIndex + i) % size];
            }
            
            return res;
        }
        
        //entries equal or surpass size
        if(size < buffer.length) { //buffer not yet full
            return (E[]) Arrays.copyOf(buffer, size, arrtype);
        }
        
        int botSize = size - botIndex;
        if(botSize > entries) {
            botSize = entries;
        }
        
        E[] res = (E[]) Array.newInstance(type, botSize);
        for(int i = 0 ; i < res.length ; i++) {
            res[i] = (E) buffer[(head + botIndex + i) % size];
        }
        
        return res;
    }
    
    /**
     * @return the amount of rows containing data
     */
    public int getSize() {
        return size;
    }
    
    /**
     * @return the amount of characters this buffer can hold
     */
    public int getCapacity() {
        return buffer.length;
    }
}
