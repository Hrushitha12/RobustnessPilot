package pilot.records;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BaselineTests {

    // ------------------------------------------------------------------
    // Basic put / get
    // ------------------------------------------------------------------

    @Test
    void baseline_put_and_get_string() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("host", "localhost");
        assertEquals("localhost", store.get("host"));
    }

    @Test
    void baseline_putNumeric_and_getNumeric() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("timeout", "30");
        assertEquals(30, store.getNumeric("timeout"));
    }

    @Test
    void baseline_putNumeric_negative_value() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("offset", "-50");
        assertEquals(-50, store.getNumeric("offset"));
    }

    @Test
    void baseline_put_overwrites_existing_key() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("env", "staging");
        store.put("env", "production");
        assertEquals("production", store.get("env"));
        assertEquals(1, store.size());
    }

    // ------------------------------------------------------------------
    // RecordType enforcement (happy path)
    // ------------------------------------------------------------------

    @Test
    void baseline_string_only_store_accepts_put() {
        RecordStore store = new RecordStore(5, RecordType.STRING_ONLY);
        store.put("name", "alice");
        assertEquals("alice", store.get("name"));
    }

    @Test
    void baseline_numeric_only_store_accepts_putNumeric() {
        RecordStore store = new RecordStore(5, RecordType.NUMERIC_ONLY);
        store.putNumeric("port", "8080");
        assertEquals(8080, store.getNumeric("port"));
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    @Test
    void baseline_store_is_open_on_creation() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        assertTrue(store.isOpen());
    }

    @Test
    void baseline_close_transitions_store_to_closed() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("key", "value");
        store.close();
        assertFalse(store.isOpen());
    }

    @Test
    void baseline_reads_still_work_after_close() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("region", "eu-west");
        store.close();
        assertEquals("eu-west", store.get("region"));
    }

    // ------------------------------------------------------------------
    // size / keys / remove
    // ------------------------------------------------------------------

    @Test
    void baseline_size_reflects_number_of_entries() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        assertEquals(0, store.size());
        store.put("a", "1");
        store.put("b", "2");
        assertEquals(2, store.size());
    }

    @Test
    void baseline_keys_returns_insertion_order() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("first", "x");
        store.put("second", "y");
        store.put("third", "z");
        assertEquals(java.util.List.of("first", "second", "third"), store.keys());
    }

    @Test
    void baseline_remove_reduces_size() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("tmp", "value");
        store.remove("tmp");
        assertEquals(0, store.size());
        assertNull(store.get("tmp"));
    }

    @Test
    void baseline_remove_nonexistent_key_is_noop() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        assertDoesNotThrow(() -> store.remove("ghost"));
    }

    // ------------------------------------------------------------------
    // putAll
    // ------------------------------------------------------------------

    @Test
    void baseline_putAll_writes_all_entries() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putAll(Map.of("x", "1", "y", "2"));
        assertEquals(2, store.size());
    }

    // ------------------------------------------------------------------
    // Boundary values (valid)
    // ------------------------------------------------------------------

    @Test
    void baseline_numeric_at_min_boundary() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("low", String.valueOf(RecordStore.MIN_NUMERIC));
        assertEquals(RecordStore.MIN_NUMERIC, store.getNumeric("low"));
    }

    @Test
    void baseline_numeric_at_max_boundary() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("high", String.valueOf(RecordStore.MAX_NUMERIC));
        assertEquals(RecordStore.MAX_NUMERIC, store.getNumeric("high"));
    }

    @Test
    void baseline_key_at_max_length() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        String key = "k".repeat(RecordStore.MAX_KEY_LENGTH);
        store.put(key, "ok");
        assertEquals("ok", store.get(key));
    }

    @Test
    void baseline_capacity_one_accepts_single_entry() {
        RecordStore store = new RecordStore(1, RecordType.STRING_ONLY);
        store.put("only", "one");
        assertEquals(1, store.size());
    }
}