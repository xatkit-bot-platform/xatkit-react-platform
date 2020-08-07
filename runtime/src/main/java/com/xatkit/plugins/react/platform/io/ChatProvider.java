package com.xatkit.plugins.react.platform.io;

import com.xatkit.plugins.react.platform.ReactPlatform;

/**
 * A generic React {@link com.xatkit.plugins.chat.platform.io.ChatIntentProvider}.
 * <p>
 * This class wraps the {@link ReactIntentProvider} and allows to use it as a generic <i>ChatIntentProvider</i> from
 * the <i>ChatPlatform</i>.
 *
 * @see ReactIntentProvider
 */
public class ChatProvider extends ReactIntentProvider {

    /**
     * Constructs a {@link ChatProvider} and binds it to the provided {@code reactPlatform}.
     *
     * @param reactPlatform the {@link ReactPlatform} managing this provider
     */
    public ChatProvider(ReactPlatform reactPlatform) {
        super(reactPlatform);
    }
}
