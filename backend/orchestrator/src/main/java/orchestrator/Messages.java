package orchestrator;

public enum Messages {
    STATUS_200("Status: 200 OK\r\n"),
    STATUS_400("Status: 400 Bad Request\r\n"),
    STATUS_404("Status: 404 Not Found\r\n"),
    STATUS_405("Status: 405 Method Not Allowed\r\nAllow: GET, POST\r\n"),
    STATUS_500("Status: 500 Internal Server Error\r\n"),
    CONTENT_TYPE("Content-Type: application/json; charset=UTF-8\r\n\r\n");

    private final String message;

    Messages(String s) {
        this.message = s;
    }

    @Override
    public String toString() {
        return message;
    }
}
