package org.crumbleworks.forge.crumbutil.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class AutocompleteDictionaryTest {
    
    @Test
    public void testLookup() {
        AutocompleteDictionary adic = new AutocompleteDictionary();
        
        adic.add(new ArrayList<String>() {{
            add("Peter");
            add("Pneu");
            add("Polobär");
            add("Velo");
            add("Vakuumpumpe");
            add("Adalbert");
            add("Apfelbrand");
            add("Apfel");
            add("Halhalhalhalhal");
            add("Hallo");
        }});
        
        assertThat(adic.lookup("Halhalhalhalhal"), hasItem("Halhalhalhalhal"));
        assertEquals(adic.lookup("Halhalhalhalhal").size(), 1);
        assertEquals(adic.lookup("Halhal").size(), 1);
        assertEquals(adic.lookup("Hal").size(), 2);
        
        assertThat(adic.lookup("Peter"), hasItem("Peter"));
        assertEquals(adic.lookup("Peter").size(), 1);
        
        assertThat(adic.lookup("P"), hasItems("Peter", "Pneu", "Polobär"));
        assertEquals(adic.lookup("P").size(), 3);
        
        assertTrue(adic.lookup("F").isEmpty());
        assertEquals(adic.lookup("F").size(), 0);
        
        assertThat(adic.lookup("Apfel"), hasItems("Apfel", "Apfelbrand"));
        assertEquals(adic.lookup("Apfel").size(), 2);
        
        assertThat(adic.lookup("A"), hasItems("Adalbert", "Apfel", "Apfelbrand"));
        assertEquals(adic.lookup("A").size(), 3);
        
        assertThat(adic.lookup("V"), hasItems("Velo", "Vakuumpumpe"));
        assertEquals(adic.lookup("V").size(), 2);
        
        adic.add("Mango");
        adic.add("Yoghurt");
        
        assertThat(adic.lookup("Mango"), hasItem("Mango"));
        assertEquals(adic.lookup("Mango").size(), 1);

        assertThat(adic.lookup("Ma"), hasItem("Mango"));
        assertEquals(adic.lookup("Ma").size(), 1);
        
        assertThat(adic.lookup("Yoghu"), hasItem("Yoghurt"));
        assertEquals(adic.lookup("Yoghu").size(), 1);
        
        adic.add("Zirkel");
        
        assertThat(adic.lookup("Zir"), hasItem("Zirkel"));
        
        assertThat(adic.lookup("A"), hasItems("Adalbert", "Apfel", "Apfelbrand"));
        assertEquals(adic.lookup("A").size(), 3);
        
        adic.add("Veronika");
        
        assertThat(adic.lookup("V"), hasItems("Velo", "Vakuumpumpe", "Veronika"));
        assertEquals(adic.lookup("V").size(), 3);
        
        assertThat(adic.lookup("Ve"), hasItems("Velo", "Veronika"));
        assertEquals(adic.lookup("Ve").size(), 2);
    }
}