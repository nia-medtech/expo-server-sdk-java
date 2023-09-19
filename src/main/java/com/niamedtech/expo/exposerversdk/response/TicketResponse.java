package com.niamedtech.expo.exposerversdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Response inlcuding tickets for push notifications. */
@Data
@EqualsAndHashCode(callSuper = false)
public final class TicketResponse extends BaseResponse<List<TicketResponse.Ticket>> {

  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class Ticket extends BaseResponse.GenericData {

    public enum Error {
      @JsonProperty("DeviceNotRegistered")
      DEVICE_NOT_REGISTERED,
      @JsonProperty("InvalidCredentials")
      INVALID_CREDENTIALS;
    }

    @Data
    public static class Details {
      private Error error;
      private Integer sentAt;
      private JsonNode additionalProperties;
    }

    private String id;
    private Status status;
    private String message;
    private Details details;
  }

  private List<TicketResponse.Ticket> data;
}
