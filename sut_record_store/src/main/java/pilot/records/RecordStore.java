package pilot.records;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A typed, bounded, in-memory key-value store with a simple lifecycle.
 *
 * Lifecycle: a store is OPEN on creation and can be closed with close().
 * Once closed, no further write operations are permitted.
 *
 * Key rules:
 *   - Keys must be non-null, non-blank, and at most 32 characters.
 *   - Values must be non-null.
 *   - Capacity is fixed at construction time (1..100 entries).
 *   - RecordType controls which put operations are permitted:
 *       STRING_ONLY  -> put() allowed, putNumeric() not allowed
 *       NUMERIC_ONLY -> putNumeric() allowed, put() not allowed
 *       MIXED        -> both allowed
 *
 * Exceptions thrown:
 *   IllegalArgumentException     - null/blank/too-long key, null value,
 *                                  invalid capacity, numeric value out of range
 *   IllegalStateException        - write after close
 *   IndexOutOfBoundsException    - capacity exceeded
 *   NumberFormatException        - non-integer string passed to putNumeric
 *   UnsupportedOperationException- operation not allowed for this RecordType
 */
public class RecordStore {

    public static final int MAX_KEY_LENGTH = 32;
    public static final int MIN_NUMERIC    = -100_000;
    public static final int MAX_NUMERIC    =  100_000;

    private final int        capacity;
    private final RecordType type;
    private final Map<String, String> data;
    private boolean open;

    public RecordStore(int capacity, RecordType type) {
        if (capacity < 1 || capacity > 100) {
            throw new IllegalArgumentException("capacity must be between 1 and 100, got: " + capacity);
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        this.capacity = capacity;
        this.type     = type;
        this.data     = new LinkedHashMap<>();
        this.open     = true;
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    public void close() {
        this.open = false;
    }

    public boolean isOpen() {
        return open;
    }

    // ------------------------------------------------------------------
    // Write operations
    // ------------------------------------------------------------------

    /**
     * Store a string value under the given key.
     * Not permitted on NUMERIC_ONLY stores.
     */
    public void put(String key, String value) {
        checkOpen();
        checkKey(key);
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        if (type == RecordType.NUMERIC_ONLY) {
            throw new UnsupportedOperationException(
                "put(String) not allowed on a NUMERIC_ONLY store");
        }
        checkCapacity(key);
        data.put(key, value);
    }

    /**
     * Parse numericValue as a plain integer and store it.
     * Accepted range: MIN_NUMERIC..MAX_NUMERIC.
     * Not permitted on STRING_ONLY stores.
     */
    public void putNumeric(String key, String numericValue) {
        checkOpen();
        checkKey(key);
        if (numericValue == null) {
            throw new IllegalArgumentException("numericValue must not be null");
        }
        if (type == RecordType.STRING_ONLY) {
            throw new UnsupportedOperationException(
                "putNumeric not allowed on a STRING_ONLY store");
        }

        String trimmed = numericValue.trim();
        if (trimmed.isEmpty()) {
            throw new NumberFormatException("numericValue must not be blank");
        }
        // Reject anything that is not a plain signed integer
        if (!trimmed.matches("[-+]?\\d+")) {
            throw new NumberFormatException("not a plain integer: \"" + numericValue + "\"");
        }

        int parsed;
        try {
            parsed = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("out of int range: \"" + numericValue + "\"");
        }

        if (parsed < MIN_NUMERIC || parsed > MAX_NUMERIC) {
            throw new IllegalArgumentException(
                "numeric value out of range [" + MIN_NUMERIC + ".." + MAX_NUMERIC + "]: " + parsed);
        }

        checkCapacity(key);
        data.put(key, trimmed);
    }

    /**
     * Remove a key. No-op if the key does not exist.
     */
    public void remove(String key) {
        checkOpen();
        checkKey(key);
        data.remove(key);
    }

    /**
     * Put all entries from the given map.
     * Fails atomically: if any entry is invalid, no entries are written.
     */
    public void putAll(Map<String, String> entries) {
        checkOpen();
        if (entries == null) {
            throw new IllegalArgumentException("entries map must not be null");
        }

        // Validate everything first, before writing anything
        for (Map.Entry<String, String> e : entries.entrySet()) {
            checkKey(e.getKey());
            if (e.getValue() == null) {
                throw new IllegalArgumentException(
                    "null value for key: " + e.getKey());
            }
            if (type == RecordType.NUMERIC_ONLY) {
                throw new UnsupportedOperationException(
                    "putAll with String values not allowed on a NUMERIC_ONLY store");
            }
        }

        // Check combined capacity
        long newKeys = entries.keySet().stream()
            .filter(k -> !data.containsKey(k)).count();
        if (data.size() + newKeys > capacity) {
            throw new IndexOutOfBoundsException(
                "putAll would exceed capacity of " + capacity);
        }

        data.putAll(entries);
    }

    // ------------------------------------------------------------------
    // Read operations (allowed after close)
    // ------------------------------------------------------------------

    public String get(String key) {
        checkKey(key);
        return data.get(key);
    }

    public int getNumeric(String key) {
        checkKey(key);
        String raw = data.get(key);
        if (raw == null) {
            throw new IllegalArgumentException("key not found: " + key);
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                "value stored under key \"" + key + "\" is not numeric: " + raw);
        }
    }

    public int size() {
        return data.size();
    }

    public List<String> keys() {
        return Collections.unmodifiableList(new ArrayList<>(data.keySet()));
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private void checkOpen() {
        if (!open) {
            throw new IllegalStateException("store is closed; no writes permitted");
        }
    }

    private static void checkKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (key.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException(
                "key exceeds max length of " + MAX_KEY_LENGTH + ": \"" + key + "\"");
        }
    }

    private void checkCapacity(String key) {
        if (!data.containsKey(key) && data.size() >= capacity) {
            throw new IndexOutOfBoundsException(
                "store is full (capacity=" + capacity + ")");
        }
    }
}