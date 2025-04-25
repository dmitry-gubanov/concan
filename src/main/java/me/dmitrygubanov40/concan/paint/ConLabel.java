package me.dmitrygubanov40.concan.paint;

import java.util.ArrayList;

import me.dmitrygubanov40.concan.utility.ConCord;



/**
 * Simple one line text line somewhere in console.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
public class ConLabel extends ConFigure
{
    
    // begining point of the label
    private final ConCord leftTop;
    // text of the label
    private final String labelText;
    
    //////////////
    
    /**
     * Location and the text of the label.
     * @param initPos where the label is put
     * @param initLabelText text of the label
     */
    public ConLabel(final ConCord initPos, final String initLabelText) {
        this.checkLabelText(initLabelText);
        //
        this.leftTop = initPos;
        this.labelText = initLabelText;
    }
    
    /**
     * Label's text cannot be multi-line.
     * @param labelText label's caption to check
     * @throws IllegalArgumentException when text is inappropriate
     * @throws NullPointerException when there is no text var
     */
    private void checkLabelText(final String labelText) {
        if ( null == labelText ) {
            String excMsg = "Console label is absent";
            throw new NullPointerException(excMsg);
        }
        //
        if ( labelText.length() <= 0 ) {
            String excMsg = "Console label cannot be empty";
            throw new IllegalArgumentException(excMsg);
        }
        //
        if ( labelText.indexOf('\n') != -1
                || labelText.indexOf('\r') != -1 ) {
            String excMsg = "Console label's text must be single line";
            throw new IllegalArgumentException(excMsg);
        }
    }
    
    //////////////
    
    @Override
    public ArrayList<ConCord> getCoords() {
        ArrayList<ConCord> coords = new ArrayList<>();
        //
        // get real star/end points
        final int yCoordLabel = this.leftTop.getY();// never changes
        final int xCoordLabelStart = this.leftTop.getX();// begins from here
        //
        for ( int x = 0; x < this.labelText.length(); x++ ) {
            coords.add(new ConCord(xCoordLabelStart + x, yCoordLabel));
        }
        //
        return coords;
    }
    
    @Override
    public ArrayList<Character> getSymbols() {
        ArrayList<Character> symbolsResult = new ArrayList<>();
        //
        for ( int x = 0; x < this.labelText.length(); x++ ) {
            Character curSymbol = this.labelText.charAt(x);
            symbolsResult.add(curSymbol);
        }
        //
        return symbolsResult;
    }
    
    
    
}
