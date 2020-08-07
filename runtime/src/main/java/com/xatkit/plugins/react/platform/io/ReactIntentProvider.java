package com.xatkit.plugins.react.platform.io;

import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.execution.StateContext;
import com.xatkit.intent.RecognizedIntent;
import com.xatkit.plugins.chat.ChatUtils;
import com.xatkit.plugins.chat.platform.io.ChatIntentProvider;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.socket.SocketEventTypes;
import com.xatkit.plugins.react.platform.socket.action.InitConfirm;
import com.xatkit.plugins.react.platform.socket.event.Init;
import com.xatkit.plugins.react.platform.socket.event.UserMessageReceived;
import com.xatkit.plugins.react.platform.socket.event.UserQuickButtonSelected;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

import static java.util.Objects.isNull;

/**
 * A {@link ChatIntentProvider} that receives message through the socket server and translates them into
 * {@link RecognizedIntent}s.
 */
public class ReactIntentProvider extends ChatIntentProvider<ReactPlatform> {

    /**
     * Constructs a {@link ReactIntentProvider} and binds it to the provided {@code reactPlatform}.
     *
     * @param reactPlatform the {@link ReactPlatform} managing this provider
     */
    public ReactIntentProvider(ReactPlatform reactPlatform) {
        super(reactPlatform);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method registers the listeners on the socker server to receive user interactions (messages, button
     * clicks, etc) and translate them into {@link RecognizedIntent}s. Received interactions won't be translated
     * until this method is invoked.
     */
    @Override
    public void start(Configuration configuration) {
        super.start(configuration);
        this.runtimePlatform.getSocketIOServer().addEventListener(SocketEventTypes.USER_MESSAGE.label,
                UserMessageReceived.class, (socketIOClient, messageObject, ackRequest) -> {
                    Log.debug("Received message {0}", messageObject.getMessage());
                    Log.debug("Session ID: {0}", socketIOClient.getSessionId());
                    String username = messageObject.getUsername();
                    String channel = socketIOClient.getSessionId().toString();
                    String rawMessage = messageObject.getMessage();
                    StateContext context = this.getRuntimePlatform().getSessionForSocketId(channel);
                    RecognizedIntent recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(rawMessage,
                            context, this.getRuntimePlatform().getXatkitCore());
                    setSessionContexts(context, username, channel, rawMessage);
                    this.sendEventInstance(recognizedIntent, context);
                });
        this.runtimePlatform.getSocketIOServer().addEventListener(SocketEventTypes.USER_BUTTON_CLICK.label,
                UserQuickButtonSelected.class, ((socketIOClient, quickButtonEventObject, ackRequest) -> {
                    Log.debug("Received click");
                    Log.debug("Session ID: {0}", socketIOClient.getSessionId());
                    String username = quickButtonEventObject.getUsername();
                    String channel = socketIOClient.getSessionId().toString();
                    String rawMessage = quickButtonEventObject.getSelectedValue();
                    StateContext context = this.getRuntimePlatform().getSessionForSocketId(channel);
                    RecognizedIntent recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(rawMessage,
                            context, this.getRuntimePlatform().getXatkitCore());
                    setSessionContexts(context, username, channel, rawMessage);
                    this.sendEventInstance(recognizedIntent, context);
                }));
        this.runtimePlatform.getSocketIOServer().addEventListener(SocketEventTypes.INIT.label, Init.class,
                (socketIOClient, initObject, ackRequest) -> {
                    String socketId = socketIOClient.getSessionId().toString();
                    StateContext context = this.runtimePlatform.getSessionForSocketId(socketId);
                    if (isNull(context)) {
                        String conversationId = initObject.getConversationId();
                        Log.debug("Client requested conversation {0}", conversationId);
                        context = this.runtimePlatform.createSessionForConversation(socketId, conversationId);
                        context.setOrigin(initObject.getOrigin());
                        socketIOClient.sendEvent(SocketEventTypes.INIT_CONFIRM.label,
                                new InitConfirm(context.getContextId()));
                    }
                    /*
                     * The session already exists, no need to send an ack event.
                     */
                });
    }

    /**
     * Sets the {@code session}'s context and context parameter from the provided {@code username}, {@code channel},
     * and {@code rawMessage}.
     * <p>
     * This method sets both the {@code react} context parameters (from {@link ReactUtils}) and the {@code chat} one
     * (from {@link ChatUtils}).
     *
     * @param context    the {@link StateContext} to set the contexts of
     * @param username   the username value to set in the session
     * @param channel    the channel value to set in the session
     * @param rawMessage the raw message value to set in the session
     */
    private void setSessionContexts(StateContext context, String username, String channel, String rawMessage) {
        /*
         * TODO remove this cast, this method should handle any StateContext.
         */
        XatkitSession session = (XatkitSession) context;
        session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                ReactUtils.CHAT_USERNAME_CONTEXT_KEY, username);
        session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                ReactUtils.CHAT_CHANNEL_CONTEXT_KEY, channel);
        session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                ReactUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY, rawMessage);
        /*
         * This provider extends ChatIntentProvider, and must set chat-related context values.
         */
        session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                ChatUtils.CHAT_USERNAME_CONTEXT_KEY, username);
        session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                ChatUtils.CHAT_CHANNEL_CONTEXT_KEY, channel);
        session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                ChatUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY, rawMessage);
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
