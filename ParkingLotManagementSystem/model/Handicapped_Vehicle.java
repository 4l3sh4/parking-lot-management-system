package model;

public class Handicapped_Vehicle extends Vehicle {
    //RM2/hour (FREE only if have handicapped card holder in handicapped spots) 
    private boolean hasHandicappedCard;

    public Handicapped_Vehicle(String licensePlate, boolean hasHandicappedCard) {
        super(licensePlate);
        this.hasHandicappedCard = hasHandicappedCard;
    }
    
    public boolean getHasHandicappedCard() {
        return hasHandicappedCard;
    }
}