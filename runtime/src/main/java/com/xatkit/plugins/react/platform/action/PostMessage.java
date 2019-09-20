package com.xatkit.plugins.react.platform.action;

import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.utils.MessageObject;
import com.xatkit.plugins.react.platform.utils.SocketEventTypes;

import java.util.UUID;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

/**
 * A {@link RuntimeMessageAction} that posts a {@code message} to a given xatkit-react {@code channel}.
 */
public class PostMessage extends RuntimeMessageAction<ReactPlatform> {

    /**
     * The channel to post the message to.
     */
    private String channel;

    /**
     * Constructs a new {@link PostMessage} with the provided {@code runtimePlatform}, {@code session}, {@code
     * message}, and {@code channel}.
     *
     * @param runtimePlatform the {@link ReactPlatform} containing this action
     * @param session         the {@link XatkitSession} associated to this action
     * @param message         the message to post
     * @param channel         the xatkit-react channel to post the message to
     * @throws NullPointerException     if the provided {@code runtimePlatform} or {@code session} is {@code null}
     * @throws IllegalArgumentException if the provided {@code message} or {@code channel} is {@code null}
     */
    public PostMessage(ReactPlatform runtimePlatform, XatkitSession session, String message, String channel) {
        super(runtimePlatform, session, message);
        checkArgument(nonNull(channel) && !(channel.isEmpty()), "Cannot construct a %s action with the provided " +
                "channel %s, expected a non-null and not empty String", this.getClass().getSimpleName(), channel);
        this.channel = channel;
    }

    /**
     * Posts the provided {@code message} to the given {@code channel}.
     * <p>
     * Posted messages are pushed to the client application using the underlying socket server.
     *
     * @return {@code null}
     */
    @Override
    protected Object compute() {
        this.runtimePlatform.getSocketIOServer().getClient(UUID.fromString(channel)).
                sendEvent(SocketEventTypes.BOT_MESSAGE.label, new MessageObject(message, "xatkit"));
        return null;
    }

    @Override
    protected XatkitSession getClientSession() {
        return this.runtimePlatform.createSessionFromChannel(channel);
    }
}
