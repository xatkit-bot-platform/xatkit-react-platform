package com.xatkit.plugins.react.platform.utils;

import com.xatkit.plugins.chat.ChatUtils;

/**
 * An utility interface that holds xatkit-react related helpers and context keys.
 */
public interface ReactUtils extends ChatUtils {

    /**
     * The {@link org.apache.commons.configuration2.Configuration} key to store the URL of the react client. This URL
     * is used to set the CORS headers of the socket server and allow cross-origin access to the socket endpoint.
     * <p>
     * <b>Note</b>: this configuration key does not have a default value. If the client URL is not provided the
     * {@link com.xatkit.plugins.react.platform.ReactPlatform} assumes that the client is running on {@code
     * <server_url>:<port_number>}, where {@code <server_url>} is provided by
     * {@link com.xatkit.core.server.XatkitServerUtils#SERVER_PUBLIC_URL_KEY}, and {@code <port_number>} is provided
     * by {@link com.xatkit.core.server.XatkitServerUtils#SERVER_PORT_KEY}.
     * <p>
     * <b>Security</b>: this property key ensures that CORS headers will be set for the specified clients, and should
     * prevent bots installed in other locations to access the Xatkit server.
     */
    String REACT_CLIENT_URL_KEY = "xatkit.react.client.url";

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
     * The {@link org.apache.commons.configuration2.Configuration} key to store the public URL of the
     * {@link com.xatkit.plugins.react.platform.ReactPlatform}'s server.
     * <p>
     * This property is used to set the server location in the generated HTML file when accessing the bot through
     * {@code /admin}.
     */
    String REACT_SERVER_PUBLIC_URL = "xatkit.react.public_url";

    /**
     * The {@link org.apache.commons.configuration2.Configuration} key to enable/disable the testing page.
     * <p>
     * The testing page is located at {@code xatkit.server.public_url/admin}. The public URL {@code localhost:5000}
     * is used by default if no public URL is specified in the configuration.
     * <p>
     * The default value of this property is {@code true} (see {@link #DEFAULT_REACT_ENABLE_TESTING_PAGE}).
     *
     * @see #DEFAULT_REACT_ENABLE_TESTING_PAGE
     */
    String REACT_ENABLE_TESTING_PAGE = "xatkit.react.enable_testing_page";

    /**
     * The default value of the {@link #REACT_ENABLE_TESTING_PAGE}
     * {@link org.apache.commons.configuration2.Configuration} key.
     */
    boolean DEFAULT_REACT_ENABLE_TESTING_PAGE = true;

    /**
     * The name of the platform data entry used to store React-related information.
     */
    String REACT_CONTEXT_KEY = "react";

    /**
     * The name of the platform data entry used to store the React hostname information.
     */
    String REACT_HOSTNAME_CONTEXT_KEY = REACT_CONTEXT_KEY + ".hostname";

    /**
     * The name of the platform data entry used to store the React url information.
     */
    String REACT_URL_CONTEXT_KEY = REACT_CONTEXT_KEY + ".url";

    /**
     * The name of the platform data entry used to store the React origin information.
     */
    String REACT_ORIGIN_CONTEXT_KEY = REACT_CONTEXT_KEY + ".origin";
}
