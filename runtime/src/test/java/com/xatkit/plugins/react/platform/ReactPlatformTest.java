package com.xatkit.plugins.react.platform;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.xatkit.AbstractPlatformTest;
import com.xatkit.core.server.XatkitServer;
import com.xatkit.core.server.XatkitServerUtils;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import org.apache.commons.configuration2.BaseConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReactPlatformTest extends AbstractPlatformTest<ReactPlatform> {

    private XatkitServer mockedXatkitServer;

    @Before
    public void setUp() {
        super.setUp();
        mockedXatkitServer = mock(XatkitServer.class);
        when(mockedXatkitBot.getXatkitServer()).thenReturn(mockedXatkitServer);
    }

    @Test
    public void constructEmptyConfiguration() {
        platform = new ReactPlatform();
        platform.start(mockedXatkitBot, new BaseConfiguration());
        Configuration configuration = checkAndGetConfiguration(platform);
        assertThat(configuration.getOrigin()).as("Origin is null").isNull();
        assertThat(configuration.getPort()).as("Port is the default one specified in ReactUtils")
                .isEqualTo(ReactUtils.DEFAULT_REACT_SERVER_PORT);
    }

    @Test
    public void constructReactClientOriginInConfiguration() {
        org.apache.commons.configuration2.Configuration platformConfiguration = new BaseConfiguration();
        String origin = "http://www.example.com:1234";
        platformConfiguration.addProperty(ReactUtils.REACT_CLIENT_URL_KEY, origin);
        platform = new ReactPlatform();
        platform.start(mockedXatkitBot, platformConfiguration);
        Configuration configuration = checkAndGetConfiguration(platform);
        assertThat(configuration.getOrigin()).as("Origin is the one provided in the configuration").isEqualTo(origin);
        assertThat(configuration.getPort()).as("Port is the default one specified in ReactUtils")
                .isEqualTo(ReactUtils.DEFAULT_REACT_SERVER_PORT);
    }

    @Test
    public void constructReactClientOriginNotSpecifiedCustomXatkitServerUrlAndPort() {
        org.apache.commons.configuration2.Configuration platformConfiguration = new BaseConfiguration();
        String customXatkitServerURL = "http://www.example.com";
        int customXatkitServerPort = 1234;
        platformConfiguration.addProperty(XatkitServerUtils.SERVER_PUBLIC_URL_KEY, customXatkitServerURL);
        platformConfiguration.addProperty(XatkitServerUtils.SERVER_PORT_KEY, customXatkitServerPort);
        platform = new ReactPlatform();
        platform.start(mockedXatkitBot, platformConfiguration);
        Configuration configuration = checkAndGetConfiguration(platform);
        assertThat(configuration.getOrigin()).as("Origin is null").isNull();
        assertThat(configuration.getPort()).as("Port is the default one specified in ReactUtils")
                .isEqualByComparingTo(ReactUtils.DEFAULT_REACT_SERVER_PORT);
    }

    @Test
    public void constructCustomReactServerPort() {
        org.apache.commons.configuration2.Configuration platformConfiguration = new BaseConfiguration();
        int reactPort = 1234;
        platformConfiguration.addProperty(ReactUtils.REACT_SERVER_PORT_KEY, reactPort);
        platform = new ReactPlatform();
        platform.start(mockedXatkitBot, platformConfiguration);
        Configuration configuration = checkAndGetConfiguration(platform);
        assertThat(configuration.getPort()).as("Port is the one defined in the configuration").isEqualTo(reactPort);
    }

    @Test
    public void constructWildcardOrigin() {
        org.apache.commons.configuration2.Configuration platformConfiguration = new BaseConfiguration();
        String origin = "*";
        platformConfiguration.addProperty(ReactUtils.REACT_CLIENT_URL_KEY, origin);
        platform = new ReactPlatform();
        platform.start(mockedXatkitBot, platformConfiguration);
        Configuration configuration = checkAndGetConfiguration(platform);
        /*
         * We need to translate wildcard origin into null, because of a non-intuitive behavior of netty-socketio, see
         * this issue for more information: https://github.com/mrniko/netty-socketio/issues/400.
         */
        assertThat(configuration.getOrigin()).as("Origin is null").isEqualTo(null);
    }

    private Configuration checkAndGetConfiguration(ReactPlatform reactPlatform) {
        assertThat(reactPlatform.getSocketIOServer()).as("Socket server not null").isNotNull();
        SocketIOServer server = reactPlatform.getSocketIOServer();
        assertThat(server.getConfiguration()).as("Socket server configuration is not null").isNotNull();
        return server.getConfiguration();
    }
}
