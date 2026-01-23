package backend;

/**
 * Classe principale de l'application.
 * Point d'entrée pour démarrer l'interface en ligne de commande.
 *
 * Cette classe initialise le menu principal et démarre la boucle d'interaction
 * avec l'utilisateur pour gérer les programmeurs et les projets.
 *
 * @author Ronan
 * @version 1.0
 */
public class Start {

    /**
     * Méthode principale (point d'entrée) de l'application.
     *
     * Crée une instance du menu et lance la gestion des choix utilisateur.
     * L'application continue de fonctionner jusqu'à ce que l'utilisateur
     * choisisse de quitter via l'option du menu.
     *
     * @param args Arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.gererChoix();
    }
}