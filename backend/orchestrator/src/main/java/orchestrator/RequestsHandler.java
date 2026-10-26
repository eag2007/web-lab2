package orchestrator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RequestsHandler {

    private static final List<String> history = new ArrayList<>();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void check(String method, String path, String query) throws IOException, InterruptedException {
        if (!method.equals("GET")) {
            sendResponse(Messages.STATUS_405, "");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:7001/api/check?%s", query)))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        String isShut = response.body().trim();
    }

    public static void validate(String method, String path, String query) throws IOException, InterruptedException {
        if (!method.equals("GET")) {
            sendResponse(Messages.STATUS_405, "");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:7001/api/validate?%s", query)))
                .GET()
                .build();

        HttpResponse<String> response = CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.body().trim().equals("true")) {
            check(method, path, query);
        }
    }

    public static void point(String method, String path, String query) {
        if (!method.equals("GET")) {
            sendResponse(Messages.STATUS_405, "");
            System.out.println("check s");
        }
    }

    public static void points(String method, String path, String query) {
        if (!method.equals("GET")) {
            sendResponse(Messages.STATUS_405, "");
            System.out.println("points s");
        }
    }

    public static void sendResponse(Messages message, String description) {
        System.out.print(message + "\r\n");
        System.out.print("Content-Type: application/json; charset=UTF-8\r\n\r\n");
        System.out.print(description);
        System.out.flush();
    }
}