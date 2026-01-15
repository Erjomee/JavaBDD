package backend.Entity;

public class Programmeur {

    private int idProgrammeur;
    private String nom;
    private String prenom;
    private int anNaissance;
    private double salaire;
    private double prime;
    private int idProjet;

    // Constructeur vide (OBLIGATOIRE pour ton usage)
    public Programmeur() {
    }

    // Constructeur complet (optionnel mais propre)
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

    // Getters & Setters
    public int getIdProgrammeur() {
        return idProgrammeur;
    }

    public void setIdProgrammeur(int idProgrammeur) {
        this.idProgrammeur = idProgrammeur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getAnNaissance() {
        return anNaissance;
    }

    public void setAnNaissance(int anNaissance) {
        this.anNaissance = anNaissance;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public double getPrime() {
        return prime;
    }

    public void setPrime(double prime) {
        this.prime = prime;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

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
