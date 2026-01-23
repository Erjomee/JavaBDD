package backend;

import backend.Entity.Programmeur;
import backend.Repository.ActionsBDD;
import backend.Repository.ActionsBDDImpl;

import java.util.Scanner;

/**
 * Classe gérant l'interface en ligne de commande (CLI) pour l'application.
 * Permet à l'utilisateur d'interagir avec le système via un menu textuel
 * pour effectuer des opérations CRUD sur les programmeurs et les projets.
 *
 * @author Ronan
 * @version 1.0
 */
public class Menu {

    /** Instance d'accès aux opérations de base de données */
    private ActionsBDD actions = new ActionsBDDImpl();

    /** Scanner pour lire les entrées utilisateur depuis la console */
    private Scanner sc = new Scanner(System.in);

    /**
     * Gère la boucle principale du menu.
     * Affiche le menu, lit le choix de l'utilisateur et exécute l'action correspondante.
     * La boucle continue jusqu'à ce que l'utilisateur choisisse de quitter (option 8).
     */
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

    /**
     * Affiche le menu principal avec toutes les options disponibles.
     * Les options incluent l'affichage, l'ajout, la modification et la suppression
     * de programmeurs et projets.
     */
    private void afficherMenu() {
        System.out.println("\n********* MENU *************");
        System.out.println("1. Afficher tous les programmeurs");
        System.out.println("2. Afficher un programmeur");
        System.out.println("3. Supprimer un programmeur");
        System.out.println("4. Ajouter un programmeur");
        System.out.println("5. Modifier le salaire");
        System.out.println("6. Afficher la liste des projets");
        System.out.println("7. Programmeurs d'un même projet");
        System.out.println("8. Quitter le programme");
        System.out.print("Votre choix : ");
        System.out.flush();
    }

    /**
     * Affiche les détails d'un programmeur spécifique.
     * Demande à l'utilisateur de saisir l'ID du programmeur à afficher.
     */
    private void afficherProgrammeur() {
        System.out.print("Entrez l'ID du programmeur : ");
        System.out.flush();
        int id = lireEntier();
        actions.afficherProgrammeurParId(id);
    }

    /**
     * Supprime un programmeur de la base de données.
     * Demande l'ID du programmeur et affiche un message de confirmation ou d'erreur.
     */
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

    /**
     * Ajoute un nouveau programmeur dans la base de données.
     * Demande à l'utilisateur de saisir toutes les informations nécessaires :
     * nom, prénom, année de naissance, salaire, prime et ID du projet.
     */
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

    /**
     * Modifie le salaire d'un programmeur existant.
     *
     * Cette méthode implémente un système de tentatives limitées :
     * l'utilisateur a 3 tentatives pour saisir un ID valide.
     * Si l'ID est correct, demande le nouveau salaire et effectue la modification.
     * Après 3 échecs, retourne au menu principal.
     */
    private void modifierSalaire() {
        int tentatives = 0;
        boolean succes = false;

        while (tentatives < 3 && !succes) {
            System.out.print("ID du programmeur : ");
            System.out.flush();
            int id = lireEntier();

            // Vérifier si le programmeur existe avant de demander le salaire
            Programmeur programmeur = actions.afficherProgrammeurParId(id);

            if (programmeur == null) {
                tentatives++;
                System.out.println("ID incorrect. Programmeur non trouvé. Tentatives restantes : " + (3 - tentatives));
                continue;
            }

            // Si le programmeur existe, demander le nouveau salaire
            System.out.print("Nouveau salaire : ");
            System.out.flush();
            double salaire = lireDouble();

            succes = actions.modifierSalaire(id, salaire);

            if (!succes) {
                System.out.println("Erreur lors de la modification du salaire.");
            }
        }

        if (succes) {
            System.out.println("Salaire modifié avec succès.");
        } else {
            System.out.println("Retour au menu principal.");
        }
    }

    /**
     * Affiche tous les programmeurs assignés à un projet spécifique.
     * Demande à l'utilisateur de saisir l'ID du projet.
     */
    private void afficherProgrammeursParProjet() {
        System.out.print("Entrez l'ID du projet : ");
        System.out.flush();
        int idProjet = lireEntier();
        actions.afficherProgrammeursParProjet(idProjet);
    }

    /**
     * Lit un nombre entier depuis l'entrée standard avec validation.
     *
     * Si l'utilisateur saisit une valeur non entière, demande une nouvelle saisie
     * jusqu'à obtenir un entier valide.
     *
     * @return L'entier saisi par l'utilisateur
     */
    private int lireEntier() {
        while (!sc.hasNextInt()) {
            System.out.print("Veuillez saisir un nombre entier : ");
            System.out.flush();
            sc.next();
        }
        return sc.nextInt();
    }

    /**
     * Lit un nombre décimal (double) depuis l'entrée standard avec validation.
     *
     * Si l'utilisateur saisit une valeur non numérique, demande une nouvelle saisie
     * jusqu'à obtenir un nombre valide.
     *
     * @return Le nombre décimal saisi par l'utilisateur
     */
    private double lireDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Veuillez saisir un nombre réel : ");
            System.out.flush();
            sc.next();
        }
        return sc.nextDouble();
    }
}