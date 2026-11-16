package Rest;
import java.util.*;

//1. Enums

enum VehicleSize{
    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int sizeValue;

    VehicleSize(int sizeValue){
        this.sizeValue = sizeValue;
    }

    public int getSizeValue(){
        return sizeValue;
    }
}

//2. Vehicle Heirarchy (abstract class & inheritance)

abstract class Vehicle{
    private final String liscencePlate;
    private final VehicleSize size;

    public Vehicle(String liscencePlate, VehicleSize size){
        this.liscencePlate = liscencePlate;
        this.size = size;
    }

    public String getLiscencePlate(){
        return liscencePlate;
    }

    public VehicleSize getSize(){
        return size;
    }
}

class Motorcycle extends Vehicle{
    public Motorcycle(String liscencePlate){
        super(liscencePlate, VehicleSize.SMALL);
    }
}

class Car extends Vehicle{
    public Car(String liscencePlate){
        super(liscencePlate, VehicleSize.MEDIUM);
    }
}

class Truck extends Vehicle{
    public Truck(String liscencePlate){
        super(liscencePlate, VehicleSize.LARGE);
    }
}

// Parking Spot (Encapsulation)

class ParkingSpot{
    private final String spotId;
    private final VehicleSize spotSize;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, VehicleSize spotSize){
        this.spotId = spotId;
        this.spotSize = spotSize;
        this.parkedVehicle = null;
    }

    public boolean isAvailable(){
        return parkedVehicle == null;
    }

    public boolean canFit(Vehicle vehicle){
        return isAvailable() && this.spotSize.getSizeValue() >= vehicle.getSize().getSizeValue();
    }

    public boolean park(Vehicle vehicle){
        if(!canFit(vehicle)){
            return false;
        }
        this.parkedVehicle = vehicle;
        return true;
    }
    public void vacate(){
        this.parkedVehicle = null;
    }

    public String getSpotId(){
        return spotId;
    }

    public VehicleSize getSpotSize(){
        return spotSize;
    }
}

// ticket & pricing strategy (interface/ Strategy Pattern)

class Ticket{
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final long entryTimeMillis;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot){
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTimeMillis = System.currentTimeMillis();
    }

    public String getTicketid(){return ticketId;}
    public Vehicle getVehicle(){return vehicle;}
    public ParkingSpot getSpot(){return spot;}
    public long getEntryTimeMillis(){return entryTimeMillis;}
}


interface PricingStrategy{
    double calculatePrice(Ticket ticket);
}

class FlatRatePricing implements PricingStrategy{
    private final double flatRate;

    public FlatRatePricing(double flatRate){
        this.flatRate = flatRate;
    }

    @Override 
    public double calculatePrice(Ticket ticket){
        return flatRate;
    }
}

class HourlyPricing implements PricingStrategy{
    private final double hourlyRate;

    public HourlyPricing(double hourlyRate){
        this.hourlyRate = hourlyRate;
    }

    @Override 
    public double calculatePrice(Ticket ticket){
        long durationMillis = System.currentTimeMillis()-ticket.getEntryTimeMillis();
        long hours = Math.max(1, (long)Math.ceil(durationMillis / (1000.0*60*60)));
        return hours*hourlyRate;
    }
}

//5. Parking Floor and Lot (Composition)

class ParkingFloor{
    private final int floorNumber;
    private final List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber, List<ParkingSpot> spots){
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>(spots);

        this.spots.sort(Comparator.comparingInt(s -> s.getSpotSize().getSizeValue()));
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle){
        for(ParkingSpot spot : spots){
            if(spot.canFit(vehicle)){
                return spot;
            }
        }
        return null;
    }

    public int getFloorNumber(){
        return floorNumber;
    }
}

class ParkingLot{
    private final List<ParkingFloor> floors;
    private PricingStrategy pricingStrategy;
    private final Map<String, Ticket> activeTickets;
    private int ticketCounter = 1;

    public ParkingLot(List<ParkingFloor> floors, PricingStrategy pricingStrategy){
        this.floors = floors;
        this.pricingStrategy = pricingStrategy;
        this.activeTickets = new HashMap<>();
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy){
        this.pricingStrategy = pricingStrategy;
    }

    public synchronized Ticket parkVehicle(Vehicle vehicle){
        for(ParkingFloor floor : floors){
            ParkingSpot spot = floor.findAvailableSpot(vehicle);
            if(spot!=null && spot.park(vehicle)){
                String ticketId = "T-" + (ticketCounter++);
                Ticket ticket = new Ticket(ticketId, vehicle, spot);
                activeTickets.put(ticketId, ticket);

                System.out.println("Parked " + vehicle.getLiscencePlate() + " at spot" + spot.getSpotId() + " (Ticket: " + ticketId + ")");
                return ticket;
            }
        }
        System.out.println("No available spot for " + vehicle.getLiscencePlate());
        return null;
    }

    public synchronized double unparkVehicle(String ticketId){
        Ticket ticket = activeTickets.remove(ticketId);
        if(ticket == null){
            throw new IllegalArgumentException("Invalid or already used Ticket Id " + ticketId);
        }

        double fee = pricingStrategy.calculatePrice(ticket);
        ticket.getSpot().vacate();

        System.out.println("Unparked " + ticket.getVehicle().getLiscencePlate() + " from spot " + ticket.getSpot().getSpotId() + ". Fee: $" + fee);
        return fee;
    }
}