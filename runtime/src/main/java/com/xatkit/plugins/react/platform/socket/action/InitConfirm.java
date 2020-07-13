package com.xatkit.plugins.react.platform.socket.action;

import lombok.Data;

/**
 * The acknowledgement event sent by the server after processing a received
 * {@link com.xatkit.plugins.react.platform.socket.event.Init} event.
 */
@Data
public class InitConfirm {

    /**
     * The ack of the identifier of the conversation.
     * <p>
     * This value is can be
     * <ul>
     *     <li>Equal to the one in the Init event: in this case this is a simple ack, the server managed to find
     *     the session associated to the conversation</li>
     *     <li>Different from the one in the Init event: this means that something went wrong and a new fresh
     *     session has been created</li>
     *     <li>Set while the Init event contained {@code null} or {@code undefined}: this means that the client
     *     requested a new session, and the server created an identifier for it</li>
     * </ul>
     */
    private String conversationId;

    /**
     * Creates an {@link InitConfirm} event with the provided {@code conversationId}.
     *
     * @param conversationId the identifier of the conversation to set in the event
     */
    public InitConfirm(String conversationId) {
        this.conversationId = conversationId;
    }
}
