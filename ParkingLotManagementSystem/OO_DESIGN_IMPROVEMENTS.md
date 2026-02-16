# PARKING LOT MANAGEMENT SYSTEM - OO DESIGN IMPROVEMENTS

## Overview
This document describes the Object-Oriented design improvements made to ensure the system is future-proof and follows proper OO principles. The system now follows SOLID principles and design patterns to make it easy to extend with new vehicle types, parking spot types, and fine schemes.

---

## 1. VEHICLE TYPE EXTENSIBILITY

### Problem Before:
- Hard-coded `if-else` chains and `switch` statements to create vehicles
- `instanceof` checks scattered throughout the codebase  
- Adding a new vehicle type required changes in multiple files
- Violated the Open/Closed Principle

### Solution Implemented:

#### A. Abstract Methods in Vehicle Class
The `Vehicle` class now includes abstract methods that all subclasses must implement:

```java
public abstract Set<SpotType> getAllowedSpotTypes();
public abstract VehicleType getVehicleType();
public boolean hasFreeParkingIn(SpotType spotType) { return false; }
```

**Benefits:**
- Each vehicle type encapsulates its own behavior
- Polymorphic behavior replaces switch statements
- New vehicle types automatically work with existing code

#### B. VehicleFactory Pattern
A new `VehicleFactory` class centralizes vehicle creation:

```java
Vehicle vehicle = VehicleFactory.createVehicle(type, plate, hasHandicappedCard);
```

**Benefits:**
- Single location for vehicle instantiation logic
- Easy to add new vehicle types
- Follows the Factory Pattern

### How to Add a New Vehicle Type:

1. **Create a new Vehicle subclass** (e.g., `ElectricVehicle.java`):
   ```java
   public class ElectricVehicle extends Vehicle {
       public ElectricVehicle(String licensePlate) {
           super(licensePlate);
       }
       
       @Override
       public Set<SpotType> getAllowedSpotTypes() {
           Set<SpotType> allowedTypes = new HashSet<>();
           allowedTypes.add(SpotType.COMPACT);
           allowedTypes.add(SpotType.REGULAR);
           allowedTypes.add(SpotType.ELECTRIC); // new spot type
           allowedTypes.add(SpotType.RESERVED);
           return allowedTypes;
       }
       
       @Override
       public VehicleType getVehicleType() {
           return VehicleType.ELECTRIC;
       }
       
       @Override
       public boolean hasFreeParkingIn(SpotType spotType) {
           // Optional: implement special pricing logic
           return false;
       }
   }
   ```

2. **Add to VehicleType enum** in `gui/VehicleType.java`:
   ```java
   public enum VehicleType {
       MOTORCYCLE,
       CAR,
       SUV_TRUCK,
       HANDICAPPED_VEHICLE,
       ELECTRIC  // Add new type
   }
   ```

3. **Update VehicleFactory** in `model/VehicleFactory.java`:
   ```java
   switch (type) {
       // ... existing cases ...
       case ELECTRIC:
           return new ElectricVehicle(licensePlate);
       default:
           return null;
   }
   ```

**That's it!** The rest of the system will automatically work with the new vehicle type because it uses polymorphism.

---

## 2. PARKING SPOT TYPE EXTENSIBILITY

### Problem Before:
- Switch statement in `ParkingSpot.assignRate()` hard-coded rates
- Adding new spot types required code changes in multiple places
- Violated the Open/Closed Principle

### Solution Implemented:

The `SpotType` enum now encapsulates its own hourly rate:

```java
public enum SpotType {
    COMPACT(2.0),
    REGULAR(5.0),
    HANDICAPPED(2.0),
    RESERVED(10.0);
    
    private final double hourlyRate;
    
    SpotType(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public double getHourlyRate() {
        return hourlyRate;
    }
}
```

**Benefits:**
- Each spot type knows its own rate
- No switch statements needed
- Single location for rate configuration

### How to Add a New Spot Type:

1. **Add to SpotType enum** in `model/SpotType.java`:
   ```java
   public enum SpotType {
       COMPACT(2.0),
       REGULAR(5.0),
       HANDICAPPED(2.0),
       RESERVED(10.0),
       ELECTRIC(3.0),  // Add new type with rate
       VIP(15.0)       // Another example
   }
   ```

2. **Update vehicle classes** to include the new spot type in their allowed types (if applicable):
   ```java
   @Override
   public Set<SpotType> getAllowedSpotTypes() {
       Set<SpotType> allowedTypes = new HashSet<>();
       allowedTypes.add(SpotType.ELECTRIC);  // Add if this vehicle can use it
       // ... other types ...
       return allowedTypes;
   }
   ```

**That's it!** The `ParkingSpot` class will automatically use the new spot type's rate.

---

## 3. FINE SCHEME EXTENSIBILITY

### Problem Before:
- Switch statement in `FineManager.calculateFine()` hard-coded fine calculations
- Adding new fine schemes required modifying existing code
- Violated the Open/Closed and Single Responsibility Principles

### Solution Implemented:

#### A. Strategy Pattern
A new `FineCalculationStrategy` interface with concrete implementations:

```java
public interface FineCalculationStrategy {
    double calculateFine(long durationHours);
    String getDescription();
}
```

**Concrete Strategies:**
- `FixedFineStrategy` - Flat RM 50 fine
- `ProgressiveFineStrategy` - Incremental fines by duration
- `HourlyFineStrategy` - RM 20 per hour

**Benefits:**
- Each strategy is independent and testable
- Easy to add new strategies without modifying existing ones
- Follows the Strategy Pattern and Open/Closed Principle

#### B. FineScheme Integration
The `FineScheme` enum now returns a strategy instance:

```java
public enum FineScheme {
    FIXED("Fixed Fine Scheme - RM 50 flat", new FixedFineStrategy()),
    PROGRESSIVE("Progressive Fine Scheme - Incremental by hours", new ProgressiveFineStrategy()),
    HOURLY("Hourly Fine Scheme - RM 20 per hour", new HourlyFineStrategy());
    
    private FineCalculationStrategy strategy;
    
    public FineCalculationStrategy getStrategy() {
        return strategy;
    }
}
```

#### C. Simplified FineManager
`FineManager.calculateFine()` now uses the strategy:

```java
public static double calculateFine(long durationHours, boolean isOverstaying, FineScheme scheme) {
    if (!isOverstaying) return 0.0;
    if (scheme == null) scheme = FineScheme.FIXED;
    
    FineCalculationStrategy strategy = scheme.getStrategy();
    return strategy.calculateFine(durationHours);
}
```

### How to Add a New Fine Scheme:

1. **Create a new strategy class** (e.g., `ExponentialFineStrategy.java`):
   ```java
   package model;
   
   public class ExponentialFineStrategy implements FineCalculationStrategy {
       
       @Override
       public double calculateFine(long durationHours) {
           // Implement your calculation logic
           // Example: RM 10 * 2^(days overstayed)
           long days = durationHours / 24;
           return 10.0 * Math.pow(2, days);
       }
       
       @Override
       public String getDescription() {
           return "Exponential Fine Scheme - Doubles each day";
       }
   }
   ```

2. **Add to FineScheme enum** in `model/FineScheme.java`:
   ```java
   public enum FineScheme {
       FIXED("Fixed Fine Scheme - RM 50 flat", new FixedFineStrategy()),
       PROGRESSIVE("Progressive Fine Scheme - Incremental by hours", new ProgressiveFineStrategy()),
       HOURLY("Hourly Fine Scheme - RM 20 per hour", new HourlyFineStrategy()),
       EXPONENTIAL("Exponential Fine Scheme - Doubles each day", new ExponentialFineStrategy());
   }
   ```

**That's it!** The new fine scheme is now available throughout the system and can be selected by admins.

---

## 4. DESIGN PATTERNS USED

### Factory Pattern (VehicleFactory)
- **Purpose:** Centralizes vehicle object creation
- **Benefit:** Adding new vehicle types requires changes in only one place
- **Location:** `model/VehicleFactory.java`

### Strategy Pattern (Fine Calculation)
- **Purpose:** Encapsulates different fine calculation algorithms
- **Benefit:** New fine schemes can be added without modifying existing code
- **Location:** `model/FineCalculationStrategy.java` and implementations

### Template Method Pattern (Vehicle)
- **Purpose:** Defines structure with abstract methods for subclasses
- **Benefit:** Ensures all vehicles implement required behavior
- **Location:** `model/Vehicle.java`

---

## 5. SOLID PRINCIPLES APPLIED

### Single Responsibility Principle
- Each fine strategy has one calculation algorithm
- SpotType manages its own rate
- Vehicle subclasses manage their own allowed spots

### Open/Closed Principle
- **Open for extension:** New vehicle types, spot types, and fine schemes can be added
- **Closed for modification:** Existing code doesn't need to change when extending

### Liskov Substitution Principle
- All Vehicle subclasses can be used interchangeably
- Polymorphic methods work consistently across all vehicle types

### Interface Segregation Principle
- `FineCalculationStrategy` interface is focused and minimal
- Clients only depend on methods they use

### Dependency Inversion Principle
- High-level code depends on abstractions (Vehicle, FineCalculationStrategy)
- Not dependent on concrete implementations

---

## 6. BENEFITS OF THE NEW DESIGN

✅ **Extensibility:** Easy to add new vehicle types, spot types, and fine schemes  
✅ **Maintainability:** Changes are localized to specific classes  
✅ **Testability:** Each strategy and vehicle type can be tested independently  
✅ **Readability:** Code is cleaner without large switch statements  
✅ **Type Safety:** Compile-time checking prevents many errors  
✅ **Flexibility:** Business logic changes don't require extensive refactoring  

---

## 7. BACKWARD COMPATIBILITY

All changes maintain backward compatibility:
- Existing data files will load correctly
- GUI appearance and functionality remain unchanged
- All existing features work as before
- Only Java Swing is used (no new library dependencies)

---

## 8. TESTING RECOMMENDATIONS

When adding new types, test:

1. **Vehicle Types:**
   - Spot compatibility (can park in allowed spots)
   - Serialization/deserialization (save/load works)
   - GUI display and selection

2. **Spot Types:**
   - Correct rate calculation
   - Vehicle compatibility checks
   - Display in UI

3. **Fine Schemes:**
   - Correct fine calculation for various durations
   - Admin can select and apply the scheme
   - Fine amounts display correctly

---

## 9. SUMMARY

The system now follows industry-standard OO design principles and patterns, making it:
- **Future-proof:** Easy to extend without breaking existing code
- **Professional:** Uses established design patterns
- **Maintainable:** Clear structure with separated concerns
- **Scalable:** Ready for additional features and types

The design ensures that business requirements can evolve without requiring extensive code rewrites, saving time and reducing the risk of introducing bugs.
