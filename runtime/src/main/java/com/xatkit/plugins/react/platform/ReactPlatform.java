package com.xatkit.plugins.react.platform;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.xatkit.core.XatkitCore;
import com.xatkit.core.XatkitException;
import com.xatkit.core.server.XatkitServerUtils;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.chat.platform.ChatPlatform;
import com.xatkit.plugins.react.platform.action.PostMessage;
import com.xatkit.plugins.react.platform.action.Reply;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import com.xatkit.util.FileUtils;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.MessageFormat;

import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;
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
        if(configuration.containsKey(ReactUtils.REACT_CLIENT_URL_KEY)) {
            /*
             * The configuration contains a client URL value, we can directly use it to setup the origin of the
             * socket server.
             */
            String configurationOrigin = configuration.getString(ReactUtils.REACT_CLIENT_URL_KEY);
            if(configurationOrigin.equals("*")) {
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

        /*
         * Set the context (i.e. the socket.io {@code path} from the configuration property.
         */
        String serverBasePath = configuration.getString(ReactUtils.REACT_SERVER_BASE_PATH,
                ReactUtils.DEFAULT_REACT_SERVER_BASE_PATH);
        socketioConfiguration.setContext(serverBasePath);

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

        setSSLContext(socketioConfiguration, configuration);

        /*
         * Allow address reuses. This allows to restart Xatkit and reuse the same port without binding errors.
         */
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        socketioConfiguration.setSocketConfig(socketConfig);

        socketIOServer = new SocketIOServer(socketioConfiguration);
        socketIOServer.addConnectListener(socketIOClient -> Log.info("Connected"));
        socketIOServer.addDisconnectListener(socketIOClient -> Log.info("Disconnected"));
        this.socketIOServer.startAsync();
    }

    /**
     * Sets the SSL context in the provided {@code socketioConfiguration} from the given {@code configuration}.
     * <p>
     * This method checks if there is an SSL configuration in the provided {@code configuration} and sets the
     * {@code socketioConfiguration} accordingly. Note that if the provided {@code configuration} does not define an
     * SSL configuration the {@code socketioConfiguration} is not modified.
     *
     * @param socketioConfiguration the SocketIO configuration
     * @param configuration         the Xatkit configuration containing the SSL configuration
     * @throws XatkitException      if the provided keystore does not exist of if an error occurred when loading the
     *                              keystore content
     * @throws NullPointerException if the {@code configuration} contains a keystore location but does not
     *                              contain a store/key password
     */
    private void setSSLContext(com.corundumstudio.socketio.Configuration socketioConfiguration,
                               Configuration configuration) {
        String keystorePath = configuration.getString(XatkitServerUtils.SERVER_KEYSTORE_LOCATION_KEY);
        if (isNull(keystorePath)) {
            Log.info("No SSL context to load");
            return;
        }
        File keystoreFile = FileUtils.getFile(keystorePath, configuration);
        InputStream keystoreIs;
        try {
            keystoreIs = new FileInputStream(keystoreFile);
        } catch (FileNotFoundException e) {
            throw new XatkitException(MessageFormat.format("Cannot get the {0} from the provided keystore location " +
                    "{1}: the file does not exist", SSLContext.class.getSimpleName(), keystorePath), e);
        }
        String storePassword = configuration.getString(XatkitServerUtils.SERVER_KEYSTORE_STORE_PASSWORD_KEY);
        String keyPassword = configuration.getString(XatkitServerUtils.SERVER_KEYSTORE_KEY_PASSWORD_KEY);
        checkNotNull(storePassword, "Cannot load the provided keystore, property %s not set",
                XatkitServerUtils.SERVER_KEYSTORE_STORE_PASSWORD_KEY);
        checkNotNull(keyPassword, "Cannot load the provided keystore, property %s not set",
                XatkitServerUtils.SERVER_KEYSTORE_KEY_PASSWORD_KEY);
        socketioConfiguration.setKeyStore(keystoreIs);
        socketioConfiguration.setKeyStorePassword(storePassword);
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
