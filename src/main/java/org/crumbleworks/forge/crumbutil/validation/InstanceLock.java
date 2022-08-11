package org.crumbleworks.forge.crumbutil.validation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Designed to prevent an application or a sub-part of it from running multiple instances.
 * 
 * @author Michael Stocker
 * @since 0.6.8
 */
public final class InstanceLock {
    private static final Path tmpdir = Paths.get(System.getProperty("java.io.tmpdir"));

    private final Path lockFilePath;
    
    private final RandomAccessFile lockFile;
    private final FileLock lock;
    private final FileChannel lockChannel;
    
    private InstanceLock(String name) throws InstanceLockException {
        try {
            lockFilePath = tmpdir.resolve(Paths.get(name + ".lock"));
            
            lockFile = new RandomAccessFile(lockFilePath.toFile(), "rw");
            lockChannel = lockFile.getChannel();
            
            lock = lockChannel.tryLock();
            if(lock == null) {
                //could not acquire lock, other process holds lock, time to go
                throw new InstanceLockException("Lock has already been acquired by another process.");
            }
        } catch(IOException e) {
            throw new InstanceLockException("Failed while trying to acquire lock: ", e);
        }
    }
    
    /**
     * Releases the lock so it can be acquired again.
     * 
     * @throws InstanceLockException if there's issues while releasing the lock.
     */
    public final void release() throws InstanceLockException {
        try {
            lock.release();
            lockChannel.close();
            lockFile.close();
            Files.deleteIfExists(lockFilePath);
        } catch(IOException e) {
            throw new InstanceLockException("Failed while trying to release lock: ", e);
        }
    }
    
    /* **********************************************************************
     * FACTORY
     */
    
    /**
     * Acquires a new lock with the provided name.
     * 
     * @param name the name of the lock
     * 
     * @return an instance for the acquire lock
     * 
     * @throws InstanceLockException if there's issues while acquiring the lock or if the lock has already been acquired.
     */
    public static InstanceLock acquire(String name) throws InstanceLockException {
        return new InstanceLock(name);
    }
}
