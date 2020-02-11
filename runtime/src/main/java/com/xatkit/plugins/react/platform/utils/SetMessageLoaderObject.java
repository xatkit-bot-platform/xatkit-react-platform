package com.xatkit.plugins.react.platform.utils;

/**
 * Represents a command asking the client to display/hide the message loader.
 */
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

    /**
     * Sets whether the message loader should be displayed or hidden.
     *
     * @param enableLoader whether the message loader should be displayed or hidden
     */
    public void setEnableLoader(boolean enableLoader) {
        this.enableLoader = enableLoader;
    }

    /**
     * Returns whether the message loader should be displayed or hidden.
     *
     * @return whether the message loader should be displayed or hidden
     */
    public boolean getEnableLoader() {
        return this.enableLoader;
    }
}
