package com.xatkit.plugins.react.platform.action;

import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.core.session.XatkitSession;
import com.xatkit.plugins.react.platform.ReactPlatform;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link Reply} action that uploads a {@link File} on the Xatkit server and prints a link to it.
 * <p>
 * This class support previews for image {@link File}s.
 */
public class ReplyFileMessage extends Reply {

    /**
     * The extension to handle as images.
     * <p>
     * Images are handled with a preview.
     */
    private static List<String> IMG_EXTENSIONS = Arrays.asList("gif", "jpg", "png");

    /**
     * Computes the message associated to the provided {@code baseMessage} and {@code file}.
     * <p>
     * This method returns a thumbnail linking to the full file of the provided {@code file} is an image, or a
     * regular link to the file if it is any other file type.
     * <p>
     * Thumbnails are automatically put on a new line, links to regular files are displayed inline.
     *
     * @param platform    the {@link RuntimePlatform} used to retrieve the {@link com.xatkit.core.server.XatkitServer}
     *                    to upload the {@code file}.
     * @param session     the {@link XatkitSession} used to upload the file
     * @param baseMessage the message to post with the provided {@code file}
     * @param file        the {@link File} to upload and post as a reply
     * @return the formatted message
     */
    private static String computeMessage(RuntimePlatform platform, XatkitSession session, String baseMessage,
                                         File file) {
        File uploadedFile = platform.getXatkitCore().getXatkitServer().createOrReplacePublicFile(session,
                file.getName(), file);
        String publicURL = platform.getXatkitCore().getXatkitServer().getPublicURL(uploadedFile);
        String fileName = uploadedFile.getName();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (IMG_EXTENSIONS.contains(extension.toLowerCase())) {
            /*
             * The file is an image, create the appropriate message content.
             */
            return MessageFormat.format("{0}  \n[![image]({1})]({1})", baseMessage, publicURL);
        } else {
            /*
             * The file isn't an image, create a generic link.
             */
            return MessageFormat.format("{0} [file]({1})", baseMessage, publicURL);
        }
    }

    /**
     * Constructs a new {@link ReplyFileMessage} with the provided {@code reactPlatform}, {@code session}, {@code
     * message}, and {@code file}.
     *
     * @param reactPlatform the {@link ReactPlatform} containing this action
     * @param session       the {@link XatkitSession} associated to this action
     * @param message       the message to post with the provided {@code file}
     * @param file          the {@link File} to upload and post
     * @throws NullPointerException if the provided {@code reactPlatform} or {@code session} is {@code null}
     * @see Reply
     */
    public ReplyFileMessage(ReactPlatform reactPlatform, XatkitSession session, String message, File file) {
        super(reactPlatform, session, computeMessage(reactPlatform, session, message, file));
    }

    /**
     * Constructs a new {@link ReplyFileMessage} with the provided {@code reactPlatform}, {@code session}, and {@code
     * file}.
     * <p>
     * This constructor is similar to {@code ReplyFileMessage(reactPlatform, session, "", file}, and displays the
     * provided {@code file} without accompanying message.
     *
     * @param reactPlatform the {@link ReactPlatform} containing this action
     * @param session       the {@link XatkitSession} associated to this action
     * @param file          the {@link File} to upload and post
     * @throws NullPointerException if the provided {@code reactPlatform} or {@code session} is {@code null}
     * @see Reply
     */
    public ReplyFileMessage(ReactPlatform reactPlatform, XatkitSession session, File file) {
        this(reactPlatform, session, "", file);
    }
}
