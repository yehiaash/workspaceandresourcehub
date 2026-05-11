import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("✔ Connected to workspaceandresourcehub!");
            Scanner sc = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("\n╔══════════════════════════════════════╗");
                System.out.println("║   Smart Corporate Workspace Hub      ║");
                System.out.println("╠══════════════════════════════════════╣");
                System.out.println("║  --- CRUD OPERATIONS ---             ║");
                System.out.println("║  1.  Insert New Member               ║");
                System.out.println("║  2.  Insert New Reservation          ║");
                System.out.println("║  3.  Delete Member                   ║");
                System.out.println("║  4.  Delete Reservation              ║");
                System.out.println("║  5.  Update Member Info              ║");
                System.out.println("║  6.  Update Reservation              ║");
                System.out.println("║  --- SELECT ---                      ║");
                System.out.println("║  7.  View All Members                ║");
                System.out.println("║  8.  View All Equipment              ║");
                System.out.println("║  9.  View Reservations (JOIN)        ║");
                System.out.println("║  10. View Equipment Usage Log (JOIN) ║");
                System.out.println("║  --- INQUIRIES ---                   ║");
                System.out.println("║  11. Most Popular Workspace Type     ║");
                System.out.println("║  12. Hubs with No Reservations       ║");
                System.out.println("║  13. Member with Most Equipment      ║");
                System.out.println("║  14. Members with No Reservations    ║");
                System.out.println("║  15. Equipment Per Hub               ║");
                System.out.println("║  16. Member Total Hours Reserved     ║");
                System.out.println("║  0.  Exit                            ║");
                System.out.println("╚══════════════════════════════════════╝");
                System.out.print("Choose: ");

                String choice = sc.nextLine();
                switch (choice) {
                    // case "1"  -> Queries.insertMember(conn);
                    // case "2"  -> Queries.insertReservation(conn);
                    // case "3"  -> Queries.deleteMember(conn);
                    // case "4"  -> Queries.deleteReservation(conn);
                    // case "5"  -> Queries.updateMember(conn);
                    // case "6"  -> Queries.updateReservation(conn);       
                    // case "7"  -> Queries.selectAllMembers(conn);
                    // case "8"  -> Queries.selectAllEquipment(conn);     
                    case "9"  -> Queries.selectReservationsJoin(conn);   
                    case "10" -> Queries.selectLogJoin(conn);            
                    case "11" -> Queries.inquiry1(conn);
                    case "12" -> Queries.inquiry2(conn);
                    // case "13" -> Queries.inquiry3(conn);
                    // case "14" -> Queries.inquiry4(conn);
                    // case "15" -> Queries.inquiry5(conn);
                    // case "16" -> Queries.inquiry6(conn);
                    case "0"  -> running = false;
                    default   -> System.out.println("Invalid choice, try again.");
                }
            }
            conn.close();
            System.out.println("Goodbye!");
        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}