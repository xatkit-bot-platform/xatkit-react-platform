package com.xatkit.plugins.react.platform.io;

import com.xatkit.core.server.JsonRestHandler;
import com.xatkit.intent.RecognizedIntent;
import com.xatkit.plugins.chat.platform.io.WebhookChatIntentProvider;
import com.xatkit.plugins.react.platform.ReactPlatform;
import org.apache.commons.configuration2.Configuration;

/**
 * A {@link WebhookChatIntentProvider} that receives xatkit-react messages and convert them into
 * {@link RecognizedIntent}.
 * <p>
 * This class should not be initialized reflexively by the core framework: it is created by the
 * {@link ReactIntentProvider} to receive messages and convert them to {@link RecognizedIntent}.
 */
class ReactIntentProvider extends WebhookChatIntentProvider<ReactPlatform, JsonRestHandler> {

    private static final String ENDPOINT_URI = "/react";

    /**
     * Construct a new {@link ReactIntentProvider} from the provided {@code runtimePlatform} and {@code configuration}.
     *
     * @param runtimePlatform the {@link ReactPlatform} containing the {@link ReactIntentProvider}
     * @param configuration   the platform's {@link Configuration}
     */
    public ReactIntentProvider(ReactPlatform runtimePlatform, Configuration configuration) {
        super(runtimePlatform, configuration);
    }

    @Override
    public String getEndpointURI() {
        return ENDPOINT_URI;
    }

    @Override
    protected JsonRestHandler createRestHandler() {
        return new ReactRestHandler(this);
    }

}
