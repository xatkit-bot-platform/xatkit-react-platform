package com.xatkit.plugins.react.platform.action;

import com.xatkit.core.platform.action.RuntimeAction;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.socket.SocketEventTypes;
import com.xatkit.plugins.react.platform.socket.action.SendLinkSnippet;
import lombok.NonNull;

import java.util.UUID;

/**
 * Tells the UI to render a link snippet with a preview image.
 */
public class ReplyLinkSnippet extends RuntimeAction<ReactPlatform> {

    /**
     * The title of the snippet.
     */
    private String title;

    /**
     * The link.
     */
    private String link;

    /**
     * The image to display.
     */
    private String img;

    /**
     * Constructs a new {@link ReplyLinkSnippet} with the provided {@code reactPlatform}, {@code xatkitSession},
     * {@code title}, {@code link}, and {@code img}
     *
     * @param platform the {@link ReactPlatform} containing this action
     * @param context       the {@link StateContext} associated to this action
     * @param title         the title of the snippet to display
     * @param link          the link of the snippet
     * @param img           the image of the snippet
     */
    public ReplyLinkSnippet(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull String title,
                            @NonNull String link, @NonNull String img) {
        super(platform, context);
        this.title = title;
        this.link = link;
        this.img = img;
    }

    /**
     * Notifies the client to render a link snippet with a preview image.
     *
     * @return {@code null}
     */
    @Override
    protected Object compute() {
        this.runtimePlatform.getSocketIOServer().getClient(UUID.fromString(Reply.getChannel(context)))
                .sendEvent(SocketEventTypes.LINK_SNIPPET.label, new SendLinkSnippet(this.title, this.link, this.img));
        return null;
    }
}
