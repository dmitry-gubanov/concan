package me.dmitrygubanov40.concan.utility;



/**
 * Cover for console coordinates.
 * Presume that first coordinate is X, than Y.
 * First position is (0, 0). But console consider (1, 1), so we need a permanent shift.
 * First position is in the left top corner.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConCord
{
    
    // permanent shift of coordinates to comply console position
    // math coordinate + SHIFT -> console position
    public static final int SHIFT_X;
    public static final int SHIFT_Y;
    
    static {
        // for position manipulations
        SHIFT_X = 1;
        SHIFT_Y = 1;
    }
    
    
    
    /**
     * Pass through coordinates to search for minimal X and Y.
     * Minimal X or Y could be taken from different points:
     * (1, 10) and (3, 7) -> (1, 7)
     * @param coordsToCheck array of coordinates to check
     * @return point with minimal X and Y
     * @throws IllegalArgumentException when less than two arguments
     */
    public static ConCord getMin(ConCord... coordsToCheck)
                            throws IllegalArgumentException {
        if ( coordsToCheck.length <= 1 ) {
            String excMsg = "Must compare at least two points. Arguments passed: "
                                + coordsToCheck.length;
            throw new IllegalArgumentException(excMsg);
        }
        //
        int minX = coordsToCheck[0].getX();
        int minY = coordsToCheck[0].getY();
        //
        for ( int i = 1; i < coordsToCheck.length; i++ ) {
            minX = (coordsToCheck[ i ].getX() < minX) ? coordsToCheck[ i ].getX() : minX;
            minY = (coordsToCheck[ i ].getY() < minY) ? coordsToCheck[ i ].getY() : minY;
        }
        //
        return new ConCord(minX, minY);
    }
    
    /**
     * Pass through coordinates to search for maximal X and Y.
     * Maximal X or Y could be taken from different points:
     * (1, 10) and (3, 7) -> (3, 10)
     * @param coordsToCheck array of coordinates to check
     * @return point with maximal X and Y
     * @throws IllegalArgumentException when less than two arguments
     */
    public static ConCord getMax(ConCord... coordsToCheck)
                            throws IllegalArgumentException {
        if ( coordsToCheck.length <= 1 ) {
            String excMsg = "Must compare at least two points. Arguments passed: "
                                + coordsToCheck.length;
            throw new IllegalArgumentException(excMsg);
        }
        //
        int maxX = coordsToCheck[0].getX();
        int maxY = coordsToCheck[0].getY();
        //
        for ( int i = 1; i < coordsToCheck.length; i++ ) {
            maxX = (coordsToCheck[ i ].getX() > maxX) ? coordsToCheck[ i ].getX() : maxX;
            maxY = (coordsToCheck[ i ].getY() > maxY) ? coordsToCheck[ i ].getY() : maxY;
        }
        //
        return new ConCord(maxX, maxY);
    }
    
    
    //////////
    
    
    // actual console coordinates of the point
    private int X;
    private int Y;
    
    
    //////////
    
    
    /**
     * Simple initialization.
     */
    public ConCord() {
        this.X = 0;
        this.Y = 0;
    }
    
    /**
     * Coordinates initialization.
     * @param initX
     * @param initY
     */
    public ConCord(final int initX, final int initY) {
        this.setCord(initX, initY);
    }
    
    
    //////////
    
    
    /**
     * Install coordinates.
     * @param newX
     * @param newY 
     */
    public final void setCord(final int newX, final int newY) {
        this.setX(newX);
        this.setY(newY);
    }
    
    /**
     * Install X.
     * @param newX
     */
    public void setX(final int newX) {
        this.X = newX;
    }
    
    /**
     * Install Y.
     * @param newY
     */
    public void setY(final int newY) {
        this.Y = newY;
    }
    
    
    
    /**
     * @return current X-coordinate
     */
    public int getX() {
        return this.X;
    }
    
    /**
     * @return current Y-coordinate
     */
    public int getY() {
        return this.Y;
    }
    
    
    
    @Override
    public String toString() {
        //
        String str = "(" + this.getX() + ", " + this.getY() + ")";
        return str;
    }
    
    
    
}
