package net.dirtcraft.dirtcommons.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    private static Pattern PASCAL_CASE = Pattern.compile("(^| |_|-)(.)");

    public static String toPascal(String input) {
        Matcher matcher = PASCAL_CASE.matcher(input);
        StringBuilder builder = new StringBuilder(input);
        while (matcher.find()) {
            int idx = matcher.start(2);
            char c = builder.charAt(idx);
            builder.setCharAt(idx, Character.toUpperCase(c));
        }
        return builder.toString();
    }
}
