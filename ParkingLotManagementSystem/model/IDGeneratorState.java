package model;

public class IDGeneratorState
{
    private int nextUserID = IDGenerator.getSaveNextUserID();
    private int nextSpotNum = IDGenerator.getSaveNextSpotNum();
    private int nextTicketID = IDGenerator.getSaveNextTicketID();
    private int nextFineID = IDGenerator.getSaveNextFineID();
    
    public IDGeneratorState() {}
    
    public int getNextUserID() {
        return nextUserID;
    }
    
    public int getNextSpotNum() {
        return nextSpotNum;
    }
    
    public int getNextTicketID() {
        return nextTicketID;
    }
    
    public int getNextFineID() {
        return nextFineID;
    }
}