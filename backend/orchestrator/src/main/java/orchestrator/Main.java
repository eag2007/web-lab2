package orchestrator;

import com.fastcgi.FCGIInterface;

public class Main {
    private static void log(String message) {
        try (java.io.FileWriter fw = new java.io.FileWriter("/tmp/orchestrator.log", true)) {
            fw.write("[" + java.time.LocalDateTime.now() + "] " + message + "\n");
        } catch (java.io.IOException _) {
        }
    }

    public static void main(String[] args) {
        FCGIInterface fastCGI = new FCGIInterface();
        log("FastCGI начал работу");
        while (fastCGI.FCGIaccept() >= 0) {
            try {
                log("ПОЛУЧЕН ЗАПРОС");
                String method = System.getProperty("REQUEST_METHOD");
                String path = System.getProperty("REQUEST_URI");
                String query = System.getProperty("QUERY_STRING");
                log("REQUEST:" + method + "\n" + "Path:" + path + "\n" + "QUERY:" + query);

                if (path != null) {
                    path = path.split("\\?", 2)[0];

                    int apiIndex = path.indexOf("/api/");
                    if (apiIndex > 0) {
                        path = path.substring(apiIndex);
                    }
                }

                switch (path) {
                    case "/api/check":
                        RequestsHandler.check(method, path, query);
                        break;

                    case "/api/points/get":
                        RequestsHandler.read(method, path, query);
                        break;

                    case "/api/validate":
                        RequestsHandler.validate(method, path, query);
                        break;

                    case "/api/points/clear":
                        RequestsHandler.clear(method, path, query);
                        break;

                    case null:
                        RequestsHandler.sendResponse(Messages.STATUS_400, "Некорректный запрос");
                        break;

                    default:
                        RequestsHandler.sendResponse(Messages.STATUS_404, "Not Found Page");
                }
            } catch (Exception e) {
                log("Упал с ошибкой " + e.getMessage());
                RequestsHandler.sendResponse(Messages.STATUS_500, "Внутренняя ошибка сервера: " + e.getMessage());
            }
        }
    }
}