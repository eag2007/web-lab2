package orchestrator;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class RequestsHandler {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void check(String method, String path, String query) throws IOException, InterruptedException {
        if (!"GET".equals(method)) {
            sendResponse(Messages.STATUS_405, "");
            return;
        }

        BigDecimal x = decimal(param(query, "x"));
        BigDecimal y = decimal(param(query, "y"));
        BigDecimal r = decimal(param(query, "global_r"));

        if (x == null || y == null || r == null) {
            sendResponse(Messages.STATUS_400, "Некорректные параметры");
            return;
        }

        checkAndWrite(x, y, r);
    }

    public static void validate(String method, String path, String query) throws IOException, InterruptedException {
        if (!"GET".equals(method)) {
            sendResponse(Messages.STATUS_405, "");
            return;
        }

        BigDecimal x = decimal(param(query, "x"));
        BigDecimal y = decimal(param(query, "y"));
        BigDecimal r = decimal(param(query, "r"));

        if (x == null || y == null || r == null) {
            sendResponse(Messages.STATUS_400, "Некорректные параметры");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:7001/api/validate?x=%s&y=%s&r=%s",
                        x.toPlainString(), y.toPlainString(), r.toPlainString())))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (!response.body().trim().equals("true")) {
            sendResponse(Messages.STATUS_400, "Данные не прошли валидацию");
            return;
        }

        checkAndWrite(x, y, r);
    }

    public static void read(String method, String path, String query) throws IOException, InterruptedException {
        if (!"GET".equals(method)) {
            sendResponse(Messages.STATUS_405, "");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7002/read"))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            sendResponse(Messages.STATUS_500, "Writer error");
            return;
        }
        sendResponse(Messages.STATUS_200, response.body());
    }

    public static void clear(String method, String path, String query) throws IOException, InterruptedException {
        if (!"POST".equals(method)) {
            sendResponse(Messages.STATUS_405, "");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7002/clear"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            sendResponse(Messages.STATUS_500, "Writer error");
            return;
        }
        sendResponse(Messages.STATUS_200, "OK");
    }

    private static void checkAndWrite(BigDecimal x, BigDecimal y, BigDecimal r) throws IOException, InterruptedException {
        HttpRequest checkRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:7001/api/check?x=%s&y=%s&global_r=%s",
                        x.toPlainString(), y.toPlainString(), r.toPlainString())))
                .GET()
                .build();

        HttpResponse<String> checkResponse = CLIENT.send(checkRequest, HttpResponse.BodyHandlers.ofString());

        if (checkResponse.statusCode() != 200) {
            sendResponse(Messages.STATUS_500, "Validator error");
            return;
        }

        String point = String.format("[%s,%s,%s,\"%s\",%d]",
                x.toPlainString(), y.toPlainString(), r.toPlainString(),
                checkResponse.body().trim(), System.currentTimeMillis());

        HttpRequest writeRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:7002/write"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(point))
                .build();

        HttpResponse<String> writeResponse = CLIENT.send(writeRequest, HttpResponse.BodyHandlers.ofString());

        if (writeResponse.statusCode() != 200) {
            sendResponse(Messages.STATUS_500, "Writer error");
            return;
        }
        sendResponse(Messages.STATUS_200, point);
    }

    private static String param(String query, String name) {
        if (query == null) {
            return null;
        }

        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].equals(name)) {
                return URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private static BigDecimal decimal(String value) {
        try {
            return value == null ? null : new BigDecimal(value.trim());
        } catch (NumberFormatException _) {
            return null;
        }
    }

    public static void sendResponse(Messages message, String description) {
        System.out.print(message);
        System.out.print("Access-Control-Allow-Origin: *\r\n");
        System.out.print("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
        System.out.print(description);
        System.out.flush();
    }
}