package com.xatkit.plugins.react.platform.io;

import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.intent.RecognizedIntent;
import com.xatkit.plugins.chat.ChatUtils;
import com.xatkit.plugins.chat.platform.io.ChatIntentProvider;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.utils.MessageObject;
import com.xatkit.plugins.react.platform.utils.QuickButtonEventObject;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import com.xatkit.plugins.react.platform.utils.SocketEventTypes;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

/**
 * A {@link ChatIntentProvider} that receives message through the socket server and translates them into
 * {@link RecognizedIntent}s.
 */
public class ReactIntentProvider extends ChatIntentProvider<ReactPlatform> {

    /**
     * Constructs a {@link ReactIntentProvider} from the provided {@code reactPlatform} and {@code configuration}.
     * <p>
     * This constructor registers the listeners to the socket server that receives user interactions (messages,
     * button clicks, etc) and translates them into {@link RecognizedIntent}s using the {@link IntentRecognitionHelper}.
     *
     * @param reactPlatform the {@link ReactPlatform} containing this provider
     * @param configuration the platform's {@link Configuration}
     * @see IntentRecognitionHelper
     */
    public ReactIntentProvider(ReactPlatform reactPlatform, Configuration configuration) {
        super(reactPlatform, configuration);
        this.runtimePlatform.getSocketIOServer().addEventListener(SocketEventTypes.USER_MESSAGE.label,
                MessageObject.class, (socketIOClient, messageObject, ackRequest) -> {
                    Log.info("Received message {0}", messageObject.getMessage());
                    Log.info("Session ID: {0}", socketIOClient.getSessionId());
                    String username = messageObject.getUsername();
                    String channel = socketIOClient.getSessionId().toString();
                    String rawMessage = messageObject.getMessage();
                    XatkitSession session = this.getRuntimePlatform().createSessionFromChannel(channel);
                    RecognizedIntent recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(rawMessage,
                            session, this.getRuntimePlatform().getXatkitCore());
                    setSessionContexts(session, username, channel, rawMessage);
                    this.sendEventInstance(recognizedIntent, session);
                });
        this.runtimePlatform.getSocketIOServer().addEventListener(SocketEventTypes.USER_BUTTON_CLICK.label,
                QuickButtonEventObject.class, ((socketIOClient, quickButtonEventObject, ackRequest) -> {
                    Log.info("Received click");
                    Log.info("Session ID: {0}", socketIOClient.getSessionId());
                    String username = quickButtonEventObject.getUsername();
                    String channel = socketIOClient.getSessionId().toString();
                    String rawMessage = quickButtonEventObject.getSelectedValue();
                    XatkitSession session = this.getRuntimePlatform().createSessionFromChannel(channel);
                    RecognizedIntent recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(rawMessage,
                            session, this.getRuntimePlatform().getXatkitCore());
                    setSessionContexts(session, username, channel, rawMessage);
                    this.sendEventInstance(recognizedIntent, session);
                }));
    }

    /**
     * Sets the {@code session}'s context and context parameter from the provided {@code username}, {@code channel},
     * and {@code rawMessage}.
     * <p>
     * This method sets both the {@code react} context parameters (from {@link ReactUtils}) and the {@code chat} one
     * (from {@link ChatUtils}).
     *
     * @param session    the {@link XatkitSession} to set the contexts of
     * @param username   the username value to set in the session
     * @param channel    the channel value to set in the session
     * @param rawMessage the raw message value to set in the session
     */
    private void setSessionContexts(XatkitSession session, String username, String channel, String rawMessage) {
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
