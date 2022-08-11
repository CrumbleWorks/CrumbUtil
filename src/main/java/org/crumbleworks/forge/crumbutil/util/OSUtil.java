package org.crumbleworks.forge.crumbutil.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import com.sun.jna.platform.win32.KnownFolders;
import com.sun.jna.platform.win32.Shell32Util;
import com.sun.jna.platform.win32.ShlObj;

/**
 * Provides utility functions regarding OS specifics such as preferred folders for data storage
 * 
 * @author Michael Stocker
 * @since 0.3.0
 */
public final class OSUtil {
    
    private static final String OS_NAME_SYSTEM_PROPERTY = "os.name";
    
    private static final String LINUX_REGEX = "^\\w+nix$";
    private static final String MACOS_REGEX = "^Mac[\\w\\s]+$";
    private static final String WINXP_and_older_REGEX = "^Windows \\b(XP|2000|ME|95|98)\\b$";
    private static final String WINVista_and_newer_REGEX = "^Windows \\b(Vista|7|8|8.1|10|11)\\b$";

    private static final OSFunctions osfunc;
    
    /*
     * static initializer block determines OS and thus underlying implementation to be used
     */
    static {
        String osName = System.getProperty(OS_NAME_SYSTEM_PROPERTY);
        
        if(osName.matches(WINVista_and_newer_REGEX)) {
            osfunc = new WinVistaAndNewerFunctions();
        } else if(osName.matches(WINXP_and_older_REGEX)) {
            osfunc = new WinXPAndOlderFunctions();
        } else if(osName.matches(MACOS_REGEX)) {
            osfunc = new MacOSFunctions();
        } else if(osName.matches(LINUX_REGEX)) {
            osfunc = new UnixFunctions();
        } else {
            throw new UnsupportedOperationSystemException("The OS: " + osName + " is not supported by this Version of the OS Utility");
        }
    }
    
    private OSUtil() {};

    /* ************************************************************************
     *  USER DATA */
    
    /**
     * Retrieves the specified file relative to the OS-specific folder for <b>User Data</b>
     * <p>This location is used for user-specific program state (e.g. savegames, emails, etc.)
     * 
     * @param relativePath the relative location of the file
     * 
     * @return an absolute <code>path</code> to the specified file OR <code>null</code> if the file does not exist
     */
    public static final Path getUserData(final String relativePath) {
        return osfunc.getUserData(relativePath);
    }
    
    /**
     * Retrieves all files located in the given subfolder relative to the OS-specific folder for <b>User Data</b>
     * <p>This location is used for user-specific program state (e.g. savegames, emails, etc.)
     *
     * @param subfolder the subfolder for which to list all files
     * 
     * @return a set of absolute <code>paths</code> to the files and folders in the specified subfolder, can be empty
     * @throws IOException if the given subfolder does not exist
     */
    public static final Set<Path> listUserData(final String subfolder) throws IOException {
        return osfunc.listUserData(subfolder);
    }
    
    /**
     * Creates the specified file relative to the OS-specific folder for <b>User Data</b>
     * <p>This location is used for user-specific program state (e.g. savegames, emails, etc.)
     * 
     * @param relativePath the relative location of the file
     * 
     * @return an absolute <code>path</code> to the specified file
     * @throws IOException if the file could not be created
     */
    public static final Path createUserData(final String relativePath) throws IOException {
        return osfunc.createUserData(relativePath);
    }
    
    /**
     * Creates the specified file relative to the OS-specific folder for <b>User Data</b>
     * <p>This location is used for user-specific program state (e.g. savegames, emails, etc.)
     * 
     * @param relativePath the relative location of the file
     * @param bytes the bytes making up the new file
     * 
     * @return an absolute <code>path</code> to the specified file
     * @throws IOException if the file could not be created
     */
    public static final Path createUserData(final String relativePath, final byte[] bytes) throws IOException {
        return osfunc.createSharedData(relativePath, bytes);
    }
    
    /* ************************************************************************
     *  USER CONFIG */
    
    /**
     * Retrieves the specified file relative to the OS-specific folder for <b>User Config</b>
     * <p>This location is used for user-specific program configuration files
     * 
     * @param relativePath the relative location of the file
     * 
     * @return an absolute <code>path</code> to the specified file OR <code>null</code> if the file does not exist
     */
    public static final Path getUserConfig(final String relativePath) {
        return osfunc.getUserData(relativePath);
    }
    
    /**
     * Retrieves all files located in the given subfolder relative to the OS-specific folder for <b>User Config</b>
     * <p>This location is used for user-specific program configuration files
     *
     * @param subfolder the subfolder for which to list all files
     * 
     * @return a set of absolute <code>paths</code> to the files and folders in the specified subfolder, can be empty
     * @throws IOException if the given subfolder does not exist
     */
    public static final Set<Path> listUserConfig(final String subfolder) throws IOException {
        return osfunc.listUserConfig(subfolder);
    }
    
    /**
     * Creates the specified file relative to the OS-specific folder for <b>User Config</b>
     * <p>This location is used for user-specific program configuration files
     * 
     * @param relativePath the relative location of the file
     * 
     * @return an absolute <code>path</code> to the specified file
     * @throws IOException if the file could not be created
     */
    public static final Path createUserConfig(final String relativePath) throws IOException {
        return osfunc.createUserData(relativePath);
    }
    
    /**
     * Creates the specified file relative to the OS-specific folder for <b>User Config</b>
     * <p>This location is used for user-specific program configuration files
     * 
     * @param relativePath the relative location of the file
     * @param bytes the bytes making up the new file
     * 
     * @return an absolute <code>path</code> to the specified file
     * @throws IOException if the file could not be created
     */
    public static final Path createUserConfig(final String relativePath, final byte[] bytes) throws IOException {
        return osfunc.createSharedData(relativePath, bytes);
    }
    
    /* ************************************************************************
     * SHARED DATA */
    
    /**
     * Retrieves the specified file relative to the OS-specific folder for <b>Shared Data</b>
     * <p>This location is used for things such as the datastores of a translation tool
     * 
     * @param relativePath the relative location of the file
     * 
     * @return an absolute <code>path</code> to the specified file OR <code>null</code> if the file does not exist
     */
    public static final Path getSharedData(final String relativePath) {
        return osfunc.getSharedData(relativePath);
    }
    
    /**
     * Retrieves all files located in the given subfolder relative to the OS-specific folder for <b>Shared Data</b>
     * <p>This location is used for things such as the datastores of a translation tool
     *
     * @param subfolder the subfolder for which to list all files
     * 
     * @return a set of absolute <code>paths</code> to the files and folders in the specified subfolder, can be empty
     * @throws IOException if the given subfolder does not exist
     */
    public static final Set<Path> listSharedData(final String subfolder) throws IOException {
        return osfunc.listSharedData(subfolder);
    }
    
    /**
     * Creates the specified file relative to the OS-specific folder for <b>Shared Data</b>
     * <p>This location is used for things such as the datastores of a translation tool
     * 
     * @param relativePath the relative location of the file
     * 
     * @return an absolute <code>path</code> to the specified file
     * @throws IOException if the file could not be created
     */
    public static final Path createSharedData(final String relativePath) throws IOException {
        return osfunc.createSharedData(relativePath);
    }

    /**
     * Creates the specified file relative to the OS-specific folder for <b>Shared Data</b>
     * <p>This location is used for things such as the datastores of a translation tool
     * 
     * @param relativePath the relative location of the file
     * @param bytes the bytes making up the new file
     * 
     * @return an absolute <code>path</code> to the specified file
     * @throws IOException if the file could not be created
     */
    public static final Path createSharedData(final String relativePath, final byte[] bytes) throws IOException {
        return osfunc.createSharedData(relativePath, bytes);
    }
    
    /* ************************************************************************
     * SHARED CONFIG */
    
    /**
     * Retrieves the specified file relative to the OS-specific folder for <b>Shared Config</b>
     * <p>This location is used for system-specific configuration files
     * 
     * @param relativePath the relative location of the file
     * 
     * @return an absolute <code>path</code> to the specified file OR <code>null</code> if the file does not exist
     */
    public static final Path getSharedConfig(final String relativePath) {
        return osfunc.getSharedData(relativePath);
    }
    
    /**
     * Retrieves all files located in the given subfolder relative to the OS-specific folder for <b>Shared Config</b>
     * <p>This location is used for system-specific configuration files
     *
     * @param subfolder the subfolder for which to list all files
     * 
     * @return a set of absolute <code>paths</code> to the files and folders in the specified subfolder, can be empty
     * @throws IOException if the given subfolder does not exist
     */
    public static final Set<Path> listSharedConfig(final String subfolder) throws IOException {
        return osfunc.listSharedConfig(subfolder);
    }
    
    /**
     * Creates the specified file relative to the OS-specific folder for <b>Shared Config</b>
     * <p>This location is used for system-specific configuration files
     * 
     * @param relativePath the relative location of the file
     * 
     * @return an absolute <code>path</code> to the specified file
     * @throws IOException if the file could not be created
     */
    public static final Path createSharedConfig(final String relativePath) throws IOException {
        return osfunc.createSharedData(relativePath);
    }

    /**
     * Creates the specified file relative to the OS-specific folder for <b>Shared Config</b>
     * <p>This location is used for system-specific configuration files
     * 
     * @param relativePath the relative location of the file
     * @param bytes the bytes making up the new file
     * 
     * @return an absolute <code>path</code> to the specified file
     * @throws IOException if the file could not be created
     */
    public static final Path createSharedConfig(final String relativePath, final byte[] bytes) throws IOException {
        return osfunc.createSharedData(relativePath, bytes);
    }
    
    /* ************************************************************************
     * STATIC UTILITIES
     */
    
    /**
     * @param file file to verify
     * @return the path if the file exists & is a file OR <code>null</code>
     */
    private static final Path verifyFile(Path file) {
        return Files.isRegularFile(file) ? file : null;
    }
    
    /* ************************************************************************
     * OS specific worker implementations & common interface
     */
    
    /**
     * @author Michael Stocker
     * @since 0.3.0
     */
    private static interface OSFunctions {
        
        public Path getUserData(final String relativePath);
        public Set<Path> listUserData(final String subfolder) throws IOException;
        public Path createUserData(final String relativePath) throws IOException;
        public Path createUserData(final String relativePath, final byte[] bytes) throws IOException;
        
        public Path getUserConfig(final String relativePath);
        public Set<Path> listUserConfig(final String subfolder) throws IOException;
        public Path createUserConfig(final String relativePath) throws IOException;
        public Path createUserConfig(final String relativePath, final byte[] bytes) throws IOException;
        
        public Path getSharedData(final String relativePath);
        public Set<Path> listSharedData(final String subfolder) throws IOException;
        public Path createSharedData(final String relativePath) throws IOException;
        public Path createSharedData(final String relativePath, final byte[] bytes) throws IOException;

        public Path getSharedConfig(final String relativePath);
        public Set<Path> listSharedConfig(final String subfolder) throws IOException;
        public Path createSharedConfig(final String relativePath) throws IOException;
        public Path createSharedConfig(final String relativePath, final byte[] bytes) throws IOException;
        
        //TODO expand with sets of functions for FOLDERID_SavedGames (savegames) and FOLDERID_Pictures (screenshots, etc)
    }
    
    /**
     * OSFunctions implementation for Windows XP and older
     * 
     * @see <a href="https://msdn.microsoft.com/en-us/library/bb762181.aspx">SHGetFolderPath</a>
     * @see <a href="https://msdn.microsoft.com/en-us/library/bb762494.aspx">CSIDL</a>
     * 
     * @author Michael Stocker
     * @since 0.3.0
     */
    private static final class WinXPAndOlderFunctions implements OSFunctions {

        //TODO: evaluate if WINXP data/saves should be stored in Documents instead
        
        /* ********************************************************************
         * USER DATA */
        
        @Override
        public Path getUserData(String relativePath) {
            return verifyFile(Paths.get(Shell32Util.getFolderPath(ShlObj.CSIDL_APPDATA), relativePath));
        }
        
        @Override
        public Set<Path> listUserData(String subfolder) throws IOException {
            return Files.list(Paths.get(Shell32Util.getFolderPath(ShlObj.CSIDL_APPDATA), subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createUserData(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(Shell32Util.getFolderPath(ShlObj.CSIDL_APPDATA), relativePath));
        }

        @Override
        public Path createUserData(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(Shell32Util.getFolderPath(ShlObj.CSIDL_APPDATA), relativePath), bytes);
        }

        /* ********************************************************************
         * USER CONFIG */
        
        @Override
        public Path getUserConfig(String relativePath) {
            return getUserData(relativePath);
        }
        
        @Override
        public Set<Path> listUserConfig(String subfolder) throws IOException {
            return listUserData(subfolder);
        }

        @Override
        public Path createUserConfig(String relativePath) throws IOException {
            return createUserData(relativePath);
        }

        @Override
        public Path createUserConfig(String relativePath, byte[] bytes) throws IOException {
            return createUserData(relativePath, bytes);
        }

        /* ********************************************************************
         * SHARED DATA */

        @Override
        public Path getSharedData(String relativePath) {
            return verifyFile(Paths.get(Shell32Util.getFolderPath(ShlObj.CSIDL_COMMON_APPDATA), relativePath));
        }
        
        @Override
        public Set<Path> listSharedData(String subfolder) throws IOException {
            return Files.list(Paths.get(Shell32Util.getFolderPath(ShlObj.CSIDL_COMMON_APPDATA), subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createSharedData(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(Shell32Util.getFolderPath(ShlObj.CSIDL_COMMON_APPDATA), relativePath));
        }

        @Override
        public Path createSharedData(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(Shell32Util.getFolderPath(ShlObj.CSIDL_COMMON_APPDATA), relativePath), bytes);
        }

        /* ********************************************************************
         * SHARED CONFIG */
        
        @Override
        public Path getSharedConfig(String relativePath) {
            return getSharedData(relativePath);
        }
        
        @Override
        public Set<Path> listSharedConfig(String subfolder) throws IOException {
            return listSharedData(subfolder);
        }

        @Override
        public Path createSharedConfig(String relativePath) throws IOException {
            return createSharedData(relativePath);
        }

        @Override
        public Path createSharedConfig(String relativePath, byte[] bytes) throws IOException {
            return createSharedData(relativePath, bytes);
        }
    }
    
    /**
     * OSFunctions implementation for Windows Vista and newer
     * 
     * @see <a href="https://msdn.microsoft.com/en-us/library/bb762181.aspx">SHGetFolderPath</a>
     * @see <a href="https://msdn.microsoft.com/en-us/library/bb762494.aspx">CSIDL</a>
     * 
     * @author Michael Stocker
     * @since 0.3.0
     */
    private static final class WinVistaAndNewerFunctions implements OSFunctions {

        //TODO: evaluate if WIN10 data/saves should be stored in Documents instead

        /* ********************************************************************
         * USER DATA */

        @Override
        public Path getUserData(String relativePath) {
            return verifyFile(Paths.get(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_RoamingAppData), relativePath));
        }
        
        @Override
        public Set<Path> listUserData(String subfolder) throws IOException {
            return Files.list(Paths.get(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_RoamingAppData), subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createUserData(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_RoamingAppData), relativePath));
        }
        
        @Override
        public Path createUserData(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_RoamingAppData), relativePath), bytes);
        }

        /* ********************************************************************
         * USER CONFIG */

        @Override
        public Path getUserConfig(String relativePath) {
            return getUserData(relativePath);
        }
        
        @Override
        public Set<Path> listUserConfig(String subfolder) throws IOException {
            return listUserData(subfolder);
        }

        @Override
        public Path createUserConfig(String relativePath) throws IOException {
            return createUserData(relativePath);
        }
        
        @Override
        public Path createUserConfig(String relativePath, byte[] bytes) throws IOException {
            return createUserData(relativePath, bytes);
        }

        /* ********************************************************************
         * SHARED DATA */

        @Override
        public Path getSharedData(String relativePath) {
            return verifyFile(Paths.get(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_ProgramData), relativePath));
        }
        
        @Override
        public Set<Path> listSharedData(String subfolder) throws IOException {
            return Files.list(Paths.get(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_ProgramData), subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createSharedData(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_ProgramData), relativePath));
        }

        @Override
        public Path createSharedData(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(Shell32Util.getKnownFolderPath(KnownFolders.FOLDERID_ProgramData), relativePath), bytes);
        }

        /* ********************************************************************
         * SHARED CONFIG */

        @Override
        public Path getSharedConfig(String relativePath) {
            return getSharedData(relativePath);
        }
        
        @Override
        public Set<Path> listSharedConfig(String subfolder) throws IOException {
            return listSharedData(subfolder);
        }

        @Override
        public Path createSharedConfig(String relativePath) throws IOException {
            return createSharedData(relativePath);
        }

        @Override
        public Path createSharedConfig(String relativePath, byte[] bytes) throws IOException {
            return createSharedData(relativePath, bytes);
        }
    }
    
    /**
     * OSFunctions implementation for MacOS
     * 
     * @see <a href="http://gamedev.stackexchange.com/a/35702/42500">gamedev.stackexchange.com: In which directory to write game save files/data?</a>
     * 
     * @author Michael Stocker
     * @since 0.3.0
     */
    private static final class MacOSFunctions implements OSFunctions {
        private static final String USER_DATA_LOC = "~/Library/Application Support";
        private static final String SHARED_DATA_LOC = "/Library/Application Support"; //no tilde!

        //TODO: evaluate if MACOS data/saves should be stored in Documents instead

        /* ********************************************************************
         * USER DATA */

        @Override
        public Path getUserData(String relativePath) {
            return verifyFile(Paths.get(USER_DATA_LOC, relativePath));
        }
        
        @Override
        public Set<Path> listUserData(String subfolder) throws IOException {
            return Files.list(Paths.get(USER_DATA_LOC, subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createUserData(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(USER_DATA_LOC, relativePath));
        }
        
        @Override
        public Path createUserData(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(USER_DATA_LOC, relativePath), bytes);
        }

        /* ********************************************************************
         * USER CONFIG */

        @Override
        public Path getUserConfig(String relativePath) {
            return getUserData(relativePath);
        }
        
        @Override
        public Set<Path> listUserConfig(String subfolder) throws IOException {
            return listUserData(subfolder);
        }

        @Override
        public Path createUserConfig(String relativePath) throws IOException {
            return createUserData(relativePath);
        }
        
        @Override
        public Path createUserConfig(String relativePath, byte[] bytes) throws IOException {
            return createUserData(relativePath, bytes);
        }

        /* ********************************************************************
         * SHARED DATA */

        @Override
        public Path getSharedData(String relativePath) {
            return verifyFile(Paths.get(SHARED_DATA_LOC, relativePath));
        }
        
        @Override
        public Set<Path> listSharedData(String subfolder) throws IOException {
            return Files.list(Paths.get(SHARED_DATA_LOC, subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createSharedData(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(SHARED_DATA_LOC, relativePath));
        }

        @Override
        public Path createSharedData(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(SHARED_DATA_LOC, relativePath), bytes);
        }

        /* ********************************************************************
         * SHARED CONFIG */

        @Override
        public Path getSharedConfig(String relativePath) {
            return getSharedData(relativePath);
        }
        
        @Override
        public Set<Path> listSharedConfig(String subfolder) throws IOException {
            return listSharedData(subfolder);
        }

        @Override
        public Path createSharedConfig(String relativePath) throws IOException {
            return createSharedData(relativePath);
        }

        @Override
        public Path createSharedConfig(String relativePath, byte[] bytes) throws IOException {
            return createSharedData(relativePath, bytes);
        }
    }
    
    /**
     * OSFunctions implementation for Unix Systems
     * 
     * @see <a href="https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html">https://specifications.freedesktop.org/basedir-spec/basedir-spec-latest.html</a>
     * @see <a href="http://gamedev.stackexchange.com/a/35703/42500">gamedev.stackexchange.com: In which directory to write game save files/data?</a>
     * 
     * @author Michael Stocker
     * @since 0.3.0
     */
    private static final class UnixFunctions implements OSFunctions {
        private static final String USER_DATA_LOC;
        private static final String USER_CONFIG_LOC;
        private static final String SHARED_DATA_LOC = "/var/opt";
        private static final String SHARED_CONFIG_LOC = "/etc/opt";
        
        static {
            String XDGDataHome = System.getenv("XDG_DATA_HOME");
            USER_DATA_LOC = StringUtil.neitherNullNorEmpty(XDGDataHome) ? XDGDataHome : "~/.local";
            
            String XDGConfigHome = System.getenv("XDG_CONFIG_HOME");
            USER_CONFIG_LOC = StringUtil.neitherNullNorEmpty(XDGConfigHome) ? XDGConfigHome : "~/.config";
        }
        
        /* ********************************************************************
         * USER DATA
         */

        @Override
        public Path getUserData(String relativePath) {
            return verifyFile(Paths.get(USER_DATA_LOC, relativePath));
        }
        
        @Override
        public Set<Path> listUserData(String subfolder) throws IOException {
            return Files.list(Paths.get(USER_DATA_LOC, subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createUserData(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(USER_DATA_LOC, relativePath));
        }
        
        @Override
        public Path createUserData(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(USER_DATA_LOC, relativePath), bytes);
        }
        
        /* ********************************************************************
         * USER CONFIG
         */

        @Override
        public Path getUserConfig(String relativePath) {
            return verifyFile(Paths.get(USER_CONFIG_LOC, relativePath));
        }
        
        @Override
        public Set<Path> listUserConfig(String subfolder) throws IOException {
            return Files.list(Paths.get(USER_CONFIG_LOC, subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createUserConfig(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(USER_CONFIG_LOC, relativePath));
        }
        
        @Override
        public Path createUserConfig(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(USER_CONFIG_LOC, relativePath), bytes);
        }
        
        /* ********************************************************************
         * SHARED DATA
         */

        @Override
        public Path getSharedData(String relativePath) {
            return verifyFile(Paths.get(SHARED_DATA_LOC, relativePath));
        }
        
        @Override
        public Set<Path> listSharedData(String subfolder) throws IOException {
            return Files.list(Paths.get(SHARED_DATA_LOC, subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createSharedData(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(SHARED_DATA_LOC, relativePath));
        }

        @Override
        public Path createSharedData(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(SHARED_DATA_LOC, relativePath), bytes);
        }
        
        /* ********************************************************************
         * SHARED CONFIG
         */

        @Override
        public Path getSharedConfig(String relativePath) {
            return verifyFile(Paths.get(SHARED_CONFIG_LOC, relativePath));
        }
        
        @Override
        public Set<Path> listSharedConfig(String subfolder) throws IOException {
            return Files.list(Paths.get(SHARED_CONFIG_LOC, subfolder)).collect(Collectors.toSet());
        }

        @Override
        public Path createSharedConfig(String relativePath) throws IOException {
            return FileUtil.createFileWithAncestors(Paths.get(SHARED_CONFIG_LOC, relativePath));
        }

        @Override
        public Path createSharedConfig(String relativePath, byte[] bytes) throws IOException {
            return Files.write(Paths.get(SHARED_CONFIG_LOC, relativePath), bytes);
        }
    }
}
