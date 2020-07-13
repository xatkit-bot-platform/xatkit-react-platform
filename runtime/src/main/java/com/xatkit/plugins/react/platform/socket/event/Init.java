package com.xatkit.plugins.react.platform.socket.event;

import lombok.Data;

/**
 * The event emitted by the client connects to the server.
 * <p>
 * This event contains additional information that cannot be set easily in the {@code connect} event.
 */
@Data
public class Init {

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
     * The identifier of the conversation between the user and the bot.
     * <p>
     * If this value is filled the client is requesting a specific conversation to the server. If this value is
     * {@code null} this means that the client is asking for a fresh conversation (with an empty session).
     */
    private String conversationId;
}
