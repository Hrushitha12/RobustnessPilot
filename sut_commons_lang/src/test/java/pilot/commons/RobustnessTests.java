package pilot.commons;

import org.junit.jupiter.api.Test;

public class RobustnessTests {

    // ----------------------------
    // Category: R1 (Structural)
    // ----------------------------

    @Test
    void test_R1_nullString_substring() {
        // Category: R1
        // Null structure: method may handle null or may throw depending on implementation.
        CommonsLangTargets.substr(null, 0, 2);
    }

    @Test
    void test_R1_nullString_abbreviate() {
        // Category: R1
        CommonsLangTargets.abbreviate(null, 5);
    }

    @Test
    void test_R1_nullString_split() {
        // Category: R1
        CommonsLangTargets.split(null, ',');
    }

    @Test
    void test_R1_extremelyLongString_abbreviate() {
        // Category: R1
        String s = "a".repeat(1_000_000);
        CommonsLangTargets.abbreviate(s, 10);
    }

    @Test
    void test_R1_weirdUnicode_substring() {
        // Category: R1
        String s = "𝟘𝟙𝟚\u0000\u0007\uD83D\uDE00";
        CommonsLangTargets.substr(s, 0, 5);
    }

    // ----------------------------
    // Category: R2 (Semantic)
    // ----------------------------

    @Test
    void test_R2_negativeMaxWidth_abbreviate() {
        // Category: R2
        // Semantically invalid: negative max width.
        CommonsLangTargets.abbreviate("HelloWorld", -1);
    }

    @Test
    void test_R2_semanticallyInvalidIndices_substring_startGreaterThanEnd() {
        // Category: R2
        CommonsLangTargets.substr("abcd", 3, 1);
    }

    @Test
    void test_R2_createNumber_extremeExponent() {
        // Category: R2
        // Very large exponent - may cause NumberFormatException or Infinity-like behavior.
        CommonsLangTargets.createNumber("1e309");
    }

    @Test
    void test_R2_createNumber_hexGarbage() {
        // Category: R2
        CommonsLangTargets.createNumber("0xGG");
    }

    @Test
    void test_R2_createNumber_doubleSign() {
        // Category: R2
        CommonsLangTargets.createNumber("--12");
    }

    @Test
    void test_R2_toInt_overflowLike() {
        // Category: R2
        // Larger than int range
        CommonsLangTargets.toInt("999999999999999999999", -1);
    }

    @Test
    void test_R2_toInt_scientificNotation() {
        // Category: R2
        // toInt does not support scientific notation in typical parsing
        CommonsLangTargets.toInt("1e2", -1);
    }
}
