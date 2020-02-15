package com.xatkit.plugins.react.platform.io;

import com.xatkit.core.platform.io.EventInstanceBuilder;
import com.xatkit.core.platform.io.RuntimeEventProvider;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.intent.EventInstance;
import com.xatkit.plugins.react.platform.ReactPlatform;
import org.apache.commons.configuration2.Configuration;

/**
 * A {@link RuntimeEventProvider} that fires non-textual events related to the Xatkit react chat component.
 * <p>
 * This class fires component life-cycle events, e.g. when the chat component is ready or when it disconnects from
 * the socket server.
 * <p>
 * Execution models using these events must listen to this provider in addition to the {@link ReactIntentProvider}
 * by adding the following <i>use</i> clause: {@code use provider ReactPlatform.ReactEventProvider}.
 */
public class ReactEventProvider extends RuntimeEventProvider<ReactPlatform> {

    /**
     * Constructs a {@link ReactEventProvider} from the provided {@code reactPlatform} and {@code configuration}.
     * <p>
     * This constructor registers dedicated listeners to the socket server that are triggered when a new client
     * connects/is disconnected from it, and creates the associated {@link EventInstance}.
     *
     * @param reactPlatform the {@link ReactPlatform} containing this provider
     * @param configuration the platform's {@link Configuration}
     */
    public ReactEventProvider(ReactPlatform reactPlatform, Configuration configuration) {
        super(reactPlatform, configuration);
        /*
         * Register the listener that creates the Client_Ready event.
         * This event is fired every time the client connects to the socket server.
         */
        this.runtimePlatform.getSocketIOServer().addConnectListener(socketIOClient -> {
            String channel = socketIOClient.getSessionId().toString();
            XatkitSession session = this.runtimePlatform.createSessionFromChannel(channel);
            EventInstance eventInstance = EventInstanceBuilder.newBuilder(this.xatkitCore.getEventDefinitionRegistry())
                    .setEventDefinitionName("Client_Ready")
                    .setOutContextValue("channel", channel)
                    .setOutContextValue("ready", "true")
                    .build();
            this.sendEventInstance(eventInstance, session);
        });
        /*
         * Register the listener that creates the Client_Closed event.
         * This event is fired every time the client disconnects from the socket server.
         */
        this.runtimePlatform.getSocketIOServer().addDisconnectListener(socketIOClient -> {
            String channel = socketIOClient.getSessionId().toString();
            XatkitSession session = this.runtimePlatform.createSessionFromChannel(channel);
            EventInstance eventInstance = EventInstanceBuilder.newBuilder(this.xatkitCore.getEventDefinitionRegistry())
                    .setEventDefinitionName("Client_Closed")
                    .setOutContextValue("channel", channel)
                    .setOutContextValue("closed", "true")
                    .build();
            this.sendEventInstance(eventInstance, session);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        /*
         * Do nothing, the socket server is started asynchronously.
         */
    }
}
