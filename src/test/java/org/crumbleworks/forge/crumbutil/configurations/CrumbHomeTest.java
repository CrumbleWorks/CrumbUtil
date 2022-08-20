package org.crumbleworks.forge.crumbutil.configurations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.crumbleworks.forge.crumbutil.configurations.CrumbHome.AppHome;
import org.crumbleworks.forge.crumbutil.util.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
@ExtendWith({MockitoExtension.class, SystemStubsExtension.class})
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({System.class, CrumbHome.class})
//@PowerMockIgnore({"javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*",
//    "org.xml.*", "org.w3c.dom.*", "com.sun.org.apache.xalan.*", "javax.activation.*"})
public class CrumbHomeTest {

    private static String TEST_DIR = Paths.get("").toAbsolutePath().toString() + File.separator + "tests";
    
    private static String USER_HOME_SYSTEM_PROPERTY_KEY = "user.home";
    private static String USER_HOME_TEST_PATH = TEST_DIR; //the modified user.home for the test
    
    private static String DEFAULT_HOME_TEST_PATH = TEST_DIR + File.separator + ".crumbleworks"; //where the CrumbHome should point to per default settings
    
    private static String CRUMBLEWORKS_HOME_ENV_VAR = "CRUMBLEWORKS_HOME";
    private static String CRUMBLEWORKS_HOME_TEST_PATH = TEST_DIR + File.separator + "keks_Haus"; //an alternate CrumbHome location provided using the envVar configuration

    //[A-Za-z0-9-_]+ (see CrumbHome.CRUMBLEWORKS_APP_NAME_REGEX)
    private static String[] validAppNames = {"Warrior2D", "Crumb2Crumb", "CrumbUtil", "CrumbEngine", "CrumbGame-Alpha", "graPHity", "Hindenburg", "Dont_Die"};
    private static String[] invalidAppNames = {"Peter, bist du da?", "Als Joghurt fliegen lernte", "?!?", ". I'm not fat, just <bold>!"};
    
    @SystemStub
    private EnvironmentVariables environmentVariables;
    @SystemStub
    private SystemProperties systemProperties;
    
    @BeforeEach
    public void everytimeSetup() throws IOException {
        Files.createDirectories(Paths.get(USER_HOME_TEST_PATH));
        
        systemProperties.set(USER_HOME_SYSTEM_PROPERTY_KEY, USER_HOME_TEST_PATH);
    }

    @AfterEach
    public void everytimeCleanup() throws IOException {
        FileUtil.deepRemoveFolder(Paths.get(USER_HOME_TEST_PATH));
    }
    
    @Test
    public void getAppHomeWithoutEnvVarSetWithValidAppNames() throws IOException {
        //return 'null' when requesting the environment variable, simulating it not being there
        when(System.getenv(CRUMBLEWORKS_HOME_ENV_VAR)).thenReturn(null);

        AppHome appHome = null;
        
        for(String appName : validAppNames) {
            //check creating the appHome from clean-slate
            appHome = CrumbHome.get(appName);
            assertEquals(appName, appHome.getApp());
            assertEquals(Paths.get(DEFAULT_HOME_TEST_PATH + File.separator + appName), appHome.getPath());
            assertEquals(appName + "@" + Paths.get(DEFAULT_HOME_TEST_PATH) + File.separator + appName, appHome.toString());
            
            //repeat, this time retrieving the just created appHome
            appHome = CrumbHome.get(appName);
            assertEquals(appName, appHome.getApp());
            assertEquals(Paths.get(DEFAULT_HOME_TEST_PATH + File.separator + appName), appHome.getPath());
            assertEquals(appName + "@" + Paths.get(DEFAULT_HOME_TEST_PATH) + File.separator + appName, appHome.toString());
        }
    }
    
    @Test
    public void getAppHomeWithEnvVarSetWithValidAppNames() throws IOException {
        //return the test path when requesting the environment variable, simulating it being set
        when(System.getenv(CRUMBLEWORKS_HOME_ENV_VAR)).thenReturn(CRUMBLEWORKS_HOME_TEST_PATH);

        AppHome appHome = null;
        
        for(String appName : validAppNames) {
            //check creating the appHome from clean-slate
            appHome = CrumbHome.get(appName);
            assertEquals(appName, appHome.getApp());
            assertEquals(Paths.get(CRUMBLEWORKS_HOME_TEST_PATH + File.separator + appName), appHome.getPath());
            assertEquals(appName + "@" + Paths.get(CRUMBLEWORKS_HOME_TEST_PATH) + File.separator + appName, appHome.toString());
            
            //repeat, this time retrieving the just created appHome
            appHome = CrumbHome.get(appName);
            assertEquals(appName, appHome.getApp());
            assertEquals(Paths.get(CRUMBLEWORKS_HOME_TEST_PATH + File.separator + appName), appHome.getPath());
            assertEquals(appName + "@" + Paths.get(CRUMBLEWORKS_HOME_TEST_PATH) + File.separator + appName, appHome.toString());
        }
    }
    
    @Test
    public void getAppHomeWithoutEnvVarSetWithInvalidAppNames() throws IOException {
        //return 'null' when requesting the environment variable, simulating it not being there
        when(System.getenv(CRUMBLEWORKS_HOME_ENV_VAR)).thenReturn(null);

        for(String appName : invalidAppNames) {
            //check creating the appHome from clean-slate
            assertThrows(IllegalArgumentException.class, () -> CrumbHome.get(appName));
        }
    }
    
    @Test
    public void getAppHomeWithEnvVarSetWithInvalidAppNames() throws IOException {
        //return the test path when requesting the environment variable, simulating it being set
        when(System.getenv(CRUMBLEWORKS_HOME_ENV_VAR)).thenReturn(CRUMBLEWORKS_HOME_TEST_PATH);

        for(String appName : invalidAppNames) {
            //check creating the appHome from clean-slate
        	assertThrows(IllegalArgumentException.class, () -> CrumbHome.get(appName));
        }
    }
    
    @Test
    public void getPathWithoutEnvVarSet() {
        //return 'null' when requesting the environment variable, simulating it not being there
        when(System.getenv(CRUMBLEWORKS_HOME_ENV_VAR)).thenReturn(null);
        
        assertEquals(Paths.get(DEFAULT_HOME_TEST_PATH), CrumbHome.getPath());
    }
    
    @Test
    public void getPathWithEnvVarSet() {
        //return the test path when requesting the environment variable, simulating it being set
        when(System.getenv(CRUMBLEWORKS_HOME_ENV_VAR)).thenReturn(CRUMBLEWORKS_HOME_TEST_PATH);

        assertEquals(Paths.get(CRUMBLEWORKS_HOME_TEST_PATH), CrumbHome.getPath());
    }
}