package org.crumbleworks.forge.crumbutil.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.crumbleworks.forge.crumbutil.datastructures.LookupNode;
import org.crumbleworks.forge.crumbutil.datastructures.LookupNode.LookupResult;

/**
 * A dictionary of strings and associated values that facilitates easy lookup without knowing the keys.
 * 
 * @author Michael Stocker
 * @since 0.6.9
 */
public class AutocompleteDictionary {
    private final LookupNode<String> sourceNode;
    
    public AutocompleteDictionary() {
        sourceNode = new LookupNode<>();
    }
    
    public AutocompleteDictionary add(final String term) {
        sourceNode.put(term, term);
        
        return this;
    }
    
    public AutocompleteDictionary add(final Collection<String> terms) {
        for(String term : terms) {
            add(term);
        }
        
        return this;
    }
    
    /**
     * Looks up the term or partial term and returns a set with <i>0</i> to <i>n</i> terms.
     * 
     * @param term the term or partial term to be looked up
     * 
     * @return an unmodifiable set with <i>0</i> to <i>n</i> terms
     */
    public Set<String> lookup(String term) {
        LookupResult<String> lookupResult = sourceNode.resolve(term, true);
        if(lookupResult != null) {
            return Collections.unmodifiableSet(lookupResult.getNode().getPossibleValues());
        } else {
            return Collections.unmodifiableSet(Collections.emptySet());
        }
    }
}
