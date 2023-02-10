package com.niamedtech.expo.exposerversdk;

import java.util.List;
import java.util.concurrent.CompletionException;

public class PushNotificationReceiptsException extends CompletionException {
    public Exception exception;
    public List<String> ids;
    public PushNotificationReceiptsException(Exception e, List<String> ids) {
        this.exception = e;
        this.ids = ids;
    }
}
