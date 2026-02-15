package storage;

import model.IDGenerator;

/**
 * State object for persisting ID generator counters to JSON.
 * Used by SaveData and LoadData to maintain ID sequences across application restarts.
 * 
 * @author Parking Lot Management System
 * @version 1.0
 */
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
