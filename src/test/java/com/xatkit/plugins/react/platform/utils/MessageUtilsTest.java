package com.xatkit.plugins.react.platform.utils;

import com.xatkit.AbstractXatkitTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageUtilsTest extends AbstractXatkitTest {

    @Test(expected = NullPointerException.class)
    public void testEventLinkNullValue() {
        MessageUtils.eventLink(null);
    }

    @Test(expected = NullPointerException.class)
    public void testEventLinkNullName() {
        MessageUtils.eventLink(null, "value");
    }

    @Test
    public void testEventLinkOnlyValue() {
        String eventLink = MessageUtils.eventLink("value");
        assertThat(eventLink).isEqualTo("[value](##value)");
    }

    @Test
    public void testEventLinkNameAndValue() {
        String eventLink = MessageUtils.eventLink("name", "value");
        assertThat(eventLink).isEqualTo("[name](##value)");
    }
}
