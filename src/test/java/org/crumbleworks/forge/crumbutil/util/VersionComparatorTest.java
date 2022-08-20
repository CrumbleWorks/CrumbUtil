package org.crumbleworks.forge.crumbutil.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

//TODO add more tests and try to break the thing
/**
 * @author Michael Stocker
 * @since 0.1.0
 */
public class VersionComparatorTest {
    
    @Test
    public void compareVersions() throws IOException {
        VersionComparator vc = new VersionComparator();
        
        //Normal
        assertEquals(-1, vc.compare("0.1.2", "1.0"));
        assertEquals(0, vc.compare("1", "1.0.0"));
        assertEquals(1, vc.compare("1.0", "0.1.2"));
        
        //Alpha, Beta
        assertEquals(-1, vc.compare("1.0a", "1.0"));
        assertEquals(0, vc.compare("1a", "1.0.0a"));
        assertEquals(1, vc.compare("0.1.2", "0.1.1a"));
        assertEquals(-1, vc.compare("0.1.1a", "0.1.1b"));
        assertEquals(1, vc.compare("0.1.1a", "0.1.0b"));
        
        //SNAPSHOT
        assertEquals(-1, vc.compare("1.2-SNAPSHOT", "1.2"));
        assertEquals(0, vc.compare("1.0-SNAPSHOT", "1.0.0-SNAPSHOT"));
        assertEquals(1, vc.compare("1.3", "1.3-SNAPSHOT"));
        assertEquals(-1, vc.compare("1.2", "1.3-SNAPSHOT"));
        assertEquals(1, vc.compare("1.4-SNAPSHOT", "1.3"));
    }
}