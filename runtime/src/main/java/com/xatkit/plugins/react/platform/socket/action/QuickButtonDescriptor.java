package com.xatkit.plugins.react.platform.socket.action;

import lombok.Data;

/**
 * Describe the content of a <i>quick button</i> to be printed to the user.
 */
@Data
public class QuickButtonDescriptor {

    /**
     * The label of the quick button.
     */
    private String label;

    /**
     * The value of the quick button.
     */
    private String value;

    /**
     * Constructs a {@link QuickButtonDescriptor} from the provided {@code label} and {@code value}.
     *
     * @param label the label of the quick button
     * @param value the value of the quick button
     */
    public QuickButtonDescriptor(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
