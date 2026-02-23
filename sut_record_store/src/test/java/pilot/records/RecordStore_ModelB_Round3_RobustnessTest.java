package pilot.records;

import org.junit.jupiter.api.Test;
import java.util.Map;

public class RecordStore_ModelB_Round3_RobustnessTest {

    @Test
    void test_null_type_on_constructor() {
        new RecordStore(10, null);
    }

    @Test
    void test_key_over_32_chars_on_put() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("a".repeat(33), "value");
    }

    @Test
    void test_empty_string_key_on_put() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("", "value");
    }

    @Test
    void test_null_value_on_put() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("key", null);
    }

    @Test
    void test_null_key_on_putNumeric() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric(null, "123");
    }

    @Test
    void test_float_value_on_putNumeric() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.putNumeric("key", "3.14");
    }

    @Test
    void test_key_null_on_get() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.get(null);
    }

    @Test
    void test_empty_string_key_on_get() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.get("");
    }

    @Test
    void test_null_value_on_getNumeric() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.put("key", null);
        store.getNumeric("key");
    }

    @Test
    void test_key_not_found_on_getNumeric() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.getNumeric("non-existent-key");
    }

    @Test
    void test_capacity_exceeded() {
        RecordStore store = new RecordStore(2, RecordType.MIXED);
        for (int i = 0; i < 3; i++) {
            store.put("a" + i, "value");
        }
    }

    @Test
    void test_put_after_close() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.close();
        store.put("key", "value");
    }

    @Test
    void test_put_on_numeric_only_store() {
        RecordStore store = new RecordStore(10, RecordType.NUMERIC_ONLY);
        store.put("name", "alice");
    }

    @Test
    void test_putNumeric_after_close() {
        RecordStore store = new RecordStore(10, RecordType.MIXED);
        store.close();
        store.putNumeric("key", "123");
    }

    @Test
    void test_put_on_string_only_store() {
        RecordStore store = new RecordStore(10, RecordType.STRING_ONLY);
        store.putNumeric("name", "alice");
    }
}