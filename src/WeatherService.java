import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

public class WeatherService {

    private static final String API_KEY = ConfigLoader.getApiKey();

    // ✅ STATIC BLOCK IS THE CORRECT PLACE FOR VALIDATION
    static {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new RuntimeException("OPENWEATHER_API_KEY not set");
        }
    }

    private static final String API_URL =
        "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    private final HttpClient client = HttpClient.newHttpClient();
    private final WeatherCache cache = new WeatherCache();

    public WeatherDTO getWeather(String city) throws Exception {

        String cached = cache.get(city);
        if (cached != null) {
            System.out.println("CACHE HIT: " + city);
            return parseWeather(cached);
        }

        System.out.println("CACHE MISS: " + city);

        String url = String.format(API_URL, city, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            throw new RuntimeException("Invalid API key");
        }
        if (response.statusCode() == 404) {
            throw new RuntimeException("City not found");
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException("Weather service unavailable");
        }

        cache.put(city, response.body());
        return parseWeather(response.body());
    }

    // ✅ Helper method belongs INSIDE the service class
    private WeatherDTO parseWeather(String json) {
        JSONObject obj = new JSONObject(json);

        return new WeatherDTO(
                obj.getString("name"),
                obj.getJSONObject("main").getDouble("temp"),
                obj.getJSONObject("main").getDouble("feels_like"),
                obj.getJSONObject("main").getInt("humidity"),
                obj.getJSONObject("wind").getDouble("speed"),
                obj.getJSONArray("weather")
                   .getJSONObject(0)
                   .getString("description")
        );
    }
}
