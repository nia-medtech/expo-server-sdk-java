package com.niamedtech.expo.exposerversdk.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Base class for responses provied by Expo Push Notification Service. */
@Data
public abstract class BaseResponse<T> {

  protected static class GenericData {

    /** Store unmapped data in case actual response is varying from specification. */
    private Map<String, JsonNode> any;

    @JsonAnyGetter
    public Map<String, JsonNode> getAny() {
      return any;
    }

    @JsonAnySetter
    public void addAny(String key, JsonNode value) {
      if (any == null) {
        any = new HashMap<>();
      }
      any.put(key, value);
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class Error extends GenericData {
    private String code;
    private String message;
  }

  public abstract T getData();

  private List<Error> errors;
}
