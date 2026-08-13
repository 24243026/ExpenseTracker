import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExpenseServer {

    static List<Expense> expenses = new ArrayList<>();
    static int nextId = 1;

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8081), 0);

        // Frontend
        server.createContext("/", ExpenseServer::serveHome);
        server.createContext("/style.css", ExpenseServer::serveCSS);
        server.createContext("/script.js", ExpenseServer::serveJS);

        // Backend API
        server.createContext("/api/expenses", ExpenseServer::handleExpenses);

        server.start();

        System.out.println("=================================");
        System.out.println("Expense Tracker Started!");
        System.out.println("Open: http://localhost:8081");
        System.out.println("=================================");
    }

    // Serve index.html
    static void serveHome(HttpExchange exchange) throws IOException {

        String response = Files.readString(
                Path.of("index.html"),
                StandardCharsets.UTF_8);

        sendResponse(exchange, response, "text/html");
    }

    // Serve CSS
    static void serveCSS(HttpExchange exchange) throws IOException {

        String response = Files.readString(
                Path.of("style.css"),
                StandardCharsets.UTF_8);

        sendResponse(exchange, response, "text/css");
    }

    // Serve JavaScript
    static void serveJS(HttpExchange exchange) throws IOException {

        String response = Files.readString(
                Path.of("script.js"),
                StandardCharsets.UTF_8);

        sendResponse(exchange, response, "application/javascript");
    }

    // API
    static void handleExpenses(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        if (method.equals("GET")) {

            StringBuilder json = new StringBuilder("[");
            
            for (int i = 0; i < expenses.size(); i++) {

                Expense e = expenses.get(i);

                json.append("{")
                        .append("\"id\":").append(e.id).append(",")
                        .append("\"name\":\"").append(e.name).append("\",")
                        .append("\"amount\":").append(e.amount).append(",")
                        .append("\"category\":\"").append(e.category).append("\"")
                        .append("}");

                if (i < expenses.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");

            sendResponse(
                    exchange,
                    json.toString(),
                    "application/json");

        } else if (method.equals("POST")) {

            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);

            String name = getValue(body, "name");
            String amount = getValue(body, "amount");
            String category = getValue(body, "category");

            Expense expense = new Expense(
                    nextId++,
                    name,
                    Double.parseDouble(amount),
                    category
            );

            expenses.add(expense);

            sendResponse(
                    exchange,
                    "{\"message\":\"Expense added successfully\"}",
                    "application/json");

        } else if (method.equals("DELETE")) {

            String query = exchange.getRequestURI().getQuery();

            if (query != null && query.startsWith("id=")) {

                int id = Integer.parseInt(query.substring(3));

                expenses.removeIf(e -> e.id == id);
            }

            sendResponse(
                    exchange,
                    "{\"message\":\"Expense deleted\"}",
                    "application/json");
        }
    }

    // Simple JSON value reader
    static String getValue(String json, String key) {

        String search = "\"" + key + "\":\"";

        int start = json.indexOf(search);

        if (start != -1) {

            start += search.length();

            int end = json.indexOf("\"", start);

            return json.substring(start, end);
        }

        search = "\"" + key + "\":";

        start = json.indexOf(search);

        start += search.length();

        int end = json.indexOf(",", start);

        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return json.substring(start, end);
    }

    static void sendResponse(
            HttpExchange exchange,
            String response,
            String contentType) throws IOException {

        exchange.getResponseHeaders()
                .set("Content-Type", contentType);

        exchange.sendResponseHeaders(
                200,
                response.getBytes(StandardCharsets.UTF_8).length);

        OutputStream output = exchange.getResponseBody();

        output.write(
                response.getBytes(StandardCharsets.UTF_8));

        output.close();
    }

    static class Expense {

        int id;
        String name;
        double amount;
        String category;

        Expense(
                int id,
                String name,
                double amount,
                String category) {

            this.id = id;
            this.name = name;
            this.amount = amount;
            this.category = category;
        }
    }
}