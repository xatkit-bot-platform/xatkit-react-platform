package com.xatkit.plugins.react.platform.action;

import com.xatkit.core.platform.action.RuntimeAction;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.socket.SocketEventTypes;

import java.util.UUID;

/**
 * Tells the web client to toggle dark mode.
 * <p>
 * This action will either enable or disable dark mode in the web client, depending on the current state of the client.
 */
public class ToggleDarkMode extends RuntimeAction<ReactPlatform> {

    /**
     * Constructs a new {@link ToggleDarkMode} with the provided {@code reactPlatform} and {@code session}.
     *
     * @param platform the {@link ReactPlatform} containing this action
     * @param context       the {@link StateContext} associated to this action
     */
    public ToggleDarkMode(ReactPlatform platform, StateContext context) {
        super(platform, context);
    }

    /**
     * Notifies the client to toggle dark mode.
     *
     * @return {@code null}
     */
    @Override
    protected Object compute() {
        this.runtimePlatform.getSocketIOServer().getClient(UUID.fromString(Reply.getChannel(context)))
                .sendEvent(SocketEventTypes.TOGGLE_DARK_MODE.label);
        return null;
    }
}
