import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

   private static final String SERVER   = "localhost";
    private static final String DATABASE = "workspaceandresourcehub";
    private static final String URL =
        "jdbc:sqlserver://" + SERVER + ":1433;" +
        "databaseName=" + DATABASE + ";" +
        "integratedSecurity=true;" +        
        "trustServerCertificate=true;";


    public static Connection getConnection() {

        Connection conn = null;

        try {

            conn = DriverManager.getConnection(URL);

            System.out.println("Connected Successfully!");

        } catch (SQLException e) {

            System.out.println("Connection Failed!");

            e.printStackTrace();
        }

        return conn;
    }
}