package me.dmitrygubanov40.concan.paint;

import java.awt.Color;
import java.util.ArrayList;

import me.dmitrygubanov40.concan.utility.ConCol;
import me.dmitrygubanov40.concan.utility.ConCord;
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
    public static void style(ConDrawFillStyle styleToAdd) {
        ConDraw.staticFill.addStyle(styleToAdd);
    }
    public static void styleRemove(ConDrawFillStyle styleToRemove) {
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
     * @param fill parameters of filling
     * @throws NullPointerException if empty coordinates or filling parameters
     */
    private static void doDraw(final ArrayList<ConCord> coords, final ConDrawFill fill)
                            throws NullPointerException {
        if ( null == coords ) {
            String excMsg = "No coordinates for drawing";
            throw new NullPointerException(excMsg);
        }
        if ( null == fill ) {
            String excMsg = "No filling parameters for drawing";
            throw new NullPointerException(excMsg);
        }
        //
        ConUt conTool = new ConUt();
        //
        // save cursor:
        conTool.sendSave();
        //
        // install filling, ConCol/Color approaches:
        if ( fill.isConCol() ) {
            // pre-made console colors
            conTool.sendBackground8B(fill.getConCol());
            conTool.sendColor8B(fill.getBrushConCol());
        } else {
            // TrueColor in RGB
            conTool.sendBackground(fill.getColor());
            conTool.sendColor(fill.getBrushColor());
        }
        //
        // add styles we have, in order they were added
        ArrayList<ConDrawFillStyle> currentStyles = fill.getStyles();
        if ( !currentStyles.isEmpty() ) {
            for ( ConDrawFillStyle curStyle : currentStyles ) {
                System.out.print(curStyle.getStyleCmd());
            }
        }
        //
        // main drawing:
        for ( ConCord curDrawPoint : coords ) {
            //
            // preventer of leaving terminal window
            if ( curDrawPoint.getX() > Term.get().maxX()
                    || curDrawPoint.getY() > Term.get().maxY() ) {
                continue;
            }
            //
            conTool.sendGoto(curDrawPoint);
            System.out.print(fill.getBrush());
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
        //
        ArrayList<ConCord> coordsToDraw = figureToDraw.getCoords();
        ConDraw.doDraw(coordsToDraw, fill);
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
        final ConCol barBrushColor = ConDraw.staticFill.getBrushConCol();
        ConDrawFill fillingObj = new ConDrawFill(color, barBrush, barBrushColor);
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
        final ConCol rectBrushColor = ConDraw.staticFill.getBrushConCol();
        ConDrawFill fillingObj = new ConDrawFill(color, rectBrush, rectBrushColor);
        fillingObj.copyStyles(ConDraw.staticFill.getStyles());
        //
        ConDraw.rect(leftTop, rightBottom, fillingObj);
    }
    //
    public static void rect(final ConCord leftTop, final ConCord rightBottom) {
        ConDraw.rect(leftTop, rightBottom, ConDraw.staticFill);
    }
    
    // end of Rect //
    
    
    
    ////////////
    
    // object's brush fields
    public ConDrawFill currentFill;
    
    
    public ConDraw() {
        this.currentFill = new ConDrawFill();
    }
    
    
    ////////////
    
    
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
    
    
}
