package model;


public abstract class Vehicle implements Payable {
    
    private String licensePlateNumber; 
    private int vehicleOwnerID;
    private Color color;
    private String brand;
    private String model;
    
    
    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }
    
    public void setVehicleOwnerID(int vehicleOwnerID) {
        this.vehicleOwnerID = vehicleOwnerID;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Vehicle(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }
    
    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }
    
    public int getVehicleOwnerID() {
        return vehicleOwnerID;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public Color getColor() {
        return color;
    }
}