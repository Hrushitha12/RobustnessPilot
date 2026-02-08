package pilot.commons;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Thin wrappers around selected Commons Lang methods.
 * This makes it explicit what we are targeting in the pilot.
 */
public final class CommonsLangTargets {

    private CommonsLangTargets() {}

    public static String substr(String s, int start, int end) {
        return StringUtils.substring(s, start, end);
    }

    public static String abbreviate(String s, int maxWidth) {
        return StringUtils.abbreviate(s, maxWidth);
    }

    public static String[] split(String s, char separator) {
        return StringUtils.split(s, separator);
    }

    public static Number createNumber(String s) {
        return NumberUtils.createNumber(s);
    }

    public static int toInt(String s, int defaultValue) {
        return NumberUtils.toInt(s, defaultValue);
    }
}
