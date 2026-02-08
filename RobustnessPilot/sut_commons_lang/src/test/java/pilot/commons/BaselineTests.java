package pilot.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaselineTests {

    @Test
    void baseline_substring_normalCase() {
        assertEquals("bc", CommonsLangTargets.substr("abcd", 1, 3));
    }

    @Test
    void baseline_substring_startEqualsEnd() {
        assertEquals("", CommonsLangTargets.substr("abcd", 2, 2));
    }

    @Test
    void baseline_substring_outOfRangeIndices_shouldNotCrash() {
        // Commons Lang substring is defensive: out-of-range is handled gracefully
        assertEquals("abcd", CommonsLangTargets.substr("abcd", -10, 100));
    }

    @Test
    void baseline_abbreviate_normalCase() {
        assertEquals("Hello...", CommonsLangTargets.abbreviate("HelloWorld", 8));
    }

    @Test
    void baseline_split_normalCase() {
        assertArrayEquals(new String[]{"x", "y"}, CommonsLangTargets.split("x|y", '|'));
    }

    @Test
    void baseline_split_emptyString_returnsEmptyArray() {
        assertArrayEquals(new String[]{}, CommonsLangTargets.split("", ','));
}


    @Test
    void baseline_createNumber_integer() {
        Number n = CommonsLangTargets.createNumber("42");
        assertEquals(42L, n.longValue());
    }

    @Test
    void baseline_createNumber_decimal() {
        Number n = CommonsLangTargets.createNumber("3.14");
        assertEquals(3.14, n.doubleValue(), 0.000001);
    }

    @Test
    void baseline_toInt_defaultOnInvalid() {
        assertEquals(7, CommonsLangTargets.toInt("not_a_number", 7));
    }

    @Test
    void baseline_toInt_whitespaceReturnsDefault() {
        assertEquals(-1, CommonsLangTargets.toInt("  15  ", -1));
}
}
