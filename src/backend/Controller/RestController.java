package backend.Controller;

import backend.Entity.Programmeur;
import backend.Entity.Projet;
import backend.Repository.ActionsBDD;
import backend.Repository.ActionsBDDImpl;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des programmeurs et des projets.
 * Expose une API HTTP sur le port 8080 avec les endpoints suivants :
 * <ul>
 *   <li>/api/programmeurs - Gestion de tous les programmeurs (GET, POST)</li>
 *   <li>/api/programmeurs/{id} - Gestion d'un programmeur spécifique (GET, PUT, DELETE)</li>
 *   <li>/api/programmeurs/{id}/prime - Modification de la prime (PUT)</li>
 *   <li>/api/programmeurs/{id}/projet - Modification du projet (PUT)</li>
 *   <li>/api/projets - Gestion des projets (GET, POST, DELETE)</li>
 * </ul>
 *
 * @author Benoit VONG A LAU
 * @version 1.0
 */
public class RestController {

    /** Instance d'accès aux données pour les opérations de base de données */
    private static final ActionsBDD actions = new ActionsBDDImpl();

    /**
     * Point d'entrée principal du serveur HTTP.
     * Démarre le serveur sur le port 8080 et configure les différents endpoints.
     *
     * @param args Arguments de la ligne de commande (non utilisés)
     * @throws IOException Si une erreur survient lors de la création du serveur
     */
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/programmeurs", new ProgrammeursHandler());
        server.createContext("/api/programmeurs/", new ProgrammeurByIdHandler());
        server.createContext("/api/projets", new ProjetsHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Serveur démarré sur le port 8080");
    }

    /**
     * Handler HTTP pour la gestion de la collection complète des programmeurs.
     * Supporte les opérations GET (liste tous les programmeurs) et POST (ajoute un programmeur).
     */
    static class ProgrammeursHandler implements HttpHandler {
        /**
         * Traite les requêtes HTTP pour l'endpoint /api/programmeurs.
         *
         * @param exchange L'objet HttpExchange contenant la requête et la réponse
         * @throws IOException Si une erreur d'I/O survient
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            String method = exchange.getRequestMethod();

            if (method.equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (method.equals("GET")) {
                List<Programmeur> programmeurs = actions.afficherTousLesProgrammeurs();
                String response = programmeursToJson(programmeurs);
                sendResponse(exchange, 200, response);

            } else if (method.equals("POST")) {
                String body = readRequestBody(exchange);
                Programmeur programmeur = jsonToProgrammeur(body);
                boolean success = actions.ajouterProgrammeur(programmeur);

                if (success) {
                    sendResponse(exchange, 201, "{\"message\":\"Programmeur ajouté\"}");
                } else {
                    sendResponse(exchange, 500, "{\"error\":\"Erreur lors de l'ajout\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
            }
        }
    }

    /**
     * Handler HTTP pour la gestion individuelle des programmeurs.
     * Supporte les opérations GET, PUT et DELETE sur un programmeur spécifique,
     * ainsi que les sous-routes /prime et /projet.
     */
    static class ProgrammeurByIdHandler implements HttpHandler {
        /**
         * Traite les requêtes HTTP pour l'endpoint /api/programmeurs/{id} et ses sous-routes.
         * Routes supportées :
         * <ul>
         *   <li>GET /api/programmeurs/{id} - Récupère un programmeur</li>
         *   <li>DELETE /api/programmeurs/{id} - Supprime un programmeur</li>
         *   <li>PUT /api/programmeurs/{id} - Modifie le salaire</li>
         *   <li>PUT /api/programmeurs/{id}/prime - Modifie la prime</li>
         *   <li>PUT /api/programmeurs/{id}/projet - Modifie le projet assigné</li>
         * </ul>
         *
         * @param exchange L'objet HttpExchange contenant la requête et la réponse
         * @throws IOException Si une erreur d'I/O survient
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            String method = exchange.getRequestMethod();

            if (method.equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts.length < 4) {
                sendResponse(exchange, 400, "{\"error\":\"ID manquant\"}");
                return;
            }

            try {
                int id = Integer.parseInt(parts[3]);
                String subAction = (parts.length >= 5) ? parts[4] : null;

                if (method.equals("GET") && subAction == null) {
                    Programmeur programmeur = actions.afficherProgrammeurParId(id);
                    if (programmeur != null) {
                        String response = programmeurToJson(programmeur);
                        sendResponse(exchange, 200, response);
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }
                    return;
                }

                if (method.equals("DELETE") && subAction == null) {
                    boolean success = actions.supprimerProgrammeur(id);
                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Programmeur supprimé\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }
                    return;
                }

                if (method.equals("PUT") && subAction == null) {
                    String body = readRequestBody(exchange);
                    double salaire = extractSalaireFromJson(body);
                    boolean success = actions.modifierSalaire(id, salaire);

                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Salaire modifié\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }
                    return;
                }

                if (method.equals("PUT") && "prime".equals(subAction)) {
                    String body = readRequestBody(exchange);
                    double prime = extractDoubleValue(body, "prime");
                    boolean success = actions.modifierPrime(id, prime);

                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Prime modifiée\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }
                    return;
                }

                if (method.equals("PUT") && "projet".equals(subAction)) {
                    String body = readRequestBody(exchange);
                    int idProjet = extractIntValueAllowZero(body, "idProjet");
                    boolean success = actions.modifierProjet(id, idProjet);

                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Projet modifié\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }
                    return;
                }

                sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");

            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\":\"ID invalide\"}");
            }
        }
    }

    /**
     * Handler HTTP pour la gestion de la collection des projets.
     * Supporte les opérations GET (liste tous les projets), POST (ajoute un projet)
     * et DELETE (supprime un projet).
     */
    static class ProjetsHandler implements HttpHandler {
        /**
         * Traite les requêtes HTTP pour l'endpoint /api/projets.
         *
         * @param exchange L'objet HttpExchange contenant la requête et la réponse
         * @throws IOException Si une erreur d'I/O survient
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);

            String method = exchange.getRequestMethod();

            if (method.equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (method.equals("GET")) {
                List<Projet> projets = actions.afficherProjets();
                String response = projetsToJson(projets);
                sendResponse(exchange, 200, response);
                return;
            }
            if (method.equals("DELETE")) {
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");

                if (parts.length == 4) {
                    int idProjet = Integer.parseInt(parts[3]);
                    boolean success = actions.supprimerProjet(idProjet);

                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Projet supprimé\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Projet non trouvé\"}");
                    }
                    return;
                }
            }
            if (method.equals("POST")) {
                String body = readRequestBody(exchange);
                Projet p = jsonToProjet(body);
                boolean success = actions.ajouterProjet(p);

                if (success) {
                    sendResponse(exchange, 201, "{\"message\":\"Projet ajouté\"}");
                } else {
                    sendResponse(exchange, 500, "{\"error\":\"Erreur lors de l'ajout projet\"}");
                }
                return;
            }

            sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
        }
    }

    // ============= CONVERSION JSON MANUELLE =============

    /**
     * Convertit une liste de programmeurs en chaîne JSON.
     *
     * @param programmeurs La liste des programmeurs à convertir
     * @return Une chaîne JSON représentant la liste des programmeurs
     */
    private static String programmeursToJson(List<Programmeur> programmeurs) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < programmeurs.size(); i++) {
            if (i > 0) json.append(",");
            json.append(programmeurToJson(programmeurs.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Convertit un objet Programmeur en chaîne JSON.
     *
     * @param p Le programmeur à convertir
     * @return Une chaîne JSON représentant le programmeur
     */
    private static String programmeurToJson(Programmeur p) {
        return String.format(
                "{\"idProgrammeur\":%d,\"nom\":\"%s\",\"prenom\":\"%s\",\"anNaissance\":%d,\"salaire\":%.2f,\"prime\":%.2f,\"idProjet\":%d}",
                p.getIdProgrammeur(),
                escapeJson(p.getNom()),
                escapeJson(p.getPrenom()),
                p.getAnNaissance(),
                p.getSalaire(),
                p.getPrime(),
                p.getIdProjet()
        );
    }

    /**
     * Convertit une liste de projets en chaîne JSON.
     *
     * @param projets La liste des projets à convertir
     * @return Une chaîne JSON représentant la liste des projets
     */
    private static String projetsToJson(List<Projet> projets) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < projets.size(); i++) {
            if (i > 0) json.append(",");
            json.append(projetToJson(projets.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Convertit un objet Projet en chaîne JSON.
     *
     * @param p Le projet à convertir
     * @return Une chaîne JSON représentant le projet
     */
    private static String projetToJson(Projet p) {
        String dd = (p.getDateDebut() == null) ? "" : p.getDateDebut().toString();
        String df = (p.getDateFin() == null) ? "" : p.getDateFin().toString();

        return String.format(
                "{\"idProjet\":%d,\"nom_projet\":\"%s\",\"dateDebut\":\"%s\",\"dateFin\":\"%s\",\"statut\":\"%s\"}",
                p.getIdProjet(),
                escapeJson(p.getNomProjet()),
                dd,
                df,
                escapeJson(p.getStatut())
        );
    }

    /**
     * Échappe les caractères spéciaux dans une chaîne pour la sérialisation JSON.
     *
     * @param str La chaîne à échapper
     * @return La chaîne échappée, ou une chaîne vide si str est null
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Convertit une chaîne JSON en objet Programmeur.
     *
     * @param json La chaîne JSON à parser
     * @return Un objet Programmeur créé à partir du JSON
     */
    private static Programmeur jsonToProgrammeur(String json) {
        Programmeur p = new Programmeur();

        p.setNom(extractStringValue(json, "nom"));
        p.setPrenom(extractStringValue(json, "prenom"));
        p.setAnNaissance(extractIntValue(json, "anNaissance"));
        p.setSalaire(extractDoubleValue(json, "salaire"));
        p.setPrime(extractDoubleValue(json, "prime"));
        p.setIdProjet(extractIntValueAllowZero(json, "idProjet"));

        return p;
    }

    /**
     * Convertit une chaîne JSON en objet Projet.
     *
     * @param json La chaîne JSON à parser
     * @return Un objet Projet créé à partir du JSON
     */
    private static Projet jsonToProjet(String json) {
        Projet p = new Projet();

        p.setNomProjet(extractStringValue(json, "nom_projet"));
        p.setStatut(extractStringValue(json, "statut"));

        String dd = extractStringValue(json, "dateDebut");
        String df = extractStringValue(json, "dateFin");

        if (dd != null && !dd.isEmpty()) p.setDateDebut(Date.valueOf(dd));
        if (df != null && !df.isEmpty()) p.setDateFin(Date.valueOf(df));

        return p;
    }

    /**
     * Extrait une valeur de type String d'une chaîne JSON.
     *
     * @param json La chaîne JSON source
     * @param key La clé dont on veut extraire la valeur
     * @return La valeur associée à la clé, ou une chaîne vide si non trouvée
     */
    private static String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    /**
     * Extrait une valeur de type int d'une chaîne JSON.
     *
     * @param json La chaîne JSON source
     * @param key La clé dont on veut extraire la valeur
     * @return La valeur associée à la clé, ou 0 si non trouvée
     */
    private static int extractIntValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    /**
     * Extrait une valeur de type int d'une chaîne JSON, en autorisant explicitement 0.
     * Identique à extractIntValue mais plus explicite pour les cas où 0 est une valeur valide
     * (par exemple pour retirer un programmeur d'un projet).
     *
     * @param json La chaîne JSON source
     * @param key La clé dont on veut extraire la valeur
     * @return La valeur associée à la clé, ou 0 si non trouvée
     */
    private static int extractIntValueAllowZero(String json, String key) {
        return extractIntValue(json, key);
    }

    /**
     * Extrait une valeur de type double d'une chaîne JSON.
     *
     * @param json La chaîne JSON source
     * @param key La clé dont on veut extraire la valeur
     * @return La valeur associée à la clé, ou 0.0 si non trouvée
     */
    private static double extractDoubleValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([\\d.]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return 0.0;
    }

    /**
     * Extrait la valeur du salaire d'une chaîne JSON.
     *
     * @param json La chaîne JSON source
     * @return Le salaire extrait, ou 0.0 si non trouvé
     */
    private static double extractSalaireFromJson(String json) {
        return extractDoubleValue(json, "salaire");
    }

    // ============= UTILITAIRES HTTP =============

    /**
     * Ajoute les en-têtes CORS nécessaires à la réponse HTTP.
     * Permet les requêtes cross-origin depuis n'importe quelle origine.
     *
     * @param exchange L'objet HttpExchange auquel ajouter les en-têtes
     */
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    /**
     * Lit le corps de la requête HTTP et le convertit en chaîne.
     *
     * @param exchange L'objet HttpExchange contenant la requête
     * @return Le corps de la requête sous forme de chaîne UTF-8
     * @throws IOException Si une erreur de lecture survient
     */
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Envoie une réponse HTTP avec le code de statut et le contenu spécifiés.
     *
     * @param exchange L'objet HttpExchange pour envoyer la réponse
     * @param statusCode Le code de statut HTTP
     * @param response Le contenu de la réponse au format JSON
     * @throws IOException Si une erreur d'écriture survient
     */
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}