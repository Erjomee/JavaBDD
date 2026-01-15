package backend.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionBDD {

    private static final String URL =
            "jdbc:postgresql://db:5432/mydb";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    private ConnexionBDD() {
        // Empêche l'instanciation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
