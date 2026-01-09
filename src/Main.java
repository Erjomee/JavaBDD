import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://db:5432/mydb";
        String user = "myuser";
        String password = "mypassword";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // 1️⃣ Créer la table si elle n'existe pas
            String createTable = "CREATE TABLE IF NOT EXISTS personne (" +
                    "id SERIAL PRIMARY KEY," +
                    "nom VARCHAR(50)," +
                    "age INT" +
                    ")";
            stmt.executeUpdate(createTable);
            System.out.println("Table 'personne' créée ou déjà existante.");

            // 2️⃣ Vider la table avant d'insérer
            stmt.executeUpdate("TRUNCATE TABLE personne RESTART IDENTITY");
            System.out.println("Table vidée.");

            // 3️⃣ Insérer les données
            String insertData = "INSERT INTO personne (nom, age) VALUES " +
                    "('Alice', 25)," +
                    "('Bob', 30)," +
                    "('Charlie', 22)";
            stmt.executeUpdate(insertData);
            System.out.println("Données insérées.");

            // 4️⃣ Lire et afficher les données
            ResultSet rs = stmt.executeQuery("SELECT * FROM personne");
            System.out.println("\n=== Contenu de la table 'personne' ===");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("nom") + " | " +
                        rs.getInt("age"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}