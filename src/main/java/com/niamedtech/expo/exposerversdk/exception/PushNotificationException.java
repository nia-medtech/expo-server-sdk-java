package com.niamedtech.expo.exposerversdk.exception;

import java.util.List;
import java.util.concurrent.CompletionException;

import com.niamedtech.expo.exposerversdk.request.ExpoPushMessageCustomData;

public class PushNotificationException extends CompletionException {
  
  public Exception exception;
  public List<? extends ExpoPushMessageCustomData> messages;

  public PushNotificationException(
      Exception e, List<? extends ExpoPushMessageCustomData<?>> messages) {
    this.exception = e;
    this.messages = messages;
  }
}
