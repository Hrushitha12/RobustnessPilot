package pilot.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanaryTest {

    @Test
    void canary_shouldAlwaysPass() {
        // Known-good inputs with deterministic behavior
        assertEquals("ell", CommonsLangTargets.substr("hello", 1, 4));
        assertEquals("hello", CommonsLangTargets.abbreviate("hello", 10));
        assertArrayEquals(new String[]{"a", "b", "c"}, CommonsLangTargets.split("a,b,c", ','));
        assertEquals(123, CommonsLangTargets.toInt("123", -1));
        assertEquals(12.5, CommonsLangTargets.createNumber("12.5").doubleValue(), 0.000001);
    }
}
