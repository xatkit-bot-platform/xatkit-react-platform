package com.xatkit.plugins.react.platform.socket.action;

import lombok.Data;

@Data
public class InitConfirm {

    private String conversationId;

    public InitConfirm(String conversationId) {
        this.conversationId = conversationId;
    }
}
