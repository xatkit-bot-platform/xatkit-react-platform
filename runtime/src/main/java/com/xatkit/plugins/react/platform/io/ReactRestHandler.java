package com.xatkit.plugins.react.platform.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xatkit.core.platform.io.IntentRecognitionHelper;
import com.xatkit.core.server.JsonRestHandler;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.intent.RecognizedIntent;
import com.xatkit.plugins.chat.ChatUtils;
import com.xatkit.plugins.react.platform.ReactUtils;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

public class ReactRestHandler extends JsonRestHandler {

    private static final String XATKIT_REACT_HEADER = "xatkit-react";

    private ReactIntentProvider provider;

    public ReactRestHandler(ReactIntentProvider provider) {
        super();
        this.provider = provider;
    }

    @Override
    public List<String> getAccessControlAllowHeaders() {
        return Collections.singletonList(XATKIT_REACT_HEADER);
    }

    @Override
    public JsonElement handleParsedContent(@Nonnull List<Header> headers, @Nonnull List<NameValuePair> params,
                                           @Nullable JsonElement content) {
        if(nonNull(this.getHeaderValue(headers, XATKIT_REACT_HEADER))) {
            JsonObject jsonContent = content.getAsJsonObject();
            String username = jsonContent.get("username").getAsString();
            String channel = jsonContent.get("channel").getAsString();
            JsonElement message = jsonContent.get("message");
            if (nonNull(message)) {
                String textMessage = message.getAsString();
                XatkitSession session = provider.getRuntimePlatform().createSessionFromChannel(channel);
                RecognizedIntent recognizedIntent = IntentRecognitionHelper.getRecognizedIntent(textMessage, session,
                        provider.getRuntimePlatform().getXatkitCore());
                session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                        ReactUtils.CHAT_USERNAME_CONTEXT_KEY, username);
                session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                        ReactUtils.CHAT_CHANNEL_CONTEXT_KEY, channel);
                session.getRuntimeContexts().setContextValue(ReactUtils.REACT_CONTEXT_KEY, 1,
                        ReactUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY, textMessage);
                /*
                 * This provider extends ChatIntentProvider, and must set chat-related context values.
                 */
                session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                        ChatUtils.CHAT_USERNAME_CONTEXT_KEY, username);
                session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                        ChatUtils.CHAT_CHANNEL_CONTEXT_KEY, channel);
                session.getRuntimeContexts().setContextValue(ChatUtils.CHAT_CONTEXT_KEY, 1,
                        ChatUtils.CHAT_RAW_MESSAGE_CONTEXT_KEY, textMessage);
                /*
                 * Use the base provider sendEventInstance method to ensure that the chat context are checked.
                 */
                provider.sendEventInstance(recognizedIntent, session);
            }
            Log.info("Received a message from user {0} (channel {1}): {2}", username, channel, message);
        } else {
            Log.error("Does not contain Xatkit REACt header");
        }
        return null;
    }
}
