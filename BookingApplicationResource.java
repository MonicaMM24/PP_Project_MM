package api;

import model.customer.Customer;
import model.booking.Booking;
import model.room.IRoom;
import service.customer.CustomerService;
import service.booking.BookingService;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;


public class BookingApplicationResource {

    private static final BookingApplicationResource SINGLETON = new BookingApplicationResource();

    private final CustomerService customerService = CustomerService.getSingleton();

    private final BookingService bookingService = BookingService.getSingleton();

    private BookingApplicationResource() {}

    public static BookingApplicationResource getSingleton(){
        return SINGLETON;
    }

    public Customer getCustomer(String email){
        return customerService.getCustomer(email);
    }

    public void createACustomer(String email, String firstName, String lastName){
        customerService.addCustomer(email, firstName, lastName);
    }

    public IRoom getRoom(String roomNumber){
        return bookingService.getARoom(roomNumber);
    }

    public Booking bookARoom(String customerEmail, IRoom room, Date checkInDate, Date checkOutDate){
        return bookingService.bookARoom(getCustomer(customerEmail), room, checkInDate,checkOutDate);
    }

    public Collection<Booking> getCustomersBookings(String customerEmail){
        final Customer customer = getCustomer(customerEmail);

        if(customer == null){
            return Collections.emptyList();
        }

        return bookingService.getCustomerBooking(getCustomer(customerEmail));
    }

    public Collection<IRoom> findARoom(final Date checkIn, final Date checkOut){
        return bookingService.findRooms(checkIn, checkOut);
    }

    public Collection<IRoom> findAlternativeRooms(final Date checkIn, final Date checkOut){
        return bookingService.findAlternativeRooms(checkIn, checkOut);
    }

    public Date addDefaultPlusDays(final Date date){
        return bookingService.addDefaultPlusDays(date);
    }
}
