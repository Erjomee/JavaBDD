package backend.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionBDD {

    private static final String URL = "jdbc:postgresql://db:5432/mydb";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";

    // Bloc statique pour charger le driver une seule fois
    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL chargé avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur : Driver PostgreSQL introuvable !");
            System.err.println("Assurez-vous que postgresql-XX.jar est dans le classpath");
            e.printStackTrace();
        }
    }

    private ConnexionBDD() {
        // Empêche l'instanciation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}