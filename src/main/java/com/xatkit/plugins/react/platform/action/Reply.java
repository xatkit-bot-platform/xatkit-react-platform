package com.xatkit.plugins.react.platform.action;

import com.xatkit.core.platform.action.RuntimeMessageAction;
import com.xatkit.execution.StateContext;
import com.xatkit.intent.EventInstance;
import com.xatkit.plugins.chat.ChatUtils;
import com.xatkit.plugins.react.platform.ReactPlatform;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;

/**
 * A {@link RuntimeMessageAction} that replies to a message using the input xatkit-react channel.
 * <p>
 * This action relies on the provided {@link StateContext} to retrieve the {@code channel} associated to the user
 * input (i.e. the socket connection identifier).
 *
 * @see PostMessage
 */
public class Reply extends PostMessage {

    /**
     * Returns the channel associated to the user input.
     * <p>
     * This method searches for the value stored with the {@link ChatUtils#CHAT_CHANNEL_CONTEXT_KEY} key in the
     * platform data of the current {@link EventInstance}.
     *
     * @param context the {@link StateContext} to retrieve the channel from
     * @return the channel associated to the user input
     * @throws NullPointerException     if the provided {@code context} is {@code null}, or if it does not contain the
     *                                  channel information
     * @throws IllegalArgumentException if the retrieved channel is not a {@link String}
     * @see StateContext#getEventInstance()
     * @see EventInstance#getPlatformData()
     */
    public static String getChannel(@NonNull StateContext context) {
        Object channelValue = context.getEventInstance().getPlatformData().get(ChatUtils.CHAT_CHANNEL_CONTEXT_KEY);
        checkNotNull(channelValue, "Cannot retrieve the React channel from the context, expected a non null " +
                ChatUtils.CHAT_CHANNEL_CONTEXT_KEY + " value, found %s", channelValue);
        checkArgument(channelValue instanceof String, "Invalid React channel type, expected %s, found %s",
                String.class.getSimpleName(), channelValue.getClass().getSimpleName());
        return (String) channelValue;
    }

    /**
     * Constructs a new {@link Reply} with the provided {@code reactPlatform}, {@code session}, and {@code message}.
     * <p>
     * This constructor is similar to {@code new Reply(runtimePlatform, session, message, Collections.emptyList())}.
     *
     * @param platform the {@link ReactPlatform} containing this action
     * @param context  the {@link StateContext} associated to this action
     * @param message  the message to post
     */
    public Reply(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull String message) {
        this(platform, context, message, Collections.emptyList());
    }

    /**
     * Constructs a new {@link Reply} with the provided {@code reactPlatform}, {@code session}, {@code buttons}, and
     * {@code message}.
     *
     * @param platform the {@link ReactPlatform} containing this action
     * @param context  the {@link StateContext} associated to this action
     * @param message  the message to post
     * @param buttons  the quick message buttons to display with the message
     * @throws NullPointerException     if the provided {@code reactPlatform} or {@code session} is {@code null}
     * @throws IllegalArgumentException if the provided {@code message} is {@code null} or empty
     * @see #getChannel(StateContext)
     * @see PostMessage
     */
    public Reply(@NonNull ReactPlatform platform, @NonNull StateContext context, @NonNull String message,
                 @NonNull List<String> buttons) {
        super(platform, context, message, buttons, getChannel(context));
    }
}
