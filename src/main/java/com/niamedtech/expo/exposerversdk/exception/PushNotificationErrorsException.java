package com.niamedtech.expo.exposerversdk.exception;

import java.util.List;

import com.niamedtech.expo.exposerversdk.response.ExpoPushError;
import com.niamedtech.expo.exposerversdk.response.ExpoPushTicket;

public class PushNotificationErrorsException extends Exception {
  public List<ExpoPushError> errors;
  public List<ExpoPushTicket> data;

  public PushNotificationErrorsException(List<ExpoPushError> errors, List<ExpoPushTicket> data) {
    this.errors = errors;
    this.data = data;
  }
}
