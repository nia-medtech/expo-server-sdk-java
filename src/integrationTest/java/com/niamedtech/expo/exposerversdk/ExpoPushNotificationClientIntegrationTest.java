package com.niamedtech.expo.exposerversdk;

import com.niamedtech.expo.exposerversdk.request.PushNotification;
import com.niamedtech.expo.exposerversdk.response.ReceiptResponse;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;
import com.niamedtech.expo.test.ResponseTestFixture;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

@Slf4j
@ExtendWith(MockServerExtension.class)
final class ExpoPushNotificationClientIntegrationTest {

  private static final String EXPO_NOTIFICATION_TOKEN = "ExponentPushToken[random1]";

  private static final String EXPO_NOTIFICATION_TOKEN_2 = "ExponentPushToken[random1]";

  private ClientAndServer client;

  private CloseableHttpClient httpClient;

  private ExpoPushNotificationClient testee;

  @BeforeEach
  void setUp(ClientAndServer client) {
    this.client = client;
    httpClient = HttpClients.createDefault();

    testee =
        ExpoPushNotificationClient.builder()
            .setBaseUri("http://localhost:" + client.getLocalPort())
            .setHttpClient(httpClient)
            .build();
  }

  @Test
  void testPush() throws Exception {
    client
        .when(HttpRequest.request().withMethod("POST").withPath("/push/send"), Times.exactly(1))
        .respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(ResponseTestFixture.PUSH_SEND_OK_MULTIPLE_RESPONSE));

    client
        .when(
            HttpRequest.request().withMethod("POST").withPath("/push/getReceipts"),
            Times.exactly(1))
        .respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(ResponseTestFixture.GET_RECEIPT_OK_MULTIPLE_RESPONSE));

    val expoPushNotification = new PushNotification();
    expoPushNotification.setTo(List.of(EXPO_NOTIFICATION_TOKEN, EXPO_NOTIFICATION_TOKEN_2));
    expoPushNotification.setTitle("Test Title");
    expoPushNotification.setBody("Test Body");

    final List<TicketResponse.Ticket> tickets =
        testee.sendPushNotifications(List.of(expoPushNotification));

    final List<String> ids = tickets.stream().map(d -> d.getId()).toList();

    final Map<String, ReceiptResponse.Receipt> receipts = testee.getPushNotificationReceipts(ids);

    log.info("{}", receipts);
  }

  @Test
  void testPushErrorFcmKey() throws Exception {
    client
        .when(HttpRequest.request().withMethod("POST").withPath("/push/send"), Times.exactly(1))
        .respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(ResponseTestFixture.PUSH_SEND_FCM_KEY_UNRETRIEVABLE));

    val expoPushNotification = new PushNotification();
    expoPushNotification.setTo(List.of(EXPO_NOTIFICATION_TOKEN, EXPO_NOTIFICATION_TOKEN_2));
    expoPushNotification.setTitle("Test Title");
    expoPushNotification.setBody("Test Body");

    final List<TicketResponse.Ticket> tickets =
        testee.sendPushNotifications(List.of(expoPushNotification));

    final List<String> ids = tickets.stream().map(d -> d.getId()).toList();

    final Map<String, ReceiptResponse.Receipt> receipts = testee.getPushNotificationReceipts(ids);

    log.info("{}", receipts);
  }
}
