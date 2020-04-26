package com.xatkit.plugins.react.platform.utils;

import lombok.Data;

/**
 * Represents a textual message received or sent by the platform.
 * <p>
 * This class is automatically instantiated and populated by Jackson when a {@link SocketEventTypes#USER_MESSAGE} is
 * received.
 * <p>
 * This class can also be used to send bot messages that will be printed back as JSON by Jackson.
 */
@Data
public class MessageObject {

    /**
     * The message.
     */
    private String message;

    /**
     * The name of the user who sent the message.
     */
    private String username;

    /**
     * The hostname of the page where the bot is accessed.
     */
    private String hostname;

    /**
     * The url of the page where the bot is accessed.
     */
    private String url;

    /**
     * The origin of the page where the bot is accessed.
     */
    private String origin;

    /**
     * Constructs an empty {@link MessageObject}.
     * <p>
     * The default constructor is required by Jackson (initialization is done through the setters).
     */
    public MessageObject() {
    }

    /**
     * Constructs a new {@link MessageObject} from the provided {@code message} and {@code username}.
     * <p>
     * This constructor is typically called to create {@link MessageObject}s to send to the client. Received messages
     * are automatically parsed by Jackson and reified into {@link MessageObject}s using the default constructor.
     *
     * @param message  the message
     * @param username the name of the user sending the message
     */
    public MessageObject(String message, String username) {
        this.message = message;
        this.username = username;
    }
}
