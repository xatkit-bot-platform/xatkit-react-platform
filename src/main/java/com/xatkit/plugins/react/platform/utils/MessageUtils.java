package com.xatkit.plugins.react.platform.utils;

import lombok.NonNull;

import java.text.MessageFormat;

/**
 * Utility class for message formatting.
 */
public class MessageUtils {

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
        return MessageFormat.format("[{0}](##{1})", name, value.replaceAll(" ", "-"));
    }
}
