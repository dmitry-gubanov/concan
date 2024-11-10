package me.dmitrygubanov40.concan.utility;



/**
 * Inner OS analyzer.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class UtilityOs
{
    
    private static final String OS_NAME;
    
    static {
        OS_NAME = System.getProperty("os.name").toLowerCase();
    }
    
    //////////
    
    public static boolean isWindows() {
        return UtilityOs.OS_NAME.contains("win");
    }
    
    public static boolean isMac() {
        return UtilityOs.OS_NAME.contains("mac");
    }
    
    public static boolean isUnix() {
        return (UtilityOs.OS_NAME.contains("nix") || UtilityOs.OS_NAME.contains("aix"));
    }
    
    public static boolean isLinux() {
        return UtilityOs.OS_NAME.contains("nux");
    }
    
    public static boolean isNx() {
        return (UtilityOs.isUnix() || UtilityOs.isLinux());
    }
    
    public static boolean isSolaris() {
        return UtilityOs.OS_NAME.contains("sunos");
    }
    
    
    
    /**
     * @return string code of an OS
     * @throws RuntimeException when OS is unknown
     */
    public static String getOS() throws RuntimeException {
        String codeOs;
        //
        if ( UtilityOs.isWindows() ) {
            codeOs = "Windows";
        } else if ( UtilityOs.isMac() ) {
            codeOs = "macOS";
        } else if ( UtilityOs.isUnix() ) {
            codeOs = "UNIX";
        } else if ( UtilityOs.isLinux() ) {
            codeOs = "Linux";
        } else if ( UtilityOs.isSolaris() ) {
            codeOs = "Solaris";
        } else {
            //
            String excMsg = "OS name/type was not detected";
            throw new RuntimeException(excMsg);
            //
        }
        //
        return codeOs;
    }
    
    
    
}
