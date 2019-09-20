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

public class ReactIntentProviderTest extends XatkitTest {

    private static XatkitCore xatkitCore;

    private static ReactPlatform reactPlatform;

    private ReactIntentProvider reactIntentProvider;

    @BeforeClass
    public static void setUpBeforeClass() {
        xatkitCore = new StubXatkitCore();
        reactPlatform = new ReactPlatform(xatkitCore, new BaseConfiguration());
    }

    @After
    public void tearDown() {
        if(nonNull(reactIntentProvider)) {
            reactIntentProvider.close();
        }
    }

    @Test(expected = NullPointerException.class)
    public void constructNullPlatform() {
        reactIntentProvider = new ReactIntentProvider(null, new BaseConfiguration());
    }

}
