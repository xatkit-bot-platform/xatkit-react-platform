package com.xatkit.plugins.react.platform.io;

import com.corundumstudio.socketio.SocketIOServer;
import com.xatkit.plugins.chat.platform.io.ChatIntentProvider;
import com.xatkit.plugins.react.platform.ReactPlatform;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.Configuration;

public class ReactSocketIntentProvider extends ChatIntentProvider<ReactPlatform> {

    private SocketIOServer socketIOServer;

    public ReactSocketIntentProvider(ReactPlatform runtimePlatform, Configuration configuration) {
        super(runtimePlatform, configuration);
        com.corundumstudio.socketio.Configuration socketioConfiguration =
                new com.corundumstudio.socketio.Configuration();
        socketioConfiguration.setHostname("localhost");
        socketioConfiguration.setPort(5001);
        // This is the public address where the chatbox is hosted
        socketioConfiguration.setOrigin("http://localhost:3000");
        socketIOServer = new SocketIOServer(socketioConfiguration);
        socketIOServer.addConnectListener(socketIOClient -> Log.info("Connected"));
        socketIOServer.addDisconnectListener(socketIOClient -> Log.info("Disconnected"));
    }

    @Override
    public void run() {
        socketIOServer.start();
    }

    @Override
    public void close() {
        super.close();
        socketIOServer.stop();
    }
}
