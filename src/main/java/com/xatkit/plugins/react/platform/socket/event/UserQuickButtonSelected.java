package com.xatkit.plugins.react.platform.socket.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An event triggered when the user selected a quick button.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQuickButtonSelected extends UserEvent {

    /**
     * The value selected by the user.
     */
    private String selectedValue;
}
