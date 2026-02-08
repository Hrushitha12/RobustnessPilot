package pilot.micro;

import org.junit.jupiter.api.Test;

public class RobustnessTests {

    // ----------------------------
    // Category: R1 (Structural)
    // ----------------------------

    @Test
    void test_R1_nullInput() {
        // Category: R1
        ConfigParser parser = new ConfigParser();
        parser.parse(null);
    }

    @Test
    void test_R1_missingEqualsDelimiter() {
        // Category: R1
        ConfigParser parser = new ConfigParser();
        parser.parse("modeSAFE\ntimeoutMs=10\nretries=1\n");
    }

    @Test
    void test_R1_emptyKey() {
        // Category: R1
        ConfigParser parser = new ConfigParser();
        parser.parse("=SAFE\ntimeoutMs=10\nretries=1\n");
    }

    @Test
    void test_R1_multipleEqualsInLine() {
        // Category: R1
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=SAFE=EXTRA\ntimeoutMs=10\nretries=1\n");
    }

    @Test
    void test_R1_missingRequiredKey_timeoutMs() {
        // Category: R1
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=SAFE\nretries=1\n");
    }

    @Test
    void test_R1_extremelyLongLine() {
        // Category: R1
        ConfigParser parser = new ConfigParser();
        String longValue = "a".repeat(200_000);
        parser.parse("mode=SAFE\ntimeoutMs=10\nretries=1\njunk=" + longValue + "\n");
    }

    @Test
    void test_R1_weirdUnicodeAndNullChar() {
        // Category: R1
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=\u0000SAFE\ntimeoutMs=10\nretries=1\n");
    }

    // ----------------------------
    // Category: R2 (Semantic)
    // ----------------------------

    @Test
    void test_R2_invalidModeEnum() {
        // Category: R2
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=SUPERFAST\ntimeoutMs=10\nretries=1\n");
    }

    @Test
    void test_R2_timeoutMs_negative() {
        // Category: R2
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=SAFE\ntimeoutMs=-1\nretries=1\n");
    }

    @Test
    void test_R2_timeoutMs_tooLarge() {
        // Category: R2
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=SAFE\ntimeoutMs=999999\nretries=1\n");
    }

    @Test
    void test_R2_retries_tooLarge() {
        // Category: R2
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=SAFE\ntimeoutMs=10\nretries=999\n");
    }

    @Test
    void test_R2_numericOverflowLike() {
        // Category: R2
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=SAFE\ntimeoutMs=999999999999999999999\nretries=1\n");
    }

    @Test
    void test_R2_scientificNotationRejected() {
        // Category: R2
        ConfigParser parser = new ConfigParser();
        parser.parse("mode=SAFE\ntimeoutMs=1e3\nretries=1\n");
    }
}
