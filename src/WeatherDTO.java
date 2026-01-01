public class WeatherDTO {

    public String city;
    public double temperature;
    public double feelsLike;
    public int humidity;
    public double windSpeed;
    public String condition;

    public WeatherDTO(
            String city,
            double temperature,
            double feelsLike,
            int humidity,
            double windSpeed,
            String condition
    ) {
        this.city = city;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
    }
}
