package pilot.records;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class RobustnessTests {

    // ------------------------------------------------------------------
    // Category: R1 — Null and blank inputs (IllegalArgumentException)
    // ------------------------------------------------------------------

    @Test
    void test_R1_null_key_on_put() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put(null, "value");
    }

    @Test
    void test_R1_blank_key_on_put() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("   ", "value");
    }

    @Test
    void test_R1_null_value_on_put() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("host", null);
    }

    @Test
    void test_R1_null_value_on_putNumeric() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("timeout", null);
    }

    // ------------------------------------------------------------------
    // Category: R2 — Key length boundary (IllegalArgumentException)
    // ------------------------------------------------------------------

    @Test
    void test_R2_key_one_over_max_length() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        String key = "k".repeat(RecordStore.MAX_KEY_LENGTH + 1);
        store.put(key, "value");
    }

    @Test
    void test_R2_key_very_long() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        String key = "x".repeat(500);
        store.put(key, "value");
    }

    // ------------------------------------------------------------------
    // Category: R3 — Capacity exhaustion (IndexOutOfBoundsException)
    // ------------------------------------------------------------------

    @Test
    void test_R3_exceed_capacity_by_one() {
        RecordStore store = new RecordStore(3, RecordType.MIXED);
        store.put("a", "1");
        store.put("b", "2");
        store.put("c", "3");
        store.put("d", "overflow");
    }

    @Test
    void test_R3_invalid_capacity_zero() {
        new RecordStore(0, RecordType.MIXED);
    }

    @Test
    void test_R3_invalid_capacity_over_max() {
        new RecordStore(101, RecordType.MIXED);
    }

    @Test
    void test_R3_putAll_exceeds_capacity() {
        RecordStore store = new RecordStore(2, RecordType.MIXED);
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("a", "1");
        entries.put("b", "2");
        entries.put("c", "3");
        store.putAll(entries);
    }

    // ------------------------------------------------------------------
    // Category: R4 — Numeric parsing (NumberFormatException)
    // ------------------------------------------------------------------

    @Test
    void test_R4_alpha_string_as_numeric() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("key", "abc");
    }

    @Test
    void test_R4_float_value_rejected() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("key", "3.14");
    }

    @Test
    void test_R4_overflow_value() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("key", "999999999999999999999");
    }

    @Test
    void test_R4_numeric_out_of_range_high() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("key", String.valueOf(RecordStore.MAX_NUMERIC + 1));
    }

    // ------------------------------------------------------------------
    // Category: R5 — Type contract violations (UnsupportedOperationException)
    // ------------------------------------------------------------------

    @Test
    void test_R5_string_put_on_numeric_only_store() {
        RecordStore store = new RecordStore(10, RecordType.NUMERIC_ONLY);
        store.put("name", "alice");
    }

    @Test
    void test_R5_putNumeric_on_string_only_store() {
        RecordStore store = new RecordStore(10, RecordType.STRING_ONLY);
        store.putNumeric("timeout", "30");
    }

    @Test
    void test_R5_null_type_on_construction() {
        new RecordStore(10, null);
    }

    // ------------------------------------------------------------------
    // Category: R6 — Lifecycle violations (IllegalStateException)
    // ------------------------------------------------------------------

    @Test
    void test_R6_put_after_close() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.close();
        store.put("key", "value");
    }

    @Test
    void test_R6_putNumeric_after_close() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.close();
        store.putNumeric("key", "42");
    }

    @Test
    void test_R6_remove_after_close() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("key", "value");
        store.close();
        store.remove("key");
    }

    // ------------------------------------------------------------------
    // Category: R7 — putAll atomicity (IllegalArgumentException)
    // ------------------------------------------------------------------

    @Test
    void test_R7_putAll_null_key_is_atomic() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("good", "value");
        entries.put(null, "oops");
        store.putAll(entries);
    }

    @Test
    void test_R7_putAll_null_value_is_atomic() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("first", "ok");
        entries.put("second", null);
        store.putAll(entries);
    }

    @Test
    void test_R7_putAll_after_close() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.close();
        store.putAll(Map.of("a", "1"));
    }
}