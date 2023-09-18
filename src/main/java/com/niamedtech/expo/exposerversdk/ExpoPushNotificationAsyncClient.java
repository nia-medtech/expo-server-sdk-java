package com.niamedtech.expo.exposerversdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niamedtech.expo.exposerversdk.request.ExpoPushNotification;
import com.niamedtech.expo.exposerversdk.request.ReceiptRequest;
import com.niamedtech.expo.exposerversdk.response.ReceiptResponse;
import com.niamedtech.expo.exposerversdk.response.SendResponse;
import com.ok2c.hc5.json.http.JsonRequestProducers;
import com.ok2c.hc5.json.http.JsonResponseConsumers;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.support.BasicRequestBuilder;

/**
 * Client for asynchronous communication with Expo Push Notificatio Servic.
 * See https://docs.expo.dev/push-notifications/sending-notifications/
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExpoPushNotificationAsyncClient {

  private static final String ACCEPT = "ACCEPT";

  private static final String CONTENT_TYPE = "Content-Type";

  private static final String CONTENT_TYPE_JSON = "application/json";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @NonNull private final URI baseUri;

  @NonNull private final CloseableHttpAsyncClient httpClient;

  public Future<Message<HttpResponse, SendResponse>> sendPushNotificationsAsync(
      List<ExpoPushNotification> messages) {

    return httpClient.execute(
        JsonRequestProducers.create(createHttpPostRequest("/push/send"), messages, objectMapper),
        JsonResponseConsumers.create(objectMapper, SendResponse.class),
        new FutureCallback<Message<HttpResponse, SendResponse>>() {

          @Override
          public void failed(Exception ex) {
            log.error("Sending push notification failed.", ex);
          }

          @Override
          public void completed(Message<HttpResponse, SendResponse> result) {
            if (result.getHead().getCode() != 200) {
              log.error(
                  "Received error code {}: {} - {}",
                  result.getHead().getCode(),
                  result.getHead().getReasonPhrase(),
                  result.getBody());
            }

            if (result.getBody() != null
                && result.getBody().getErrors() != null
                && !result.getBody().getErrors().isEmpty()) {
              log.error("Received errors for request: {}", result.getBody().getErrors());
            }
          }

          @Override
          public void cancelled() {
            log.warn("Request cancelled");
          }
        });
  }

  private BasicHttpRequest createHttpPostRequest(String subpath) {
    return BasicRequestBuilder.post(URI.create(baseUri.toString() + subpath))
        .addHeader(CONTENT_TYPE, CONTENT_TYPE_JSON)
        .addHeader(ACCEPT, CONTENT_TYPE_JSON)
        .build();
  }

  public Future<Message<HttpResponse, ReceiptResponse>> getPushNotificationReceiptsAsync(
      List<String> ids) {

    final ReceiptRequest receiptRequest = new ReceiptRequest(ids);

    return httpClient.execute(
        JsonRequestProducers.create(
            createHttpPostRequest("/push/getReceipts"), receiptRequest, objectMapper),
        JsonResponseConsumers.create(objectMapper, ReceiptResponse.class),
        new FutureCallback<Message<HttpResponse, ReceiptResponse>>() {

          @Override
          public void failed(Exception ex) {
            log.error("Sending push notification failed.", ex);
          }

          @Override
          public void completed(Message<HttpResponse, ReceiptResponse> result) {
            if (result.getHead().getCode() != 200) {
              log.error(
                  "Received error code {}: {} - {}",
                  result.getHead().getCode(),
                  result.getHead().getReasonPhrase(),
                  result.getBody());
            }

            if (result.getBody() != null
                && result.getBody().getErrors() != null
                && !result.getBody().getErrors().isEmpty()) {
              log.error("Received errors for request: {}", result.getBody().getErrors());
            }
          }

          @Override
          public void cancelled() {
            log.warn("Request cancelled");
          }
        });
  }

  public static class Builder {

    private String baseUri = "https://exp.host/--/api/v2/";

    private CloseableHttpAsyncClient httpClient;

    public Builder setBaseUri(@NonNull String baseUri) {
      this.baseUri = baseUri;
      return this;
    }

    public Builder setHttpClient(@NonNull CloseableHttpAsyncClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    public ExpoPushNotificationAsyncClient build() {
      return new ExpoPushNotificationAsyncClient(URI.create(baseUri), httpClient);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
