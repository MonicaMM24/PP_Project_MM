import api.BookingApplicationResource;
import model.booking.Booking;
import model.room.IRoom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

public class MainMenu {

    private static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";

    private static final BookingApplicationResource bookingapplicationresource = BookingApplicationResource.getSingleton();

    public static void mainMenu() {
        String line = "";
        Scanner scanner = new Scanner(System.in);

        printMainMenu();

        try {
            do {
                line = scanner.nextLine();

                if (line.length() == 1) {
                    switch (line.charAt(0)) {
                        case '1':
                            findAndBookRoom();
                            break;
                        case '2':
                            seeMyBooking();
                            break;
                        case '3':
                            createAccount();
                            break;
                        case '4':
                            AdminMenu.adminMenu();
                            break;
                        case '5':
                            System.out.println("Exit");
                            break;
                        default:
                            System.out.println("Unkown action\n");
                            break;
                    }
                } else {
                    System.out.println("Error: Invalid action\n");
                }
            } while (line.charAt(0) != '5' || line.length() != 1);
        } catch (StringIndexOutOfBoundsException ex) {
            System.out.println("Empty input received. Exiting program...");
        }
    }

    private static void findAndBookRoom() {
        final Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Check-In Date mm/dd/yyyy example 01/19/2022");
        Date checkIn = getInputDate(scanner);

        System.out.println("Enter Check-Out Date mm/dd/yyyy example 01/25/2022");
        Date checkOut = getInputDate(scanner);

        if (checkIn != null && checkOut != null) {
            Collection<IRoom> availableRooms = bookingapplicationresource.findARoom(checkIn, checkOut);

            if (availableRooms.isEmpty()) {
                Collection<IRoom> alternativeRooms = bookingapplicationresource.findAlternativeRooms(checkIn, checkOut);

                if (availableRooms.isEmpty()) {
                    System.out.println("No rooms found!");
                } else {
                    final Date alternativeCheckIn = bookingapplicationresource.addDefaultPlusDays(checkIn);
                    final Date alternativeCheckOut = bookingapplicationresource.addDefaultPlusDays(checkOut);
                    System.out.println("We have only found rooms on alternative dates:" +
                            "\nCheck-In Date:" + alternativeCheckIn +
                            "\nCheck-Out Date:" + alternativeCheckOut);

                    printRooms(alternativeRooms);
                    bookRoom(scanner, alternativeCheckIn, alternativeCheckOut, alternativeRooms);
                }
            } else {
                printRooms(availableRooms);
                bookRoom(scanner, checkIn, checkOut, availableRooms);
            }

        }
    }

    private static Date getInputDate(final Scanner scanner) {
        try {
            return new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(scanner.nextLine());
        } catch (ParseException ex) {
            System.out.println("Error: Invalid date.");
            findAndBookRoom();
        }

        return null;
    }

    private static void bookRoom(final Scanner scanner, final Date checkInDate, final Date checkOutDate, final Collection<IRoom> rooms) {
        System.out.println("Would you like to book? y/n");
        final String bookRoom = scanner.nextLine();

        if ("y".equals(bookRoom)) {
            System.out.println("Do you have an account with us? y/n");
            final String haveAccount = scanner.nextLine();

            if ("y".equals(haveAccount)) {
                System.out.println("Enter Email format: name@domain.com");
                final String customerEmail = scanner.nextLine();

                if (bookingapplicationresource.getCustomer(customerEmail) == null) {
                    System.out.println("Customer not found.\n You need to create a new account.");
                } else {
                    System.out.println("What room number would you like to reserve?");
                    final String roomNumber = scanner.nextLine();

                    if (rooms.stream().anyMatch(room -> room.getRoomNumber().equals(roomNumber))) {
                        final IRoom room = bookingapplicationresource.getRoom(roomNumber);

                        final Booking booking = bookingapplicationresource
                                .bookARoom(customerEmail, room, checkInDate, checkOutDate);
                        System.out.println("Booking created successfully!");
                        System.out.println(booking);
                    } else {
                        System.out.println("Error: room number not available\nStart booking again.");
                    }
                }

                printMainMenu();
            } else {
                System.out.println("Please, create an account.");
                printMainMenu();
            }
        } else if ("n".equals(bookRoom)) {
            printMainMenu();
        } else {
            bookRoom(scanner, checkInDate, checkOutDate, rooms);
        }
    }

    private static void printRooms(final Collection<IRoom> rooms) {
        if (rooms.isEmpty()) {
            System.out.println("No rooms found");
        } else {
            rooms.forEach(System.out::println);
        }
    }

    private static void seeMyBooking() {
        final Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your Email format: name@domain.com");
        final String customerEmail = scanner.nextLine();

        printBookings(bookingapplicationresource.getCustomersBookings(customerEmail));
    }

    private static void printBookings(final Collection<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            bookings.forEach(booking -> System.out.println("\n" + booking));
        }
    }

    private static void createAccount() {
        final Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Email format: name@domain.com");
        final String email = scanner.nextLine();

        System.out.println("First Name:");
        final String firstName = scanner.nextLine();

        System.out.println("Last Name");
        final String lastName = scanner.nextLine();

        try {
            bookingapplicationresource.createACustomer(email, firstName, lastName);
            System.out.println("Account created successfully!");

            printMainMenu();
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getLocalizedMessage());
            createAccount();
        }


    }

    public static void printMainMenu() {
        System.out.println("\n Welcome to the Booking Application\n" +
                "------------------------------------------\n" +
                "1.Find and reserve a room\n" +
                "2.See my bookings\n" +
                "3.Create an Account\n" +
                "4.Admin\n" +
                "5.Exit\n" +
                "--------------------------------------------\n" +
                "Please select a number for the menu option:\n");
    }

}
