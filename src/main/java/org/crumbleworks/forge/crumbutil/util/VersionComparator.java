package org.crumbleworks.forge.crumbutil.util;

import static org.crumbleworks.forge.crumbutil.validation.Parameters.verifyAgainstRegex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A comparator to compare version number strings according to the following rules:
 * <ul>
 *     <li>Versionnumbers are tokenized using a '.' (dot) as delimited</li>
 *     <li>Tokens are stronger from first to last (left to right)</li>
 *     <li>Trailing zero tokens ('0') are ignored</li>
 *     <li>Marked builds are always weaker than unmarked builds</li>
 *     <li>Marked builds are ordered as follows (strongest to weakest):
 *         <ol>
 *             <li>-stable</li>
 *             <li>-Beta, b</li>
 *             <li>-Alpha, a</li>
 *             <li>-SNAPSHOT</li>
 *             <li>-nightly</li>
 *             <li><i>unknown mark</i></li>
 *         </ol>
 *     </li>
 *     <li>Marks are compared case insensitive</li>
 * </ul>
 * Example for valid versionnumbers:
 * <ul>
 *     <li>1.0.23</li>
 *     <li>0.9-SNAPSHOT</li>
 *     <li>4.32.111a</li>
 * </ul>
 * 
 * @author Michael Stocker
 * @since 0.1.0
 */
public final class VersionComparator implements Comparator<String> {
    
    /**
     * Acquired from Stackoverflow: <a href="http://stackoverflow.com/a/8270824/2889776">http://stackoverflow.com/a/8270824/2889776</a>
     * @author Qtax
     */
    public static final String NUMBER_LETTER_SPLITTER = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";
    
    public static final String VERSION_REGEX = "^(?:[\\d]+\\.)*(?:[\\d]+)(?:[\\-][A-Za-z]+)?[A-Za-z]?$";
    public static final String VERSION_DELIM = "\\."; //regex format, thus we need to make sure it's handled as a literal '.'
    public static final Map<String, Integer> WEIGHTED_TOKEN_MAP;
    static {
        @SuppressWarnings("serial")
        Map<String, Integer> tmpWeightedTokenMap = new HashMap<String, Integer>() {{
            put("-stable", 100);
            put("-Beta", 60);
            put("b", 60);
            put("-Alpha", 50);
            put("a", 50);
            put("-SNAPSHOT", 20);
            put("-nightly", 10);
        }};
        WEIGHTED_TOKEN_MAP = Collections.unmodifiableMap(tmpWeightedTokenMap);
    }
    
    private static final String ZERO_STRING = "0";

    @Override
    public final int compare(final String o1, final String o2) {
        //1. split strings up at each '.'
        List<String> o1_ = new ArrayList<String>(Arrays.asList(verifyAgainstRegex(o1, VERSION_REGEX).split(VERSION_DELIM)));
        List<String> o2_ = new ArrayList<String>(Arrays.asList(verifyAgainstRegex(o2, VERSION_REGEX).split(VERSION_DELIM)));
        
        //2. run last element of both arrays against letter splitter
        String[] tmp_;
        String o1Mark = (tmp_ = o1_.get(o1_.size() - 1).split(NUMBER_LETTER_SPLITTER)).length > 1 ? tmp_[1] : null;
        o1_.set(o1_.size() - 1, tmp_[0]);
        String o2Mark = (tmp_ = o2_.get(o2_.size() - 1).split(NUMBER_LETTER_SPLITTER)).length > 1 ? tmp_[1] : null;
        o2_.set(o2_.size() - 1, tmp_[0]);
        
        //3. fill up the shorter one with 0s
        if(o1_.size() > o2_.size()) { //o1 is at least 1 item longer than o2
            for(int i = o1_.size() - o2_.size();i > 0;i--) {
                o2_.add(ZERO_STRING);
            }
        } else if(o2_.size() > o1_.size()) { //o2 is at least 1 item longer than o1
            for(int i = o2_.size() - o1_.size();i > 0;i--) {
                o1_.add(ZERO_STRING);
            }
        }
        
        //4. preps done, lets compare them
        for(int i = 0 ; i < o1_.size() ; i++) { //both have same length so doesna matter
            int res;
            if((res = Integer.compare(Integer.parseInt(o1_.get(i)), Integer.parseInt(o2_.get(i)))) != 0) {
                return res;
            }
        }
        
        //5. still not done, lets compare marks
        boolean o1HasMark = StringUtil.neitherNullNorEmpty(o1Mark);
        boolean o2HasMark = StringUtil.neitherNullNorEmpty(o2Mark);
        
        if(o1HasMark && !o2HasMark) { //o1 marked, o2 not
            return -1;
        } else if(!o1HasMark && o2HasMark) { //o2 marked, o1 not
            return 1;
        } else if(o1HasMark && o2HasMark) { // both marked
            //get mark weights
            Integer tmp;
            Integer o1MarkWeight = (tmp = WEIGHTED_TOKEN_MAP.get(o1Mark)) != null ? tmp : 0;
            Integer o2MarkWeight = (tmp = WEIGHTED_TOKEN_MAP.get(o2Mark)) != null ? tmp : 0;
            
            //compare weights
            return Integer.compare(o1MarkWeight, o2MarkWeight);
        }
        //no marks...
        return 0;
    }
}