import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class WeatherServer {

    public static void start() throws Exception {
        WeatherService service = new WeatherService();

        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);

        server.createContext("/api/weather", exchange -> {

            // CORS HEADERS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            String query = exchange.getRequestURI().getQuery();

            if (query == null || !query.contains("city=")) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            String city = URLDecoder.decode(
                    query.split("=")[1], StandardCharsets.UTF_8);

            try {
               WeatherDTO dto = service.getWeather(city);

                JSONObject json = new JSONObject();
                json.put("city", dto.city);
                json.put("temperature", dto.temperature);
                json.put("feelsLike", dto.feelsLike);
                json.put("humidity", dto.humidity);
                json.put("windSpeed", dto.windSpeed);
                json.put("condition", dto.condition);
                json.put("lastUpdated", System.currentTimeMillis());

                String response = json.toString();


                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } catch (RuntimeException e) {
                // Business / validation errors
                String errorJson = "{\"error\":\"" + e.getMessage() + "\"}";

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, errorJson.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorJson.getBytes());
                }

            } catch (Exception e) {
                // Unexpected errors
                String errorJson = "{\"error\":\"Internal server error\"}";

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, errorJson.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorJson.getBytes());
                }
            }
        });

        server.createContext("/health", exchange -> {
            String response = "{\"status\":\"UP\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });


        server.start();
        System.out.println("Server running on http://localhost:9090");
    }
}
