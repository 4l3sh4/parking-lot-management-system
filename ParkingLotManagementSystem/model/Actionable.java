package model;

import java.util.Scanner;
/**
 * Write a description of interface Actionable here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public interface Actionable {
    
    public String getLabel();
    
    public void execute(Scanner s, User u);
    
    public boolean isAdminOnly();
}