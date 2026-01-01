public class CacheEntry {
    public final String data;
    public final long timestamp;

    public CacheEntry(String data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
}
