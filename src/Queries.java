import java.sql.*;
import java.util.Scanner;

public class Queries {

    static Scanner sc = new Scanner(System.in);

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
        if (!found) System.out.println("All hubs had reservations last month.");
    }

}