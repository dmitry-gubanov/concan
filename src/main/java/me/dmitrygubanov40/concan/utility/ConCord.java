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
