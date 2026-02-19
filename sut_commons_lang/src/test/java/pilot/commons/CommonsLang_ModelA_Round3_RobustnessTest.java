
package pilot.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommonsLang_ModelA_Round3_RobustnessTest {

    @Test
    void substr_nullString() {
        assertThrows(NullPointerException.class, () -> CommonsLangTargets.substr(null, 0, 1));
    }

    @Test
    void substr_negativeStartIndex() {
        assertEquals("", CommonsLangTargets.substr("hello", -1, 2));
    }

    @Test
    void substr_startIndexGreaterThanLength() {
        assertEquals("", CommonsLangTargets.substr("hello", 5, 10));
    }

    @Test
    void substr_endIndexLessThanStartIndex() {
        assertThrows(StringIndexOutOfBoundsException.class, () -> CommonsLangTargets.substr("hello", 2, 1));
    }

    @Test
    void abbreviate_nullString() {
        assertEquals("", CommonsLangTargets.abbreviate(null, 5));
    }

    @Test
    void abbreviate_maxWidthLessThanMinAbbreviationLength() {
        assertEquals("he...", CommonsLangTargets.abbreviate("hello", 3));
    }

    @Test
    void split_nullString() {
        assertThrows(NullPointerException.class, () -> CommonsLangTargets.split(null, ','));
    }

    @Test
    void split_emptyString() {
        String[] result = CommonsLangTargets.split("", ',');
        assertEquals(0, result.length);
    }

    @Test
    void createNumber_nullString() {
        assertNull(CommonsLangTargets.createNumber(null));
    }

    @Test
    void createNumber_nonNumericString() {
        assertNull(CommonsLangTargets.createNumber("abc"));
    }

    @Test
    void toInt_nullString() {
        assertEquals(0, CommonsLangTargets.toInt(null, 0));
    }

    @Test
    void toInt_nonNumericString_withDefaultValue() {
        assertEquals(42, CommonsLangTargets.toInt("abc", 42));
    }

    @Test
    void toInt_emptyString_withDefaultValue() {
        assertEquals(10, CommonsLangTargets.toInt("", 10));
    }

    @Test
    void toInt_negativeNumber() {
        assertEquals(-5, CommonsLangTargets.toInt("-5", 0));
    }

    @Test
    void toInt_positiveNumber() {
        assertEquals(7, CommonsLangTargets.toInt("7", 0));
    }

    @Test
    void toInt_stringWithLeadingSpaces() {
        assertEquals(123, CommonsLangTargets.toInt(" 123 ", 0));
    }

    @Test
    void toInt_stringWithTrailingSpaces() {
        assertEquals(456, CommonsLangTargets.toInt("456 ", 0));
    }

    @Test
    void toInt_stringWithLeadingAndTrailingSpaces() {
        assertEquals(-789, CommonsLangTargets.toInt(" -789 ", 0));
    }

}


