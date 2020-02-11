package com.xatkit.plugins.react.platform.utils;

/**
 * The types of events supported by the socket server.
 */
public enum SocketEventTypes {

    /**
     * A message sent by the bot.
     */
    BOT_MESSAGE("bot_message"),
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
    SET_MESSAGE_LOADER("set_message_loader");

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
