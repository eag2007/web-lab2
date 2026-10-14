package ru.web2;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RequestsHandler {

    private static final List<String> history = new ArrayList<>();

    public static void handle(String method, String path, String query) {

        if (!"GET".equals(method)) {
            sendResponse(405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        if ("/api/check".equals(path)) {
            handleGet(query);
        } else if ("/api/history".equals(path)) {
            handleHistory();
        } else {
            sendResponse(404, "{\"error\":\"Not found\"}");
        }
    }

    private static void handleGet(String query) {
        long start = System.nanoTime();

        if (query == null || query.isEmpty()) {
            sendResponse(400, "{\"error\":\"Invalid query\"}");
            return;
        }

        try {
            BigDecimal[] coordinates = parseQuery(query);

            BigDecimal x = coordinates[0];
            BigDecimal y = coordinates[1];
            BigDecimal r = coordinates[2];

            if (!Validator.validate(x, y, r)) {
                sendResponse(400, "{\"error\":\"Invalid coordinates\"}");
                return;
            }

            boolean flag = AreaChecker.check(x, y, r);

            String timestamp = Instant.now().toString();
            long executionTime = System.nanoTime() - start;

            String result = "{"
                    + "\"x\":\"" + x.toPlainString() + "\","
                    + "\"y\":\"" + y.toPlainString() + "\","
                    + "\"r\":\"" + r.toPlainString() + "\","
                    + "\"flag\":" + flag + ","
                    + "\"timestamp\":\"" + timestamp + "\","
                    + "\"executionTimeNs\":" + executionTime
                    + "}";

            history.add(result);
            sendResponse(200, result);
        } catch (IllegalArgumentException e) {
            sendResponse(400, "{\"error\":\"Invalid parameters\"}");
        }
    }

    private static void handleHistory() {
        String response = "{\"history\":[" + String.join(",", history) + "]}";

        sendResponse(200, response);
    }

    private static BigDecimal[] parseQuery(String query) {
        String x = null;
        String y = null;
        String r = null;

        String[] params = query.split("&", -1);

        if (params.length != 3) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        for (String param : params) {
            String[] pair = param.split("=", 2);

            if (pair.length != 2) {
                throw new IllegalArgumentException("Invalid parameter");
            }

            String key = pair[0];
            String value = pair[1];

            if ("x".equals(key)) {
                if (x != null) {
                    throw new IllegalArgumentException("Duplicate x");
                }
                x = value;

            } else if ("y".equals(key)) {
                if (y != null) {
                    throw new IllegalArgumentException("Duplicate y");
                }
                y = value;

            } else if ("r".equals(key)) {
                if (r != null) {
                    throw new IllegalArgumentException("Duplicate r");
                }
                r = value;

            } else {
                throw new IllegalArgumentException("Unknown parameter");
            }
        }

        if (x == null || y == null || r == null) {
            throw new IllegalArgumentException("Missing parameters");
        }

        return new BigDecimal[]{
                new BigDecimal(x),
                new BigDecimal(y),
                new BigDecimal(r)
        };
    }

    public static void sendResponse(int code, String message) {
        if (code == 400) {
            System.out.print("Status: 400 Bad Request\r\n");
        } else if (code == 404) {
            System.out.print("Status: 404 Not Found\r\n");
        } else if (code == 405) {
            System.out.print("Status: 405 Method Not Allowed\r\n");
            System.out.print("Allow: GET\r\n");
        } else if (code != 200) {
            System.out.print("Status: 500 Internal Server Error\r\n");
        }

        System.out.print("Content-Type: application/json; charset=UTF-8\r\n\r\n");
        System.out.print(message);
        System.out.flush();
    }
}