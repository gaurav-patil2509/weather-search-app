import java.io.FileInputStream;
import java.util.Properties;

public class ConfigLoader {

    public static String getApiKey() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            return props.getProperty("openweather.api.key");
        } catch (Exception e) {
            throw new RuntimeException("Unable to load config.properties");
        }
    }
}
