package com.example.blog.util;

import java.util.regex.Pattern;

public final class PostTextUtils {
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
    private PostTextUtils() {}
    public static String plainText(String html) {
        if (html == null || html.isEmpty()) return "";
        return HTML_TAG.matcher(html).replaceAll(" ").replace("&nbsp;", " ").replaceAll("\\s+", " ").trim();
    }
    public static int plainTextLength(String html) { return plainText(html).length(); }
    public static String excerpt(String html, int maxLen) {
        String t = plainText(html);
        if (t.length() <= maxLen) return t.isEmpty() ? "（无摘要）" : t;
        return t.substring(0, maxLen) + "…";
    }
}
