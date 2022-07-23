package xyz.lucaci32u4.urlsh;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Locale;

public interface Utils {



    static long numberToReference(String reference) throws InvalidParameterException {
        long n = 0;
        reference = reference.toLowerCase();
        int length = reference.length();
        for (long multiplier = 1; length-- != 0; multiplier *= (1 + 'z' - 'a')) {
            char c = reference.charAt(length);
            if (c < 'a' || c > 'z') throw new InvalidParameterException();
            n = n + multiplier * (c - 'a');
        }
        return n;
    }

    static String numberToReference(long num) {
        StringBuilder sb = new StringBuilder();
        long multiplier = 1;
        while (multiplier < num) {
            multiplier *= (1 + 'z' - 'a');
        }
        while (multiplier != 0) {
            long div = num / multiplier;
            sb.append((char)('a' + div));
            num -= div * multiplier;
            multiplier /= (1 + 'z' - 'a');
        }
        while (sb.charAt(0) == 'a' && sb.length() > 1) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    static String unpackSourceIpProxy(String X_forwarded_for, String defaultValue) {
        if (X_forwarded_for == null) {
            return defaultValue;
        }
        return Arrays.stream(X_forwarded_for.split(",")).map(String::trim).filter(s -> !s.isBlank()).findFirst().orElse(defaultValue);
    }

}
