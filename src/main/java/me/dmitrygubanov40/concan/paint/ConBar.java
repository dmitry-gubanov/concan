package me.dmitrygubanov40.concan.paint;

import java.util.ArrayList;

import me.dmitrygubanov40.concan.utility.ConCord;



/**
 * Bar's shape for console.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConBar extends ConFigure
{
    
    // presume start point of bar coordinates
    private final ConCord leftTop;
    
    // presume end point of bar coordinates
    private final ConCord rightBottom;
    
    //////////////
    
    /**
     * Two points are enough to init bar.
     * @param initLeftTop suppose to be bar's left top corner
     * @param initRightBottom suppose to be bar's right bottom corner
     */
    public ConBar(final ConCord initLeftTop, final ConCord initRightBottom) {
        this.leftTop = initLeftTop;
        this.rightBottom = initRightBottom;
    }
    
    //////////////
    
    @Override
    public ArrayList<ConCord> getCoords() {
        ArrayList<ConCord> coords = new ArrayList<>();
        //
        // get real star/end points
        ConCord startPoint = ConCord.getMin(this.leftTop, this.rightBottom);
        ConCord endPoint = ConCord.getMax(this.leftTop, this.rightBottom);
        //
        for ( int y = startPoint.getY(); y <= endPoint.getY(); y++ ) {
            for ( int x = startPoint.getX(); x <= endPoint.getX(); x++ ) {
                coords.add(new ConCord(x, y));
            }
        }
        //
        return coords;
    }
    
    
    
}
