package Rest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

// ==================== ENUMS ====================

enum SeatType { REGULAR, VIP, IMAX }

enum SeatStatus { AVAILABLE, RESERVED, BOOKED }

enum BookingStatus { PENDING, CONFIRMED, CANCELLED, EXPIRED }

enum PaymentStatus { SUCCESS, FAILED, PENDING }

// ==================== MODELS ====================

class User {
    private String userId;
    private String name;
    private String email;

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
}

class Movie {
    private String movieId;
    private String title;
    private int durationInMinutes;

    public Movie(String movieId, String title, int durationInMinutes) {
        this.movieId = movieId;
        this.title = title;
        this.durationInMinutes = durationInMinutes;
    }

    public String getTitle() { return title; }
}

class Seat {
    private String seatId;
    private int row;
    private int col;
    private SeatType type;
    private SeatStatus status;
    private double price;
    private final ReentrantLock lock = new ReentrantLock(); // For thread safety

    public Seat(String seatId, int row, int col, SeatType type, double price) {
        this.seatId = seatId;
        this.row = row;
        this.col = col;
        this.type = type;
        this.status = SeatStatus.AVAILABLE;
        this.price = price;
    }

    public ReentrantLock getLock() { return lock; }
    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }
    public double getPrice() { return price; }
    public String getSeatId() { return seatId; }
}

class Show {
    private String showId;
    private Movie movie;
    private Date startTime;
    private Date endTime;
    private Map<String, Seat> seats; // seatId -> Seat

    public Show(String showId, Movie movie, Date startTime, Date endTime, List<Seat> seatList) {
        this.showId = showId;
        this.movie = movie;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seats = new ConcurrentHashMap<>();
        for (Seat seat : seatList) {
            seats.put(seat.getSeatId(), seat);
        }
    }

    public Map<String, Seat> getSeats() { return seats; }
    public String getShowId() { return showId; }
}

class Booking {
    private String bookingId;
    private User user;
    private Show show;
    private List<Seat> bookedSeats;
    private double totalAmount;
    private BookingStatus status;
    private Payment payment;

    public Booking(String bookingId, User user, Show show, List<Seat> bookedSeats) {
        this.bookingId = bookingId;
        this.user = user;
        this.show = show;
        this.bookedSeats = bookedSeats;
        this.totalAmount = bookedSeats.stream().mapToDouble(Seat::getPrice).sum();
        this.status = BookingStatus.PENDING;
    }

    public void setStatus(BookingStatus status) { this.status = status; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public String getBookingId() { return bookingId; }
    public BookingStatus getStatus() { return status; }
}

class Payment {
    private String paymentId;
    private double amount;
    private PaymentStatus status;

    public Payment(String paymentId, double amount) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void setStatus(PaymentStatus status) { this.status = status; }
    public PaymentStatus getStatus() { return status; }
}

// ==================== SYSTEM CONTROLLER ====================

class MovieBookingSystem {
    private Map<String, Movie> movies =new HashMap<>();
    private Map<String, Show> shows = new HashMap<>();
    private Map<String, Booking> bookings = new ConcurrentHashMap<>();

    public void addMovie(Movie movie) {
        movies.put(movie.getTitle(), movie);
    }

    public void addShow(Show show) {
        shows.put(show.getShowId(), show);
    }

    // Core Booking Logic with Concurrency Handling
    public Booking bookTickets(User user, String showId, List<String> seatIds) {
        Show show = shows.get(showId);
        if (show == null) throw new IllegalArgumentException("Show not found");

        List<Seat> selectedSeats = new ArrayList<>();
        
        // 1. Lock individual seats to avoid race conditions (double-booking)
        // Sort seat IDs to prevent deadlocks when locking multiple seats
        seatIds.sort(String::compareTo);

        try {
            for (String seatId : seatIds) {
                Seat seat = show.getSeats().get(seatId);
                if (seat == null) throw new IllegalArgumentException("Seat " + seatId + " not found");
                
                seat.getLock().lock();
                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    // Release already acquired locks before failing
                    rollbackLocks(selectedSeats);
                    throw new IllegalStateException("Seat " + seatId + " is already reserved or booked.");
                }
                seat.setStatus(SeatStatus.RESERVED);
                selectedSeats.add(seat);
            }

            // 2. Create Booking
            String bookingId = "BKG_" + UUID.randomUUID().toString().substring(0, 8);
            Booking booking = new Booking(bookingId, user, show, selectedSeats);
            bookings.put(bookingId, booking);

            // 3. Simulate Payment Process
            boolean paymentSuccess = processPayment(booking);
            if (paymentSuccess) {
                booking.setStatus(BookingStatus.CONFIRMED);
                for (Seat seat : selectedSeats) {
                    seat.setStatus(SeatStatus.BOOKED);
                }
            } else {
                booking.setStatus(BookingStatus.CANCELLED);
                for (Seat seat : selectedSeats) {
                    seat.setStatus(SeatStatus.AVAILABLE); // Release seats
                }
            }

            return booking;

        } finally {
            // Unlock seats
            for (Seat seat : selectedSeats) {
                seat.getLock().unlock();
            }
        }
    }

    private void rollbackLocks(List<Seat> lockedSeats) {
        for (Seat seat : lockedSeats) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.getLock().unlock();
        }
    }

    private boolean processPayment(Booking booking) {
        // Payment gateway integration simulation
        Payment payment = new Payment("PAY_" + UUID.randomUUID().toString().substring(0, 8), 100.0);
        payment.setStatus(PaymentStatus.SUCCESS);
        booking.setPayment(payment);
        return payment.getStatus() == PaymentStatus.SUCCESS;
    }
}