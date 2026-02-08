package pilot.micro;

import java.util.HashMap;
import java.util.Map;

import static pilot.micro.ParsedConfig.Mode;

public class ConfigParser {

    private ParsedConfig lastGoodConfig;

    /**
     * Parse a config from "key=value" lines.
     * Required keys: mode, timeoutMs, retries
     *
     * Rules:
     * - mode ∈ {FAST, SAFE}
     * - timeoutMs ∈ [1..60000]
     * - retries ∈ [0..10]
     * - ignore blank lines
     * - lines starting with '#' are comments
     *
     * State:
     * - lastGoodConfig updated only on successful parse
     */
    public ParsedConfig parse(String text) {
        if (text == null) {
            throw new IllegalArgumentException("config text must not be null");
        }

        Map<String, String> kv = new HashMap<>();
        String[] lines = text.split("\\R", -1); // keep empty trailing lines

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];

            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            int eq = line.indexOf('=');
            if (eq <= 0 || eq == line.length() - 1) {
                throw new IllegalArgumentException("malformed line at " + (i + 1) + ": " + raw);
            }

            // If multiple '=' exist, treat as structural violation (common config gotcha)
            if (line.indexOf('=', eq + 1) != -1) {
                throw new IllegalArgumentException("multiple '=' in line at " + (i + 1) + ": " + raw);
            }

            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();

            if (key.isEmpty()) {
                throw new IllegalArgumentException("empty key at line " + (i + 1));
            }

            // Duplicate key is allowed structurally, but last wins (explicit choice).
            kv.put(key, value);
        }

        String modeStr = requireKey(kv, "mode");
        String timeoutStr = requireKey(kv, "timeoutMs");
        String retriesStr = requireKey(kv, "retries");

        Mode mode = parseMode(modeStr);
        int timeoutMs = parseIntStrict(timeoutStr, "timeoutMs");
        int retries = parseIntStrict(retriesStr, "retries");

        if (timeoutMs < 1 || timeoutMs > 60000) {
            throw new IllegalArgumentException("timeoutMs out of range: " + timeoutMs);
        }
        if (retries < 0 || retries > 10) {
            throw new IllegalArgumentException("retries out of range: " + retries);
        }

        ParsedConfig cfg = new ParsedConfig(mode, timeoutMs, retries);
        this.lastGoodConfig = cfg;
        return cfg;
    }

    public ParsedConfig getLastGoodConfig() {
        return lastGoodConfig;
    }

    private static String requireKey(Map<String, String> kv, String key) {
        String v = kv.get(key);
        if (v == null) {
            throw new IllegalArgumentException("missing required key: " + key);
        }
        return v;
    }

    private static Mode parseMode(String s) {
        if (s == null) {
            throw new IllegalArgumentException("mode must not be null");
        }
        String norm = s.trim().toUpperCase();
        try {
            return Mode.valueOf(norm);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid mode: " + s);
        }
    }

    /**
     * Strict int parsing: rejects scientific notation, hex, blanks, etc.
     */
    private static int parseIntStrict(String s, String field) {
        if (s == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
        String t = s.trim();
        if (t.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be empty");
        }
        // Reject anything not a normal signed integer string
        if (!t.matches("[-+]?\\d+")) {
            throw new IllegalArgumentException(field + " not a plain integer: " + s);
        }
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            // includes overflow
            throw new IllegalArgumentException(field + " out of int range: " + s);
        }
    }
}
