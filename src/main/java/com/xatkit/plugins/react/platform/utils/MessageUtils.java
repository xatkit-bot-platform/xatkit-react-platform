package com.xatkit.plugins.react.platform.utils;

import lombok.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for message formatting.
 */
public final class MessageUtils {

    /**
     * The markdown code to create a new line.
     * <p>
     * The markdown parser we use on the client side strictly follows markdown syntax, and new lines ({@code \n})
     * must be prefixed with two spaces.
     */
    public static String NEW_LINE = "  \n";

    /**
     * Creates a string that is rendered as an event link in the client widget.
     * <p>
     * Event links are rendered as regular links, but do not perform any navigation. Instead, their value is
     * interpreted as a user input, allowing to select elements from rendered messages.
     * <p>
     * This method uses the provided {@code value} for both the name of the link and its value (the user input sent
     * when the link is clicked).
     *
     * @param value the value of the event link
     * @return the string representing the event link
     */
    public static String eventLink(@NonNull String value) {
        return eventLink(value, value);
    }

    /**
     * Creates a string that is rendered as an event link in the client widget.
     * <p>
     * Event links are rendered as regular links, but do not perform any navigation. Instead, their value is
     * interpreted as a user input, allowing to select elements from rendered messages.
     *
     * @param name  the name of the event link
     * @param value the value of the event link
     * @return the string representing the event link
     */
    public static String eventLink(@NonNull String name, @NonNull String value) {
        try {
            return MessageFormat.format("[{0}](##{1})", name, URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Creates a string from the provided {@code list} that is rendered as a bullet list in the client widget.
     * <p>
     * Each element of the provided {@code list} is rendered with the following markdown pattern: {@code "- item
     * .toString()\n"}.
     * <p>
     * Non-string elements are rendered using {@link Object#toString()}.
     * <p>
     * Empty {@code list} is rendered as an empty string.
     *
     * @param list the list of elements to render as a bullet list
     * @return the string representing the bullet list
     * @throws NullPointerException if the provided {@code list} is {@code null}
     */
    public static String itemizeList(@NonNull List<?> list) {
        if (list.isEmpty()) {
            return "";
        } else {
            return "- " + list.stream().map(Object::toString)
                    .collect(Collectors.joining("  \n- "))
                    + "  \n";
        }
    }

    /**
     * Creates a string from the provided {@code list} that is rendered as an enumeration in the client widget.
     * <p>
     * Each element of the provided {@code list} is rendered with the following markdown pattern: {@code "[index] item
     * .toString()\n"}.
     * <p>
     * Non-string elements are rendered using {@link Object#toString()}.
     * <p>
     * Empty {@code list} is rendered as an empty string.
     *
     * @param list the list of elements to render as an enumeration
     * @return the string representing the enumeration
     * @throws NullPointerException if the provided {@code list} is {@code null}
     */
    public static String enumerateList(@NonNull List<?> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append("[")
                    .append(i)
                    .append("] ")
                    .append(list.get(i).toString())
                    .append("  \n");
        }
        return sb.toString();
    }

    /**
     * Disable constructor, this is an utility class.
     */
    private MessageUtils() {

    }
}
