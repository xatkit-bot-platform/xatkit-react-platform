package com.xatkit.plugins.react.platform;

import com.corundumstudio.socketio.SocketIOServer;
import com.xatkit.core.XatkitCore;
import com.xatkit.core.server.XatkitServerUtils;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.chat.platform.ChatPlatform;
import com.xatkit.plugins.react.platform.action.PostMessage;
import com.xatkit.plugins.react.platform.action.Reply;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

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
     * Constructs a new {@link ReactPlatform} from the provided {@link XatkitCore} and {@link Configuration}.
     * <p>
     * This constructor initializes the underlying socket server using the port specified in the provided
     * {@link Configuration}.
     *
     * @param xatkitCore    the {@link XatkitCore} instance associated to this runtimePlatform
     * @param configuration the platform's {@link Configuration} containing the port of the socket server
     * @throws NullPointerException if the provided {@code xatkitCore} or {@code configuration} is {@code null}
     */
    public ReactPlatform(XatkitCore xatkitCore, Configuration configuration) {
        super(xatkitCore, configuration);
        int socketServerPort = configuration.getInt(ReactUtils.REACT_SERVER_PORT_KEY,
                ReactUtils.DEFAULT_REACT_SERVER_PORT);
        String originLocation = configuration.getString(XatkitServerUtils.SERVER_PUBLIC_URL_KEY,
                XatkitServerUtils.DEFAULT_SERVER_LOCATION);
        int originPort = configuration.getInt(XatkitServerUtils.SERVER_PORT_KEY,
                XatkitServerUtils.DEFAULT_SERVER_PORT);
        String origin = originLocation + ":" + Integer.toString(originPort);
        com.corundumstudio.socketio.Configuration socketioConfiguration =
                new com.corundumstudio.socketio.Configuration();
        /*
         * TODO Doesn't seem to be needed for the moment, needs to be tested when deployed on a server.
         */
//        socketioConfiguration.setHostname("localhost");
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
        socketIOServer = new SocketIOServer(socketioConfiguration);
        socketIOServer.addConnectListener(socketIOClient -> Log.info("Connected"));
        socketIOServer.addDisconnectListener(socketIOClient -> Log.info("Disconnected"));
        this.socketIOServer.startAsync();
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
        this.socketIOServer.stop();
    }

    /**
     * Creates a {@link XatkitSession} from the provided {@code channel}.
     * <p>
     * This method ensures that the same {@link XatkitSession} is returned for the same {@code channel}.
     *
     * @param channel the channel to create a {@link XatkitSession} from
     * @return the created {@link XatkitSession}
     */
    public XatkitSession createSessionFromChannel(String channel) {
        return this.xatkitCore.getOrCreateXatkitSession(channel);
    }

}
