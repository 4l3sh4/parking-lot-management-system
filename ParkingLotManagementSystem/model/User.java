package model;

import model.Actionable;

public abstract class User{
    
    private int ID; //unique id
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private boolean vip;
    protected transient Actionable[] actions;
    protected transient gui.Actionable[] guiActions;
    
    public User() {
        
    }
    
    public User(int ID, String firstName, String lastName, String email, String password) {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
    
    public int getID() {
        return ID;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public Actionable[] getActions() {
        return actions;
    }
    
    public gui.Actionable[] getGUIActions() {
        return guiActions;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}