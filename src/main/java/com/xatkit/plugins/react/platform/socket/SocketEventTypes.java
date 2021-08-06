package com.xatkit.plugins.react.platform.socket;

/**
 * The types of events supported by the socket server.
 */
public enum SocketEventTypes {

    /**
     * A message sent by the bot.
     */
    BOT_MESSAGE("bot_message"),
    /**
     * A link snippet with preview image sent by the bot.
     */
    LINK_SNIPPET("link_snippet_with_img"),

    AUDIO("audio"),
    /**
     * A message sent by the user.
     */
    USER_MESSAGE("user_message"),
    /**
     * A quick button clicked by the user.
     */
    USER_BUTTON_CLICK("user_button_click"),
    /**
     * Tells the client to display/hide the message loader.
     */
    SET_MESSAGE_LOADER("set_message_loader"),
    /**
     * Tells the client to toggle dark mode.
     */
    TOGGLE_DARK_MODE("toggle_dark_mode"),
    /**
     * The Init message sent by the client.
     */
    INIT("init"),
    /**
     * The ACK message sent to the client after receiving an INIT event.
     */
    INIT_CONFIRM("init_confirm");

    /**
     * The label of the enumeration value.
     */
    public final String label;

    /**
     * Constructs a new value for the enum with the given label.
     *
     * @param label the label of the enum value
     */
    SocketEventTypes(String label) {
        this.label = label;
    }
}
