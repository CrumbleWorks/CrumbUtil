package org.crumbleworks.forge.crumbutil.configurations;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO switch to using OSUtil or get rid of entirely even
/**
 * Represents the <i>.crumbhome</i>
 * 
 * @author Michael Stocker
 * @since 0.1.0
 */
public class CrumbHome {
    private final static Logger logger = LoggerFactory.getLogger(CrumbHome.class);
    
    private final static String CRUMBLEWORKS_HOME_DIR_NAME = ".crumbleworks";
    private final static String CRUMBLEWORKS_HOME_DIR_DEFAULT_PATH = System.getProperty("user.home") + File.separator + CRUMBLEWORKS_HOME_DIR_NAME;
    
    public final static String CRUMBLEWORKS_HOME_ENV_VAR = "CRUMBLEWORKS_HOME";
    
    public final static String CRUMBLEWORKS_APP_NAME_REGEX = "[A-Za-z0-9-_]+";

    private CrumbHome() {};
    
    /**
     * This returns the a {@link Path} element, pointing to the base folder for configurations
     */
    public static Path getPath() {
        String env = System.getenv(CRUMBLEWORKS_HOME_ENV_VAR);
        if(null != env && !"".equals(env)) {
            logger.debug("Using environment variable '{}' for crumbHome path: {}", CRUMBLEWORKS_HOME_ENV_VAR, env);
            return Paths.get(env);
        } else {
            logger.debug("Environment variable '{}' not set or empty. Using default value for crumbHome path: {}", CRUMBLEWORKS_HOME_ENV_VAR, CRUMBLEWORKS_HOME_DIR_DEFAULT_PATH);
            return Paths.get(CRUMBLEWORKS_HOME_DIR_DEFAULT_PATH);
        }
    }
    
    /**
     * This returns an {@link AppHome} representing the system specific path to the app's crumbleworks config folder.
     * <p>If there is no config folder present, it will create the necessary folder structure.
     * 
     * @param appName must adhere to regex: [A-Za-z0-9-_]+
     * @return appHome for the specified <i>app</i>
     * 
     * @throws IllegalArgumentException if the appName contains any invalid characters
     * @throws IOException if the <i>appHome</i> creation operation fails
     */
    public static AppHome get(String appName) throws IOException {
        if(appName.matches(CRUMBLEWORKS_APP_NAME_REGEX)) {
            Path homePath = getPath(); //the base folder
            logger.debug("Checking if appHome for '{}' exists on the filesystem.", appName);
            if(!Files.exists(homePath.resolve(appName))) {
                logger.debug("Not found. Trying to create new appHome for '{}' under '{}'.", appName, homePath.resolve(appName));
                Files.createDirectories(homePath.resolve(appName));
                logger.info("Created appHome for '{}' under '{}'.", appName, homePath.resolve(appName));
            } else {
                logger.debug("Found appHome for '{}' under '{}'.", appName, homePath.resolve(appName));
            }
            return new AppHome(appName, homePath.resolve(appName));
        } else {
            throw new IllegalArgumentException("The appName must adhere to the regex '[A-Za-z0-9-_]+'!");
        }
    }
    
    /**
     * Represents the <i>homedir</i> of an <i>app</i>.
     * 
     * @author Michael
     * @since 0.1
     */
    public static class AppHome {
        private String appName;
        private Path homePath;
        
        private AppHome(String appName, Path homePath) {
            this.appName = appName;
            this.homePath = homePath;
        }
        
        /**
         * @return <b>name</b> of the <i>app</i> this <code>AppHome</code> represents
         */
        public String getApp() {
            return this.appName;
        }
        
        /**
         * @return <b>path</b> to the <i>home dir</i> of this <i>app</i>
         */
        public Path getPath() {
            return this.homePath;
        }
        
        @Override
        public String toString() {
            return new StringBuilder().append(getApp()).append("@").append(getPath().toAbsolutePath().toString()).toString();
        }
    }
}