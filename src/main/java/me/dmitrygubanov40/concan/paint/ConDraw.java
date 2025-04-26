package me.dmitrygubanov40.concan.paint;

import java.awt.Color;
import java.util.ArrayList;

import me.dmitrygubanov40.concan.utility.ConCol;
import me.dmitrygubanov40.concan.utility.ConCord;
import me.dmitrygubanov40.concan.utility.ConStyles;
import me.dmitrygubanov40.concan.utility.ConUt;
import me.dmitrygubanov40.concan.utility.Term;



/**
 * Drawer of console primitives without buffering.
 * Operate terminal's console and chars places, not geometrical space.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConDraw
{
    
    // static brush
    private static ConDrawFill staticFill;
    
    
    static {
        // initialized with default values in 'ConDrawFill'
        ConDraw.staticFill = new ConDrawFill();
    }
    
    ////////////
    
    
    /**
     * Install new 'ConDraw.staticFill'.
     * @param newFill new filling attributes
     * @throws NullPointerException if empty new filling
     */
    private static void updateStaticFill(final ConDrawFill newFill)
                            throws NullPointerException {
        if ( null == newFill ) {
            String excMsg = "Cannot apply filling parameters";
            throw new NullPointerException(excMsg);
        }
        //
        ConDraw.staticFill = newFill;
    }
    
    /**
     * All necessary approaches to update static filling,
     * including 'ConCol'/'Color' combinations.
     */
    public static void fill(final ConDrawFill fill) {
        ConDraw.updateStaticFill(fill);
    }
    //
    public static void fill(final ConCol color,
                                final String brush,
                                final ConCol brushColor) {
        ConDraw.fill(new ConDrawFill(color, brush, brushColor));
    }
    public static void fill(final Color color,
                                final String brush,
                                final Color brushColor) {
        ConDraw.fill(new ConDrawFill(color, brush, brushColor));
    }
    //
    public static void fill(final ConCol color) {
        ConDraw.fill(new ConDrawFill(color,
                                        ConDraw.staticFill.getBrush(),
                                        ConDraw.staticFill.getBrushConCol()));
    }
    public static void fill(final Color color) {
        ConDraw.fill(new ConDrawFill(color,
                                        ConDraw.staticFill.getBrush(),
                                        ConDraw.staticFill.getBrushColor()));
    }
    
    /**
     * Static filling styles updaters.
     */
    public static void style(ConStyles styleToAdd) {
        ConDraw.staticFill.addStyle(styleToAdd);
    }
    public static void styleRemove(ConStyles styleToRemove) {
        ConDraw.staticFill.removeStyle(styleToRemove);
    }
    
    
    // Universal drawer of primitive //
    
    /**
     * Basic drawer of any primitive on console.
     * Temporary switches cursor, draws,
     * and returns cursor with all previous settings.
     * Avoiding console borders, i.e. do not move new line when we have met end of console.
     * Use for all other drawings, which must prepare only coordinates.
     * @param coords all console positions we should paint
     * @param symbols special characters if necessary
     * @param fill parameters of filling
     * @throws NullPointerException if empty coordinates or filling parameters
     * @throws IllegalArgumentException when 'coords' and 'symbols' are inappropriate
     */
    private static void doDraw(final ArrayList<ConCord> coords,
                                final ArrayList<Character> symbols,
                                final ConDrawFill fill)
                            throws NullPointerException {
        if ( null == coords ) {
            String excMsg = "No coordinates for drawing";
            throw new NullPointerException(excMsg);
        }
        if ( null == symbols ) {
            String excMsg = "No symbols for drawing";
            throw new NullPointerException(excMsg);
        }
        if ( null == fill ) {
            String excMsg = "No filling parameters for drawing";
            throw new NullPointerException(excMsg);
        }
        if ( !symbols.isEmpty() && symbols.size() != coords.size() ) {
            String excMsg = "Number of coordinates and symbols discrods";
            throw new IllegalArgumentException(excMsg);
        }
        //
        ConUt conTool = new ConUt();
        //
        // save cursor:
        conTool.sendSave();
        //
        // install filling colors:
        conTool.sendBackground(fill.getColor());
        conTool.sendColor(fill.getBrushColor());
        //
        // add styles we have, in order they were added
        ArrayList<ConStyles> currentStyles = fill.getStyles();
        if ( !currentStyles.isEmpty() ) {
            for ( ConStyles curStyle : currentStyles ) {
                System.out.print(curStyle.getStyleCmd());
            }
        }
        //
        // main drawing:
        for ( int i = 0; i < coords.size(); i++ ) {
            ConCord curDrawPoint = coords.get(i);
            //
            // preventer of leaving terminal window
            if ( curDrawPoint.getX() > Term.get().maxX()
                    || curDrawPoint.getY() > Term.get().maxY() ) {
                continue;
            }
            //
            String curBrush = fill.getBrush();
            if ( !symbols.isEmpty() && null != symbols.get(i) ) {
                curBrush = symbols.get(i).toString();
            }
            //
            conTool.sendGoto(curDrawPoint);
            System.out.print(curBrush);
        }
        //
        // restore cursor:
        conTool.sendRestore();
    }
    
    /**
     * @param figureToDraw what we want to draw
     * @param fill how we want to draw the figure
     * @throws NullPointerException when figure is not given
     */
    private static void draw(final ConFigure figureToDraw,
                                final ConDrawFill fill) {
        if ( null == figureToDraw ) {
            String excMsg = "Where is no figure to draw";
            throw new NullPointerException(excMsg);
        }
        if ( null == fill ) {
            String excMsg = "Where is no fillment for the figure to draw";
            throw new NullPointerException(excMsg);
        }
        //
        ArrayList<ConCord> coordsToDraw = figureToDraw.getCoords();
        ArrayList<Character> charactersToDraw = figureToDraw.getSymbols();
        ConDraw.doDraw(coordsToDraw, charactersToDraw, fill);
    }
    
    
    
    // Bar //
    
    /**
     * Most wide console bar drawer.
     * Independent from 'staticFill'.
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     * @param fill filling's data of the bar
     */
    public static void bar(final ConCord leftTop,
                            final ConCord rightBottom,
                            final ConDrawFill fill) {
        ConFigure bar = new ConBar(leftTop, rightBottom);
        ConDraw.draw(bar, fill);
    }
    
    /**
     * Full arguments bar version.
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     * @param color main color
     * @param brush brush char texture
     * @param brushColor brush char texture color
     */
    public static void bar(final ConCord leftTop,
                            final ConCord rightBottom,
                            final ConCol color,
                            final String brush,
                            final ConCol brushColor) {
        ConDrawFill fillingObj = new ConDrawFill(color, brush, brushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        ConDraw.bar(leftTop, rightBottom, fillingObj);
    }
    //
    public static void bar(final ConCord leftTop,
                            final ConCord rightBottom,
                            final ConCol color) {
        final String barBrush = ConDraw.staticFill.getBrush();
        final Color barBrushColor = ConDraw.staticFill.getBrushColor();
        ConDrawFill fillingObj = new ConDrawFill(color.getTrueColor(), barBrush, barBrushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        //
        ConDraw.bar(leftTop, rightBottom, fillingObj);
    }
    //
    public static void bar(final ConCord leftTop, final ConCord rightBottom) {
        ConDraw.bar(leftTop, rightBottom, ConDraw.staticFill);
    }
    
    // end of Bar //
    
    
    // Rect //
    
    /**
     * Most wide console rectangle drawer.
     * Independent from 'staticFill'.
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     * @param fill filling's data of the rectangle's line
     */
    public static void rect(final ConCord leftTop,
                            final ConCord rightBottom,
                            final ConDrawFill fill) {
        ConFigure rect = new ConRect(leftTop, rightBottom);
        ConDraw.draw(rect, fill);
    }
    
    /**
     * Full arguments rectangle version.
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     * @param color main color
     * @param brush brush char texture
     * @param brushColor brush char texture color
     */
    public static void rect(final ConCord leftTop,
                            final ConCord rightBottom,
                            final ConCol color,
                            final String brush,
                            final ConCol brushColor) {
        ConDrawFill fillingObj = new ConDrawFill(color, brush, brushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        ConDraw.rect(leftTop, rightBottom, fillingObj);
    }
    //
    public static void rect(final ConCord leftTop,
                            final ConCord rightBottom,
                            final ConCol color) {
        final String rectBrush = ConDraw.staticFill.getBrush();
        final Color rectBrushColor = ConDraw.staticFill.getBrushColor();
        ConDrawFill fillingObj = new ConDrawFill(color.getTrueColor(), rectBrush, rectBrushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        //
        ConDraw.rect(leftTop, rightBottom, fillingObj);
    }
    //
    public static void rect(final ConCord leftTop, final ConCord rightBottom) {
        ConDraw.rect(leftTop, rightBottom, ConDraw.staticFill);
    }
    
    // end of Rect //
    
    
    // Rectangles with borders (borders) //
    
    /**
     * Most wide console any border drawer.
     * Independent from 'staticFill'.
     * @param type border type (bold, double, etc.)
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     * @param fill filling's data
     */
    public static void border(final ConBorderRectType type,
                                final ConCord leftTop,
                                final ConCord rightBottom,
                                final ConDrawFill fill) {
        ConFigure border;
        border = new ConBorderRect(leftTop, rightBottom, type);
        ConDraw.draw(border, fill);
    }
    // default single-line version:
    public static void border(final ConCord leftTop,
                                        final ConCord rightBottom,
                                        final ConDrawFill fill) {
        ConDraw.border(ConBorderRectType.SINGLE, leftTop, rightBottom, fill);
    }
    
    /**
     * Full arguments for any border's version.
     * @param type border type (bold, double, etc.)
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     * @param color main color
     * @param brushColor brush char texture color
     */
    public static void border(final ConBorderRectType type,
                                final ConCord leftTop,
                                final ConCord rightBottom,
                                final ConCol color,
                                final ConCol brushColor) {
        // no need of brush
        final String noBrushForBorder = " ";
        ConDrawFill fillingObj = new ConDrawFill(color, noBrushForBorder, brushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        ConDraw.border(type, leftTop, rightBottom, fillingObj);
    }
    // default single-line version:
    public static void border(final ConCord leftTop,
                                final ConCord rightBottom,
                                final ConCol color,
                                final ConCol brushColor) {
        // no need of brush
        final String noBrushForBorder = " ";
        ConDrawFill fillingObj = new ConDrawFill(color, noBrushForBorder, brushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        ConDraw.border(leftTop, rightBottom, fillingObj);
    }
    
    
    public static void border(final ConBorderRectType type,
                                final ConCord leftTop,
                                final ConCord rightBottom,
                                final ConCol color) {
        final Color rectBrushColor = ConDraw.staticFill.getBrushColor();
        final String noBrushForBorder = " ";
        ConDrawFill fillingObj = new ConDrawFill(color.getTrueColor(), noBrushForBorder, rectBrushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        //
        ConDraw.border(type, leftTop, rightBottom, fillingObj);
    }
    // default single-line version:
    public static void border(final ConCord leftTop,
                                final ConCord rightBottom,
                                final ConCol color) {
        final Color rectBrushColor = ConDraw.staticFill.getBrushColor();
        final String noBrushForBorder = " ";
        ConDrawFill fillingObj = new ConDrawFill(color.getTrueColor(), noBrushForBorder, rectBrushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        //
        ConDraw.border(leftTop, rightBottom, fillingObj);
    }
    
    public static void border(final ConBorderRectType type,final ConCord leftTop, final ConCord rightBottom) {
        ConDraw.border(type, leftTop, rightBottom, ConDraw.staticFill);
    }
    // default single-line version:
    public static void border(final ConCord leftTop, final ConCord rightBottom) {
        ConDraw.border(leftTop, rightBottom, ConDraw.staticFill);
    }
    
    // end of rectangles with borders (borders) //
    
    
    // Label //
    
    /**
     * Most wide console label drawer.
     * Independent from 'staticFill'.
     * @param leftTop start text position
     * @param labelCaption text to be put
     * @param fill filling's data of the rectangle's line
     */
    public static void label(final ConCord leftTop,
                                final String labelCaption,
                                final ConDrawFill fill) {
        ConFigure rect = new ConLabel(leftTop, labelCaption);
        ConDraw.draw(rect, fill);
    }
    
    /**
     * Full arguments label version.
     * @param leftTop start text position
     * @param labelCaption text to be put
     * @param textColor text color
     * @param backgroundColor text background color
     */
    public static void label(final ConCord leftTop,
                                final String labelCaption,
                                final ConCol textColor,
                                final ConCol backgroundColor) {
        final String noBrushForLabel = " ";
        ConDrawFill fillingObj = new ConDrawFill(backgroundColor,
                                                    noBrushForLabel,
                                                    textColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        ConDraw.label(leftTop, labelCaption, fillingObj);
    }
    //
    public static void label(final ConCord leftTop,
                                final String labelCaption,
                                final ConCol textColor) {
        final String labelBrush = ConDraw.staticFill.getBrush();// no real usage
        final Color backgroundColor = ConDraw.staticFill.getColor();
        ConDrawFill fillingObj = new ConDrawFill(backgroundColor, labelBrush, textColor.getTrueColor());
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        //
        ConDraw.label(leftTop, labelCaption, fillingObj);
    }
    //
    public static void label(final ConCord leftTop, final String labelCaption) {
        ConDraw.label(leftTop, labelCaption, ConDraw.staticFill);
    }
    
    // end of Label //
    
    
    ////////////
    
    
    
    // object's brush fields
    private ConDrawFill currentFill;
    
    
    public ConDraw() {
        this.currentFill = new ConDrawFill();
    }
    
    
    ////////////
    
    /**
     * What brush will paint the dynamic object?
     * @return current brush settings object
     */
    public ConDrawFill getCurrentFill() {
        return currentFill;
    }
    /**
     * Dynamic ConDraw object will paint with such brush.
     * @param setFill brush settings we want to install
     * @throws NullPointerException when there is no brush
     */
    public void setCurrentFill(ConDrawFill setFill) {
        if ( null == setFill ) {
            String excMsg = "Where is no brush filling for console painting";
            throw new NullPointerException(excMsg);
        }
        //
        this.currentFill = setFill;
    }
    
    
    
    /**
     * Through static 'draw'-method paint in console via an object.
     * @param figureToDraw our object to be in console
     * @throws NullPointerException if there is no figure
     */
    public void drawFigure(ConFigure figureToDraw) {
        if ( null == figureToDraw ) {
            String excMsg = "Where is no figure to draw";
            throw new NullPointerException(excMsg);
        }
        //
        ConDraw.draw(figureToDraw, this.currentFill);
    }
    
    
    
    /**
     * Make the object to draw the bar.
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     */
    public void drawBar(final ConCord leftTop,
                            final ConCord rightBottom) {
        ConFigure bar = new ConBar(leftTop, rightBottom);
        this.drawFigure(bar);
    }
    
    
    
    /**
     * Make the object to draw the rectangle.
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     */
    public void drawRect(final ConCord leftTop,
                            final ConCord rightBottom) {
        ConFigure rect = new ConRect(leftTop, rightBottom);
        this.drawFigure(rect);
    }
    
    
    
    /**
     * Make the object to draw the single border rectangle.
     * @param borderType requested border type
     * @param leftTop start coordinate (presume)
     * @param rightBottom end coordinate (presume)
     */
    public void drawBorderRect(final ConBorderRectType borderType,
                                    final ConCord leftTop,
                                    final ConCord rightBottom) {
        ConFigure singleBorderRect = new ConBorderRect(leftTop, rightBottom, borderType);
        this.drawFigure(singleBorderRect);
    }
    public void drawBorderRect(final ConCord leftTop,
                                    final ConCord rightBottom) {
        this.drawBorderRect(ConBorderRectType.SINGLE, leftTop, rightBottom);
    }
    
    
    
    /**
     * Make the object to draw the single line text label into the console.
     * @param leftTop label's position
     * @param text label's caption
     */
    public void drawLabel(final ConCord leftTop, final String text) {
        ConFigure label = new ConLabel(leftTop, text);
        this.drawFigure(label);
    }
    
    
    
}
