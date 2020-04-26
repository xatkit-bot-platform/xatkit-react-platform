package com.xatkit.plugins.react.platform.utils;

import lombok.Data;

/**
 * The event triggered when a user clicks one a quick button.
 */
@Data
public class QuickButtonEventObject {

    /**
     * The name of the user who clicked the quick button.
     */
    private String username;

    /**
     * The value selected by the user.
     * <p>
     * This selected value matches the {@link QuickButtonValue#getValue()}, i.e. it is not possible to retrieve the
     * selected label from the user.
     */
    private String selectedValue;

    /**
     * The hostname of the page where the bot is accessed.
     */
    private String hostname;

    /**
     * The url of the page where the bot is accessed.
     */
    private String url;

    /**
     * The origin of the page where the bot is accessed.
     */
    private String origin;

    /**
     * Constructs an empty {@link QuickButtonEventObject}.
     */
    public QuickButtonEventObject() {
        // Empty constructor required by Jackson
    }
}
