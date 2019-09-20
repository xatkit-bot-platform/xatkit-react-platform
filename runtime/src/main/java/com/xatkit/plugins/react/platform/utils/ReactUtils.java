package com.xatkit.plugins.react.platform.utils;

import com.xatkit.core.session.RuntimeContexts;
import com.xatkit.plugins.chat.ChatUtils;

/**
 * An utility interface that holds xatkit-react related helpers and context keys.
 */
public interface ReactUtils extends ChatUtils {

    /**
     * The {@link org.apache.commons.configuration2.Configuration} key to store the port of the
     * {@link com.xatkit.plugins.react.platform.ReactPlatform}'s socket server.
     */
    String REACT_SERVER_PORT_KEY = "xatkit.react.port";

    /**
     * The default value of the {@link #REACT_SERVER_PORT_KEY}
     * {@link org.apache.commons.configuration2.Configuration} key.
     */
    int DEFAULT_REACT_SERVER_PORT = 5001;

    /**
     * The {@link RuntimeContexts} key used to store React-related information.
     */
    String REACT_CONTEXT_KEY = "react";
}
