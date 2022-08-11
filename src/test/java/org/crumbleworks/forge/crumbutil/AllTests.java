package org.crumbleworks.forge.crumbutil;

import org.crumbleworks.forge.crumbutil.commandline.CommandlineOptionProcessorTest;
import org.crumbleworks.forge.crumbutil.configurations.CrumbHomeTest;
import org.crumbleworks.forge.crumbutil.datastructures.HeadInRingBufferTest;
import org.crumbleworks.forge.crumbutil.datastructures.TailInRingBufferTest;
import org.crumbleworks.forge.crumbutil.util.AllUtilTests;
import org.crumbleworks.forge.crumbutil.validation.ParametersTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Patrick Bächli
 * @since 0.5.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    CommandlineOptionProcessorTest.class,
    CrumbHomeTest.class,
    HeadInRingBufferTest.class,
    TailInRingBufferTest.class,
    AllUtilTests.class,
    ParametersTest.class
})
public class AllTests {}
