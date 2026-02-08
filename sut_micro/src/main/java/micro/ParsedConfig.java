package pilot.micro;

import java.util.Objects;

public final class ParsedConfig {
    public enum Mode { FAST, SAFE }

    private final Mode mode;
    private final int timeoutMs;
    private final int retries;

    public ParsedConfig(Mode mode, int timeoutMs, int retries) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.timeoutMs = timeoutMs;
        this.retries = retries;
    }

    public Mode getMode() {
        return mode;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public int getRetries() {
        return retries;
    }

    @Override
    public String toString() {
        return "ParsedConfig{" +
                "mode=" + mode +
                ", timeoutMs=" + timeoutMs +
                ", retries=" + retries +
                '}';
    }
}
