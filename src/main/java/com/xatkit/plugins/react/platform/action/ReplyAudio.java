package com.xatkit.plugins.react.platform.action;

import com.xatkit.core.platform.action.RuntimeAction;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.socket.SocketEventTypes;
import com.xatkit.plugins.react.platform.socket.action.SendAudio;
import lombok.NonNull;

import java.util.UUID;

public class ReplyAudio extends RuntimeAction<ReactPlatform> {

    private String src;

    public ReplyAudio(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull String src) {
        super(platform, context);
        this.src = src;
    }

    /**
     * Notifies the client to render a link snippet with a preview image.
     *
     * @return {@code null}
     */
    @Override
    protected Object compute() {
        this.runtimePlatform.getSocketIOServer().getClient(UUID.fromString(Reply.getChannel(context)))
                .sendEvent(SocketEventTypes.AUDIO.label, new SendAudio(this.src));
        return null;
    }
}
