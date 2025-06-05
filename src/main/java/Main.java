import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dao.*;
import service.BookService;
import service.LoanService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {

//        BookDao bookDao = new SqliteBookDao();
//        LoanDao loanDao = new SqliteLoanDao(bookDao);
//        BookService bookService = new BookService(bookDao);
//        LoanService loanService = new LoanService(loanDao, bookDao);

        try {
            DatabaseUtil.initializeDatabase();
        } catch (RuntimeException e) {
            System.err.println("DB 초기화 오류: " + e.getMessage());
            System.out.println("프로그램을 종료합니다.");
            return;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new FormHandler());
        server.createContext("/hello", new HelloHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("서버 시작됨: http://localhost:8080/");
    }


    static class FormHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                    <html>
                        <head>
                          
                        </head>
                        <body>
                          <h2>이름을 입력하세요</h2>
                          <form method="POST" action="/hello">
                            이름: <input type="text" name="name">
                            <input type="submit" value="제출">
                          </form>
                        </body>
                    </html>
                    """;

            //<meta charset="UTF-8">
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        }
    }


    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            InputStream is = exchange.getRequestBody();
            String formData = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = parseFormData(formData);

            String name = params.getOrDefault("name", "손님");
            String response = "<h1>안녕하세요, " + name + "님</h1>";

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private Map<String, String> parseFormData(String data) throws UnsupportedEncodingException {
            Map<String, String> map = new HashMap<>();
            for (String pair : data.split("&")) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                    map.put(key, value);
                }
            }
            return map;
        }
    }
}