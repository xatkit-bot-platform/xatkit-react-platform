package com.xatkit.plugins.react.platform.utils;

/**
 * The event triggered when a user clicks one a quick button.
 */
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
     * Constructs an empty {@link QuickButtonEventObject}.
     */
    public QuickButtonEventObject() {
        // Empty constructor required by Jackson
    }

    /**
     * Sets the username associated to this event.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the selected value associated to this event.
     *
     * @param selectedValue the selected value
     */
    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    /**
     * Returns the name of the user who clicked on a quick button.
     *
     * @return the name of the user who clicked on a quick button
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the value selected by the user.
     * <p>
     * This selected value matches the {@link QuickButtonValue#getValue()}, i.e. it is not possible to retrieve the
     * selected label from the user.
     *
     * @return the value selected by the user
     */
    public String getSelectedValue() {
        return this.selectedValue;
    }
}
