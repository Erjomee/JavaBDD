package backend.Entity;

/**
 * Représente un programmeur dans le système.
 * Un programmeur possède des informations personnelles (nom, prénom, année de naissance),
 * des informations salariales (salaire, prime) et peut être assigné à un projet.
 *
 * @author Ronan
 * @version 1.0
 */
public class Programmeur {

    /** Identifiant unique du programmeur */
    private int idProgrammeur;

    /** Nom de famille du programmeur */
    private String nom;

    /** Prénom du programmeur */
    private String prenom;

    /** Année de naissance du programmeur */
    private int anNaissance;

    /** Salaire mensuel ou annuel du programmeur */
    private double salaire;

    /** Prime accordée au programmeur */
    private double prime;

    /** Identifiant du projet auquel le programmeur est assigné (0 si aucun projet) */
    private int idProjet;

    /**
     * Constructeur par défaut.
     * Nécessaire pour la création d'instances vides qui seront remplies ultérieurement.
     */
    public Programmeur() {
    }

    /**
     * Constructeur complet pour créer un programmeur avec toutes ses informations.
     *
     * @param idProgrammeur L'identifiant unique du programmeur
     * @param nom Le nom de famille du programmeur
     * @param prenom Le prénom du programmeur
     * @param anNaissance L'année de naissance du programmeur
     * @param salaire Le salaire du programmeur
     * @param prime La prime du programmeur
     * @param idProjet L'identifiant du projet assigné
     */
    public Programmeur(int idProgrammeur, String nom, String prenom,
                       int anNaissance, double salaire, double prime, int idProjet) {
        this.idProgrammeur = idProgrammeur;
        this.nom = nom;
        this.prenom = prenom;
        this.anNaissance = anNaissance;
        this.salaire = salaire;
        this.prime = prime;
        this.idProjet = idProjet;
    }

    /**
     * Obtient l'identifiant du programmeur.
     *
     * @return L'identifiant du programmeur
     */
    public int getIdProgrammeur() {
        return idProgrammeur;
    }

    /**
     * Définit l'identifiant du programmeur.
     *
     * @param idProgrammeur Le nouvel identifiant du programmeur
     */
    public void setIdProgrammeur(int idProgrammeur) {
        this.idProgrammeur = idProgrammeur;
    }

    /**
     * Obtient le nom de famille du programmeur.
     *
     * @return Le nom de famille du programmeur
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom de famille du programmeur.
     *
     * @param nom Le nouveau nom de famille
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Obtient le prénom du programmeur.
     *
     * @return Le prénom du programmeur
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Définit le prénom du programmeur.
     *
     * @param prenom Le nouveau prénom
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Obtient l'année de naissance du programmeur.
     *
     * @return L'année de naissance
     */
    public int getAnNaissance() {
        return anNaissance;
    }

    /**
     * Définit l'année de naissance du programmeur.
     *
     * @param anNaissance La nouvelle année de naissance
     */
    public void setAnNaissance(int anNaissance) {
        this.anNaissance = anNaissance;
    }

    /**
     * Obtient le salaire du programmeur.
     *
     * @return Le salaire du programmeur
     */
    public double getSalaire() {
        return salaire;
    }

    /**
     * Définit le salaire du programmeur.
     *
     * @param salaire Le nouveau salaire
     */
    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    /**
     * Obtient la prime du programmeur.
     *
     * @return La prime du programmeur
     */
    public double getPrime() {
        return prime;
    }

    /**
     * Définit la prime du programmeur.
     *
     * @param prime La nouvelle prime
     */
    public void setPrime(double prime) {
        this.prime = prime;
    }

    /**
     * Obtient l'identifiant du projet auquel le programmeur est assigné.
     *
     * @return L'identifiant du projet (0 si aucun projet assigné)
     */
    public int getIdProjet() {
        return idProjet;
    }

    /**
     * Définit l'identifiant du projet auquel le programmeur est assigné.
     *
     * @param idProjet Le nouvel identifiant de projet (0 pour retirer du projet)
     */
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    /**
     * Retourne une représentation textuelle du programmeur.
     *
     * @return Une chaîne contenant toutes les informations du programmeur
     */
    @Override
    public String toString() {
        return "backend.Entity.Programmeur{" +
                "id=" + idProgrammeur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", anNaissance=" + anNaissance +
                ", salaire=" + salaire +
                ", prime=" + prime +
                ", idProjet=" + idProjet +
                '}';
    }
}