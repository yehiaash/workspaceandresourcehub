import java.sql.*;
import java.util.Scanner;

public class Queries {

    static Scanner sc = new Scanner(System.in);

    public static void insertMember(Connection conn) throws SQLException {
        System.out.println("Please enter member's data (id,name,email,phone,affiliation): ");
        int id = sc.nextInt();
        sc.nextLine();
        String name = sc.nextLine();
        String email = sc.nextLine();
        String phone = sc.nextLine();
        String affiliation = sc.nextLine();
        String sql = "insert into member values(?,?,?,?,?,getdate())";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, id);
        statement.setString(2, name);
        statement.setString(3, email);
        statement.setString(4, phone);
        statement.setString(5, affiliation);
        statement.executeUpdate();

    }

    public static void insertReservation(Connection conn) throws SQLException {
        System.out.println("Please enter reservation's data (reservationid,workspaceid,memberid,starttime,endtime): ");
        int reservationid = sc.nextInt();
        int workspaceid = sc.nextInt();
        int memberid = sc.nextInt();
        sc.nextLine();
        String starttime = sc.nextLine();
        String endtime = sc.nextLine();
        String sql = "insert into reservation values(?,?,?,'reserved',getdate(),?,?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, reservationid);
        statement.setInt(2, workspaceid);
        statement.setInt(3, memberid);
        statement.setString(4, starttime);
        statement.setString(5, endtime);
        statement.executeUpdate();

    }

    // ============================================================
    // JOIN 1 — Reservation with Member + Workspace + Hub
    // ============================================================
    public static void selectReservationsJoin(Connection conn) throws SQLException {
        System.out.println("\n--- Reservations with Full Details (JOIN) ---");
        String sql = """
                SELECT
                    r.RESERVATION_ID,
                    m.MEMBER_ID,
                    m.FULLNAME,
                    w.TYPE,
                    h.HUB_NAME,
                    h.DISTRICT,
                    r.STATUS,
                    r.STARTTIME
                FROM RESERVATION r
                JOIN MEMBER    m ON r.MEMBER_ID    = m.MEMBER_ID
                JOIN WORKSPACE w ON r.WORKSPACE_ID = w.WORKSPACE_ID
                JOIN URBAN_HUB h ON w.HUB_ID       = h.HUB_ID
                ORDER BY r.RESERVATIONDATE DESC
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.printf("%-6s %-6s %-18s %-25s %-16s %-15s %-12s %-22s%n",
                "ResID", "MemID", "Member Name", "Workspace",
                "Hub", "District", "Status", "Start Time");
        System.out.println("-".repeat(122));
        while (rs.next()) {
            System.out.printf("%-6s %-6s %-18s %-25s %-16s %-15s %-12s %-22s%n",
                    String.valueOf(rs.getInt(1)),
                    String.valueOf(rs.getInt(2)),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),
                    rs.getString(8));
        }
    }

    // ============================================================
    // JOIN 2 — LOG with Equipment + Member + Reservation
    // ============================================================
    public static void selectLogJoin(Connection conn) throws SQLException {
        System.out.println("\n--- Equipment Usage Log with Full Details (JOIN) ---");
        String sql = """
                SELECT
                    l.LOG_ID,
                    m.MEMBER_ID,
                    m.FULLNAME,
                    e.EQUIPMENTID,
                    e.EQUIPMENTNAME,
                    e.EQUIPMENTTYPE,
                    l.DURATION,
                    h.HUB_NAME
                FROM LOG l
                JOIN RESERVATION r ON l.RESERVATION_ID = r.RESERVATION_ID
                JOIN MEMBER      m ON r.MEMBER_ID       = m.MEMBER_ID
                JOIN EQUIPMENT   e ON l.EQUIPMENTID     = e.EQUIPMENTID
                JOIN WORKSPACE   w ON r.WORKSPACE_ID    = w.WORKSPACE_ID
                JOIN URBAN_HUB   h ON w.HUB_ID          = h.HUB_ID
                ORDER BY l.LOG_ID
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.printf("%-8s %-6s %-18s %-6s %-20s %-15s %-10s %-15s%n",
                "Log ID", "MemID", "Member Name", "EqID",
                "Equipment Name", "Type", "Duration", "Hub");
        System.out.println("-".repeat(105));
        while (rs.next()) {
            System.out.printf("%-8s %-6s %-18s %-6s %-20s %-15s %-10s %-15s%n",
                    String.valueOf(rs.getInt(1)),
                    String.valueOf(rs.getInt(2)),
                    rs.getString(3),
                    String.valueOf(rs.getInt(4)),
                    rs.getString(5),
                    rs.getString(6),
                    String.valueOf(rs.getInt(7)),
                    rs.getString(8));
        }
    }

    // ============================================================
    // INQUIRY 1 — Most popular workspace TYPE
    // ============================================================
    public static void inquiry1(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 1] Most popular workspace type (max reservations):");
        String sql = """
                SELECT TOP 1 w.TYPE, COUNT(r.RESERVATION_ID) AS TotalReservations
                FROM RESERVATION r
                JOIN WORKSPACE w ON r.WORKSPACE_ID = w.WORKSPACE_ID
                GROUP BY w.TYPE
                ORDER BY TotalReservations DESC
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next())
            System.out.printf("Workspace Type: %-20s | Total Reservations: %s%n",
                    rs.getString(1), String.valueOf(rs.getInt(2)));
        else
            System.out.println("No data found.");
    }

    // ============================================================
    // INQUIRY 2 — Hubs with NO reservations last month
    // ============================================================
    public static void inquiry2(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 2] Urban hubs with NO reservations last month:");
        String sql = """
                SELECT h.HUB_ID, h.HUB_NAME, h.DISTRICT
                FROM URBAN_HUB h
                WHERE h.HUB_ID NOT IN (
                    SELECT w.HUB_ID
                    FROM RESERVATION r
                    JOIN WORKSPACE w ON r.WORKSPACE_ID = w.WORKSPACE_ID
                    WHERE MONTH(r.RESERVATIONDATE) = MONTH(DATEADD(MONTH,-1,GETDATE()))
                      AND YEAR(r.RESERVATIONDATE)  = YEAR(DATEADD(MONTH,-1,GETDATE()))
                )
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        boolean found = false;
        while (rs.next()) {
            System.out.printf("Hub ID: %-5s | Name: %-20s | District: %s%n",
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    rs.getString(3));
            found = true;
        }
        if (!found)
            System.out.println("All hubs had reservations last month.");
    }

    // ============================================================
    // INQUIRY 3 — Member with Most Equipment usage
    // ============================================================
    public static void inquiry3(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 3] Member with the most equipment usage:");
        String sql = """
                SELECT TOP 1
                    m.MEMBER_ID,
                    m.FULLNAME,
                    COUNT(DISTINCT l.EQUIPMENTID) AS EquipmentCount
                FROM MEMBER m
                JOIN RESERVATION r ON m.MEMBER_ID = r.MEMBER_ID
                JOIN LOG l ON r.RESERVATION_ID = l.RESERVATION_ID
                GROUP BY m.MEMBER_ID, m.FULLNAME
                ORDER BY EquipmentCount DESC
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next())
            System.out.printf("Member ID: %-6s | Name: %-20s | Equipment Used: %s%n",
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    String.valueOf(rs.getInt(3)));
        else
            System.out.println("No equipment usage data found.");
    }

    // ============================================================
    // INQUIRY 4 — Members with NO reservations
    // ============================================================
    public static void inquiry4(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 4] Members with no reservations:");
        String sql = """
                SELECT m.MEMBER_ID, m.FULLNAME, m.EMAIL, m.PHONE
                FROM MEMBER m
                WHERE m.MEMBER_ID NOT IN (
                    SELECT DISTINCT r.MEMBER_ID
                    FROM RESERVATION r
                    WHERE r.MEMBER_ID IS NOT NULL
                )
                ORDER BY m.MEMBER_ID
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        boolean found = false;
        while (rs.next()) {
            System.out.printf("Member ID: %-6s | Name: %-20s | Email: %-25s | Phone: %s%n",
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4));
            found = true;
        }
        if (!found)
            System.out.println("All members have made at least one reservation.");
    }

    // ============================================================
    // INQUIRY 5 — Equipment Count Per Hub
    // ============================================================
    public static void inquiry5(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 5] Equipment count per urban hub:");
        String sql = """
                SELECT
                    h.HUB_ID,
                    h.HUB_NAME,
                    h.DISTRICT,
                    COUNT(e.EQUIPMENTID) AS EquipmentCount
                FROM URBAN_HUB h
                LEFT JOIN WORKSPACE w ON h.HUB_ID = w.HUB_ID
                LEFT JOIN EQUIPMENT e ON w.WORKSPACE_ID = e.WORKSPACE_ID
                GROUP BY h.HUB_ID, h.HUB_NAME, h.DISTRICT
                ORDER BY EquipmentCount DESC
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        boolean found = false;
        while (rs.next()) {
            System.out.printf("Hub ID: %-5s | Name: %-20s | District: %-15s | Equipment: %s%n",
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    rs.getString(3),
                    String.valueOf(rs.getInt(4)));
            found = true;
        }
        if (!found)
            System.out.println("No hub data found.");
    }

}
