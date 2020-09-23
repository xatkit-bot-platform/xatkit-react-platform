package com.xatkit.plugins.react.platform.socket.event;

import lombok.Data;

/**
 * An event triggered by the user.
 */
@Data
public class UserEvent {

    /**
     * The name of the user that triggered the event.
     */
    private String username;
}
