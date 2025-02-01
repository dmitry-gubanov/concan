package me.dmitrygubanov40.concan.utility;

import java.awt.Color;



/**
 * Single Terminal class-object.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class Term
{
    
    // our only Terminal
    private static Term term;
    
    // our only access point Terminal
    public static Term get() {
        if ( null == Term.term ) {
            Term.term = new Term();
        }
        //
        return Term.term;
    }
    
    
    // letters are of this color by default
    private static final Color DEFAULT_COLOR;
    // background is this by default
    private static final Color DEFAULT_BACKGROUND;
    
    // character just to cover space (empty)
    public static final String EMPTY_CHAR;
    
    
    static {
        DEFAULT_COLOR = ConCol.WHITE.getTrueColor();
        DEFAULT_BACKGROUND = ConCol.GREYSCALE4.getTrueColor();
        //
        EMPTY_CHAR = " ";
    }
    
    
    /////////////////////////////////
    
    
    // max X and Y axises coordinates in the instance
    private final ConCord maxCoords;
    
    // max width and height of the instance
    private final ConCord maxSize;
    
    // suppose current terminal color settings (24b)
    private Color defaultColor;
    private Color defaultBackground;
    // shortcut for ConCol
    private ConCol defaultColor8b;
    private ConCol defaultBackground8b;
    
    
    /**
     * Single element constructor.
     */
    private Term() {
        this.maxCoords = ConUt.getTerminalMaxCoord();
        this.maxSize = ConUt.getTerminalSize();
        //
        this.updateColors(DEFAULT_COLOR, DEFAULT_BACKGROUND);
    }
    
    
    
    /**
     * Simultaneously update TrueColor and ConCol-color in the Terminal. 
     * @param setColor TrueColor font
     * @param setBackground TrueColor background
     * @throws NullPointerException in case of null-pointer colors
     */
    private void updateColors(final Color setColor, final Color setBackground)
                    throws NullPointerException {
        if ( null == setColor || null == setBackground ) {
            String excMsg = "Foreground and/or background colors for Terminal are not given";
            throw new NullPointerException(excMsg);
        }
        //
        this.defaultColor = setColor;
        this.defaultBackground = setBackground;
        //
        this.defaultColor8b = ConCol.getAnalog(this.defaultColor);
        this.defaultBackground8b = ConCol.getAnalog(this.defaultBackground);
    }
    private void updateColors(final ConCol setColor, final ConCol setBackground)
                    throws NullPointerException {
        if ( null == setColor || null == setBackground ) {
            String excMsg = "Foreground and/or background colors for Terminal are not given";
            throw new NullPointerException(excMsg);
        }
        //
        this.defaultColor = setColor.getTrueColor();
        this.defaultBackground = setBackground.getTrueColor();
        //
        this.defaultColor8b = setColor;
        this.defaultBackground8b = setBackground;
    }
    
    /**
     * To change pseudo-default font color in Terminal.
     * @param setColor 
     */
    public void color(final ConCol setColor) {
        this.updateColors(setColor, this.background8B());
    }
    public void color(final Color setColor) {
        this.updateColors(setColor, this.background());
    }
    
    /**
     * To change pseudo-default background color in Terminal.
     * @param setBg 
     */
    public void background(final ConCol setBg) {
        this.updateColors(this.color8B(), setBg);
    }
    public void background(final Color setBg) {
        this.updateColors(this.color(), setBg);
    }
    
    
    
    /**
     * Getters of terminal max coordinates.
     * 'get'-naming saved in access method.
     */
    public ConCord maxCoord() {
        return this.maxCoords;
    }
    public int maxX() {
        return this.maxCoords.getX();
    }
    public int maxY() {
        return this.maxCoords.getY();
    }
    
    /**
     * Getters of terminal max size.
     * 'get'-naming saved in access method.
     */
    public ConCord maxSize() {
        return this.maxSize;
    }
    public int maxWidth() {
        return this.maxSize.getX();
    }
    public int maxHeight() {
        return this.maxSize.getY();
    }
    
    /**
     * Getters of terminal color palette.
     * 'get'-naming saved in access method.
     */
    public Color color() {
        return this.defaultColor;
    }
    public ConCol color8B() {
        return this.defaultColor8b;
    }
    public Color background() {
        return this.defaultBackground;
    }
    public ConCol background8B() {
        return this.defaultBackground8b;
    }
    
    
    /////////////////////////////////////
    
    /**
     * DOS-like CLS:
     *  - empty page,
     *  - reset all font styles,
     *  - (0, 0) position.
     */
    public void cls() {
        ConUt tool = new ConUt();
        //
        tool.sendReset();
        tool.sendColor(this.color());
        tool.sendBackground(this.background());
        tool.sendEraseAll();
        tool.sendGoto(0, 0);
    }
    
    
    
    /**
     * Terminal-based saving of all styles and conditions.
     */
    public void save() {
        System.out.print(ConUt.SAVE);
    }
    /**
     * Terminal-based restoration of all styles and conditions.
     */
    public void restore() {
        System.out.print(ConUt.RESTORE);
    }
    
    
    
}
