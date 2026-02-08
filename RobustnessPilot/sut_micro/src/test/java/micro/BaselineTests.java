package pilot.micro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaselineTests {

    @Test
    void baseline_validConfig_FAST() {
        ConfigParser parser = new ConfigParser();
        String txt = "mode=FAST\ntimeoutMs=1\nretries=0\n";
        ParsedConfig cfg = parser.parse(txt);

        assertEquals(ParsedConfig.Mode.FAST, cfg.getMode());
        assertEquals(1, cfg.getTimeoutMs());
        assertEquals(0, cfg.getRetries());
        assertNotNull(parser.getLastGoodConfig());
    }

    @Test
    void baseline_validConfig_SAFE_withCommentsAndWhitespace() {
        ConfigParser parser = new ConfigParser();
        String txt = "# comment\n\n  mode = SAFE \n timeoutMs = 60000 \n retries=10\n";
        ParsedConfig cfg = parser.parse(txt);

        assertEquals(ParsedConfig.Mode.SAFE, cfg.getMode());
        assertEquals(60000, cfg.getTimeoutMs());
        assertEquals(10, cfg.getRetries());
        assertNotNull(parser.getLastGoodConfig());
    }

    @Test
    void baseline_duplicateKey_lastWins() {
        ConfigParser parser = new ConfigParser();
        String txt = "mode=SAFE\nmode=FAST\ntimeoutMs=10\nretries=1\n";
        ParsedConfig cfg = parser.parse(txt);

        assertEquals(ParsedConfig.Mode.FAST, cfg.getMode());
    }

    @Test
    void baseline_lastGoodConfig_notUpdatedOnNewInstance() {
        ConfigParser parser = new ConfigParser();
        assertNull(parser.getLastGoodConfig());
    }
}
