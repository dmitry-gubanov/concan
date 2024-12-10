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
    
}
