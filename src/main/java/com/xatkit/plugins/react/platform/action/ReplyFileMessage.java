package com.xatkit.plugins.react.platform.action;

import com.xatkit.core.platform.RuntimePlatform;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.react.platform.ReactPlatform;
import lombok.NonNull;

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
     * @param platform    the {@link RuntimePlatform} used to access the Xatkit server.
     *                    to upload the {@code file}.
     * @param context     the {@link StateContext} used to upload the file
     * @param baseMessage the message to post with the provided {@code file}
     * @param file        the {@link File} to upload and post as a reply
     * @return the formatted message
     */
    private static String computeMessage(RuntimePlatform platform, StateContext context, String baseMessage,
                                         File file) {
        File uploadedFile = platform.getXatkitBot().getXatkitServer().createOrReplacePublicFile(context,
                file.getName(), file);
        String publicURL = platform.getXatkitBot().getXatkitServer().getPublicURL(uploadedFile);
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
     * @param platform the {@link ReactPlatform} containing this action
     * @param context  the {@link StateContext} associated to this action
     * @param message  the message to post with the provided {@code file}
     * @param file     the {@link File} to upload and post
     * @see Reply
     */
    public ReplyFileMessage(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull String message,
                            @NonNull File file) {
        super(platform, context, computeMessage(platform, context, message, file));
    }

    /**
     * Constructs a new {@link ReplyFileMessage} with the provided {@code reactPlatform}, {@code session}, and {@code
     * file}.
     * <p>
     * This constructor is similar to {@code ReplyFileMessage(reactPlatform, context, "", file}, and displays the
     * provided {@code file} without accompanying message.
     *
     * @param platform the {@link ReactPlatform} containing this action
     * @param context  the {@link StateContext} associated to this action
     * @param file     the {@link File} to upload and post
     * @see Reply
     */
    public ReplyFileMessage(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull File file) {
        this(platform, context, "", file);
    }
}
