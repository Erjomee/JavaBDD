package backend.Repository;

import backend.Entity.Programmeur;
import backend.Entity.Projet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface ActionsBDD.
 * Cette classe gère toutes les opérations CRUD (Create, Read, Update, Delete)
 * pour les entités Programmeur et Projet en utilisant JDBC pour communiquer avec PostgreSQL.
 *
 * @author Jerome TRAN
 * @version 1.0
 */
public class ActionsBDDImpl implements ActionsBDD {

    /** Requête SQL pour sélectionner tous les programmeurs */
    private static final String SELECT_ALL_PROGRAMMEURS =
            "SELECT * FROM programmeur";

    /** Requête SQL pour sélectionner un programmeur par son ID */
    private static final String SELECT_PROGRAMMEUR_BY_ID =
            "SELECT * FROM programmeur WHERE id_programmeur = ?";

    /** Requête SQL pour supprimer un programmeur */
    private static final String DELETE_PROGRAMMEUR =
            "DELETE FROM programmeur WHERE id_programmeur = ?";

    /** Requête SQL pour insérer un nouveau programmeur */
    private static final String INSERT_PROGRAMMEUR =
            "INSERT INTO programmeur (nom, prenom, an_naissance, salaire, prime, id_projet) VALUES (?, ?, ?, ?, ?, ?)";

    /** Requête SQL pour mettre à jour le salaire d'un programmeur */
    private static final String UPDATE_SALAIRE =
            "UPDATE programmeur SET salaire = ? WHERE id_programmeur = ?";

    /** Requête SQL pour mettre à jour la prime d'un programmeur */
    private static final String UPDATE_PRIME =
            "UPDATE programmeur SET prime = ? WHERE id_programmeur = ?";

    /** Requête SQL pour mettre à jour le projet d'un programmeur */
    private static final String UPDATE_PROJET =
            "UPDATE programmeur SET id_projet = ? WHERE id_programmeur = ?";

    /** Requête SQL pour sélectionner tous les projets */
    private static final String SELECT_ALL_PROJETS =
            "SELECT * FROM projet";

    /** Requête SQL pour insérer un nouveau projet */
    private static final String INSERT_PROJET =
            "INSERT INTO projet (nom_projet, date_debut, date_fin, statut) VALUES (?, ?, ?, ?)";

    /** Requête SQL pour détacher tous les programmeurs d'un projet */
    private static final String DETACH_PROGRAMMEURS_FROM_PROJET =
            "UPDATE programmeur SET id_projet = NULL WHERE id_projet = ?";

    /** Requête SQL pour supprimer un projet */
    private static final String DELETE_PROJET =
            "DELETE FROM projet WHERE id_projet = ?";

    /** Requête SQL pour sélectionner les programmeurs d'un projet spécifique */
    private static final String SELECT_PROGRAMMEURS_BY_PROJET =
            "SELECT * FROM programmeur WHERE id_projet = ?";

    // ================= PROGRAMMEURS =================

    /**
     * Récupère tous les programmeurs de la base de données.
     *
     * @return Une liste contenant tous les programmeurs, ou une liste vide en cas d'erreur
     */
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

    /**
     * Récupère un programmeur spécifique par son identifiant.
     *
     * @param id L'identifiant du programmeur à rechercher
     * @return Le programmeur correspondant, ou null s'il n'existe pas ou en cas d'erreur
     */
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

    /**
     * Ajoute un nouveau programmeur dans la base de données.
     * Si l'ID du projet est 0, le programmeur n'est assigné à aucun projet (NULL en base).
     *
     * @param p Le programmeur à ajouter
     * @return true si l'ajout a réussi, false en cas d'erreur
     */
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

    /**
     * Supprime un programmeur de la base de données.
     *
     * @param id L'identifiant du programmeur à supprimer
     * @return true si la suppression a réussi, false en cas d'erreur
     */
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

    /**
     * Modifie le salaire d'un programmeur.
     *
     * @param id L'identifiant du programmeur
     * @param salaire Le nouveau montant du salaire
     * @return true si la modification a réussi, false en cas d'erreur
     */
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

    /**
     * Modifie la prime d'un programmeur.
     *
     * @param id L'identifiant du programmeur
     * @param prime Le nouveau montant de la prime
     * @return true si la modification a réussi, false en cas d'erreur
     */
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

    /**
     * Modifie le projet assigné à un programmeur.
     * Si idProjet est 0, le programmeur est retiré de tout projet (NULL en base).
     *
     * @param id L'identifiant du programmeur
     * @param idProjet L'identifiant du nouveau projet (0 pour retirer du projet)
     * @return true si la modification a réussi, false en cas d'erreur
     */
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

    /**
     * Récupère tous les programmeurs assignés à un projet spécifique.
     *
     * @param idProjet L'identifiant du projet
     * @return Une liste contenant tous les programmeurs du projet, ou une liste vide en cas d'erreur
     */
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

    /**
     * Récupère tous les projets de la base de données.
     *
     * @return Une liste contenant tous les projets, ou une liste vide en cas d'erreur
     */
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

    /**
     * Ajoute un nouveau projet dans la base de données.
     *
     * @param p Le projet à ajouter
     * @return true si l'ajout a réussi, false en cas d'erreur
     */
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

    /**
     * Supprime un projet de la base de données de manière propre.
     * Cette méthode effectue deux opérations dans une transaction :
     * <ol>
     *   <li>Détache tous les programmeurs assignés au projet (met leur id_projet à NULL)</li>
     *   <li>Supprime le projet</li>
     * </ol>
     * Si une erreur survient, la transaction est annulée automatiquement.
     *
     * @param idProjet L'identifiant du projet à supprimer
     * @return true si la suppression a réussi, false en cas d'erreur
     */
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

    /**
     * Mappe une ligne de ResultSet vers un objet Programmeur.
     * Cette méthode utilitaire facilite la conversion des résultats SQL en objets Java.
     * Gère correctement les valeurs NULL pour l'ID du projet.
     *
     * @param rs Le ResultSet contenant les données du programmeur
     * @return Un objet Programmeur construit à partir des données du ResultSet
     * @throws SQLException Si une erreur survient lors de la lecture des données
     */
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