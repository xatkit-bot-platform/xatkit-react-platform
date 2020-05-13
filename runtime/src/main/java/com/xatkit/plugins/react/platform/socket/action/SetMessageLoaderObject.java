package com.xatkit.plugins.react.platform.socket.action;

import lombok.Data;

/**
 * Tells the bot UI to display/hide the message loader.
 */
@Data
public class SetMessageLoaderObject {

    /**
     * Whether the loader should be displayed or hidden.
     */
    private boolean enableLoader;

    /**
     * Constructs an empty {@link SetMessageLoaderObject}.
     */
    public SetMessageLoaderObject() {

    }

    /**
     * Constructs a {@link SetMessageLoaderObject} instance with the provided {@code enableLoader}.
     *
     * @param enableLoader whether the message loader should be displayed or hidden
     */
    public SetMessageLoaderObject(boolean enableLoader) {
        this.enableLoader = enableLoader;
    }
}
