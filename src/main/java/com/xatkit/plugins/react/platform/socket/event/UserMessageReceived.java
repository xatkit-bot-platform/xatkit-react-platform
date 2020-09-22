package com.xatkit.plugins.react.platform.socket.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An event triggered when an user sent a message to the bot.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserMessageReceived extends UserEvent {

    /**
     * The message sent by the user.
     */
    private String message;
}
