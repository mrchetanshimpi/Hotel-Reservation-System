package src;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "--------";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        Scanner sc = new Scanner(System.in);

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");

                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");

                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, sc, statement);
                        break;
                    case 2:
                        viewReservations(connection, sc, statement);
                        break;
                    case 3:
                        getRoomNumber(connection, sc, statement);
                        break;
                    case 4:
                        updateReservation(connection, sc, statement);
                        break;
                    case 5:
                        deleteReservation(connection, sc, statement);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        statement.close();
                        connection.close();
                        return;
                    default:
                        System.out.println("Invalid Choice. Try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void reserveRoom(Connection connection, Scanner sc, Statement statement) {
        try {
            System.out.println("Enter guest name: ");
            String guestName = sc.next();
            System.out.println("Enter room number: ");
            int roomNumber = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter contact number: ");
            String contactNumber = sc.next();

            String sql = "INSERT INTO reservations(guest_name, room_number, contact_number) " +
                    "VALUES('" + guestName + "', " + roomNumber + ", '" + contactNumber + "');";

            try {
                int affectedrows = statement.executeUpdate(sql);

                if (affectedrows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            } finally {

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection connection, Scanner sc, Statement statement) {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try {
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println();
            System.out.println("Current Reservations: ");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestname = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.println("Reservation Id: " + reservationId);
                System.out.println("Guest Name: " + guestname);
                System.out.println("Room Number: " + roomNumber);
                System.out.println("Contact Number: " + contactNumber);
                System.out.println("Reservation Date: " + reservationDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getRoomNumber(Connection connection, Scanner sc, Statement statement) {
        try {
            System.out.println("Enter reservation ID: ");
            int reservationId = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Guest Name: ");
            String guestName = sc.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try {
                ResultSet resultSet = statement.executeQuery(sql);

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println();
                    System.out.println("Room Number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            } finally {

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner sc, Statement statement) {
        try {
            System.out.println("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(connection, reservationId, statement)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = sc.next();
            System.out.println("Enter new room number: ");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            } finally {

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner sc, Statement statement) {
        try {
            System.out.println("Enter reservation ID to delete: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(connection, reservationId, statement)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            } finally {

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId, Statement statement) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (ResultSet resultSet = statement.executeQuery(sql)) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou for Using Hotel Reservation System!!!");
    }
}