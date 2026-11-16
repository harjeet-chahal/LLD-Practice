package Rest;


// Core Entities:
// 1. User: Customer booking the tickets
// 2. Movie : Details about the firm (titcle, duration, genre)
// 3. Theater: Location containing multiple screens
// 4. Screen: Physical auditorium containing seats
// 5. Show : A specific screening of a movie art a given time on a screen
// 6. Seat : Individual seats in a screen with types (Regular, VIP) and pricing.
// 7. Booking: The transaction record linking a user, a show, selected seats and payment.

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

// ====== Enums ======

enum SeatType {REGULAR, VIP, IMAX}
enum SeatStatus {AVAILABLE, RESERVED, BOOKED}
enum BookingStatus {PENDING, CONFIRMED, CANCELED, EXPIRED}
enum PaymentStatus {SUCCESS, FAILED, PENDING}

// ===== MODELS ====

class User{
    private String userId;
    private String name;
    private String email;

    public User(String userID, String name, String email){
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {return userId;}
    public String getName() {return name;}
}

class Movie{
    private String movieId;
    private String title;
    private int durationInMinutes;

    public Movie(String movieId, String title, int durationInMinutes){
        this.movieid = movieId;
        this.title = title;
        this.durationInMinutes = durationInMinutes;
    }

    public String getTitle(){return title;}
}

class Seat{
    private String seatId;
    private int row;
    private int col;
    private SeatType type;
    private SeatStatus status;
    private double price;
    private final ReentrantLock lock = new ReentrantLock(); // for thread safety

    public Seat(String seatId, int row, int col, SeatType type, SeatStatus status, double price){
        this.seatId = seatId;
        this.row = row;
        this.col = col;
        this.type = type;
        this.status = SeatStatus.AVAILABLE;
        this.price = price;
    }

    public ReentrantLock getLock() { return lock;}
}