package api;

import model.customer.Customer;
import model.room.IRoom;
import service.customer.CustomerService;
import service.booking.BookingService;

import java.util.Collection;
import java.util.List;

public class AdminResource {

    private static final AdminResource SINGLETON = new AdminResource();

    private final CustomerService customerService = CustomerService.getSingleton();

    private final BookingService bookingService = BookingService.getSingleton();

    private AdminResource(){}

    public static AdminResource getSingleton(){
        return SINGLETON;
    }

    public  Customer getCustomer(String email){
        return customerService.getCustomer(email);
    }

    public void addRoom(List<IRoom>rooms){
        rooms.forEach(bookingService::addRoom);
    }

    public Collection<IRoom> getAllRooms(){
        return bookingService.getAllRooms();
    }

    public Collection<Customer> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    public void displayAllBookings(){
        bookingService.printAllBooking();
    }
}
