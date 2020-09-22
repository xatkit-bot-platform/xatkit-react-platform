package com.xatkit.plugins.react.platform.socket.action;

import lombok.Data;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Tells the bot UI to render a message with the provided text and optional quick buttons.
 */
@Data
public class SendBotMessage {

    /**
     * The name of the bot.
     */
    private String username;

    /**
     * The message to render.
     */
    private String message;

    /**
     * The optional quick button descriptors to render.
     */
    private List<QuickButtonDescriptor> quickButtonValues;

    /**
     * Creates a {@link SendBotMessage} action with the provided {@code username}, {@code message}, and optional
     * {@code quickButtonValues}.
     *
     * @param username          the name of the bot
     * @param message           the message to render
     * @param quickButtonDescriptors the optional quick button descriptors to render
     */
    public SendBotMessage(String username, String message, @Nullable List<QuickButtonDescriptor> quickButtonDescriptors) {
        this.username = username;
        this.message = message;
        this.quickButtonValues = quickButtonDescriptors;
    }

    /**
     * Creates a {@link SendBotMessage} action with the provided {@code username} and {@code message}.
     *
     * @param username the name of the bot
     * @param message  the message to render
     */
    public SendBotMessage(String username, String message) {
        this(username, message, Collections.emptyList());
    }
}
