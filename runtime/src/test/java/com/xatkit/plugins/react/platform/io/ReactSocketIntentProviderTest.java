package com.xatkit.plugins.react.platform.io;

import com.xatkit.XatkitTest;
import com.xatkit.core.XatkitCore;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.stubs.StubXatkitCore;
import org.apache.commons.configuration2.BaseConfiguration;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Objects.nonNull;

public class ReactSocketIntentProviderTest extends XatkitTest {

    private static XatkitCore xatkitCore;

    private static ReactPlatform reactPlatform;

    private ReactSocketIntentProvider reactSocketIntentProvider;

    @BeforeClass
    public static void setUpBeforeClass() {
        xatkitCore = new StubXatkitCore();
        reactPlatform = new ReactPlatform(xatkitCore, new BaseConfiguration());
    }

    @After
    public void tearDown() {
        if(nonNull(reactSocketIntentProvider)) {
            reactSocketIntentProvider.close();
        }
    }

    @Test(expected = NullPointerException.class)
    public void constructNullPlatform() {
        reactSocketIntentProvider = new ReactSocketIntentProvider(null, new BaseConfiguration());
    }

    @Test
    public void startTest() throws InterruptedException {
        reactSocketIntentProvider = new ReactSocketIntentProvider(reactPlatform, new BaseConfiguration());
        reactSocketIntentProvider.run();
        Thread.sleep(Integer.MAX_VALUE);
    }

}
