package util;

import java.util.Scanner;


public class ConsoleInput {
    
    //read string input with spaces
    public static String readLineString(Scanner scanner){
        String line;
        do {
            line = scanner.nextLine();
        } 
        
        while (line.isEmpty());
        return line;
    }
    
    //read only 1 word without spaces
    public static String readString(Scanner scanner) {
        return scanner.next();
    }
    
    //only double (read string and then convert it to double)
    public static double readDouble(Scanner scanner) {
        double inputDouble = 0;
        boolean doubleInput = false;
        while (!doubleInput) {
            try {
                String input = readLineString(scanner);
                inputDouble = Double.parseDouble(input);
                doubleInput = true;
            }
            catch (Exception e) {
                System.out.println("Please enter double");
            }
        }
        return inputDouble;
    }
    
    public static int readInt(Scanner scanner) {
        int inputInt = 0;
        boolean intIn = false;
        while (!intIn) {
            try {
                String input = readLineString(scanner);
                inputInt = Integer.parseInt(input);
                intIn = true;
            }
            catch (Exception e) {
                System.out.println("Please enter int");
            }
        }
        return inputInt;
    }
}