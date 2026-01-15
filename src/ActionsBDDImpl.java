import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
       PROGRAMMEURS
       ========================= */

    @Override
    public List<Programmeur> afficherTousLesProgrammeurs() {
        List<Programmeur> liste = new ArrayList<>();

        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_PROGRAMMEURS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Programmeur p = mapProgrammeur(rs);
                liste.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    @Override
    public Programmeur afficherProgrammeurParId(int id) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PROGRAMMEUR_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapProgrammeur(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean ajouterProgrammeur(Programmeur p) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_PROGRAMMEUR)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setInt(3, p.getAnNaissance());
            ps.setDouble(4, p.getSalaire());
            ps.setDouble(5, p.getPrime());
            ps.setInt(6, p.getIdProjet());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimerProgrammeur(int id) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_PROGRAMMEUR)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean modifierSalaire(int id, double nouveauSalaire) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SALAIRE)) {

            ps.setDouble(1, nouveauSalaire);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Programmeur> afficherProgrammeursParProjet(int idProjet) {
        List<Programmeur> liste = new ArrayList<>();

        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PROGRAMMEURS_BY_PROJET)) {

            ps.setInt(1, idProjet);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                liste.add(mapProgrammeur(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /* =========================
       PROJETS
       ========================= */

    @Override
    public List<Projet> afficherProjets() {
        List<Projet> projets = new ArrayList<>();

        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_PROJETS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Projet p = new Projet();
                p.setIdProjet(rs.getInt("id_projet"));
                p.setNomProjet(rs.getString("nom_projet"));
                p.setStatut(rs.getString("statut"));
                projets.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }

    /* =========================
       MAPPING
       ========================= */

    private Programmeur mapProgrammeur(ResultSet rs) throws SQLException {
        Programmeur p = new Programmeur();
        p.setIdProgrammeur(rs.getInt("id_programmeur"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setAnNaissance(rs.getInt("an_naissance"));
        p.setSalaire(rs.getDouble("salaire"));
        p.setPrime(rs.getDouble("prime"));
        p.setIdProjet(rs.getInt("id_projet"));
        return p;
    }
}
