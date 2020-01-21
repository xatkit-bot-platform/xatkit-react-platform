package com.xatkit.plugins.react.platform.utils;

/**
 * Describe the content of a <i>quick button</i> to be printed to the user.
 */
public class QuickButtonValue {

    /**
     * The label of the quick button.
     */
    private String label;

    /**
     * The value of the quick button.
     */
    private String value;

    /**
     * Constructs a {@link QuickButtonValue} from the provided {@code label} and {@code value}.
     *
     * @param label the label of the quick button
     * @param value the value of the quick button
     */
    public QuickButtonValue(String label, String value) {
        this.label = label;
        this.value = value;
    }

    /**
     * Sets the label of the quick button.
     *
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Sets the value of the quick button.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the label of the quick button.
     *
     * @return the label of the quick button
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Returns the value of the quick button.
     *
     * @return the value of the quick button
     */
    public String getValue() {
        return this.value;
    }
}
