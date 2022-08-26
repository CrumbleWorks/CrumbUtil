package org.crumbleworks.forge.crumbutil.configurations;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.crumbleworks.forge.crumbutil.configurations.CrumbHome.AppHome;
import org.crumbleworks.forge.crumbutil.util.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Michael Stocker
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
public class CrumbHomeTest {

    private static String TEST_DIR = Paths.get("").toAbsolutePath().toString() + File.separator + "tests";

    private static String USER_HOME_SYSTEM_PROPERTY_KEY = "user.home";
    private static String USER_HOME_TEST_PATH = TEST_DIR; //the modified user.home for the test

    private static String DEFAULT_HOME_TEST_PATH = TEST_DIR + File.separator + ".crumbleworks"; //where the CrumbHome should point to per default settings

    private static String CRUMBLEWORKS_HOME_ENV_VAR = "CRUMBLEWORKS_HOME";
    private static String CRUMBLEWORKS_HOME_TEST_PATH = TEST_DIR + File.separator + "keks_Haus"; //an alternate CrumbHome location provided using the envVar configuration

    @AfterEach
    public void everytimeCleanup() throws IOException {
        FileUtil.deepRemoveFolder(Paths.get(USER_HOME_TEST_PATH));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Warrior2D", "Crumb2Crumb", "CrumbUtil", "CrumbEngine", "CrumbGame-Alpha", "graPHity", "Hindenburg", "Dont_Die"})
    public void getAppHomeWithoutEnvVarSetWithValidAppNames(String appName) throws Exception {
        restoreSystemProperties(() -> {
            System.setProperty(USER_HOME_SYSTEM_PROPERTY_KEY, USER_HOME_TEST_PATH);
            withEnvironmentVariable(CRUMBLEWORKS_HOME_ENV_VAR, null) //set environment variable to null, simulating it not being there
                .execute(() -> {
                    AppHome appHome = null;
                    
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
                });
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"Warrior2D", "Crumb2Crumb", "CrumbUtil", "CrumbEngine", "CrumbGame-Alpha", "graPHity", "Hindenburg", "Dont_Die"})
    public void getAppHomeWithEnvVarSetWithValidAppNames(String appName) throws Exception {
        restoreSystemProperties(() -> {
            System.setProperty(USER_HOME_SYSTEM_PROPERTY_KEY, USER_HOME_TEST_PATH);
            withEnvironmentVariable(CRUMBLEWORKS_HOME_ENV_VAR, CRUMBLEWORKS_HOME_TEST_PATH) //set environment variable to test path, simulating it being set
                .execute(() -> {
                    AppHome appHome = null;
                    
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
                });
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"Peter, bist du da?", "Als Joghurt fliegen lernte", "?!?", ". I'm not fat, just <bold>!"})
    public void getAppHomeWithoutEnvVarSetWithInvalidAppNames(String appName) throws Exception {
        restoreSystemProperties(() -> {
            System.setProperty(USER_HOME_SYSTEM_PROPERTY_KEY, USER_HOME_TEST_PATH);
            withEnvironmentVariable(CRUMBLEWORKS_HOME_ENV_VAR, null) //set environment variable to null, simulating it not being there
                .execute(() -> {
                    //check creating the appHome from clean-slate
                    assertThrows(IllegalArgumentException.class, () -> CrumbHome.get(appName));
                });
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"Peter, bist du da?", "Als Joghurt fliegen lernte", "?!?", ". I'm not fat, just <bold>!"})
    public void getAppHomeWithEnvVarSetWithInvalidAppNames(String appName) throws Exception {
        restoreSystemProperties(() -> {
            System.setProperty(USER_HOME_SYSTEM_PROPERTY_KEY, USER_HOME_TEST_PATH);
            withEnvironmentVariable(CRUMBLEWORKS_HOME_ENV_VAR, CRUMBLEWORKS_HOME_TEST_PATH) //set environment variable to test path, simulating it being set
                .execute(() -> {
                    //check creating the appHome from clean-slate
                    assertThrows(IllegalArgumentException.class, () -> CrumbHome.get(appName));
                });
        });
    }

    @Test
    public void getPathWithoutEnvVarSet() throws Exception {
        restoreSystemProperties(() -> {
            System.setProperty(USER_HOME_SYSTEM_PROPERTY_KEY, USER_HOME_TEST_PATH);
            withEnvironmentVariable(CRUMBLEWORKS_HOME_ENV_VAR, null) //set environment variable to null, simulating it not being there
                .execute(() -> {
                    assertEquals(Paths.get(DEFAULT_HOME_TEST_PATH), CrumbHome.getPath());
                });
        });
    }

    @Test
    public void getPathWithEnvVarSet() throws Exception {
        restoreSystemProperties(() -> {
            System.setProperty(USER_HOME_SYSTEM_PROPERTY_KEY, USER_HOME_TEST_PATH);
            withEnvironmentVariable(CRUMBLEWORKS_HOME_ENV_VAR, CRUMBLEWORKS_HOME_TEST_PATH) //set environment variable to test path, simulating it being set
                .execute(() -> {
                    assertEquals(Paths.get(CRUMBLEWORKS_HOME_TEST_PATH), CrumbHome.getPath());
                });
        });
        
    }
}