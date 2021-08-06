package com.xatkit.plugins.react.platform.socket.action;

import lombok.Data;

@Data
public class SendAudio {

    private String src;

    public SendAudio(String src) {
        this.src = src;
    }
}
