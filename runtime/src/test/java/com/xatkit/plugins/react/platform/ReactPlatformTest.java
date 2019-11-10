package com.xatkit.plugins.react.platform;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.xatkit.AbstractXatkitTest;
import com.xatkit.core.XatkitCore;
import com.xatkit.core.server.XatkitServerUtils;
import com.xatkit.plugins.react.platform.io.ReactIntentProvider;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import com.xatkit.stubs.StubXatkitCore;
import org.apache.commons.configuration2.BaseConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class ReactPlatformTest extends AbstractXatkitTest {

    private static XatkitCore xatkitCore;

    @BeforeClass
    public static void setUpBeforeClass() {
        xatkitCore = new StubXatkitCore();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        if (!xatkitCore.isShutdown()) {
            xatkitCore.shutdown();
        }
    }

    private ReactPlatform reactPlatform;

    @After
    public void tearDown() {
        if (nonNull(reactPlatform)) {
            reactPlatform.shutdown();
        }
    }

    @Test(expected = NullPointerException.class)
    public void constructNullXatkitCore() {
        reactPlatform = new ReactPlatform(null, new BaseConfiguration());
    }

    @Test(expected = NullPointerException.class)
    public void constructNullConfiguration() {
        reactPlatform = new ReactPlatform(xatkitCore, null);
    }

    @Test
    public void constructEmptyConfiguration() {
        reactPlatform = new ReactPlatform(xatkitCore, new BaseConfiguration());
        Configuration configuration = checkAndGetConfiguration(reactPlatform);
        assertThat(configuration.getOrigin()).as("Origin is default XatkitServer values")
                .isEqualTo(XatkitServerUtils.DEFAULT_SERVER_LOCATION + ":" + XatkitServerUtils.DEFAULT_SERVER_PORT);
        assertThat(configuration.getPort()).as("Port is the default one specified in ReactUtils")
                .isEqualTo(ReactUtils.DEFAULT_REACT_SERVER_PORT);
    }

    @Test
    public void constructReactClientOriginInConfiguration() {
        org.apache.commons.configuration2.Configuration platformConfiguration = new BaseConfiguration();
        String origin = "http://www.example.com:1234";
        platformConfiguration.addProperty(ReactUtils.REACT_CLIENT_URL_KEY, origin);
        reactPlatform = new ReactPlatform(xatkitCore, platformConfiguration);
        Configuration configuration = checkAndGetConfiguration(reactPlatform);
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
        reactPlatform = new ReactPlatform(xatkitCore, platformConfiguration);
        Configuration configuration = checkAndGetConfiguration(reactPlatform);
        assertThat(configuration.getOrigin()).as("Origin is the XatkitServer URL").isEqualTo(customXatkitServerURL +
                ":" + customXatkitServerPort);
        assertThat(configuration.getPort()).as("Port is the default one specified in ReactUtils")
                .isEqualTo(ReactUtils.DEFAULT_REACT_SERVER_PORT);
    }

    @Test
    public void constructCustomReactServerPort() {
        org.apache.commons.configuration2.Configuration platformConfiguration = new BaseConfiguration();
        int reactPort = 1234;
        platformConfiguration.addProperty(ReactUtils.REACT_SERVER_PORT_KEY, reactPort);
        reactPlatform = new ReactPlatform(xatkitCore, platformConfiguration);
        Configuration configuration = checkAndGetConfiguration(reactPlatform);
        assertThat(configuration.getPort()).as("Port is the one defined in the configuration").isEqualTo(reactPort);
    }

    @Test
    public void constructWildcardOrigin() {
        org.apache.commons.configuration2.Configuration platformConfiguration = new BaseConfiguration();
        String origin = "*";
        platformConfiguration.addProperty(ReactUtils.REACT_CLIENT_URL_KEY, origin);
        reactPlatform = new ReactPlatform(xatkitCore, platformConfiguration);
        Configuration configuration = checkAndGetConfiguration(reactPlatform);
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
