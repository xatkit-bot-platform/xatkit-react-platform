package com.xatkit.plugins.react.platform;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.xatkit.core.XatkitCore;
import com.xatkit.core.server.XatkitServerUtils;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.chat.platform.ChatPlatform;
import com.xatkit.plugins.react.platform.action.PostMessage;
import com.xatkit.plugins.react.platform.action.Reply;
import com.xatkit.plugins.react.platform.server.ReactRestEndpointsManager;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import org.apache.commons.configuration2.Configuration;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * A {@link ChatPlatform} class that interacts with the
 * <a href="https://github.com/xatkit-bot-platform/xatkit-react">Xatkit React component</a>.
 * <p>
 * This platform creates a server that accepts socket connexions from the client application. Messages are received
 * in real-time, and replies are sent to the client using a push mechanism.
 * <p>
 * This platform provides the following actions:
 * <ul>
 * <li>{@link Reply}: replies to a user input</li>
 * <li>{@link PostMessage}: post a message to a given channel (i.e. window running a xatkit-react instance)</li>
 * </ul>
 * <p>
 * This class is part of xatkit's core paltform, and can be used in an execution model by importing the
 * <i>ReactPlatform</i> package.
 */
public class ReactPlatform extends ChatPlatform {

    /**
     * The socket server used to receive and send messages.
     */
    private SocketIOServer socketIOServer;

    /**
     * Stores the mapping from {@code socketId} to {@code conversationId}.
     * <p>
     * This mapping allows to retrieve the conversation associated to a given socket. Adding a new
     * socket/conversation entry allows to continue an existing conversation in a different socket connection (e.g.
     * when the client reloads the page).
     */
    private Map<String, String> socketToConversationMap = new HashMap<>();

    /**
     * Constructs a new {@link ReactPlatform} from the provided {@link XatkitCore} and {@link Configuration}.
     * <p>
     * This constructor initializes the underlying socket server using the {@link ReactUtils#REACT_CLIENT_URL_KEY}
     * property specified in the {@link Configuration}. If this property is not specified the {@link ReactPlatform}
     * assumes that the page embedding the react client is served by the Xatkit server and initializes the socket
     * server with the {@link XatkitServerUtils#SERVER_PUBLIC_URL_KEY} and {@link XatkitServerUtils#SERVER_PORT_KEY}
     * properties.
     *
     * @param xatkitCore    the {@link XatkitCore} instance associated to this runtimePlatform
     * @param configuration the platform's {@link Configuration} containing the port of the socket server
     * @throws NullPointerException if the provided {@code xatkitCore} or {@code configuration} is {@code null}
     */
    public ReactPlatform(XatkitCore xatkitCore, Configuration configuration) {
        super(xatkitCore, configuration);
        /*
         * Register the shutdown hook first to make sure it is registered even if the constructor throws an exception.
         */
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        int socketServerPort = configuration.getInt(ReactUtils.REACT_SERVER_PORT_KEY,
                ReactUtils.DEFAULT_REACT_SERVER_PORT);
        /*
         * Set to null by default: this corresponds to the * origin.
         */
        String origin = null;
        if (configuration.containsKey(ReactUtils.REACT_CLIENT_URL_KEY)) {
            /*
             * The configuration contains a client URL value, we can directly use it to setup the origin of the
             * socket server.
             */
            String configurationOrigin = configuration.getString(ReactUtils.REACT_CLIENT_URL_KEY);
            if (configurationOrigin.equals("*")) {
                /*
                 * We need to set the origin to null otherwise the Access-Control-Allow-Credentials header is set to
                 * true and the browser will deny access to the resource. This is a workaround for a non-intuitive
                 * behavior in netty-socketio, see this issue for more information: https://github
                 * .com/mrniko/netty-socketio/issues/400.
                 */
                origin = null;
            } else {
                origin = configurationOrigin;
            }
        }

        com.corundumstudio.socketio.Configuration socketioConfiguration =
                new com.corundumstudio.socketio.Configuration();

        socketioConfiguration.setPort(socketServerPort);
        /*
         * The URL where the chatbox is displayed. Setting this is required to avoid CORS issues.
         * Note: wildcards don't work here.
         */
        socketioConfiguration.setOrigin(origin);
        /*
         * Use random sessions to avoid sharing the same session ID between multiple tabs (see https://github
         * .com/mrniko/netty-socketio/issues/617).
         */
        socketioConfiguration.setRandomSession(true);

        /*
         * Allow address reuses. This allows to restart Xatkit and reuse the same port without binding errors.
         */
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        socketioConfiguration.setSocketConfig(socketConfig);

        socketIOServer = new SocketIOServer(socketioConfiguration);
        socketIOServer.addConnectListener(socketIOClient -> Log.debug("Client connected"));
        socketIOServer.addDisconnectListener(socketIOClient -> Log.debug("Client disconnected"));
        this.socketIOServer.startAsync();

        ReactRestEndpointsManager restEndpointsManager =
                new ReactRestEndpointsManager(this.xatkitCore.getXatkitServer(), configuration);
        restEndpointsManager.registerRestEndpoints();
    }

    /**
     * Returns the socket server used to receive and send messages.
     *
     * @return the socket server used to receive and send messages
     */
    public SocketIOServer getSocketIOServer() {
        return this.socketIOServer;
    }

    /**
     * Stops the underlying socket server.
     */
    @Override
    public void shutdown() {
        if (nonNull(socketIOServer)) {
            Log.info("Stopping SocketIO server");
            this.socketIOServer.stop();
            this.socketIOServer = null;
        }
    }

    /**
     * Retrieves the {@link XatkitSession} associated to the provided {@code socketId}.
     * <p>
     * This method looks for an existing <i>conversation ID</i> associated to the provided {@code socketId} and
     * returns the corresponding {@link XatkitSession} if it exists, or {@code null} if it cannot be found.
     *
     * @param socketId the socketId to create a {@link XatkitSession} from
     * @return the retrieved {@link XatkitSession}, or {@code null} if it cannot be found
     * @see #createSessionForConversation(String, String) to create a {@link XatkitSession} for a given {@code
     * sessionId} and {@code conversationId}.
     */
    public @Nullable
    XatkitSession getSessionForSocketId(@NonNull String socketId) {
        String conversationId = this.socketToConversationMap.get(socketId);
        if (isNull(conversationId)) {
            /*
             * The conversationId can be null if the event/intent provider ask for the session before it has been
             * associated to a conversation. In this case the provider should include some initialization code
             * relying on #createSessionForConversation to create a new XatkitSession.
             */
            return null;
        }
        return this.xatkitCore.getOrCreateXatkitSession(conversationId);
    }

    /**
     * Creates a {@link XatkitSession} for the provided {@code conversationId} hosted by the socket {@code socketId}.
     * <p>
     * Conversations typically live longer than socket connections (e.g. when the user reloads the page where the bot
     * is hosted the socket changes but the conversation is the same), this implies that the {@link ReactPlatform}
     * can only create sessions for a given {@code socketId}/{@code conversationId} pair, and will update the mapping
     * when new socket connections are opened and referencing an existing conversation.
     * <p>
     * <b>Note</b>: the provided {@code conversationId} can be {@code null} (i.e. a totally new conversation is
     * created). In this case the method will create a new {@code conversationId} and affect a random identifier to
     * it.
     *
     * @param socketId       the identifier of the socket connection hosting the conversation
     * @param conversationId the identifier of the conversation to create a session for
     * @return the created {@link XatkitSession}
     */
    public @NonNull XatkitSession createSessionForConversation(@NonNull String socketId,
                                                               @Nullable String conversationId) {
        if (isNull(conversationId)) {
            conversationId = UUID.randomUUID().toString();
        }
        this.socketToConversationMap.put(socketId, conversationId);
        return this.xatkitCore.getOrCreateXatkitSession(conversationId);
    }

}
