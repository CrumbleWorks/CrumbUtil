package org.crumbleworks.forge.crumbutil.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.crumbleworks.forge.crumbutil.util.UrlUtil;
import org.junit.Test;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
public class UrlUtilTest {
    
    public static final Path URLS_FILE_PATH = Paths.get("src/test/resources/com/ianonavy/files/urls.txt");
    public static final String COMMENT_CHAR = "#";

    @Test
    public void testValidURL() throws IOException {
        Files.lines(URLS_FILE_PATH).forEach((s) -> {if(!s.startsWith(COMMENT_CHAR)){assertTrue(UrlUtil.checkIfIsURL(s));}});
    }
}
