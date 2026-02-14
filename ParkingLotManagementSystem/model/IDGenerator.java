package model;

public class IDGenerator {
    
    private static int nextUserID = 0;
    private static int nextSpotNum = 1;
    private static int nextTicketID = 0;
    private static int nextFineID = 0;
    
    public static int getNextUserID() {
        return nextUserID++;
    }
    
    public static int getNextSpotNum() {
        return nextSpotNum++;
    }
    
    public static int getNextTicketID() {
        return nextTicketID++;
    }
    
    public static int getNextFineID() {
        return nextFineID++;
    }
    
    public static int getSaveNextUserID() {
        return nextUserID;
    }
    
    public static int getSaveNextSpotNum() {
        return nextSpotNum;
    }
    
    public static int getSaveNextTicketID() {
        return nextTicketID;
    }
    
    public static int getSaveNextFineID() {
        return nextFineID;
    }
    
    public static void setData(int nextUserID, int nextSpotNum, int nextTicketID, int nextFineID) {
        IDGenerator.nextUserID = nextUserID;
        IDGenerator.nextSpotNum = nextSpotNum;
        IDGenerator.nextTicketID = nextTicketID;
        IDGenerator.nextFineID = nextFineID;
    }
}