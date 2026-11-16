package Rest;
import java.time.*;
import java.util.*;

public class ParkingLotDemo{
    enum VehicleType{
        MOTORCYCLE, CAR, TRUCK
    }

    enum SpotType{
        MOTORCYCLE, COMPACT, LARGE
    }

    //1. Vehicle
    static class Vehicle{
        String plate;
        VehicleType type;

        Vehicle(String plate, VehicleType type){
            this.plate = plate;
            this.type = type;
        }
    }

    //2. Parking Spot
    static class ParkingSpot{
        String id;
        SpotType type;
        Vehicle vehicle;

        ParkingSpot(String id, SpotType type){
            this.id = id;
            this.type = type;
        }

        boolean isAvailable(){
            return vehicle == null;
        }

        void park(Vehicle vehicle){
            if(!isAvailable()){
                throw new IllegalStateException("Spot occupied");
            }
            this.vehicle = vehicle;
        }

        void removeVehicle(){
            vehicle = null;
        }
    }

    //3. Parking Floor
    static class ParkingFloor{
        int floorNumber;
        List<ParkingSpot> spots = new ArrayList<>();

        ParkingFloor(int floorNumber){
            this.floorNumber = floorNumber;
        }

        void addSpot(ParkingSpot spot){
            spots.add(spot);
        }
    }

    //4. Ticket
    static class Ticket{
        String ticketId;
        Vehicle vehicle;
        ParkingSpot spot;
        int floorNumber;
        Instant entryTime;

        Ticket(Vehicle vehicle, ParkingSpot spot, int floorNumber){
            this.ticketId = UUID.randomUUID().toString();
            this.vehicle = vehicle;
            this.spot = spot;
            this.floorNumber = floorNumber;
            this.entryTime = Instant.now();
        }
    }

    //5. Parking Allocation Strategy
    interface ParkingStrategy{
        ParkingSpot findSpot(
            List<ParkingFloor> floor,
            VehicleType vehicleType
        );
    }

    static class BestFitStrategy implements ParkingStrategy{

        public ParkingSpot findSpot(List<ParkingFloor> floors,VehicleType vehicleType){
            List<SpotType> preferred;

            switch(vehicleType){
                case MOTORCYCLE:
                    preferred = Arrays.asList(SpotType.MOTORCYCLE, SpotType.COMPACT, SpotType.LARGE);
                    break;
                
                case CAR:
                    preferred = Arrays.asList(SpotType.COMPACT, SpotType.LARGE);
                    break;
                
                case TRUCK:
                    preferred = Arrays.asList(SpotType.LARGE);
                    break;
                
                default:
                    return null;
            }

            // Prefer the smallest suitable spot

            for(SpotType type : preferred){
                for(ParkingFloor floor : floors){
                    for(ParkingSpot spot : floor.spots){
                        if(spot.type == type && spot.isAvailable()){
                            return spot;
                        }
                    }
                }
            }
            return null;
        }
    }

    //6. Fee Strategy
    interface FeeStrategy{
        long calculateFee(Ticket ticket, Instant exitTime);
    }

    static class HourlyFeeStrategy implements FeeStrategy{
        public long calculateFee(Ticket ticket, Instant exitTime){
            Duration duration = Duration.between(ticket.entryTime, exitTime);

            if(duration.isNegative())
                throw new IllegalArgumentException("Invalid exit time");

            long completeHours = duration.toHours();

            long hours = Math.max(1, completeHours+(duration.minusHours(completeHours).isZero()?0:1));
            long rate;

            switch (ticket.vehicle.type){
                case MOTORCYCLE:
                    rate = 2;
                    break;

                case CAR:
                    rate = 5;
                    break;
                case TRUCK:
                    rate = 10;
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            return hours*rate;
        }

        
    }

}