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
    public static final ConCord SHIFT;
    
    static {
        // for position manipulations
        SHIFT_X = 1;
        SHIFT_Y = 1;
        SHIFT = new ConCord(SHIFT_X, SHIFT_Y);
    }
    
    
    
    /**
     * Summarize coordinates by each axis.
     * (1, 10) plus (3, 7) -> (4, 17)
     * @param coordsToSum array of coordinates to sum up
     * @return point with the sum of X and Y coordinates
     * @throws IllegalArgumentException when less than one argument
     */
    public static ConCord getSum(final ConCord... coordsToSum)
                    throws IllegalArgumentException {
        if ( coordsToSum.length <= 0 ) {
            String excMsg = "Need points to sum up. Number of arguments passed: "
                                + coordsToSum.length;
            throw new IllegalArgumentException(excMsg);
        }
        //
        int sumX = coordsToSum[0].getX();
        int sumY = coordsToSum[0].getY();
        //
        for ( int i = 1; i < coordsToSum.length; i++ ) {
            sumX += coordsToSum[ i ].getX();
            sumY += coordsToSum[ i ].getY();
        }
        //
        return new ConCord(sumX, sumY);
    }
    
    /**
     * Pass through coordinates to search for minimal X and Y.
     * Minimal X or Y could be taken from different points:
     * (1, 10) and (3, 7) -> (1, 7)
     * @param coordsToCheck array of coordinates to check
     * @return point with minimal X and Y
     * @throws IllegalArgumentException when less than one argument
     */
    public static ConCord getMin(final ConCord... coordsToCheck)
                            throws IllegalArgumentException {
        if ( coordsToCheck.length <= 0 ) {
            String excMsg = "Need points to compare. Number of arguments passed: "
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
     * @throws IllegalArgumentException when less than one argument
     */
    public static ConCord getMax(final ConCord... coordsToCheck)
                            throws IllegalArgumentException {
        if ( coordsToCheck.length <= 0 ) {
            String excMsg = "Need points to compare. Number of arguments passed: "
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
    
    
    
    /**
     * @param add coordinate we will add
     * @return sum of this point and the 'add'
     * @throws NullPointerException in case of adding the null
     */
    public ConCord plus(final ConCord add) throws NullPointerException {
        if ( null == add ) {
            String excMsg = "Cannot add 'null' to the point";
            throw new NullPointerException(excMsg);
        }
        //
        return ConCord.getSum(this, add);
    }
    /**
     * Calculate the difference: "this" - "subtrahend".
     * @param subtrahend coordinate we will subtract
     * @return mathematical difference between current point and the 'subtrahend'
     * @throws NullPointerException in case of adding the null
     */
    public ConCord minus(final ConCord subtrahend) throws NullPointerException {
        if ( null == subtrahend ) {
            String excMsg = "Cannot subtract 'null' from the point";
            throw new NullPointerException(excMsg);
        }
        //
        // to subtract means to add the same but negative value:
        ConCord subtrahendToSum = new ConCord(-1 * subtrahend.getX(), -1 * subtrahend.getY());
        //
        return ConCord.getSum(this, subtrahendToSum);
    }
    
    
    
    /**
     * Transforms base math coordinates (starting at '0') to console scale.
     * We use math coordinates usually. Console coordinates are used in raw commands.
     * @return new console coordinates which starts from '1' both
     */
    public ConCord addConsoleShift() {
        return this.plus(ConCord.SHIFT);
    }
    
    /**
     * Transforms console coordinates (starting at '1') to base math scale.
     * We use math coordinates usually. Console coordinates are used in raw commands.
     * @return new math coordinates which starts from '0' both
     */
    public ConCord removeConsoleShift() {
        return this.minus(ConCord.SHIFT);
    }
    
    
    
    @Override
    public String toString() {
        //
        String str = "(" + this.getX() + ", " + this.getY() + ")";
        return str;
    }
    
    
    
}
