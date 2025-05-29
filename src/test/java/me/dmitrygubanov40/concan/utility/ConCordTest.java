package me.dmitrygubanov40.concan.utility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class ConCordTest
{
    
    ConCord pos1;
    ConCord pos2;
    ConCord pos3;
    ConCord pos4;
    ConCord pos5;
    ConCord pos6;
    
    ////////////////
    
    public ConCordTest() {
        // Test data loading...
        pos1 = new ConCord( 1,  2);
        pos2 = new ConCord( 2,  3);
        pos3 = new ConCord(-4, -6);
        pos4 = new ConCord(-5,  3);
        pos5 = new ConCord( 4, -6);
        pos6 = new ConCord( 7,  1);
    }
    
    ////////////////
    
    @Test
    public void testGetSum() {
        ConCord expPos1Pos2 = new ConCord(3, 5);
        ConCord factPos1Pos2 = ConCord.getSum(pos1, pos2);
        assertEquals(expPos1Pos2, factPos1Pos2);
        //
        ConCord expPos1Pos2Pos3 = new ConCord(-1, -1);
        ConCord factPos1Pos2Pos3 = ConCord.getSum(pos1, pos2, pos3);
        assertEquals(expPos1Pos2Pos3, factPos1Pos2Pos3);
    }
    
    @Test
    public void testGetMin() {
        ConCord expPos1Pos2Pos3 = new ConCord(-4, -6);
        ConCord factPos1Pos2Pos3 = ConCord.getMin(pos1, pos2, pos3);
        assertEquals(expPos1Pos2Pos3, factPos1Pos2Pos3);
        //
        ConCord expPos1Pos4 = new ConCord(-5, 2);
        ConCord factPos1Pos4 = ConCord.getMin(pos1, pos4);
        assertEquals(expPos1Pos4, factPos1Pos4);
    }
    
    @Test
    public void testGetMax() {
        ConCord expPos1Pos2Pos5 = new ConCord(4, 3);
        ConCord factPos1Pos2Pos5 = ConCord.getMax(pos1, pos2, pos5);
        assertEquals(expPos1Pos2Pos5, factPos1Pos2Pos5);
        //
        ConCord expPos4Pos6 = new ConCord(7, 3);
        ConCord factPos4Pos6 = ConCord.getMax(pos4, pos6);
        assertEquals(expPos4Pos6, factPos4Pos6);
    }
    
    @Test
    public void testConsoleShift() {
        ConCord exp = new ConCord(0, 0);
        ConCord fact = exp.addConsoleShift().removeConsoleShift();
        assertEquals(exp, fact);
        //
        ConCord expOther = new ConCord(3, 7);
        ConCord factOther = expOther.addConsoleShift().removeConsoleShift();
        assertEquals(expOther, factOther);
    }
    
    
    
}
