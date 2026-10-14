package ru.web2;

import com.fastcgi.FCGIInterface;

public class Main {
    public static void main(String[] args) {
        FCGIInterface fastcgi = new FCGIInterface();
        while (fastcgi.FCGIaccept() >= 0) {
            try {
                String method = System.getProperty("REQUESTS_METHOD");
                String path = System.getProperty("REQUEST_URI");
                String query = System.getProperty("QUERY_STRING");

                if (path != null) {
                    path = path.split("\\?", 2)[0];
                }

                RequestsHandler.handle(method, path, query);
            } catch (Exception e) {
                System.err.println(e.getMessage());

                System.out.println(
                        "Status 500 Internal Server Error\r\n" +
                                "Content-Type: application/json; charset=UTF-8\r\n\r\n" +
                                "{\"error\":\"Internal server error\""

                );

                System.out.flush();
            }
        }
    }
}