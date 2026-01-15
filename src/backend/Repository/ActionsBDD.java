package backend.Repository;

import backend.Entity.Programmeur;
import backend.Entity.Projet;

import java.util.List;

public interface ActionsBDD {

    List<Programmeur> afficherTousLesProgrammeurs();

    Programmeur afficherProgrammeurParId(int id);

    boolean supprimerProgrammeur(int id);

    boolean ajouterProgrammeur(Programmeur p);

    boolean modifierSalaire(int id, double nouveauSalaire);

    List<Projet> afficherProjets();

    List<Programmeur> afficherProgrammeursParProjet(int idProjet);
}
