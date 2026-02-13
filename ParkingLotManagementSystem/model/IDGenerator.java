package model;

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
    
    public static int getSaveNextUserID() {
        return nextUserID;
    }
    
    public static int getSaveNextSpotNum() {
        return nextSpotNum;
    }
    
    public static int getSaveNextTicketID() {
        return nextTicketID;
    }
    
    public static void setData(int nextUserID, int nextSpotNum, int nextTicketID) {
        IDGenerator.nextUserID = nextUserID;
        IDGenerator.nextSpotNum = nextSpotNum;
        IDGenerator.nextTicketID = nextTicketID;
    }
}