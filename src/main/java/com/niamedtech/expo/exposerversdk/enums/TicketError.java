package com.niamedtech.expo.exposerversdk.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TicketError {
  @JsonProperty("DeviceNotRegistered")
  DEVICENOTREGISTERED("DeviceNotRegistered");

  private String error;

  private TicketError(String error) {
    this.error = error;
  }

  @Override
  public String toString() {
    return error;
  }
}
