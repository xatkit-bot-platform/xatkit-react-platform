package com.xatkit.plugins.react.platform.utils;

import lombok.Data;

/**
 * The event emitted by the client connects to the server.
 * <p>
 * This event contains additional information that cannot be set easily in the {@code connect} event.
 */
@Data
public class InitObject {

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
}
