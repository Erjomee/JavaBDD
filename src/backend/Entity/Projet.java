package backend.Entity;

import java.sql.Date;

public class Projet {

    private int idProjet;
    private String nom_projet;
    private Date dateDebut;
    private Date dateFin;
    private String statut;

    // Constructeur vide
    public Projet() {
    }

    // Constructeur complet
    public Projet(int idProjet, String nom_projet, Date dateDebut, Date dateFin, String statut) {
        this.idProjet = idProjet;
        this.nom_projet = nom_projet;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
    }

    // Getters et Setters
    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public String getNomProjet() {
        return nom_projet;
    }

    public void setNomProjet(String nom_projet) {
        this.nom_projet = nom_projet;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

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