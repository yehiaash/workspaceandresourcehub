
import java.sql.*;
import java.util.Scanner;

public class Queries {

    static Scanner sc = new Scanner(System.in);

    // ============================================================
    // INSERT MEMBER — Dynamic ID (Manual MAX+1, no schema change)
    // ============================================================
    public static void insertMember(Connection conn) throws SQLException {
        System.out.println("\n--- Insert New Member ---");

        System.out.print("Full Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Phone: ");
        String phone = sc.nextLine();

        System.out.print("Affiliation: ");
        String affiliation = sc.nextLine();

        // Fetch the current max MEMBER_ID and add 1
        int newId = 1;
        String maxSql = "SELECT MAX(MEMBER_ID) FROM MEMBER";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(maxSql)) {
            if (rs.next() && rs.getObject(1) != null) {
                newId = rs.getInt(1) + 1;
            }
        }

        String sql = """
            INSERT INTO MEMBER (MEMBER_ID, FULLNAME, EMAIL, PHONE, AFFILIATION, REGISTRATIONDATE)
            VALUES (?, ?, ?, ?, ?, GETDATE())
            """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.setString(5, affiliation);

            statement.executeUpdate();
            System.out.println("✔ Member inserted successfully! Generated Member ID: " + newId);
        } catch (SQLException e) {
            System.out.println("Error inserting member: " + e.getMessage());
        }
    }
    // ============================================================
    // INSERT RESERVATION — Dynamic ID (Manual MAX+1, no schema change)
    // ============================================================

    public static void insertReservation(Connection conn) throws SQLException {
        System.out.println("\n--- Insert New Reservation ---");

        System.out.print("Workspace ID: ");
        int workspaceId = sc.nextInt();

        System.out.print("Member ID: ");
        int memberId = sc.nextInt();

        sc.nextLine(); // Consume the leftover newline

        System.out.print("Start Time: ");
        String startTime = sc.nextLine();

        System.out.print("End Time: ");
        String endTime = sc.nextLine();

        // Fetch the current max RESERVATION_ID and add 1
        int newId = 1;
        String maxSql = "SELECT MAX(RESERVATION_ID) FROM RESERVATION";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(maxSql)) {
            if (rs.next() && rs.getObject(1) != null) {
                newId = rs.getInt(1) + 1;
            }
        }

        String sql = """
            INSERT INTO RESERVATION (RESERVATION_ID, WORKSPACE_ID, MEMBER_ID, STATUS, RESERVATIONDATE, STARTTIME, ENDTIME)
            VALUES (?, ?, ?, 'reserved', GETDATE(), ?, ?)
            """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.setInt(2, workspaceId);
            statement.setInt(3, memberId);
            statement.setString(4, startTime);
            statement.setString(5, endTime);

            statement.executeUpdate();
            System.out.println("✔ Reservation inserted successfully! Generated Reservation ID: " + newId);
        } catch (SQLException e) {
            System.out.println("Error inserting reservation: " + e.getMessage());
        }
    }

    public static void deleteReservation(Connection conn) throws SQLException {
        System.out.println("Enter reservation ID to delete: ");
        int reservationid = sc.nextInt();
        sc.nextLine();

        String checksql = "Select count(*) from RESERVATION where RESERVATION_ID = ?";
        PreparedStatement checkstmt = conn.prepareStatement(checksql);
        checkstmt.setInt(1, reservationid);

        ResultSet rs = checkstmt.executeQuery();
        rs.next();

        if (rs.getInt(1) == 0) {
            System.out.println("Reservation with ID " + reservationid + " not found!");
            return;
        }

        String sql = "Delete from RESERVATION where RESERVATION_ID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, reservationid);
        int rowsDeleted = stmt.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Reservation deleted successfully!");
        }
    }

    public static void deleteMember(Connection conn) throws SQLException {
        System.out.println("Enter member ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String checksql = "Select count(*) from member where MEMBER_ID = ?";
        PreparedStatement checkstmt = conn.prepareStatement(checksql);
        checkstmt.setInt(1, id);

        ResultSet rs = checkstmt.executeQuery();
        rs.next();
        if (rs.getInt(1) == 0) {
            System.out.println("Member with ID " + id + " not found!");
            return;
        }

        try {
            String sql = "Delete from member where member_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Member deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Cannot delete member: They have existing reservation.");
        }

    }
    public static void updateMember(Connection conn) throws SQLException {
        System.out.println("Please enter member id: ");
        int id=sc.nextInt();
        sc.nextLine();
        System.out.println("Please enter data you wish to update(email,phone,affiliation): ");
        String email=sc.nextLine();
        String phone=sc.nextLine();
        String affiliation=sc.nextLine();
        String sql="update member set email=?, phone=?, affiliation=? where member_id=?";
        PreparedStatement statement= conn.prepareStatement(sql);
        statement.setString(1,email);
        statement.setString(2,phone);
        statement.setString(3,affiliation);
        statement.setInt(4,id);
        int rows=statement.executeUpdate();
        if(rows==0){
            System.out.println("Member id not found");
        }
        else{
            System.out.println("Data updated successfully");
        }

    }
    public static void updateReservation(Connection conn) throws SQLException {
        System.out.println("Please enter reservation id: ");
        int id=sc.nextInt();
        sc.nextLine();
        System.out.println("Please enter data you wish to update(start time,end time): ");
        String stime=sc.nextLine();
        String etime=sc.nextLine();
        String sql="update reservation set starttime=?, endtime=? where reservation_id=?";
        PreparedStatement statement= conn.prepareStatement(sql);
        statement.setString(1,stime);
        statement.setString(2,etime);
        statement.setInt(3,id);
        int rows=statement.executeUpdate();
        if(rows==0){
            System.out.println("Reservation id not found");
        }
        else{
            System.out.println("Data updated successfully");
        }

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
    // SELECT ALL MEMBERS
    // ============================================================
    public static void selectAllMembers(Connection conn) throws SQLException {
        System.out.println("\n--- All Members ---");
        String sql = """
                SELECT MEMBER_ID, FULLNAME, EMAIL, PHONE, AFFILIATION, REGISTRATIONDATE
                FROM MEMBER
                ORDER BY MEMBER_ID
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.printf("%-8s %-20s %-25s %-15s %-25s %-15s%n",
                "ID", "Full Name", "Email", "Phone", "Affiliation", "Registered");
        System.out.println("-".repeat(110));

        boolean found = false;
        while (rs.next()) {
            System.out.printf("%-8s %-20s %-25s %-15s %-25s %-15s%n",
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    String.valueOf(rs.getDate(6)));
            found = true;
        }
        if (!found) System.out.println("No members found.");
        st.close();
    }

    // ============================================================
    // SELECT ALL EQUIPMENT
    // ============================================================
    public static void selectAllEquipment(Connection conn) throws SQLException {
        System.out.println("\n--- All Equipment ---");
        String sql = """
                SELECT EQUIPMENTID, EQUIPMENTNAME, EQUIPMENTTYPE, USAGERATE, AVAILABILITYSTATUS
                FROM EQUIPMENT
                ORDER BY EQUIPMENTID
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.printf("%-8s %-22s %-18s %-12s %-15s%n",
                "ID", "Equipment Name", "Type", "Usage Rate", "Status");
        System.out.println("-".repeat(80));

        boolean found = false;
        while (rs.next()) {
            System.out.printf("%-8s %-22s %-18s %-12s %-15s%n",
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    rs.getString(3),
                    String.valueOf(rs.getDouble(4)),
                    rs.getString(5));
            found = true;
        }
        if (!found) System.out.println("No equipment found.");
        st.close();
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
        if (rs.next()) {
            System.out.printf("Workspace Type: %-20s | Total Reservations: %s%n",
                    rs.getString(1), String.valueOf(rs.getInt(2)));
        } else {
            System.out.println("No data found.");
        }
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
        if (!found) {
            System.out.println("All hubs had reservations last month.");
        }
    }

    // ============================================================
    // INQUIRY 3 — Member with Most Equipment usage (LAST MONTH ONLY)
    // ============================================================
    public static void inquiry3(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 3] Member with the most equipment usage (Last Month):");
        String sql = """
                SELECT TOP 1
                    m.MEMBER_ID,
                    m.FULLNAME,
                    COUNT(DISTINCT l.EQUIPMENTID) AS EquipmentCount
                FROM MEMBER m
                JOIN RESERVATION r ON m.MEMBER_ID = r.MEMBER_ID
                JOIN LOG l ON r.RESERVATION_ID = l.RESERVATION_ID
                WHERE MONTH(r.RESERVATIONDATE) = MONTH(DATEADD(MONTH, -1, GETDATE()))
                  AND YEAR(r.RESERVATIONDATE)  = YEAR(DATEADD(MONTH, -1, GETDATE()))
                GROUP BY m.MEMBER_ID, m.FULLNAME
                ORDER BY EquipmentCount DESC
                """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            System.out.printf("Member ID: %-6s | Name: %-20s | Equipment Used: %s%n",
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    String.valueOf(rs.getInt(3)));
        } else {
            System.out.println("No equipment usage data found for last month.");
        }
        st.close();
    }

    // ============================================================
    // INQUIRY 4 — Members with NO reservations (LAST MONTH ONLY)
    // ============================================================
    public static void inquiry4(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 4] Members with no reservations (Last Month):");
        String sql = """
                SELECT m.MEMBER_ID, m.FULLNAME, m.EMAIL, m.PHONE
                FROM MEMBER m
                WHERE m.MEMBER_ID NOT IN (
                    SELECT DISTINCT r.MEMBER_ID
                    FROM RESERVATION r
                    WHERE r.MEMBER_ID IS NOT NULL
                      AND MONTH(r.RESERVATIONDATE) = MONTH(DATEADD(MONTH, -1, GETDATE()))
                      AND YEAR(r.RESERVATIONDATE)  = YEAR(DATEADD(MONTH, -1, GETDATE()))
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
        if (!found) {
            System.out.println("All members made at least one reservation last month.");
        }
        st.close();
    }

    // ============================================================
    // INQUIRY 5 — Equipment Count Per Hub (LAST MONTH ONLY)
    // ============================================================
    public static void inquiry5(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 5] Equipment count per urban hub (Last Month):");
        String sql = """
                SELECT
                h.HUB_NAME,
                h.DISTRICT,
                e.EQUIPMENTID,
                e.EQUIPMENTNAME,
                e.EQUIPMENTTYPE,
                SUM(l.DURATION) AS TotalDuration
            FROM LOG l
            JOIN RESERVATION r ON l.RESERVATION_ID = r.RESERVATION_ID
            JOIN WORKSPACE   w ON r.WORKSPACE_ID   = w.WORKSPACE_ID
            JOIN URBAN_HUB   h ON w.HUB_ID         = h.HUB_ID
            JOIN EQUIPMENT   e ON l.EQUIPMENTID    = e.EQUIPMENTID
            WHERE MONTH(r.RESERVATIONDATE) = MONTH(DATEADD(MONTH,-1,GETDATE()))
              AND YEAR(r.RESERVATIONDATE)  = YEAR(DATEADD(MONTH,-1,GETDATE()))
            GROUP BY h.HUB_NAME, h.DISTRICT, e.EQUIPMENTID, e.EQUIPMENTNAME, e.EQUIPMENTTYPE
            ORDER BY h.HUB_NAME, e.EQUIPMENTNAME
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
        if (!found) {
            System.out.println("No hub data found for last month.");
        }
        st.close();
    }
    // ===============================================================
    // INQUIRY 6 — Each Member's Full Profile + Total Hours Reserved
    // ===============================================================

    public static void inquiry6(Connection conn) throws SQLException {
        System.out.println("\n[Inquiry 6] Each member's full profile and total hours reserved:");
        String sql = """
            SELECT
                m.MEMBER_ID,
                m.FULLNAME,
                m.EMAIL,
                m.PHONE,
                m.AFFILIATION,
                m.REGISTRATIONDATE,
                COALESCE(SUM(DATEDIFF(HOUR, r.STARTTIME, r.ENDTIME)), 0) AS TotalHoursReserved
            FROM MEMBER m
            LEFT JOIN RESERVATION r ON m.MEMBER_ID = r.MEMBER_ID
            GROUP BY m.MEMBER_ID, m.FULLNAME, m.EMAIL, m.PHONE, m.AFFILIATION, m.REGISTRATIONDATE
            ORDER BY TotalHoursReserved DESC
            """;
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        boolean found = false;
        while (rs.next()) {
            System.out.printf(
                    "ID: %-5s | Name: %-20s | Email: %-25s | Phone: %-15s | Affiliation: %-20s | Registered: %-12s | Total Hours: %s%n",
                    String.valueOf(rs.getInt(1)),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    String.valueOf(rs.getDate(6)),
                    String.valueOf(rs.getInt(7))
            );
            found = true;
        }
        if (!found) {
            System.out.println("No member data found.");
        }
        st.close();
    }
}
