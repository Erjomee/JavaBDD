package backend.Repository;

import backend.Entity.Programmeur;
import backend.Entity.Projet;

import java.sql.*;
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
            "INSERT INTO programmeur (nom, prenom, an_naissance, salaire, prime, id_projet) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SALAIRE =
            "UPDATE programmeur SET salaire = ? WHERE id_programmeur = ?";

    private static final String UPDATE_PRIME =
            "UPDATE programmeur SET prime = ? WHERE id_programmeur = ?";

    private static final String UPDATE_PROJET =
            "UPDATE programmeur SET id_projet = ? WHERE id_programmeur = ?";

    private static final String SELECT_ALL_PROJETS =
            "SELECT * FROM projet";

    private static final String INSERT_PROJET =
            "INSERT INTO projet (nom_projet, date_debut, date_fin, statut) VALUES (?, ?, ?, ?)";


    private static final String DETACH_PROGRAMMEURS_FROM_PROJET =
            "UPDATE programmeur SET id_projet = NULL WHERE id_projet = ?";

    private static final String DELETE_PROJET =
            "DELETE FROM projet WHERE id_projet = ?";

    private static final String SELECT_PROGRAMMEURS_BY_PROJET =
            "SELECT * FROM programmeur WHERE id_projet = ?";

    // ================= PROGRAMMEURS =================

    @Override
    public List<Programmeur> afficherTousLesProgrammeurs() {
        List<Programmeur> list = new ArrayList<>();

        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_PROGRAMMEURS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapProgrammeur(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Programmeur afficherProgrammeurParId(int id) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PROGRAMMEUR_BY_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapProgrammeur(rs);

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

            if (p.getIdProjet() == 0) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, p.getIdProjet());
            }

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
    public boolean modifierSalaire(int id, double salaire) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SALAIRE)) {

            ps.setDouble(1, salaire);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean modifierPrime(int id, double prime) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PRIME)) {

            ps.setDouble(1, prime);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean modifierProjet(int id, int idProjet) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PROJET)) {

            if (idProjet == 0) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, idProjet);
            }
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Programmeur> afficherProgrammeursParProjet(int idProjet) {
        List<Programmeur> list = new ArrayList<>();

        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PROGRAMMEURS_BY_PROJET)) {

            ps.setInt(1, idProjet);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapProgrammeur(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= PROJETS =================

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
                p.setDateDebut(rs.getDate("date_debut"));
                p.setDateFin(rs.getDate("date_fin"));
                p.setStatut(rs.getString("statut"));
                projets.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }

    @Override
    public boolean ajouterProjet(Projet p) {
        try (Connection conn = ConnexionBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_PROJET)) {

            ps.setString(1, p.getNomProjet());
            ps.setDate(2, p.getDateDebut());
            ps.setDate(3, p.getDateFin());
            ps.setString(4, p.getStatut());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NOUVEAU : suppression propre
    @Override
    public boolean supprimerProjet(int idProjet) {
        try (Connection conn = ConnexionBDD.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(DETACH_PROGRAMMEURS_FROM_PROJET);
                 PreparedStatement ps2 = conn.prepareStatement(DELETE_PROJET)) {

                ps1.setInt(1, idProjet);
                ps1.executeUpdate();

                ps2.setInt(1, idProjet);
                int deleted = ps2.executeUpdate();

                conn.commit();
                return deleted > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= MAPPING =================

    private Programmeur mapProgrammeur(ResultSet rs) throws SQLException {
        Programmeur p = new Programmeur();
        p.setIdProgrammeur(rs.getInt("id_programmeur"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setAnNaissance(rs.getInt("an_naissance"));
        p.setSalaire(rs.getDouble("salaire"));
        p.setPrime(rs.getDouble("prime"));

        int idProjet = rs.getInt("id_projet");
        if (rs.wasNull()) idProjet = 0;
        p.setIdProjet(idProjet);

        return p;
    }
}