package com.xatkit.plugins.react.platform.utils;

import kotlin.internal.contracts.Returns;
import lombok.Data;

/**
 * Represents a command asking the client to display/hide the message loader.
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
