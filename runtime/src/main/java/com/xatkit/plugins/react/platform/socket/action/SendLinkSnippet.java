package com.xatkit.plugins.react.platform.socket.action;

import lombok.Data;

/**
 * Tells the bot UI to render a message with a link snippet containing a preview image.
 */
@Data
public class SendLinkSnippet {

    /**
     * The title of the snippet.
     */
    private String title;

    /**
     * The link of the snippet.
     */
    private String link;

    /**
     * The image of the snippet.
     */
    private String img;

    /**
     * Creates a {@link SendLinkSnippet} with the provided {@code title}, {@code link}, and {@code img}
     *
     * @param title the title of the snippet
     * @param link  the link of the snippet
     * @param img   the image of the snippet
     */
    public SendLinkSnippet(String title, String link, String img) {
        this.title = title;
        this.link = link;
        this.img = img;
    }
}
