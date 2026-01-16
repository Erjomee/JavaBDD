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

public class RestController {

    private static final ActionsBDD actions = new ActionsBDDImpl();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

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

    // Handler pour un programmeur par ID + sous-actions
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

            // Exemples:
            // /api/programmeurs/3
            // /api/programmeurs/3/prime
            // /api/programmeurs/3/projet
            if (parts.length < 4) {
                sendResponse(exchange, 400, "{\"error\":\"ID manquant\"}");
                return;
            }

            try {
                int id = Integer.parseInt(parts[3]);
                String subAction = (parts.length >= 5) ? parts[4] : null;

                // ----- GET /api/programmeurs/{id}
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

                // ----- DELETE /api/programmeurs/{id}
                if (method.equals("DELETE") && subAction == null) {
                    boolean success = actions.supprimerProgrammeur(id);
                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Programmeur supprimé\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"error\":\"Programmeur non trouvé\"}");
                    }
                    return;
                }

                // ----- PUT /api/programmeurs/{id}  (EXISTANT : salaire)
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

                // ----- PUT /api/programmeurs/{id}/prime
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

                // ----- PUT /api/programmeurs/{id}/projet
                if (method.equals("PUT") && "projet".equals(subAction)) {
                    String body = readRequestBody(exchange);

                    // On accepte {"idProjet": 2} ou {"idProjet":0} pour retirer du projet
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
            // AJOUT : POST /api/projets
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

    private static String programmeursToJson(List<Programmeur> programmeurs) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < programmeurs.size(); i++) {
            if (i > 0) json.append(",");
            json.append(programmeurToJson(programmeurs.get(i)));
        }
        json.append("]");
        return json.toString();
    }

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

    private static String projetsToJson(List<Projet> projets) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < projets.size(); i++) {
            if (i > 0) json.append(",");
            json.append(projetToJson(projets.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    // AJOUT dates
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

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

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

    // AJOUT : parse Projet
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

    private static String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private static int extractIntValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    // Comme extractIntValue mais autorise explicitement 0 (utile pour "retirer du projet")
    private static int extractIntValueAllowZero(String json, String key) {
        // même pattern, mais on garde 0 si présent
        return extractIntValue(json, key);
    }

    private static double extractDoubleValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([\\d.]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return 0.0;
    }

    private static double extractSalaireFromJson(String json) {
        return extractDoubleValue(json, "salaire");
    }

    // ============= UTILITAIRES HTTP =============

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}