package backend.Repository;
import backend.Entity.Programmeur;
import backend.Entity.Projet;

import java.util.List;

public interface ActionsBDD {

    // ===== Programmeurs =====
    List<Programmeur> afficherTousLesProgrammeurs();
    Programmeur afficherProgrammeurParId(int id);
    boolean ajouterProgrammeur(Programmeur p);
    boolean supprimerProgrammeur(int id);

    boolean modifierSalaire(int id, double nouveauSalaire);
    boolean modifierPrime(int id, double nouvellePrime);
    boolean modifierProjet(int id, int idProjet); // idProjet = 0 => aucun projet

    List<Programmeur> afficherProgrammeursParProjet(int idProjet);

    // ===== Projets =====
    List<Projet> afficherProjets();
    boolean ajouterProjet(Projet p);
    boolean supprimerProjet(int idProjet);
}