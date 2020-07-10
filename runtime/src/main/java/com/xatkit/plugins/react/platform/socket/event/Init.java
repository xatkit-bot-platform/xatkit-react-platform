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

    private String conversationId;
}
