package backend.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer les connexions à la base de données PostgreSQL.
 * Utilise le pattern Singleton et fournit des connexions JDBC.
 *
 * Cette classe charge le driver PostgreSQL au démarrage de l'application
 * et fournit une méthode statique pour obtenir des connexions.
 *
 * @author Jerome TRAN
 * @version 1.0
 */
public class ConnexionBDD {

    /** URL de connexion à la base de données PostgreSQL */
    private static final String URL = "jdbc:postgresql://db:5432/mydb";

    /** Nom d'utilisateur pour la connexion à la base de données */
    private static final String USER = "myuser";

    /** Mot de passe pour la connexion à la base de données */
    private static final String PASSWORD = "mypassword";

    /**
     * Bloc statique pour charger le driver PostgreSQL une seule fois au démarrage.
     * Ce bloc s'exécute automatiquement lors du premier chargement de la classe.
     * En cas d'échec, un message d'erreur détaillé est affiché.
     */
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

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     * Cette classe ne doit être utilisée que via ses méthodes statiques.
     */
    private ConnexionBDD() {
        // Empêche l'instanciation
    }

    /**
     * Obtient une nouvelle connexion à la base de données PostgreSQL.
     *
     * Cette méthode crée une nouvelle connexion à chaque appel.
     *
     * @return Une connexion active à la base de données
     * @throws SQLException Si une erreur survient lors de la connexion
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}