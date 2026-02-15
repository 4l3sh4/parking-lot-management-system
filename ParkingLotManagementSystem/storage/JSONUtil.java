package storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.*;

/**
 * Custom JSON utility class to replace Gson dependency.
 * Provides serialization and deserialization for all model classes.
 */
public class JSONUtil {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // ==================== SERIALIZATION ====================
    
    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof List) {
            return listToJson((List<?>) obj);
        } else if (obj instanceof User) {
            return userToJson((User) obj);
        } else if (obj instanceof Vehicle) {
            return vehicleToJson((Vehicle) obj);
        } else if (obj instanceof Ticket) {
            return ticketToJson((Ticket) obj);
        } else if (obj instanceof ParkingSpot) {
            return parkingSpotToJson((ParkingSpot) obj);
        } else if (obj instanceof IDGeneratorState) {
            return idGeneratorStateToJson((IDGeneratorState) obj);
        } else if (obj instanceof Reservation) {
            return reservationToJson((Reservation) obj);
        } else if (obj instanceof Fine) {
            return fineToJson((Fine) obj);
        }
        
        return "{}";
    }
    
    private static String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < list.size(); i++) {
            json.append("  ").append(toJson(list.get(i)));
            if (i < list.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        return json.toString();
    }
    
    private static String userToJson(User user) {
        StringBuilder json = new StringBuilder("{\n");
        
        if (user instanceof Admin) {
            json.append("    \"UserType\": \"Admin\",\n");
        } else {
            json.append("    \"UserType\": \"Client\",\n");
        }
        
        json.append("    \"ID\": ").append(user.getID()).append(",\n");
        json.append("    \"firstName\": ").append(quote(user.getFirstName())).append(",\n");
        json.append("    \"lastName\": ").append(quote(user.getLastName())).append(",\n");
        json.append("    \"email\": ").append(quote(user.getEmail())).append(",\n");
        json.append("    \"vip\": ").append(user.isVip()).append(",\n");
        json.append("    \"password\": ").append(quote(user.getPassword())).append("\n");
        json.append("  }");
        
        return json.toString();
    }
    
    private static String vehicleToJson(Vehicle vehicle) {
        StringBuilder json = new StringBuilder("{\n");
        
        if (vehicle instanceof Motorcycle) {
            json.append("    \"VehicleType\": \"MOTORCYCLE\",\n");
        } else if (vehicle instanceof Car) {
            json.append("    \"VehicleType\": \"CAR\",\n");
        } else if (vehicle instanceof SUV_Truck) {
            json.append("    \"VehicleType\": \"SUV_TRUCK\",\n");
        } else if (vehicle instanceof Handicapped_Vehicle) {
            json.append("    \"VehicleType\": \"HANDICAPPED_VEHICLE\",\n");
        }
        
        json.append("    \"LicensePlateNumber\": ").append(quote(vehicle.getLicensePlateNumber())).append(",\n");
        json.append("    \"OwnerID\": ").append(vehicle.getVehicleOwnerID()).append(",\n");
        json.append("    \"Color\": ").append(quote(vehicle.getColor() != null ? vehicle.getColor().name() : "")).append(",\n");
        json.append("    \"Brand\": ").append(quote(vehicle.getBrand())).append(",\n");
        json.append("    \"Model\": ").append(quote(vehicle.getModel()));
        
        if (vehicle instanceof Handicapped_Vehicle) {
            Handicapped_Vehicle handicappedVehicle = (Handicapped_Vehicle) vehicle;
            json.append(",\n    \"HasHandicappedCard\": ").append(handicappedVehicle.getHasHandicappedCard());
        }
        
        json.append("\n  }");
        return json.toString();
    }
    
    private static String ticketToJson(Ticket ticket) {
        StringBuilder json = new StringBuilder("{\n");
        
        json.append("    \"id\": ").append(ticket.getId()).append(",\n");
        json.append("    \"ticketCode\": ").append(quote(ticket.getTicketCode())).append(",\n");
        json.append("    \"vehicle\": ").append(vehicleToJson(ticket.getVehicle())).append(",\n");
        json.append("    \"spotNumber\": ").append(quote(ticket.getSpotNumber())).append(",\n");
        json.append("    \"entryTime\": ").append(quote(ticket.getEntryTime().format(DATE_TIME_FORMATTER))).append(",\n");
        
        if (ticket.getExitTime() != null) {
            json.append("    \"exitTime\": ").append(quote(ticket.getExitTime().format(DATE_TIME_FORMATTER))).append(",\n");
        } else {
            json.append("    \"exitTime\": null,\n");
        }
        
        json.append("    \"durationHours\": ").append(ticket.getDurationHours()).append(",\n");
        json.append("    \"hourlyRate\": ").append(ticket.getHourlyRate()).append(",\n");
        json.append("    \"parkingFee\": ").append(ticket.getParkingFee()).append(",\n");
        json.append("    \"totalFee\": ").append(ticket.getTotalFee()).append("\n");
        json.append("  }");
        
        return json.toString();
    }
    
    private static String parkingSpotToJson(ParkingSpot spot) {
        StringBuilder json = new StringBuilder("{\n");
        
        json.append("    \"spotNumber\": ").append(quote(spot.getSpotNumber())).append(",\n");
        json.append("    \"type\": ").append(quote(spot.getType().name())).append(",\n");
        json.append("    \"occupied\": ").append(spot.isOccupied()).append(",\n");
        
        if (spot.getVehicle() != null) {
            json.append("    \"vehicle\": ").append(vehicleToJson(spot.getVehicle())).append(",\n");
        } else {
            json.append("    \"vehicle\": null,\n");
        }
        
        json.append("    \"hourlyRate\": ").append(spot.getHourlyRate()).append("\n");
        json.append("  }");
        
        return json.toString();
    }
    
    private static String idGeneratorStateToJson(IDGeneratorState state) {
        StringBuilder json = new StringBuilder("{\n");
        json.append("  \"nextUserID\": ").append(state.getNextUserID()).append(",\n");
        json.append("  \"nextSpotNum\": ").append(state.getNextSpotNum()).append(",\n");
        json.append("  \"nextTicketID\": ").append(state.getNextTicketID()).append("\n");
        json.append("}");
        return json.toString();
    }
    
    private static String reservationToJson(Reservation reservation) {
        StringBuilder json = new StringBuilder("{\n");
        json.append("    \"plate\": ").append(quote(reservation.getPlate())).append(",\n");
        json.append("    \"spotNumber\": ").append(quote(reservation.getSpotNumber())).append(",\n");
        json.append("    \"active\": ").append(reservation.isActive()).append(",\n");
        json.append("    \"createdAt\": ").append(quote(reservation.getCreatedAt().format(DATE_TIME_FORMATTER))).append("\n");
        json.append("  }");
        return json.toString();
    }
    
    private static String fineToJson(Fine fine) {
        StringBuilder json = new StringBuilder("{\n");
        json.append("    \"id\": ").append(fine.getId()).append(",\n");
        json.append("    \"licensePlate\": ").append(quote(fine.getLicensePlate())).append(",\n");
        json.append("    \"amount\": ").append(fine.getAmount()).append(",\n");
        json.append("    \"reason\": ").append(quote(fine.getReason())).append(",\n");
        json.append("    \"paid\": ").append(fine.isPaid()).append(",\n");
        json.append("    \"createdAt\": ").append(quote(fine.getCreatedAt().format(DATE_TIME_FORMATTER))).append(",\n");
        
        if (fine.getPaidAt() != null) {
            json.append("    \"paidAt\": ").append(quote(fine.getPaidAt().format(DATE_TIME_FORMATTER))).append("\n");
        } else {
            json.append("    \"paidAt\": null\n");
        }
        
        json.append("  }");
        return json.toString();
    }
    
    // ==================== DESERIALIZATION ====================
    
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        
        if (json == null || json.trim().isEmpty() || json.trim().equals("[]")) {
            return list;
        }
        
        // Remove leading/trailing whitespace and array brackets
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        
        // Split into objects
        List<String> objects = splitJsonObjects(json);
        
        for (String objStr : objects) {
            T obj = fromJson(objStr, clazz);
            if (obj != null) {
                list.add(obj);
            }
        }
        
        return list;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty() || json.trim().equals("null")) {
            return null;
        }
        
        if (clazz == User.class || clazz == Admin.class || clazz == Client.class) {
            return (T) jsonToUser(json);
        } else if (clazz == Vehicle.class || clazz == Motorcycle.class || 
                   clazz == Car.class || clazz == SUV_Truck.class || 
                   clazz == Handicapped_Vehicle.class) {
            return (T) jsonToVehicle(json);
        } else if (clazz == Ticket.class) {
            return (T) jsonToTicket(json);
        } else if (clazz == ParkingSpot.class) {
            return (T) jsonToParkingSpot(json);
        } else if (clazz == IDGeneratorState.class) {
            return (T) jsonToIDGeneratorState(json);
        } else if (clazz == Reservation.class) {
            return (T) jsonToReservation(json);
        } else if (clazz == Fine.class) {
            return (T) jsonToFine(json);
        }
        
        return null;
    }
    
    private static User jsonToUser(String json) {
        String userType = extractStringValue(json, "UserType");
        if (userType == null) userType = "Client";
        
        int id = extractIntValue(json, "ID");
        String firstName = extractStringValue(json, "firstName");
        String lastName = extractStringValue(json, "lastName");
        String email = extractStringValue(json, "email");
        String password = extractStringValue(json, "password");
        boolean vip = extractBooleanValue(json, "vip");
        
        User user;
        if ("Admin".equalsIgnoreCase(userType)) {
            user = new Admin();
        } else {
            user = new Client();
        }
        
        user.setID(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setVip(vip);
        
        return user;
    }
    
    private static Vehicle jsonToVehicle(String json) {
        String vehicleType = extractStringValue(json, "VehicleType");
        String plate = extractStringValue(json, "LicensePlateNumber");
        
        Vehicle vehicle;
        
        switch (vehicleType) {
            case "MOTORCYCLE":
                vehicle = new Motorcycle(plate);
                break;
            case "CAR":
                vehicle = new Car(plate);
                break;
            case "SUV_TRUCK":
                vehicle = new SUV_Truck(plate);
                break;
            case "HANDICAPPED_VEHICLE":
                boolean hasCard = extractBooleanValue(json, "HasHandicappedCard");
                vehicle = new Handicapped_Vehicle(plate, hasCard);
                break;
            default:
                return null;
        }
        
        vehicle.setLicensePlateNumber(plate);
        vehicle.setVehicleOwnerID(extractIntValue(json, "OwnerID"));
        
        String colorStr = extractStringValue(json, "Color");
        if (colorStr != null && !colorStr.isEmpty()) {
            try {
                vehicle.setColor(Color.valueOf(colorStr));
            } catch (IllegalArgumentException e) {
                vehicle.setColor(null);
            }
        }
        
        vehicle.setBrand(extractStringValue(json, "Brand"));
        vehicle.setModel(extractStringValue(json, "Model"));
        
        return vehicle;
    }
    
    private static Ticket jsonToTicket(String json) {
        // Extract vehicle JSON
        String vehicleJson = extractObjectValue(json, "vehicle");
        Vehicle vehicle = jsonToVehicle(vehicleJson);
        
        String spotNumber = extractStringValue(json, "spotNumber");
        
        // Create ticket using reflection or a special constructor
        // Since Ticket constructor sets entryTime to now(), we need to override it
        Ticket ticket = new Ticket(vehicle, spotNumber);
        
        // Use reflection to set private fields
        try {
            java.lang.reflect.Field idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ticket, extractIntValue(json, "id"));
            
            java.lang.reflect.Field ticketCodeField = Ticket.class.getDeclaredField("ticketCode");
            ticketCodeField.setAccessible(true);
            ticketCodeField.set(ticket, extractStringValue(json, "ticketCode"));
            
            java.lang.reflect.Field entryTimeField = Ticket.class.getDeclaredField("entryTime");
            entryTimeField.setAccessible(true);
            String entryTimeStr = extractStringValue(json, "entryTime");
            if (entryTimeStr != null) {
                entryTimeField.set(ticket, LocalDateTime.parse(entryTimeStr, DATE_TIME_FORMATTER));
            }
            
            java.lang.reflect.Field exitTimeField = Ticket.class.getDeclaredField("exitTime");
            exitTimeField.setAccessible(true);
            String exitTimeStr = extractStringValue(json, "exitTime");
            if (exitTimeStr != null && !exitTimeStr.equals("null")) {
                exitTimeField.set(ticket, LocalDateTime.parse(exitTimeStr, DATE_TIME_FORMATTER));
            }
            
            java.lang.reflect.Field durationHoursField = Ticket.class.getDeclaredField("durationHours");
            durationHoursField.setAccessible(true);
            durationHoursField.set(ticket, extractLongValue(json, "durationHours"));
            
            java.lang.reflect.Field hourlyRateField = Ticket.class.getDeclaredField("hourlyRate");
            hourlyRateField.setAccessible(true);
            hourlyRateField.set(ticket, extractDoubleValue(json, "hourlyRate"));
            
            java.lang.reflect.Field parkingFeeField = Ticket.class.getDeclaredField("parkingFee");
            parkingFeeField.setAccessible(true);
            parkingFeeField.set(ticket, extractDoubleValue(json, "parkingFee"));
            
            java.lang.reflect.Field totalFeeField = Ticket.class.getDeclaredField("totalFee");
            totalFeeField.setAccessible(true);
            totalFeeField.set(ticket, extractDoubleValue(json, "totalFee"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ticket;
    }
    
    private static ParkingSpot jsonToParkingSpot(String json) {
        String spotNumber = extractStringValue(json, "spotNumber");
        String typeStr = extractStringValue(json, "type");
        
        SpotType type = SpotType.valueOf(typeStr);
        ParkingSpot spot = new ParkingSpot(spotNumber, type);
        
        boolean occupied = extractBooleanValue(json, "occupied");
        spot.setOccupied(occupied);
        
        String vehicleJson = extractObjectValue(json, "vehicle");
        if (vehicleJson != null && !vehicleJson.equals("null")) {
            Vehicle vehicle = jsonToVehicle(vehicleJson);
            spot.setVehicle(vehicle);
        }
        
        return spot;
    }
    
    private static IDGeneratorState jsonToIDGeneratorState(String json) {

        int nextUserID = extractIntValue(json, "nextUserID");
        int nextSpotNum = extractIntValue(json, "nextSpotNum");
        int nextTicketID = extractIntValue(json, "nextTicketID");
        int nextFineID = extractIntValue(json, "nextFineID");

        // Safety: never allow 0
        if (nextUserID <= 0) nextUserID = 1;
        if (nextSpotNum <= 0) nextSpotNum = 1;
        if (nextTicketID <= 0) nextTicketID = 1;
        if (nextFineID <= 0) nextFineID = 1;

        return new IDGeneratorState(nextUserID, nextSpotNum, nextTicketID, nextFineID);
    }
    
    private static Reservation jsonToReservation(String json) {
        String plate = extractStringValue(json, "plate");
        String spotNumber = extractStringValue(json, "spotNumber");
        
        Reservation reservation = new Reservation(plate, spotNumber);
        
        // Use reflection to set fields since constructor creates new timestamps
        try {
            java.lang.reflect.Field activeField = Reservation.class.getDeclaredField("active");
            activeField.setAccessible(true);
            activeField.set(reservation, extractBooleanValue(json, "active"));
            
            java.lang.reflect.Field createdAtField = Reservation.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            String createdAtStr = extractStringValue(json, "createdAt");
            if (createdAtStr != null) {
                createdAtField.set(reservation, LocalDateTime.parse(createdAtStr, DATE_TIME_FORMATTER));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return reservation;
    }
    
    private static Fine jsonToFine(String json) {
        String licensePlate = extractStringValue(json, "licensePlate");
        double amount = extractDoubleValue(json, "amount");
        String reason = extractStringValue(json, "reason");
        
        Fine fine = new Fine(licensePlate, amount, reason);
        
        // Use reflection to set fields since constructor sets id and timestamps
        try {
            java.lang.reflect.Field idField = Fine.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(fine, extractIntValue(json, "id"));
            
            java.lang.reflect.Field paidField = Fine.class.getDeclaredField("paid");
            paidField.setAccessible(true);
            paidField.set(fine, extractBooleanValue(json, "paid"));
            
            java.lang.reflect.Field createdAtField = Fine.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            String createdAtStr = extractStringValue(json, "createdAt");
            if (createdAtStr != null) {
                createdAtField.set(fine, LocalDateTime.parse(createdAtStr, DATE_TIME_FORMATTER));
            }
            
            java.lang.reflect.Field paidAtField = Fine.class.getDeclaredField("paidAt");
            paidAtField.setAccessible(true);
            String paidAtStr = extractStringValue(json, "paidAt");
            if (paidAtStr != null && !paidAtStr.equals("null")) {
                paidAtField.set(fine, LocalDateTime.parse(paidAtStr, DATE_TIME_FORMATTER));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return fine;
    }
    
    // ==================== HELPER METHODS ====================
    
    private static String quote(String str) {
        if (str == null) return "\"\"";
        return "\"" + str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }
    
    private static List<String> splitJsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        int start = 0;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '{') {
                if (braceCount == 0) start = i;
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    objects.add(json.substring(start, i + 1).trim());
                }
            }
        }
        
        return objects;
    }
    
    private static String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    private static int extractIntValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
    
    private static long extractLongValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Long.parseLong(m.group(1));
        }
        return 0L;
    }
    
    private static double extractDoubleValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+\\.?\\d*)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return 0.0;
    }
    
    private static boolean extractBooleanValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return false;
    }
    
    private static String extractObjectValue(String json, String key) {
        int index = json.indexOf("\"" + key + "\"");
        if (index == -1) return null;
        
        index = json.indexOf(":", index);
        if (index == -1) return null;
        
        index++;
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
        
        if (index >= json.length()) return null;
        
        if (json.charAt(index) == 'n') { // null
            return "null";
        }
        
        if (json.charAt(index) == '{') {
            int braceCount = 0;
            int start = index;
            for (int i = index; i < json.length(); i++) {
                if (json.charAt(i) == '{') braceCount++;
                else if (json.charAt(i) == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        return json.substring(start, i + 1);
                    }
                }
            }
        }
        
        return null;
    }
}
