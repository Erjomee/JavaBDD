import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RestController {

    private static final ActionsBDD actions = new ActionsBDDImpl();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Configuration CORS pour toutes les routes
        server.createContext("/api/programmeurs", new ProgrammeursHandler());
        server.createContext("/api/programmeurs/", new ProgrammeurByIdHandler());
        server.createContext("/api/projets", new ProjetsHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Serveur démarré sur le port 8080");
    }

    // Handler pour tous les programmeurs
    static class ProgrammeursHandler implements HttpHandler {
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

    // Handler pour un programmeur par ID
    static class ProgrammeurByIdHandler implements HttpHandler {
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

                if (method.equals("GET")) {
                    Programmeur programmeur = actions.afficherProgrammeurParId(id);
                    if (programmeur != null) {
                        String response = programmeurToJson(programmeur);
                        sendResponse(exchange, 200, response);
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }

                } else if (method.equals("DELETE")) {
                    boolean success = actions.supprimerProgrammeur(id);
                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Programmeur supprimé\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }

                } else if (method.equals("PUT")) {
                    String body = readRequestBody(exchange);
                    double salaire = extractSalaireFromJson(body);
                    boolean success = actions.modifierSalaire(id, salaire);

                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Salaire modifié\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }
                } else {
                    sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
                }

            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\":\"ID invalide\"}");
            }
        }
    }

    // Handler pour les projets
    static class ProjetsHandler implements HttpHandler {
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
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Méthode non autorisée\"}");
            }
        }
    }

    // ============= CONVERSION JSON MANUELLE =============

    // Convertir une liste de programmeurs en JSON
    private static String programmeursToJson(List<Programmeur> programmeurs) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < programmeurs.size(); i++) {
            if (i > 0) json.append(",");
            json.append(programmeurToJson(programmeurs.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    // Convertir un programmeur en JSON
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

    // Convertir une liste de projets en JSON
    private static String projetsToJson(List<Projet> projets) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < projets.size(); i++) {
            if (i > 0) json.append(",");
            json.append(projetToJson(projets.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    // Convertir un projet en JSON
    private static String projetToJson(Projet p) {
        return String.format(
                "{\"idProjet\":%d,\"nomProjet\":\"%s\",\"statut\":\"%s\"}",
                p.getIdProjet(),
                escapeJson(p.getNomProjet()),
                escapeJson(p.getStatut())
        );
    }

    // Échapper les caractères spéciaux dans les chaînes JSON
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Parser un JSON simple vers un objet Programmeur
    private static Programmeur jsonToProgrammeur(String json) {
        Programmeur p = new Programmeur();

        p.setNom(extractStringValue(json, "nom"));
        p.setPrenom(extractStringValue(json, "prenom"));
        p.setAnNaissance(extractIntValue(json, "anNaissance"));
        p.setSalaire(extractDoubleValue(json, "salaire"));
        p.setPrime(extractDoubleValue(json, "prime"));
        p.setIdProjet(extractIntValue(json, "idProjet"));

        return p;
    }

    // Extraire une valeur string d'un JSON
    private static String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    // Extraire une valeur int d'un JSON
    private static int extractIntValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    // Extraire une valeur double d'un JSON
    private static double extractDoubleValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([\\d.]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return 0.0;
    }

    // Extraire le salaire du JSON pour la mise à jour
    private static double extractSalaireFromJson(String json) {
        return extractDoubleValue(json, "salaire");
    }

    // ============= UTILITAIRES HTTP =============

    // Ajouter les en-têtes CORS
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    // Lire le corps de la requête
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    // Envoyer une réponse
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}