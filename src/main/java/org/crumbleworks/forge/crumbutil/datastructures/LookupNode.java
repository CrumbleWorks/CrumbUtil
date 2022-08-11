package org.crumbleworks.forge.crumbutil.datastructures;

import static org.crumbleworks.forge.crumbutil.validation.Parameters.notNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Allows creating simple lookup trees.
 * 
 * <p>A lookup tree starts with a single node at it's root. Then add keys as needed with {@link #put(String, Object)}.
 * 
 * <p>To get values call {@link #resolve(String, boolean)}.
 * 
 * @author Michael Stocker
 * @since 0.6.9
 * 
 * @param <V> the type of the associated values
 */
public final class LookupNode<V> {
    private final Map<Character, LookupNode<V>> childNodes;

    private V value;
    private TreeSet<V> possibleValues;
    
    public LookupNode() {
        childNodes = new HashMap<>(1, 1.00f);
    }
    
    public LookupNode(final V value) {
        this.value = notNull(value);
        childNodes = new HashMap<>(0, 1.00f);
    }
    
    /* **********************************************************************
     * OPERATIONS ON NODE
     */
    
    /**
     * Sets the value for this node.
     * 
     * @param value the new value to be assigned to this node
     * 
     * @return the old value assigned to this node or <code>null</code> if no value was assigned
     */
    public final V setValue(final V value) {
        V ret = this.value;
        this.value = notNull(value);
        return ret;
    }
    
    /**
     * Gets the value for this node.
     * 
     * @return the value assigned to this node or <code>null</code> if no value was assigned
     */
    public final V getValue() {
        return value;
    }
    
    //TODO add method to get possible keys
    
    /**
     * Retrieves a set of all values that are further down the tree.
     * 
     * @return an unmodifiable set with all the values further down the tree.
     */
    public final Set<V> getPossibleValues() {
        synchronized(childNodes) {
            if(possibleValues == null) {
                TreeSet<V> values = new TreeSet<>();
                
                traverseNodesToCollectValues(values, this);
                
                possibleValues = values;
            }
            
            return Collections.unmodifiableSortedSet(possibleValues);
        }
    }
    
    //TODO evaluate performance and consider multithreading
    /**
     * Traverses nodes and their children and adds any value found to the given set.
     * 
     * @param values the set to add every found value to
     * @param node the next node to traverse
     */
    private final void traverseNodesToCollectValues(final Set<V> values, final LookupNode<V> node) {
        synchronized(node.childNodes) {
            if(node.value != null) {
                values.add(node.value);
            }
            
            for(LookupNode<V> childNode : node.childNodes.values()) {
                traverseNodesToCollectValues(values, childNode);
            }
        }
    }
    
    /* **********************************************************************
     * OPERATIONS ON TREE
     */
    
    /**
     * Adds a new value.
     * 
     * <p>This method will chip off the first <code>char</code> of the given key and then pass it further down the path.
     * 
     * <p>This operation will force this node and any child-node further down the path of the given key to recalculate their sets of possible values.
     * 
     * @param key the key to be added
     * @param v the value to be added
     * 
     * @return the previous value associated with this key or <code>null</code> if no value was associated previously
     */
    public final V put(final String key, final V v) {
        synchronized(childNodes) {
            V prevValue = null;
            char c = notNull(key).charAt(0);
            
            if(key.length() == 1) {
                //reached last element of key
                if(childNodes.containsKey(c)) {
                    prevValue = childNodes.get(c).setValue(v);
                } else {
                    childNodes.put(c, new LookupNode<>(v));
                }
            } else {
                //key has still more elements
                if(!childNodes.containsKey(c)) {
                    childNodes.put(c, new LookupNode<>());
                }
                
                childNodes.get(c).put(key.substring(1), v);
            }
            
            possibleValues = null;
            return prevValue;
        }
    }
    
    /**
     * Resolves the supplied key against this tree.
     * 
     * <p>If the <code>partial</code> flag is set, this method will treat the supplied key as a partial-key and thus try looking further along a straight path as described in {@link LookupNode#explore(char)}.
     * 
     * @param key the key or partial-key to be resolved
     * @param partial tells the method to explore further if the supplied key has no associated value
     * 
     * @return a {@link LookupResult} or <code>null</code> if the supplied key cannot be fully resolved
     */
    public final LookupResult<V> resolve(final String key, boolean partial) {
        synchronized(childNodes) {
            //we're technically abusing the lookup result here
            LookupResult<V> lookupResult = findNode(key, this);
            
            //if we found our node the key in the result should be 1 char long
            if(lookupResult != null && lookupResult.getKey().length() == 1) {
                if(partial) {
                    if(lookupResult.getNode().getPossibleValues().size() > 1
                    || (lookupResult.getNode().getPossibleValues().size() == 1 && lookupResult.getNode().getValue() != null)) {
                        return new LookupResult<>(key, lookupResult.getNode());
                    }
                    
                    return traverseNodesStraight(
                            new StringBuilder(lookupResult.getNode().childNodes.keySet().iterator().next()),
                            lookupResult.getNode().childNodes.values().iterator().next());
                }
                
                if(lookupResult.getNode().getValue() != null) {
                    //got a value, no more exploring
                    return new LookupResult<>(key, lookupResult.getNode());
                }
            }
            
            //could not finish looking up key
            return null;
        }
    }
    
    /**
     * Traverses nodes until it either finds the node associated with the key or hits a dead-end.
     * 
     * @param key the key representing the path to the node
     * @param node the node on which to start looking
     * 
     * @return a {@link LookupResult} or <code>null</code> if the key cannot be found
     */
    private final LookupResult<V> findNode(final String key, final LookupNode<V> node) {
        synchronized(childNodes) {
            char c = notNull(key).charAt(0);
            
            if(node.childNodes.containsKey(c)) {
                if(key.length() == 1) {
                    //reached last element of key
                    return new LookupResult<>(key, node.childNodes.get(c));
                }
                
                //key has still more elements
                return findNode(
                        key.substring(1),
                        node.childNodes.get(c));
            }
            
            return null;
        }
    }
    
    /**
     * Traverses child-nodes starting with the given <code>char</code> until either:<BR>
     * - a child-node has a value assigned<BR>
     * - a child-node has multiple child-nodes
     * 
     * @param c the child-node from which to start searching
     * 
     * @return a {@link LookupResult} or <code>null</code> if there's no match along the branch
     */
    public final LookupResult<V> explore(char c) {
        synchronized(childNodes) {
            if(!childNodes.containsKey(c)) {
                //has no matching child-node > null
                return null;
            }
            
            return traverseNodesStraight(new StringBuilder(c), childNodes.get(c));
        }
    }
    
    /**
     * Traverses a straight line of nodes until either a dead-end or a fork.
     * 
     * @param s a {@link StringBuilder} containing the traversed key-segment
     * @param node the next node to check
     */
    private final LookupResult<V> traverseNodesStraight(final StringBuilder s, final LookupNode<V> node) {
        synchronized(childNodes) {
            if(node.getValue() != null) {
                //has value
                return new LookupResult<>(s.toString(), node);
            }
            
            if(node.getPossibleValues().size() > 1) {
                //multiple branches
                return new LookupResult<>(s.toString(), node);
            }
            
            return traverseNodesStraight(
                    s.append(node.childNodes.keySet().iterator().next()),
                    node.childNodes.values().iterator().next());
        }
    }
    
    /* **********************************************************************
     * HELPERS
     */
    
    /**
     * Represents the result of a lookup, consisting of a {@link LookupNode} and a corresponding {@link String}.
     * 
     * @author Michael Stocker
     * @since 0.6.9
     * 
     * @param <V> the type of the value of the lookup-node
     */
    public static final class LookupResult<V> {
        private final String key;
        private final LookupNode<V> node;
        
        private LookupResult(final String key, final LookupNode<V> node) {
            this.key = notNull(key);
            this.node = notNull(node);
        }
        
        public final String getKey() {
            return key;
        }
        
        public final LookupNode<V> getNode() {
            return node;
        }
    }
}
