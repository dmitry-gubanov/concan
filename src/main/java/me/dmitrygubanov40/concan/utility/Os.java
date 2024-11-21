package me.dmitrygubanov40.concan.utility;



/**
 * Inner OS analyzer.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class Os
{
    
    private static final String NAME;
    
    static {
        NAME = System.getProperty("os.name").toLowerCase();
    }
    
    //////////
    
    public static boolean isWindows() {
        return NAME.contains("win");
    }
    
    public static boolean isMac() {
        return NAME.contains("mac");
    }
    
    public static boolean isUnix() {
        return (NAME.contains("nix") || NAME.contains("aix"));
    }
    
    public static boolean isLinux() {
        return NAME.contains("nux");
    }
    
    public static boolean isNx() {
        return (Os.isUnix() || Os.isLinux());
    }
    
    public static boolean isSolaris() {
        return NAME.contains("sunos");
    }
    
    
    
    /**
     * @return string code of an OS
     * @throws RuntimeException when OS is unknown
     */
    public static String getOsName() throws RuntimeException {
        String codeOs;
        //
        if ( Os.isWindows() ) {
            codeOs = "Windows";
        } else if ( Os.isMac() ) {
            codeOs = "macOS";
        } else if ( Os.isUnix() ) {
            codeOs = "UNIX";
        } else if ( Os.isLinux() ) {
            codeOs = "Linux";
        } else if ( Os.isSolaris() ) {
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
