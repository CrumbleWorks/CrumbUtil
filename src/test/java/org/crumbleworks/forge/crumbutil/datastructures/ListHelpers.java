package org.crumbleworks.forge.crumbutil.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains helper methods for testing list structures
 * 
 * @author Michael Stocker
 * @since 0.1.0
 */
public class ListHelpers {

    public static String elements = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    /**
     * Creates a {@link List} and fills it with the passed in elements
     * 
     * @param elements the elements for the list to consist of
     * @return the new list
     */
    public static List<Character> createCharacterArrayListWithLoad(String elements) {
        List<Character> list = new ArrayList<Character>(elements.length());
        
        for(Character c : elements.toCharArray()) {
            list.add(c);
        }
        
        return list;
    }
    
    /**
     * Creates a string of the specified length, filled with the following elements, wrapping and beginning again if needed:<BR/>
     * &nbsp;<code>0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ</code>
     * 
     * @param length
     * @return a string of the specified length
     */
    public static String createStringOfNElements(int length) {
        StringBuilder s = new StringBuilder();
        
        for(; length > elements.length() ; length -= elements.length()) {
            s.append(elements);
        }
        
        for(int i = 0; i < length ; i++) {
            s.append(elements.charAt(i));
        }
        
        return s.toString();
    }
}
