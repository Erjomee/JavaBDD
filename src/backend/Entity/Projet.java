package backend.Entity;

import java.sql.Date;

/**
 * Représente un projet dans le système.
 * Un projet possède un nom, des dates de début et de fin, ainsi qu'un statut.
 *
 * @author Ronan
 * @version 1.0
 */
public class Projet {

    /** Identifiant unique du projet */
    private int idProjet;

    /** Nom du projet */
    private String nom_projet;

    /** Date de début du projet */
    private Date dateDebut;

    /** Date de fin du projet */
    private Date dateFin;

    /** Statut actuel du projet (ex: "En cours", "Terminé", "En attente") */
    private String statut;

    /**
     * Constructeur par défaut.
     * Nécessaire pour la création d'instances vides qui seront remplies ultérieurement.
     */
    public Projet() {
    }

    /**
     * Constructeur complet pour créer un projet avec toutes ses informations.
     *
     * @param idProjet L'identifiant unique du projet
     * @param nom_projet Le nom du projet
     * @param dateDebut La date de début du projet
     * @param dateFin La date de fin du projet
     * @param statut Le statut du projet
     */
    public Projet(int idProjet, String nom_projet, Date dateDebut, Date dateFin, String statut) {
        this.idProjet = idProjet;
        this.nom_projet = nom_projet;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
    }

    /**
     * Obtient l'identifiant du projet.
     *
     * @return L'identifiant du projet
     */
    public int getIdProjet() {
        return idProjet;
    }

    /**
     * Définit l'identifiant du projet.
     *
     * @param idProjet Le nouvel identifiant du projet
     */
    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    /**
     * Obtient le nom du projet.
     *
     * @return Le nom du projet
     */
    public String getNomProjet() {
        return nom_projet;
    }

    /**
     * Définit le nom du projet.
     *
     * @param nom_projet Le nouveau nom du projet
     */
    public void setNomProjet(String nom_projet) {
        this.nom_projet = nom_projet;
    }

    /**
     * Obtient la date de début du projet.
     *
     * @return La date de début du projet
     */
    public Date getDateDebut() {
        return dateDebut;
    }

    /**
     * Définit la date de début du projet.
     *
     * @param dateDebut La nouvelle date de début
     */
    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    /**
     * Obtient la date de fin du projet.
     *
     * @return La date de fin du projet
     */
    public Date getDateFin() {
        return dateFin;
    }

    /**
     * Définit la date de fin du projet.
     *
     * @param dateFin La nouvelle date de fin
     */
    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * Obtient le statut du projet.
     *
     * @return Le statut du projet
     */
    public String getStatut() {
        return statut;
    }

    /**
     * Définit le statut du projet.
     *
     * @param statut Le nouveau statut du projet
     */
    public void setStatut(String statut) {
        this.statut = statut;
    }

    /**
     * Retourne une représentation textuelle du projet.
     *
     * @return Une chaîne contenant toutes les informations du projet
     */
    @Override
    public String toString() {
        return "backend.Entity.Projet{" +
                "id=" + idProjet +
                ", nom='" + nom_projet + '\'' +
                ", debut=" + dateDebut +
                ", fin=" + dateFin +
                ", statut='" + statut + '\'' +
                '}';
    }
}