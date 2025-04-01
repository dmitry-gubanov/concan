package me.dmitrygubanov40.concan.paint;

import java.util.ArrayList;

import me.dmitrygubanov40.concan.utility.ConCord;



/**
 * Parent of all console shapes to draw.
 * Presume only 'lines' and zones of figure, not its color settings of any kind.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public abstract class ConFigure
{
    
    /**
     * @return coordinates of console points of the figure
     */
    public abstract ArrayList<ConCord> getCoords();
    
    /**
     * If we need special chars for drawing (borders or some kind of algorithm).
     * Empty list means no special symbol (use from filling),
     * 'null' in list means to skip character with default filling brush.
     * @return chars for each coordinate from 'getCoords()', or empty list
     */
    public abstract ArrayList<Character> getSymbols();
    
}
