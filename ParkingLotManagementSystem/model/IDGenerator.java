package model;


/**
 * Write a description of class IDGenerator here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class IDGenerator {
    
    private static int nextUserID = 0;
    private static int nextSpotNum = 1;
    private static int nextTicketID = 0;
    
    public static int getNextUserID() {
        return nextUserID++;
    }
    
    public static int getNextSpotNum() {
        return nextSpotNum++;
    }
    
    public static int getNextTicketID() {
        return nextTicketID++;
    }
}