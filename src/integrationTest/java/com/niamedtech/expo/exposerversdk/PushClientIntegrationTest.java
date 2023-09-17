package com.niamedtech.expo.exposerversdk;

import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.niamedtech.expo.exposerversdk.request.ExpoPushMessage;

import lombok.SneakyThrows;
import lombok.val;

final class PushClientIntegrationTest {

    private static final String EXPO_NOTIFICATION_TOKEN = "ExponentPushToken[azIuGHK7rAYs4BODCQq9Og]";

    private static final URL BASE_URL = createBaseUrl();

    private PushServerResolver pushServerResolver;
    
    private PushClient testee;

    @SneakyThrows
    private static URL createBaseUrl() {
        return new URL("https://exp.host/--/api/v2/");
    }

    @BeforeAll
    void setUp() {
        pushServerResolver = new PushServerResolver();
        testee = new PushClient<ExpoPushMessage>(BASE_URL, pushServerResolver);
    }
    
    @Test
    void testPush() {
        val message = new ExpoPushMessage();
        message.getTo().add(EXPO_NOTIFICATION_TOKEN);
        message.setTitle("Test Title");
        message.setBody("Test Body");

        testee.sendPushNotificationsAsync(List.of(message));
    }

}
