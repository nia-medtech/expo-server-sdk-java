package com.niamedtech.expo.exposerversdk.exception;

import java.util.List;

import com.niamedtech.expo.exposerversdk.response.ExpoPushError;
import com.niamedtech.expo.exposerversdk.response.ExpoPushReceipt;

public class PushNotificationReceiptsErrorsException extends Exception {
  public List<ExpoPushError> errors;
  public List<ExpoPushReceipt> receipts;

  public PushNotificationReceiptsErrorsException(
      List<ExpoPushError> errors, List<ExpoPushReceipt> receipts) {
    this.errors = errors;
    this.receipts = receipts;
  }
}
