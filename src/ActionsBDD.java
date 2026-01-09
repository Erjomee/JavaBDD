public interface ActionsBDD {

    void afficherTousLesProgrammeurs();

    void afficherProgrammeurParId(int id);

    boolean supprimerProgrammeur(int id);

    void ajouterProgrammeur(Programmeur p);

    boolean modifierSalaire(int id, double nouveauSalaire);

    void afficherProjets();

    void afficherProgrammeursParProjet(int idProjet);
}
