package com.niamedtech.expo.exposerversdk.util;

import com.niamedtech.expo.exposerversdk.request.ExpoPushNotification;
import java.util.ArrayList;
import java.util.List;

public final class PushNotificationUtil {

  private static final int PUSH_NOTIFICATION_CHUNK_LIMIT = 100;
  private static final int PUSH_NOTIFICATION_RECEIPT_CHUNK_LIMIT = 300;

  private PushNotificationUtil() {}

  public static boolean isExponentPushToken(String token) {
    String prefixA = "ExponentPushToken[";
    String prefixB = "ExpoPushToken[";
    String postfix = "]";
    String regex = "[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";

    if (token.matches(regex)) {
      return true;
    }

    if (!token.endsWith(postfix)) {
      return false;
    }

    if (token.startsWith(prefixA)) {
      return true;
    }

    if (token.startsWith(prefixB)) {
      return true;
    }

    return false;
  }

  public static List<List<String>> chunkPushNotificationReceiptIds(List<String> recieptIds) {
    return chunkItems(recieptIds, PUSH_NOTIFICATION_RECEIPT_CHUNK_LIMIT);
  }

  public static <T> List<List<T>> chunkItems(List<T> items, long chunkSize) {
    List<List<T>> chunks = new ArrayList<>();
    List<T> chunk = new ArrayList<>();
    for (T item : items) {
      chunk.add(item);
      if (chunk.size() >= chunkSize) {
        chunks.add(chunk);
        chunk = new ArrayList<>();
      }
    }

    if (!chunk.isEmpty()) {
      chunks.add(chunk);
    }
    return chunks;
  }

  public static List<List<ExpoPushNotification>> chunkPushNotifications(
      List<ExpoPushNotification> messages) {
    List<List<ExpoPushNotification>> chunks = new ArrayList<>();
    List<ExpoPushNotification> chunk = new ArrayList<>();

    long chunkMessagesCount = 0;
    for (ExpoPushNotification message : messages) {
      List<String> partialTo = new ArrayList<>();
      for (String recipient : message.getTo()) {
        if (recipient.length() <= 0) continue;
        partialTo.add(recipient);
        chunkMessagesCount++;
        if (chunkMessagesCount >= PUSH_NOTIFICATION_CHUNK_LIMIT) {
          // Cap this chunk here if it already exceeds PUSH_NOTIFICATION_CHUNK_LIMIT.
          // Then create a new chunk to continue on the remaining recipients for this
          // message.
          // Because we're using generics, we can't use the constructor. Instead, clone()
          // the
          // message
          ExpoPushNotification tmpCopy = new ExpoPushNotification(message);
          tmpCopy.setTo(partialTo);
          chunk.add(tmpCopy);
          chunks.add(chunk);
          chunk = new ArrayList<>();
          chunkMessagesCount = 0;
          partialTo = new ArrayList<>();
        }
      }

      if (!partialTo.isEmpty()) {
        ExpoPushNotification tmpCopy = new ExpoPushNotification(message);
        tmpCopy.setTo(partialTo);
        chunk.add(tmpCopy);
      }

      if (chunkMessagesCount >= PUSH_NOTIFICATION_CHUNK_LIMIT) {
        // Cap this chunk if it exceeds PUSH_NOTIFICATION_CHUNK_LIMIT.
        // Then create a new chunk to continue on the remaining messages.
        chunks.add(chunk);
        chunk = new ArrayList<>();
        chunkMessagesCount = 0;
      }
    }

    if (chunkMessagesCount > 0) {
      // Add the remaining chunk to the chunks.
      chunks.add(chunk);
    }

    return chunks;
  }
}
