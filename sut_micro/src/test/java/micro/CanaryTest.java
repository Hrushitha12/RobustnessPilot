package pilot.micro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CanaryTest {

    private static final String GOOD =
            "mode=SAFE\n" +
            "timeoutMs=5000\n" +
            "retries=3\n";

    @Test
    void canary_shouldAlwaysPass() {
        ConfigParser parser = new ConfigParser();
        ParsedConfig cfg = parser.parse(GOOD);

        assertEquals(ParsedConfig.Mode.SAFE, cfg.getMode());
        assertEquals(5000, cfg.getTimeoutMs());
        assertEquals(3, cfg.getRetries());

        assertNotNull(parser.getLastGoodConfig());
        assertEquals(5000, parser.getLastGoodConfig().getTimeoutMs());
    }
}
