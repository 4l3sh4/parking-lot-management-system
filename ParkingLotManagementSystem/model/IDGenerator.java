package model;

public class IDGenerator {

    private static int nextUserID = 1;
    private static int nextSpotNum = 1;
    private static int nextTicketID = 1;
    private static int nextFineID = 1;

    public static synchronized int getNextUserID() { return nextUserID++; }
    public static synchronized int getNextSpotNum() { return nextSpotNum++; }
    public static synchronized int getNextTicketID() { return nextTicketID++; }
    public static synchronized int getNextFineID() { return nextFineID++; }

    public static synchronized int getSaveNextUserID() { return nextUserID; }
    public static synchronized int getSaveNextSpotNum() { return nextSpotNum; }
    public static synchronized int getSaveNextTicketID() { return nextTicketID; }
    public static synchronized int getSaveNextFineID() { return nextFineID; }

    public static synchronized void setData(int nextUserID, int nextSpotNum, int nextTicketID, int nextFineID) {
        IDGenerator.nextUserID = Math.max(1, nextUserID);
        IDGenerator.nextSpotNum = Math.max(1, nextSpotNum);
        IDGenerator.nextTicketID = Math.max(1, nextTicketID);
        IDGenerator.nextFineID = Math.max(1, nextFineID);
    }
}
