package model;

public class IDGeneratorState
{
    private int nextUserID = IDGenerator.getSaveNextUserID();
    private int nextSpotNum = IDGenerator.getSaveNextSpotNum();
    private int nextTicketID = IDGenerator.getSaveNextTicketID();
    
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
}