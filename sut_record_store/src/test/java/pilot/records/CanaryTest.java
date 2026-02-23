package pilot.records;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanaryTest {

    @Test
    void canary_shouldAlwaysPass() {
        // Always creates a brand-new store — completely independent of any
        // robustness test that ran before it.
        RecordStore store = new RecordStore(10, RecordType.MIXED);

        store.put("host", "localhost");
        store.putNumeric("port", "5432");
        store.put("env", "test");

        assertTrue(store.isOpen());
        assertEquals(3, store.size());
        assertEquals("localhost", store.get("host"));
        assertEquals(5432, store.getNumeric("port"));
        assertEquals("test", store.get("env"));

        store.close();
        assertFalse(store.isOpen());

        // Reads still work after close
        assertEquals("localhost", store.get("host"));
    }
}