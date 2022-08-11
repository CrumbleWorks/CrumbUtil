package org.crumbleworks.forge.crumbutil.util;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Michael Stocker
 * @since 0.6.6
 */
public class FileUtil {

    private FileUtil() {}
    
    /**
     * Removes directory and all its contents.
     * 
     * @param directory the directory to be removed completely
     * 
     * @throws IllegalArgumentException if the given {@link Path} is **not** a directory
     * @throws IOException
     */
    public static final void deepRemoveFolder(final Path directory) throws IOException {
        deepRemoveFolder(directory, new String[]{});
    }
    
    /**
     * Removes directory and all its contents except the excepted directories.
     * 
     * @param directory the directory to be removed completely
     * @param exceptedDirectories an array of directories that are to persist
     * 
     * @throws IllegalArgumentException if the given {@link Path} is **not** a directory
     * @throws IOException
     */
    public static final void deepRemoveFolder(final Path directory, String[] exceptedDirectories) throws IOException {
        if(!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Not a directory!");
        }
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                for(String exceptedDirectory : exceptedDirectories) {
                    if(exceptedDirectory.toString().equals(dir.getFileName().toString())) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }
                
                return super.preVisitDirectory(dir, attrs);
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.delete(dir);
                } catch (DirectoryNotEmptyException e) {
                    //this is alright, it means that we preserve one or more subdirectories
                }
                
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * Creates a file and all non-existent parent directories
     * 
     * @param file the file to be created
     * 
     * @return the {@link Path} to the created file
     * 
     * @throws IOException if anything goes haywire..
     */
    public static final Path createFileWithAncestors(final Path file) throws IOException {
        if(file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        
        return Files.createFile(file);
    }
}
