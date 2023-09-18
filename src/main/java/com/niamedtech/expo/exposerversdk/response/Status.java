package com.niamedtech.expo.exposerversdk.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
  @JsonProperty("ok")
  OK,

  @JsonProperty("error")
  ERROR
}
