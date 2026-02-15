package storage;

public class IDGeneratorState {

    private int nextUserID;
    private int nextSpotNum;
    private int nextTicketID;
    private int nextFineID;

    public IDGeneratorState() {
        // Safe defaults (never 0)
        this.nextUserID = 1;
        this.nextSpotNum = 1;
        this.nextTicketID = 1;
        this.nextFineID = 1;
    }

    public IDGeneratorState(int nextUserID, int nextSpotNum, int nextTicketID, int nextFineID) {
        this.nextUserID = nextUserID;
        this.nextSpotNum = nextSpotNum;
        this.nextTicketID = nextTicketID;
        this.nextFineID = nextFineID;
    }

    public int getNextUserID() { return nextUserID; }
    public int getNextSpotNum() { return nextSpotNum; }
    public int getNextTicketID() { return nextTicketID; }
    public int getNextFineID() { return nextFineID; }
}
