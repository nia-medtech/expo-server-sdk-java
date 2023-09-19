package com.niamedtech.expo.exposerversdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Reponse including receipts for tickets. */
@Data
@EqualsAndHashCode(callSuper = false)
public final class ReceiptResponse extends BaseResponse<Map<String, ReceiptResponse.Receipt>> {

  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class Receipt extends BaseResponse.GenericData {

    @Data
    public static class Details {

      public enum Error {
        @JsonProperty("DeviceNotRegistered")
        DEVICE_NOT_REGISTERED,

        @JsonProperty("MessageTooBig")
        MESSAGE_TOO_BIG,

        @JsonProperty("MessageRateExceeded")
        MESSAGE_RATE_EXCEEDED,

        @JsonProperty("InvalidCredentials")
        INVALID_CREDENTIALS,

        @JsonProperty("InvalidProviderToken")
        INVALID_PROVIDERTOKEN;
      }

      private Error error;
      private Integer sentAt;
      private String errorCodeEnum;
      private JsonNode additionalProperties;
    }

    private Status status;
    private String message;
    private Details details;
  }

  private Map<String, ReceiptResponse.Receipt> data;
}
