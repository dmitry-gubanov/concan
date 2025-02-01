package me.dmitrygubanov40.concan.windows;


import java.awt.Color;

import me.dmitrygubanov40.concan.utility.ConCol;
import me.dmitrygubanov40.concan.utility.ConUt;
import me.dmitrygubanov40.concan.utility.Term;



/**
 * Keeper of the current state of WindowOutputBuffer brush.
 * Any interactions with the console restore style conditions.
 * That allows to work different windows and native output without
 * mess of styles.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConWinOutBrush
{
    
    // sequence of Esc-commands to install current brush
    private StringBuilder brush;
    
    
    ///////////////////////////////
    
    
    ConWinOutBrush() {
        this.brush = new StringBuilder("");
    }
    
    
    /**
     * Terminal-based saving of all styles and conditions.
     */
    public void saveTerminalState() {
        System.out.print(ConUt.SAVE);
    }
    /**
     * Terminal-based restoration of all styles and conditions.
     */
    public void restoreTerminalState() {
        System.out.print(ConUt.RESTORE);
    }
    
    
    
    /**
     * This color is considered to be the current background ("empty color").
     * Potentially may differ away from Terminal default background.
     * @return color the for current filling of the zone
     */
    public Color getFillingColor() {
        Color resColor;
        //
        resColor = Term.get().background();
        //
        return resColor;
    }
    // ConCol analog
    public ConCol getFillingColor8B() {
        Color trueColor = this.getFillingColor();
        //
        ConCol resColor = ConCol.getAnalog(trueColor);
        //
        return resColor;
    }
    
    
    
    /**
     * Collect all the necessary data to re-create style (brush)
     * in the window's zone.
     * @return string to output to set actual style
     */
    public String getBrush() {
        // clear cursor settings
        StringBuilder brushRes = new StringBuilder(ConUt.RESET);
        brushRes.append(this.brush);
        //
        return this.brush.toString();
    }
    
    
    
}
