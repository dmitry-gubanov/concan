package me.dmitrygubanov40.concan.paint;

import java.util.ArrayList;

import me.dmitrygubanov40.concan.utility.ConCord;



/**
 * Pseudographics border - rectangle with specific symbols.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConBorderRect extends ConRect
{
    
    private ConBorderRectType type;
    
    //////////////////////
    
    /**
     * Two points are enough to init rectangle.
     * @param initLeftTop suppose to be the left top corner
     * @param initRightBottom suppose to be the right bottom corner
     * @param initType type of the rectangle border
     */
    public ConBorderRect(final ConCord initLeftTop,
                            final ConCord initRightBottom,
                            final ConBorderRectType initType) {
        super(initLeftTop, initRightBottom);
        this.type = initType;
    }
    public ConBorderRect(final ConCord initLeftTop,
                            final ConCord initRightBottom) {
        this(initLeftTop, initRightBottom, ConBorderRectType.SINGLE);
    }
    
    //////////////
    
    @Override
    public ArrayList<Character> getSymbols() {
        ArrayList<Character> symbolsResult = new ArrayList<>();
        //
        // get real star/end points
        ConCord startPoint = ConCord.getMin(this.leftTop, this.rightBottom);
        ConCord endPoint = ConCord.getMax(this.leftTop, this.rightBottom);
        //
        for ( int y = startPoint.getY(); y <= endPoint.getY(); y++ ) {
            for ( int x = startPoint.getX(); x <= endPoint.getX(); x++ ) {
                // need only border of the figure:
                if ( y == startPoint.getY() || y == endPoint.getY()
                        || x == startPoint.getX() || x == endPoint.getX() ) {
                    //
                    Character curSymbol = null;
                    //
                    if ( y == startPoint.getY() ) {
                        if ( x == startPoint.getX() ) {
                            // left-top corner point
                            curSymbol = this.type.getBorderSymbol(ConBorderRectLineType.TOP_LEFT);
                        }
                        else if ( x == endPoint.getX() ) {
                            // right-top corner point
                            curSymbol = this.type.getBorderSymbol(ConBorderRectLineType.TOP_RIGHT);
                        }
                        else {
                            // top line
                            curSymbol = this.type.getBorderSymbol(ConBorderRectLineType.HORIZONTAL_TOP);
                        }
                    }
                    else if ( y == endPoint.getY() ) {
                        if ( x == startPoint.getX() ) {
                            // left-bottom corner point
                            curSymbol = this.type.getBorderSymbol(ConBorderRectLineType.BOTTOM_LEFT);
                        }
                        else if ( x == endPoint.getX() ) {
                            // right-bottom corner point
                            curSymbol = this.type.getBorderSymbol(ConBorderRectLineType.BOTTOM_RIGHT);
                        }
                        else {
                            // bottom line
                            curSymbol = this.type.getBorderSymbol(ConBorderRectLineType.HORIZONTAL_BOTTOM);
                        }
                    }
                    else {// somewhere between top and bottom
                        if ( x == startPoint.getX() ) {
                            // left-bottom corner point
                            curSymbol = this.type.getBorderSymbol(ConBorderRectLineType.VERTICAL_LEFT);
                        }
                        else if ( x == endPoint.getX() ) {
                            // right-bottom corner point
                            curSymbol = this.type.getBorderSymbol(ConBorderRectLineType.VERTICAL_RIGHT);
                        }
                    }
                    //
                    symbolsResult.add(curSymbol);
                }
            }
        }
        //
        return symbolsResult;
    }
    
    
    
}
