package com.xatkit.plugins.react.platform.utils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Represents a message sent by the platform that can be complemented with quick buttons.
 */
public class BotMessageObject extends MessageObject {

    /**
     * The values of the <i>quick buttons</i> to print to the user.
     */
    private List<QuickButtonValue> quickButtonValues;

    /**
     * Constructs a {@link BotMessageObject} from the provided {@code message}, {@code username}, and {@code
     * quickButtonValues}.
     *
     * @param message           the message to print to the user
     * @param username          the name of the user to send a message to
     * @param quickButtonValues the quick buttons to print to the user
     */
    public BotMessageObject(String message, String username, @Nullable List<QuickButtonValue> quickButtonValues) {
        super(message, username);
        this.quickButtonValues = isNull(quickButtonValues) ? Collections.emptyList() : quickButtonValues;
    }

    /**
     * Constructs a {@link BotMessageObject} from the provided {@code message} and {@code username}.
     * <p>
     * This constructor is similar to {@code new BotMessageObject(message, username, Collections.emptyList())}, and
     * will be translated into a simple message without quick buttons.
     *
     * @param message  the message to print to the user
     * @param username the name of the user to send a message to
     */
    public BotMessageObject(String message, String username) {
        this(message, username, Collections.emptyList());
    }

    /**
     * Sets the quick button descriptors for this message.
     *
     * @param quickButtonValues the quick button descriptors
     */
    public void setQuickButtonValues(List<QuickButtonValue> quickButtonValues) {
        this.quickButtonValues = quickButtonValues;
    }

    /**
     * Returns the quick button descriptors for this message.
     *
     * @return the quick button descriptors for this message
     */
    public List<QuickButtonValue> getQuickButtonValues() {
        return this.quickButtonValues;
    }
}
