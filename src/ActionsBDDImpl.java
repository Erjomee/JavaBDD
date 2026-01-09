import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActionsBDDImpl implements ActionsBDD {
    private static final String SELECT_ALL_PROGRAMMEURS =
            "SELECT * FROM programmeur";

    private static final String SELECT_PROGRAMMEUR_BY_ID =
            "SELECT * FROM programmeur WHERE id_programmeur = ?";

    private static final String DELETE_PROGRAMMEUR =
            "DELETE FROM programmeur WHERE id_programmeur = ?";

    private static final String INSERT_PROGRAMMEUR =
            "INSERT INTO programmeur (nom, prenom, an_naissance, salaire, prime, id_projet) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SALAIRE =
            "UPDATE programmeur SET salaire = ? WHERE id_programmeur = ?";

    private static final String SELECT_ALL_PROJETS =
            "SELECT * FROM projet";

    private static final String SELECT_PROGRAMMEURS_BY_PROJET =
            "SELECT * FROM programmeur WHERE id_projet = ?";

    /* =========================
       MÉTHODES
       ========================= */

    @Override
    public void afficherTousLesProgrammeurs() {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_PROGRAMMEURS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getInt("id_programmeur") + " | "
                        + rs.getString("nom") + " "
                        + rs.getString("prenom") + " | Salaire : "
                        + rs.getDouble("salaire"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des programmeurs.");
        }
    }

    @Override
    public void afficherProgrammeurParId(int id) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PROGRAMMEUR_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("ID : " + rs.getInt("id_programmeur"));
                System.out.println("Nom : " + rs.getString("nom"));
                System.out.println("Prénom : " + rs.getString("prenom"));
                System.out.println("Salaire : " + rs.getDouble("salaire"));
                System.out.println("Prime : " + rs.getDouble("prime"));
            } else {
                System.out.println("Aucun programmeur trouvé avec cet ID.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du programmeur.");
        }
    }

    @Override
    public boolean supprimerProgrammeur(int id) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_PROGRAMMEUR)) {

            ps.setInt(1, id);
            int lignes = ps.executeUpdate();

            return lignes > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression.");
            return false;
        }
    }

    @Override
    public void ajouterProgrammeur(Programmeur p) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_PROGRAMMEUR)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setInt(3, p.getAnNaissance());
            ps.setDouble(4, p.getSalaire());
            ps.setDouble(5, p.getPrime());
            ps.setInt(6, p.getIdProjet());

            ps.executeUpdate();
            System.out.println("Programmeur ajouté avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du programmeur.");
        }
    }

    @Override
    public boolean modifierSalaire(int id, double nouveauSalaire) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SALAIRE)) {

            ps.setDouble(1, nouveauSalaire);
            ps.setInt(2, id);

            int lignes = ps.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du salaire.");
            return false;
        }
    }

    @Override
    public void afficherProjets() {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_PROJETS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getInt("id_projet") + " | "
                        + rs.getString("intitule") + " | "
                        + rs.getString("etat"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des projets.");
        }
    }

    @Override
    public void afficherProgrammeursParProjet(int idProjet) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PROGRAMMEURS_BY_PROJET)) {

            ps.setInt(1, idProjet);
            ResultSet rs = ps.executeQuery();

            boolean vide = true;

            while (rs.next()) {
                vide = false;
                System.out.println(rs.getString("nom") + " "
                        + rs.getString("prenom"));
            }

            if (vide) {
                System.out.println("Aucun programmeur pour ce projet.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des programmeurs du projet.");
        }
    }
}
