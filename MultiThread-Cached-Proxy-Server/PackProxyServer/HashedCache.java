package PackProxyServer;
import java.util.HashMap;
import java.util.Map;

public class HashedCache {
    private int capacity;
    private Map<String, String> cache;
    private String[] accessOrder;
    private int size;

    public HashedCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        accessOrder = new String[capacity]; // Assuming capacity as the maximum size of the cache
        size = 0;
    }

    public void put(String key, String value) {
        if (cache.containsKey(key)) {
            cache.put(key, value);
            updateAccessOrder(key);
        } else {
            if (size >= capacity) {
                evictLRU();
            }
            cache.put(key, value);
            accessOrder[size++] = key;
            // Log that a new entry is cached
            System.out.println("Entry cached for key: " + key);
        }
    }

    public String get(String key) {
        if (cache.containsKey(key)) {
            updateAccessOrder(key);
            return cache.get(key);
        }
        return null;
    }

    private void updateAccessOrder(String key) {
        for (int i = 0; i < size; i++) {
            if (accessOrder[i].equals(key)) {
                System.arraycopy(accessOrder, 0, accessOrder, 1, i);
                accessOrder[0] = key;
                break;
            }
        }
    }

    private void evictLRU() {
        String lruKey = accessOrder[size - 1];
        cache.remove(lruKey);
        System.arraycopy(accessOrder, 1, accessOrder, 0, size - 1);
        size--;
    }
}
