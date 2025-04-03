package me.dmitrygubanov40.concan.paint;

import java.util.ArrayList;

import me.dmitrygubanov40.concan.utility.ConCord;



/**
 * Rectangle's shape for console.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConRect extends ConFigure
{
    
    // presume start point of bar coordinates
    protected final ConCord leftTop;
    
    // presume end point of bar coordinates
    protected final ConCord rightBottom;
    
    //////////////
    
    /**
     * Two points are enough to init rectangle.
     * @param initLeftTop suppose to be the left top corner
     * @param initRightBottom suppose to be the right bottom corner
     */
    public ConRect(final ConCord initLeftTop, final ConCord initRightBottom) {
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
                // need only border of the figure
                if ( y == startPoint.getY() || y == endPoint.getY()
                        || x == startPoint.getX() || x == endPoint.getX() ) {
                    coords.add(new ConCord(x, y));
                }
            }
        }
        //
        return coords;
    }
    
    @Override
    public ArrayList<Character> getSymbols() {
        ArrayList<Character> noSymbolsNecessary = new ArrayList<>();
        return noSymbolsNecessary;
    }
    
    
    
}
