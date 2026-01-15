package backend;

import backend.Entity.Programmeur;
import backend.Repository.ActionsBDD;
import backend.Repository.ActionsBDDImpl;

import java.util.Scanner;

public class Menu {

    private ActionsBDD actions = new ActionsBDDImpl();
    private Scanner sc = new Scanner(System.in);

    public void gererChoix() {
        int choix;

        do {
            afficherMenu();
            choix = lireEntier();

            switch (choix) {
                case 1 -> actions.afficherTousLesProgrammeurs();
                case 2 -> afficherProgrammeur();
                case 3 -> supprimerProgrammeur();
                case 4 -> ajouterProgrammeur();
                case 5 -> modifierSalaire();
                case 6 -> actions.afficherProjets();
                case 7 -> afficherProgrammeursParProjet();
                case 8 -> System.out.println("Fermeture du programme.");
                default -> System.out.println("Choix invalide.");
            }
        } while (choix != 8);
    }

    private void afficherMenu() {
        System.out.println("\n********* MENU *************");
        System.out.println("1. Afficher tous les programmeurs");
        System.out.println("2. Afficher un programmeur");
        System.out.println("3. Supprimer un programmeur");
        System.out.println("4. Ajouter un programmeur");
        System.out.println("5. Modifier le salaire");
        System.out.println("6. Afficher la liste des projets");
        System.out.println("7. Programmeurs d’un même projet");
        System.out.println("8. Quitter le programme");
        System.out.print("Votre choix : ");
        System.out.flush();
    }

    private void afficherProgrammeur() {
        System.out.print("Entrez l'ID du programmeur : ");
        System.out.flush();
        int id = lireEntier();
        actions.afficherProgrammeurParId(id);
    }

    private void supprimerProgrammeur() {
        System.out.print("Entrez l'ID du programmeur à supprimer : ");
        System.out.flush();
        int id = lireEntier();

        boolean succes = actions.supprimerProgrammeur(id);

        if (succes) {
            System.out.println("backend.Entity.Programmeur supprimé avec succès.");
        } else {
            System.out.println("Aucun programmeur trouvé avec cet ID.");
        }
    }

    private void ajouterProgrammeur() {
        System.out.print("Nom : ");
        System.out.flush();
        String nom = sc.next();

        System.out.print("Prénom : ");
        System.out.flush();
        String prenom = sc.next();

        System.out.print("Année de naissance : ");
        System.out.flush();
        int anNaissance = lireEntier();

        System.out.print("Salaire : ");
        System.out.flush();
        double salaire = lireDouble();

        System.out.print("Prime : ");
        System.out.flush();
        double prime = lireDouble();

        System.out.print("ID du projet : ");
        System.out.flush();
        int idProjet = lireEntier();

        Programmeur p = new Programmeur();
        p.setNom(nom);
        p.setPrenom(prenom);
        p.setAnNaissance(anNaissance);
        p.setSalaire(salaire);
        p.setPrime(prime);
        p.setIdProjet(idProjet);

        actions.ajouterProgrammeur(p);
    }

    private void modifierSalaire() {
        int tentatives = 0;
        boolean succes = false;

        while (tentatives < 3 && !succes) {
            System.out.print("ID du programmeur : ");
            System.out.flush();
            int id = lireEntier();

            System.out.print("Nouveau salaire : ");
            System.out.flush();
            double salaire = lireDouble();

            succes = actions.modifierSalaire(id, salaire);

            if (!succes) {
                tentatives++;
                System.out.println("ID incorrect. Tentatives restantes : " + (3 - tentatives));
            }
        }

        if (succes) {
            System.out.println("Salaire modifié avec succès.");
        } else {
            System.out.println("Retour au menu principal.");
        }
    }

    private void afficherProgrammeursParProjet() {
        System.out.print("Entrez l'ID du projet : ");
        System.out.flush();
        int idProjet = lireEntier();
        actions.afficherProgrammeursParProjet(idProjet);
    }

    private int lireEntier() {
        while (!sc.hasNextInt()) {
            System.out.print("Veuillez saisir un nombre entier : ");
            System.out.flush();
            sc.next();
        }
        return sc.nextInt();
    }

    private double lireDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Veuillez saisir un nombre réel : ");
            System.out.flush();
            sc.next();
        }
        return sc.nextDouble();
    }
}
