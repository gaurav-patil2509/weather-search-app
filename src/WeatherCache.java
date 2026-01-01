import java.util.concurrent.ConcurrentHashMap;

public class WeatherCache {

    private static final long EXPIRY_TIME = 10 * 60 * 1000; // 10 minutes
    private static final int MAX_SIZE = 100;

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public String get(String city) {
        CacheEntry entry = cache.get(city);
        if (entry == null) return null;

        if (System.currentTimeMillis() - entry.timestamp > EXPIRY_TIME) {
            cache.remove(city);
            return null;
        }
        return entry.data;
    }

    public void put(String city, String data) {
        if (cache.size() >= MAX_SIZE) {
            cache.clear(); // simple eviction
        }
        cache.put(city, new CacheEntry(data));
    }
}
