package me.dmitrygubanov40.concan.windows;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dmitrygubanov40.concan.utility.ConStyles;
import me.dmitrygubanov40.concan.utility.ConUt;
import me.dmitrygubanov40.concan.utility.Term;



/**
 * Keeper of the current state of WindowOutputBuffer brush.
 * Any interactions with the console restore style conditions.
 * That allows to work different windows and native output without
 * mess of styles.
 * @see WindowOutputBuffer.validCommands for list of allowed commands
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConWinOutBrush
{
    
    // regular expressions for all we consider Esc-sequence color commands
    private static final List<String> colorCmds;
    // regular expressions for all we consider Esc-sequence background commands
    private static final List<String> backgroundCmds;
    // regular expression for all styles commands
    private static final List<String> styleCmds;
    
    
    static {
        colorCmds = new ArrayList<>();
        initColorCommandsRegex();
        //
        backgroundCmds = new ArrayList<>();
        initBackgroundCommandsRegex();
        //
        styleCmds = new ArrayList<>();
        initStyleCommandsRegex();
    }
    
    /**
     * Write down all regex expressions to cover all escape sequences
     * we consider to be font color generator.
     * Only these Esc-commands will be saved as font color.
     */
    private static void initColorCommandsRegex() {
        String[] fontColorCommands = {
            "38;2;\\d+;\\d+;\\d+m",     // TrueColor
            "38;5;\\d+m",               // 8B-color
            "((3[0-9])?|(9[0-7])?)m"    // 4B-color + default color ('39m')
        };
        //
        for ( String curExpression : fontColorCommands ) {
            // all lines must start as 'ESC' + '[':
            String regexToAdd = "\033\\["
                                + curExpression;
            ConWinOutBrush.colorCmds.add(regexToAdd);
        }
    }
    
    /**
     * Write down all regex expressions to cover all escape sequences
     * we consider to be font background generator.
     * Only these Esc-commands will be saved as font background.
     */
    private static void initBackgroundCommandsRegex() {
        String[] fontColorCommands = {
            "48;2;\\d+;\\d+;\\d+m",     // TrueColor
            "48;5;\\d+m",               // 8B-color
            "((4[0-9])?|(10[0-7])?)m"   // 4B-color + default bg-color ('49m')
        };
        //
        for ( String curExpression : fontColorCommands ) {
            // all lines must start as 'ESC' + '[':
            String regexToAdd = "\033\\["
                                + curExpression;
            ConWinOutBrush.backgroundCmds.add(regexToAdd);
        }
    }
    
    /**
     * Write down all regex expressions to cover all escape sequences
     * we consider to be styles: bold, dim, etc.
     * Only these Esc-commands will be saved as style commands.
     */
    private static void initStyleCommandsRegex() {
        String[] styleCommands = {
            "([0-5]|[7-9])m",   // on-styles + RESET
            "(2[2-5]|2[7-9])m", // off-styles
            "\\?[1-2](2|5)(h|l)"  // cursor controller
        };
        //
        for ( String curExpression : styleCommands ) {
            // all lines must start as 'ESC' + '[':
            String regexToAdd = "\033\\["
                                + curExpression;
            ConWinOutBrush.styleCmds.add(regexToAdd);
        }
    }
    
    
    
    /**
     * Guarantee the string is a Esc-command.
     * @param strToCheck
     * @throws NullPointerException for null-string
     */
    private static boolean checkCmdStr(final String strToCheck)
                            throws NullPointerException {
        final String escCmdPrefix = ConUt.ESC + ConUt.ESC_CMD_SEPARATOR;
        //
        if ( null == strToCheck ) {
            String excMsg = "Cannot check ESC-sequence: the string is null";
            throw new NullPointerException(excMsg);
        }
        //
        // too short, it cannot be Esc-command:
        if ( strToCheck.length() <= 1 ) return false;
        // command 'signature' is wrong:
        if ( !strToCheck.substring(0, 2).equals(escCmdPrefix) ) return false;
        //
        return true;// +
    }
    
    /**
     * Check if the whole string is a color Esc-command or not.
     * @param strToCheck line we must analyze
     * @return 'true' when it is a color, or 'false'
     */
    public static boolean isColorEscCommand(final String strToCheck) {
        if ( !ConWinOutBrush.checkCmdStr(strToCheck) ) return false;
        //
        Pattern pattern;
        Matcher matcher;
        //
        for ( String curValidCmd : ConWinOutBrush.colorCmds ) {
            pattern = Pattern.compile(curValidCmd);
            matcher = pattern.matcher(strToCheck);
            //
            if ( matcher.matches() ) return true;// +
        }
        //
        // here the match did not occur:
        return false;
    }
    
    /**
     * Check if the whole string is a background Esc-command or not.
     * @param strToCheck line we must analyze
     * @return 'true' when it is a background, or 'false'
     */
    public static boolean isBackgroundEscCommand(final String strToCheck) {
        if ( !ConWinOutBrush.checkCmdStr(strToCheck) ) return false;
        //
        Pattern pattern;
        Matcher matcher;
        //
        for ( String curValidCmd : ConWinOutBrush.backgroundCmds ) {
            pattern = Pattern.compile(curValidCmd);
            matcher = pattern.matcher(strToCheck);
            //
            if ( matcher.matches() ) return true;// +
        }
        //
        // here the match did not occur:
        return false;
    }
    
    /**
     * Check if the whole string is a style Esc-command, or not.
     * @param strToCheck line we must analyze
     * @return 'true' when it is a style, or 'false'
     */
    public static boolean isStyleEscCommand(final String strToCheck) {
        if ( !ConWinOutBrush.checkCmdStr(strToCheck) ) return false;
        //
        Pattern pattern;
        Matcher matcher;
        //
        for ( String curValidCmd : ConWinOutBrush.styleCmds ) {
            pattern = Pattern.compile(curValidCmd);
            matcher = pattern.matcher(strToCheck);
            //
            if ( matcher.matches() ) return true;// +
        }
        //
        // here the match did not occur:
        return false;
    }
    
    
    ////////////////////////////////
    
    
    // special style for font color
    // (without it uses default from Terminal)
    private StringBuffer brushColor;
    
    // special style for background color
    // (without it uses default from Terminal)
    private StringBuffer brushBackground;
    
    // applied styles (with mutual block of opposite styles)
    private ArrayList<ConStyles> brushStyles;
    
    
    ///////////////////////////////
    
    /**
     * Empty default constructor.
     */
    public ConWinOutBrush() {
        this.brushColor = new StringBuffer("");
        this.brushBackground = new StringBuffer("");
        this.brushStyles = new ArrayList<>();
    }
    
    /**
     * 'Clone' constructor.
     * @param brushToCopy object to be copied
     * @throws NullPointerException in case there is nothing to copy
     */
    public ConWinOutBrush(ConWinOutBrush brushToCopy) {
        if ( null == brushToCopy ) {
            String excMsg = "There is no brush to copy";
            throw new NullPointerException(excMsg);
        }
        //
        this.brushBackground = new StringBuffer( brushToCopy.brushBackground );
        this.brushColor = new StringBuffer( brushToCopy.brushColor );
        this.brushStyles = new ArrayList<>( brushToCopy.brushStyles );
    }
    
    //////////////////////////
    
    /**
     * Setter of font color.
     * @param setColor any Esc-command for new font color
     * @throws NullPointerException when brush string is empty
     */
    public void setBrushColor(final String setColor) throws NullPointerException {
        if ( null == setColor ) {
            String excMsg = "Cannot set new brush color"
                                + " (color Esc-command is null)";
            throw new NullPointerException(excMsg);
        }
        //
        this.brushColor = new StringBuffer(setColor);
    }
    
    /**
     * Setter of font background.
     * @param setBackground any Esc-command for new font background
     * @throws NullPointerException when brush string is empty
     */
    public void setBrushBackground(final String setBackground) throws NullPointerException {
        if ( null == setBackground ) {
            String excMsg = "Cannot set new brush background"
                                + " (background Esc-command is null)";
            throw new NullPointerException(excMsg);
        }
        //
        this.brushBackground = new StringBuffer(setBackground);
    }
    
    
    
    /**
     * Will add new console style if style is not applied already.
     * Skips the same (already applied) style.
     * Opposite style removes the first one ('bold_off' just removes 'bold',
     * and 'bold' removes 'bold_off').
     * @param styleToAdd standard console style to add in the filling
     * @throws NullPointerException if new style does not exist
     */
    public void addBrushStyle(ConStyles styleToAdd)
                    throws NullPointerException {
        if ( null == styleToAdd ) {
            String excMsg = "Cannot add new style to the list";
            throw new NullPointerException(excMsg);
        }
        //
        if ( this.brushStyles.contains(styleToAdd) ) {
            // prevent adding of the same style many times
            return;
        }
        //
        // if have opposite style - remove it before adding the new one
        ConStyles oppStyle = styleToAdd.getOppositeStyleOrNull();
        if ( null != oppStyle
                        && this.brushStyles.contains(oppStyle) ) {
            // already have the opposite style
            this.brushStyles.remove(oppStyle);
        }
        //
        this.brushStyles.add(styleToAdd);
    }
    
    
    
    /**
     * Collect all the necessary data to re-create style (brush)
     * in the window's zone.
     * Is cleaned before putting the real Esc-commands composition.
     * @return string to output to set actual style
     */
    public String getBrush() {
        // clear cursor settings
        StringBuilder brushRes = new StringBuilder(ConUt.RESET);
        //
        // install the font color:
        if ( this.brushColor.length() > 0 ) {
            brushRes.append(this.brushColor);
        } else {
            // no brush color - use default:
            final String defaultColorCmd = ConUt.COLOR( Term.get().color() );
            brushRes.append(defaultColorCmd);
        }
        //
        // install the font background:
        if ( this.brushBackground.length() > 0 ) {
            brushRes.append(this.brushBackground);
        } else {
            // no brush color - use default:
            final String defaultColorCmd = ConUt.BACKGROUND( Term.get().background() );
            brushRes.append(defaultColorCmd);
        }
        //
        // install possible style:
        if ( !this.brushStyles.isEmpty() ) {
            for ( ConStyles curStyle : this.brushStyles ) {
                brushRes.append( curStyle.getStyleCmd() );
            }
        }
        //
        return brushRes.toString();
    }
    
    
    
    /**
     * Check command string and write down a style (if any).
     * @param cmd the string we will analyze to update brush condition
     * @throws NullPointerException when command string was not given
     */
    public void analyseAndUpdateBrush(final String cmd) throws NullPointerException {
        if ( null == cmd ) {
                String excMsg = "Command string to analyse the brush was not given";
                throw new NullPointerException(excMsg);
        }
        //
        if ( ConWinOutBrush.isColorEscCommand(cmd) ) {
            // remove data of specific color:
            if ( cmd.equals(ConUt.COLOR_DEFAULT) ) this.setBrushColor("");
            // update font color:
            else this.setBrushColor(cmd);
        }
        //
        if ( ConWinOutBrush.isBackgroundEscCommand(cmd) ) {
            // update font background
            if ( cmd.equals(ConUt.BACKGROUND_DEFAULT) ) this.setBrushBackground("");
            else this.setBrushBackground(cmd);
        }
        //
        if ( ConWinOutBrush.isStyleEscCommand(cmd) ) {
            // update styles list
            ConStyles styleToAdd = ConStyles.getByCmd(cmd);
            if ( ConStyles.RESET == styleToAdd ) {
                this.brushStyles.clear();
                this.setBrushColor("");
                this.setBrushBackground("");
            }
            else if ( ConStyles.NONE != styleToAdd ) {
                // it is an empty (null) style
                this.addBrushStyle(styleToAdd);
            }
        }
    }
    
    
    
}
