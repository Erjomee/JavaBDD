package backend.Repository;

import backend.Entity.Programmeur;
import backend.Entity.Projet;

import java.util.List;

/**
 * Interface définissant les opérations de base de données pour la gestion des programmeurs et des projets.
 * Cette interface suit le pattern Repository pour abstraire l'accès aux données.
 *
 * @author Ronan
 * @version 1.0
 */
public interface ActionsBDD {

    // ===== Programmeurs =====

    /**
     * Récupère tous les programmeurs de la base de données.
     *
     * @return Une liste contenant tous les programmeurs
     */
    List<Programmeur> afficherTousLesProgrammeurs();

    /**
     * Récupère un programmeur spécifique par son identifiant.
     *
     * @param id L'identifiant du programmeur à rechercher
     * @return Le programmeur correspondant, ou null s'il n'existe pas
     */
    Programmeur afficherProgrammeurParId(int id);

    /**
     * Ajoute un nouveau programmeur dans la base de données.
     *
     * @param p Le programmeur à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouterProgrammeur(Programmeur p);

    /**
     * Supprime un programmeur de la base de données.
     *
     * @param id L'identifiant du programmeur à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimerProgrammeur(int id);

    /**
     * Modifie le salaire d'un programmeur.
     *
     * @param id L'identifiant du programmeur
     * @param nouveauSalaire Le nouveau montant du salaire
     * @return true si la modification a réussi, false sinon
     */
    boolean modifierSalaire(int id, double nouveauSalaire);

    /**
     * Modifie la prime d'un programmeur.
     *
     * @param id L'identifiant du programmeur
     * @param nouvellePrime Le nouveau montant de la prime
     * @return true si la modification a réussi, false sinon
     */
    boolean modifierPrime(int id, double nouvellePrime);

    /**
     * Modifie le projet assigné à un programmeur.
     *
     * @param id L'identifiant du programmeur
     * @param idProjet L'identifiant du nouveau projet (0 pour retirer du projet)
     * @return true si la modification a réussi, false sinon
     */
    boolean modifierProjet(int id, int idProjet);

    /**
     * Récupère tous les programmeurs assignés à un projet spécifique.
     *
     * @param idProjet L'identifiant du projet
     * @return Une liste contenant tous les programmeurs du projet
     */
    List<Programmeur> afficherProgrammeursParProjet(int idProjet);

    // ===== Projets =====

    /**
     * Récupère tous les projets de la base de données.
     *
     * @return Une liste contenant tous les projets
     */
    List<Projet> afficherProjets();

    /**
     * Ajoute un nouveau projet dans la base de données.
     *
     * @param p Le projet à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouterProjet(Projet p);

    /**
     * Supprime un projet de la base de données.
     * Cette opération détache également tous les programmeurs assignés au projet.
     *
     * @param idProjet L'identifiant du projet à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimerProjet(int idProjet);
}