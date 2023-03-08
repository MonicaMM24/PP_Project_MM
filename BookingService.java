package service.booking;

import model.customer.Customer;
import model.booking.Booking;
import model.room.IRoom;
import service.customer.CustomerService;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class BookingService {

    private static final BookingService SINGLETON = new BookingService();

    private static final int RECOMMENDED_ROOMS_DEFAULT_PLUS_DAYS = 7;

    private final Map<String, IRoom> rooms = new HashMap<>();

    private final Map<String, Collection<Booking>> bookings = new HashMap<>();

    private BookingService() {};

    public static BookingService getSingleton(){
        return SINGLETON;
    }

    public void addRoom(final IRoom room){
        rooms.put(room.getRoomNumber(), room);
    }

    public IRoom getARoom(final String roomNumber){
        return rooms.get(roomNumber);
    }

    public Collection<IRoom> getAllRooms() {
        return rooms.values();
    }

    public Booking bookARoom(final Customer customer, final IRoom room, final Date checkInDate, final Date checkOutDate ){
        final Booking booking = new Booking(customer, room, checkInDate, checkOutDate);

        Collection<Booking>customerBookings = getCustomerBooking(customer);

        if(customerBookings == null){
            customerBookings = new LinkedList<>();
        }

        customerBookings.add(booking);
        bookings.put(customer.getEmail(), customerBookings);

        return booking;

    }

    public Collection<IRoom> findRooms(final Date checkInDate, final Date checkOutDate){
        return findAvailableRooms(checkInDate, checkOutDate);
    }

    public Collection<IRoom> findAlternativeRooms(final Date checkInDate, final Date checkOutDate){
        return findAlternativeRooms(addDefaultPlusDays(checkInDate), addDefaultPlusDays(checkOutDate));
    }

    private Collection<IRoom> findAvailableRooms(final Date checkInDate, final Date checkOutDate){
        final Collection<Booking> allBookings = getAllBookings();
        final Collection<IRoom> notAvailableRooms = new LinkedList<>();

        for(Booking booking : allBookings){
            if(bookingOverlaps(booking, checkInDate, checkOutDate)){
                notAvailableRooms.add(booking.getRoom());
            }
        }

        return rooms.values().stream().filter(room ->notAvailableRooms.stream().noneMatch(notAvailableRoom->notAvailableRooms.equals(room))).collect(Collectors.toList());
    }

    public Date addDefaultPlusDays(final Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, RECOMMENDED_ROOMS_DEFAULT_PLUS_DAYS);

        return calendar.getTime();
    }

    private boolean bookingOverlaps(final Booking booking, final Date checkInDate, final Date checkOutDate){
        return checkInDate.before(booking.getCheckOutDate()) && checkOutDate.after(booking.getCheckInDate());
    }

    public Collection<Booking> getCustomerBooking(final Customer customer){
        return bookings.get(customer.getEmail());
    }

    public void printAllBooking(){
        final Collection<Booking> bookings = getAllBookings();

        if(bookings.isEmpty()){
            System.out.println("No bookings found.");
        }else{
            for (Booking booking : bookings){
                System.out.println(booking + "\n");
            }
        }
    }

    private Collection<Booking> getAllBookings(){
        final Collection<Booking>allBookings = new LinkedList<>();

        for (Collection<Booking> bookings : bookings.values()){
            allBookings.addAll(bookings);
        }

        return allBookings;
    }
}
