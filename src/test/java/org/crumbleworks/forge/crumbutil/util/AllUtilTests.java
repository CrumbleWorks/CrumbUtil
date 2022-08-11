package org.crumbleworks.forge.crumbutil.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Patrick Bächli
 * @since 0.5.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    MathUtilTest.class,
    OSUtilTest.class,
    StringUtilTest.class,
    UrlUtilTest.class,
    VersionComparatorTest.class
})
public class AllUtilTests {}
