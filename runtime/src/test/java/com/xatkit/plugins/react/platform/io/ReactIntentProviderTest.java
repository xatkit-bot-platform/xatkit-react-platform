package com.xatkit.plugins.react.platform.io;

import com.xatkit.AbstractEventProviderTest;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.server.XatkitServer;
import com.xatkit.plugins.react.platform.ReactPlatform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Objects.nonNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReactIntentProviderTest extends AbstractEventProviderTest<ReactIntentProvider, ReactPlatform> {

    private XatkitServer mockedXatkitServer;

    @Before
    public void setUp() {
        /*
         * Can't call super.setUp because we need to mock the XatkitServer before calling getPlatform.
         */
        mockedXatkitBot = mock(XatkitBot.class);
        mockedXatkitServer = mock(XatkitServer.class);
        when(mockedXatkitBot.getXatkitServer()).thenReturn(mockedXatkitServer);
        platform = getPlatform();
    }

    @After
    public void tearDown() {
        if(nonNull(provider)) {
            provider.close();
        }
    }

    @Test(expected = NullPointerException.class)
    public void constructNullPlatform() {
        provider = new ReactIntentProvider(null);
    }

    @Override
    protected ReactPlatform getPlatform() {
        return new ReactPlatform();
    }
}
