package me.dmitrygubanov40.concan.paint;


import java.awt.Color;
import java.util.ArrayList;

import me.dmitrygubanov40.concan.utility.ConCol;
import me.dmitrygubanov40.concan.utility.ConStyles;
import me.dmitrygubanov40.concan.utility.Term;



/**
 * Brush-class for console text primitives.
 * Has two combinations of colors: trueColor-color and brushTrueColor-brushColor,
 * each pair is always set, and they are max close colors (RGB <-> console pre-made color).
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConDrawFill
{
    
    // base color from start
    private static final Color DEFAULT_COLOR;
    // what we put as a texture for the brush
    private static final String DEFAULT_BRUSH;
    // brush color
    private static final Color DEFAULT_BRUSH_COLOR;
    
    // the only length of brush string
    private static final int BRUSH_LENGTH_ALLOWED;
    
    
    static {
        DEFAULT_COLOR = Term.get().background();
        DEFAULT_BRUSH = Term.EMPTY_CHAR;
        DEFAULT_BRUSH_COLOR = Term.get().color();
        BRUSH_LENGTH_ALLOWED = 1;
    }
    
    //////////
    
    // true color base filling color
    private Color trueColor;
    // base filling color (analog)
    private ConCol color;
    
    // additional texture
    private String brush;
    
    // additional texture true color
    private Color brushTrueColor;
    // additional texture color (analog)
    private ConCol brushColor;
    
    // applied styles (with mutual block of opposite styles)
    private ArrayList<ConStyles> styles;
    
    //////////
    
    /**
     * Full-scale constructor of color filling
     * via console color constants.
     * @param initColor base filling color
     * @param initBrush additional texture
     * @param initBrushColor additional texture color
     */
    public ConDrawFill(final ConCol initColor,
                        final String initBrush,
                        final ConCol initBrushColor) {
        this.init(initColor, initBrush, initBrushColor);
    }
    public ConDrawFill(final ConCol initColor, final ConCol initBrushColor) {
        this.init(initColor.getTrueColor(), DEFAULT_BRUSH, initBrushColor.getTrueColor());
    }
    public ConDrawFill(final ConCol initColor) {
        this.init(initColor.getTrueColor(), DEFAULT_BRUSH, DEFAULT_BRUSH_COLOR);
    }
    
    /**
     * Full-scale constructor of true-color fillings.
     * @param initTrueColor base filling color
     * @param initBrush additional texture
     * @param initBrushTrueColor additional texture color
     */
    public ConDrawFill(final Color initTrueColor,
                        final String initBrush,
                        final Color initBrushTrueColor) {
        this.init(initTrueColor, initBrush, initBrushTrueColor);
    }
    public ConDrawFill(final Color initTrueColor, final Color initBrushTrueColor) {
        this.init(initTrueColor, DEFAULT_BRUSH, initBrushTrueColor);
    }
    public ConDrawFill(final Color initTrueColor) {
        this.init(initTrueColor, DEFAULT_BRUSH, DEFAULT_BRUSH_COLOR);
    }
    
    /**
     * Most simple initialization with no arguments.
     */
    public ConDrawFill() {
        this.init(DEFAULT_COLOR, DEFAULT_BRUSH, DEFAULT_BRUSH_COLOR);
    }
    
    
    
    /**
     * Install object parameters via console coded colors.
     * @param initColor base filling console color
     * @param initBrush additional texture (char)
     * @param initBrushColor additional texture console color
     */
    private void init(final ConCol initColor,
                        final String initBrush,
                        final ConCol initBrushColor) {
        this.setColor(initColor);
        this.setBrush(initBrush);
        this.setBrushColor(initBrushColor);
        //
        this.styles = new ArrayList<>();
    }
    
    /**
     * Install object parameters via true colors.
     * @param initTrueColor base filling console color
     * @param initBrush additional texture (char)
     * @param initBrushTrueColor additional texture console color
     */
    private void init(final Color initTrueColor,
                        final String initBrush,
                        final Color initBrushTrueColor) {
        this.setColor(initTrueColor);
        this.setBrush(initBrush);
        this.setBrushColor(initBrushTrueColor);
        //
        this.styles = new ArrayList<>();
    }
    
    
    
    /**
     * @param brushToCheck
     * @return 'true' if brush is OK, 'false' otherwise
     * @throws NullPointerException when there is no brush char
     */
    protected boolean checkBrush(final String brushToCheck) {
        if ( null == brushToCheck ) {
            String excMsg = "Brush is absent";
            throw new NullPointerException(excMsg);
        }
        //
        boolean result = (brushToCheck.length() == ConDrawFill.BRUSH_LENGTH_ALLOWED);
        return result;
    }
    
    /**
     * @param colorToCheck
     * @throws NullPointerException when there is no color
     */
    protected void checkColor(final Color colorToCheck) {
        if ( null == colorToCheck ) {
            String excMsg = "Color is not set";
            throw new NullPointerException(excMsg);
        }
    }
    
    /**
     * @param concolToCheck
     * @throws NullPointerException when there is no console color
     */
    protected void checkColor(final ConCol concolToCheck) {
        if ( null == concolToCheck ) {
            String excMsg = "Console color is not set";
            throw new NullPointerException(excMsg);
        }
    }
    
    
    
    /**
     * Getters block
     */
    public ConCol getConCol() {
        return this.color;
    }
    public Color getColor() {
        return this.trueColor;
    }
    public String getBrush() {
        return this.brush;
    }
    public ConCol getBrushConCol() {
        return this.brushColor;
    }
    public Color getBrushColor() {
        return this.brushTrueColor;
    }
    public ArrayList<ConStyles> getStyles() {
        return this.styles;
    }
    
    
    /**
     * To check and set new brush character.
     * @param brush additional texture to fill
     */
    public void setBrush(String brush) throws IllegalArgumentException {
        //
        if ( !this.checkBrush(brush) ) {
            String excMsg = "Cannot assign brush: '" + brush + "'";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.brush = brush;
    }
    
    
    
    /**
     * Other setters block.
     */
    public void setColor(final ConCol color) {
        checkColor(trueColor);
        //
        this.trueColor = color.getTrueColor();
        this.color = color;
    }
    public void setColor(final Color trueColor) {
        checkColor(trueColor);
        //
        this.trueColor = trueColor;
        this.color = ConCol.getAnalog(trueColor);
    }
    public void setBrushColor(final ConCol brushColor) {
        checkColor(trueColor);
        //
        this.brushTrueColor = brushColor.getTrueColor();
        this.brushColor = brushColor;
    }
    public void setBrushColor(final Color brushTrueColor) {
        checkColor(trueColor);
        //
        this.brushTrueColor = brushTrueColor;
        this.brushColor = ConCol.getAnalog(brushTrueColor);
    }
    
    
    
    /**
     * Whether the filling using limited colors of console,
     * or full RGB-palette.
     * @return 'true' in case colors are limited to console constants
     */
    public boolean isConCol() {
        boolean res;
        //
        // Both main color and brush color are the same RGB in
        // TrueColor and console constants.
        res = this.trueColor.equals( this.color.getTrueColor() )
                && this.brushTrueColor.equals( this.brushColor.getTrueColor() );
        //
        return res;
    }
    
    
    
    /**
     * Will add new style if style is not applied already.
     * Skips the same (already applied) style.
     * Ignores opposite style (bold <-> bold_off).
     * @param styleToAdd standard console style to add in the filling
     * @throws NullPointerException if new style does not exist
     * @throws IllegalArgumentException when try to add opposite style ("bold_off" to "bold")
     */
    public void addStyle(ConStyles styleToAdd)
                    throws NullPointerException {
        if ( null == styleToAdd ) {
            String excMsg = "Cannot add new style to the list";
            throw new NullPointerException(excMsg);
        }
        //
        // can not add the same style, only one the same style at once
        if ( this.styles.contains(styleToAdd) ) {
            return;
        }
        // can not add the "opposite" style ("bold" to "bold_off")
        // when non-opposite style is in array
        ConStyles oppStyle = styleToAdd.getOppositeStyleOrNull();
        if ( null != oppStyle && this.styles.contains(oppStyle) ) {
            String excMsg = "Cannot add opposite style '" + styleToAdd
                                + "' to the list (opposite to '"
                                + oppStyle + "')";
            throw new IllegalArgumentException(excMsg);
        }
        //
        this.styles.add(styleToAdd);
    }
    
    /**
     * Will remove the style if it is already applied.
     * Skips the absent in list style.
     * @param styleToRemove standard console style to remove from the filling
     * @throws NullPointerException if style to remove does not exist
     */
    public void removeStyle(ConStyles styleToRemove)
                    throws NullPointerException {
        if ( null == styleToRemove ) {
            String excMsg = "Cannot remove null-style from the list";
            throw new NullPointerException(excMsg);
        }
        //
        if ( !this.styles.contains(styleToRemove) ) {
            return;
        }
        //
        this.styles.remove(styleToRemove);
    }
    
    /**
     * Will update (overwrite) styles.
     * @param newStyles
     * @throws NullPointerException if new list does not exist
     */
    public void copyStyles(ArrayList<ConStyles> newStyles)
                    throws NullPointerException {
        if ( null == newStyles ) {
            String excMsg = "Cannot update styles list";
            throw new NullPointerException(excMsg);
        }
        //
        this.styles = new ArrayList<>();
        //
        for ( ConStyles curStyle : newStyles ) {
            this.styles.add(curStyle);
        }
    }
    
    
    
}
