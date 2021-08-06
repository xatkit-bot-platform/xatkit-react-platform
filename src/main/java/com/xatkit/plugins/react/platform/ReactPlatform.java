package com.xatkit.plugins.react.platform;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.platform.action.RuntimeActionResult;
import com.xatkit.core.server.XatkitServerUtils;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.chat.platform.ChatPlatform;
import com.xatkit.plugins.chat.platform.io.ChatIntentProvider;
import com.xatkit.plugins.react.platform.action.PostMessage;
import com.xatkit.plugins.react.platform.action.Reply;
import com.xatkit.plugins.react.platform.action.ReplyAudio;
import com.xatkit.plugins.react.platform.action.ReplyFileMessage;
import com.xatkit.plugins.react.platform.action.ReplyLinkSnippet;
import com.xatkit.plugins.react.platform.action.ToggleDarkMode;
import com.xatkit.plugins.react.platform.action.Wait;
import com.xatkit.plugins.react.platform.io.ReactEventProvider;
import com.xatkit.plugins.react.platform.io.ReactIntentProvider;
import com.xatkit.plugins.react.platform.server.ReactRestEndpointsManager;
import com.xatkit.plugins.react.platform.utils.MessageUtils;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import org.apache.commons.configuration2.Configuration;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
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
     * {@inheritDoc}
     */
    @Override
    public ChatIntentProvider<? extends ChatPlatform> getChatIntentProvider() {
        return this.getReactIntentProvider();
    }

    /**
     * Initializes and returns a new {@link ReactEventProvider}.
     *
     * @return the {@link ReactEventProvider}
     */
    public ReactEventProvider getReactEventProvider() {
        return new ReactEventProvider(this);
    }

    /**
     * Initializes and returns a new {@link ReactIntentProvider}.
     *
     * @return the {@link ReactIntentProvider}
     */
    public ReactIntentProvider getReactIntentProvider() {
        return new ReactIntentProvider(this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method initializes the underlying socket server using the {@link ReactUtils#REACT_CLIENT_URL_KEY}
     * property specified in the {@code configuration}. If this property is not specified the platform assumes that
     * the page embedding the react client is served by the Xatkit server and initializes the socket server with
     * {@link XatkitServerUtils#SERVER_PUBLIC_URL_KEY} and {@link XatkitServerUtils#SERVER_PORT_KEY} properties.
     */
    @Override
    public void start(@NonNull XatkitBot xatkitBot, @NonNull Configuration configuration) {
        super.start(xatkitBot, configuration);
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
                new ReactRestEndpointsManager(this.xatkitBot.getXatkitServer(), configuration);
        restEndpointsManager.registerRestEndpoints();
    }

    /**
     * Formats the provided {@code list} into a markdown enumeration.
     * <p>
     * This method accepts any {@link List} and relies on the {@code toString} implementation of its elements.
     * <p>
     * <b>Deprecated</b>: use {@link MessageUtils#enumerateList(List)}.
     * @param context the current {@link StateContext}
     * @param list    the {@link List} to format
     * @return the enumeration formatted in markdown
     */
    @Deprecated
    public String enumerateList(@NonNull StateContext context, @NonNull List<?> list) {
        return MessageUtils.enumerateList(list);
    }

    /**
     * Formats the provided {@code list} into a markdown item list.
     * <p>
     * This method accepts any {@link List} and relies on the {@code toString} implementation of its elements.
     * <p>
     * <b>Deprecated</b>: use {@link MessageUtils#itemizeList(List)}.
     * @param context the current {@link StateContext}
     * @param list    the {@link List} to format
     * @return the item list formatted in markdown
     */
    @Deprecated
    public String itemizeList(@NonNull StateContext context, @NonNull List<?> list) {
        return MessageUtils.itemizeList(list);
    }

    /**
     * Posts the provided {@code message} in the given {@code channel}.
     *
     * @param context the current {@link StateContext}
     * @param message the message to post
     * @param channel the socket identifier to post the message to
     */
    public void postMessage(@NonNull StateContext context, @NonNull String message, @NonNull String channel) {
        PostMessage action = new PostMessage(this, context, message, channel);
        RuntimeActionResult result = action.call();
    }

    /**
     * Posts the provided {@code message} with the provided {@code buttons} in the given {@code channel}.
     *
     * @param context the current {@link StateContext}
     * @param message the message to post
     * @param buttons the list of button values to display to the user
     * @param channel the socket identifier to post the message to
     */
    public void postMessage(@NonNull StateContext context, @NonNull String message, @NonNull List<String> buttons,
                            @NonNull String channel) {
        PostMessage action = new PostMessage(this, context, message, buttons, channel);
        RuntimeActionResult result = action.call();
    }

    /**
     * Posts the provided {@code message} in the current channel.
     * <p>
     * The current channel is extracted from the provided {@code context}.
     *
     * @param context the current {@link StateContext}
     * @param message the message to post
     */
    public void reply(@NonNull StateContext context, @NonNull String message) {
        Reply action = new Reply(this, context, message);
        RuntimeActionResult result = action.call();
    }

    /**
     * Posts the provided {@code message} with the provided {@code buttons} in the current channel.
     * <p>
     * The current channel is extracted from the provided {@code context}.
     *
     * @param context the current {@link StateContext}
     * @param message the message to post
     * @param buttons the list of button values to display to the user
     */
    public void reply(@NonNull StateContext context, @NonNull String message, @NonNull List<String> buttons) {
        Reply action = new Reply(this, context, message, buttons);
        RuntimeActionResult result = action.call();
    }

    /**
     * Posts the provided {@code message} with a link to the provided {@code file} in the current channel.
     * <p>
     * This action takes care of uploading the file and creating a public URL for it.
     *
     * @param context the current {@link StateContext}
     * @param message the message to post
     * @param file    the {@link File} to post a link to
     */
    public void replyFileMessage(@NonNull StateContext context, @NonNull String message, @NonNull File file) {
        ReplyFileMessage action = new ReplyFileMessage(this, context, message, file);
        RuntimeActionResult result = action.call();
    }

    /**
     * Posts a link to the provided {@code file} in the current channel.
     * <p>
     * This action takes care of uploading the file and creating a public URL for it.
     *
     * @param context the current {@link StateContext}
     * @param file    the {@link File} to post a link to
     */
    public void replyFileMessage(@NonNull StateContext context, @NonNull File file) {
        ReplyFileMessage action = new ReplyFileMessage(this, context, file);
        RuntimeActionResult result = action.call();
    }

    /**
     * Posts a card pointing to the provided {@code link} in the current channel.
     *
     * @param context the current {@link StateContext}
     * @param title   the title of the card to display to the user
     * @param link    the link to embed in the displayed card
     * @param img     the image to set in the card
     */
    public void replyLinkSnippet(@NonNull StateContext context, @NonNull String title, @NonNull String link,
                                 @NonNull String img) {
        ReplyLinkSnippet action = new ReplyLinkSnippet(this, context, title, link, img);
        RuntimeActionResult result = action.call();
    }

    public void replyAudio(@NonNull StateContext context, @NonNull String src) {
        ReplyAudio action = new ReplyAudio(this, context, src);
        RuntimeActionResult result = action.call();
    }

    /**
     * Toggles the dark mode in the client widget.
     *
     * @param context the current {@link StateContext}
     */
    public void toggleDarkMode(@NonNull StateContext context) {
        ToggleDarkMode action = new ToggleDarkMode(this, context);
        RuntimeActionResult result = action.call();
    }

    /**
     * Tells the client widget to wait for a given {@code delay}.
     * <p>
     * The client widget can use this information to display loading dots or notify the user about the delay.
     *
     * @param context the current {@link StateContext}
     * @param delay   the delay to wait for
     */
    public void wait(@NonNull StateContext context, int delay) {
        Wait action = new Wait(this, context, delay);
        RuntimeActionResult result = action.call();
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
     * Retrieves the {@link StateContext} associated to the provided {@code socketId}.
     * <p>
     * This method looks for an existing <i>conversation ID</i> associated to the provided {@code socketId} and
     * returns the corresponding {@link StateContext} if it exists, or {@code null} if it cannot be found.
     *
     * @param socketId the socketId to create a {@link StateContext} from
     * @return the retrieved {@link StateContext}, or {@code null} if it cannot be found
     * @see #createStateContextForConversation(String, String) to create a {@link StateContext} for a given {@code
     * sessionId} and {@code conversationId}.
     */
    public @Nullable
    StateContext getStateContextForSocketId(@NonNull String socketId) {
        String conversationId = this.socketToConversationMap.get(socketId);
        if (isNull(conversationId)) {
            /*
             * The conversationId can be null if the event/intent provider ask for the session before it has been
             * associated to a conversation. In this case the provider should include some initialization code
             * relying on #createSessionForConversation to create a new XatkitSession.
             */
            return null;
        }
        return this.xatkitBot.getOrCreateContext(conversationId);
    }

    /**
     * Creates a {@link StateContext} for the provided {@code conversationId} hosted by the socket {@code socketId}.
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
     * @return the created {@link StateContext}
     */
    public @NonNull StateContext createStateContextForConversation(@NonNull String socketId,
                                                                   @Nullable String conversationId) {
        if (isNull(conversationId)) {
            conversationId = UUID.randomUUID().toString();
        }
        this.socketToConversationMap.put(socketId, conversationId);
        return this.xatkitBot.getOrCreateContext(conversationId);
    }

}
