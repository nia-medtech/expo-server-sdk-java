package com.niamedtech.expo.exposerversdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niamedtech.expo.exposerversdk.enums.Status;
import com.niamedtech.expo.exposerversdk.enums.TicketError;
import com.niamedtech.expo.exposerversdk.exception.PushNotificationErrorsException;
import com.niamedtech.expo.exposerversdk.exception.PushNotificationException;
import com.niamedtech.expo.exposerversdk.exception.PushNotificationReceiptsErrorsException;
import com.niamedtech.expo.exposerversdk.exception.PushNotificationReceiptsException;
import com.niamedtech.expo.exposerversdk.request.ExpoPushMessageCustomData;
import com.niamedtech.expo.exposerversdk.response.ExpoPushError;
import com.niamedtech.expo.exposerversdk.response.ExpoPushMessageTicketPair;
import com.niamedtech.expo.exposerversdk.response.ExpoPushReceipt;
import com.niamedtech.expo.exposerversdk.response.ExpoPushTicket;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public final class PushClient<TPushMessage extends ExpoPushMessageCustomData<?>> {
  private static final int PUSH_NOTIFICATION_CHUNK_LIMIT = 100;
  private static final int PUSH_NOTIFICATION_RECEIPT_CHUNK_LIMIT = 300;

  @NonNull private final URL baseApiUrl;

  @NonNull private final PushServerResolver pushServerResolver;

  public URL getBaseApiUrl() {
    return baseApiUrl;
  }

  public CompletableFuture<List<ExpoPushTicket>> sendPushNotificationsAsync(
      List<TPushMessage> messages) throws PushNotificationException {
        
    CompletableFuture<List<ExpoPushTicket>> ret = null;
    try {
      ret =
          _postNotificationAsync(new URL(baseApiUrl + "/push/send"), messages)
              .thenApply(
                  (String jsonString) -> {
                    try {
                      ObjectMapper mapper = new ObjectMapper();
                      JsonNode responseJson = mapper.readTree(jsonString);

                      List<ExpoPushTicket> retList = new ArrayList<>();

                      JsonNode dataNode = mapper.readTree(jsonString).get("data");
                      if (dataNode != null) {
                        for (JsonNode node : dataNode) {
                          retList.add(mapper.convertValue(node, ExpoPushTicket.class));
                        }
                      }

                      JsonNode errorsNode = responseJson.get("errors");
                      if (errorsNode != null) {
                        List<ExpoPushError> errorsList = new ArrayList<>();
                        for (JsonNode node : errorsNode) {
                          errorsList.add(mapper.convertValue(node, ExpoPushError.class));
                        }
                        throw new PushNotificationException(
                            new PushNotificationErrorsException(errorsList, retList), messages);
                      }

                      return retList;
                    } catch (IOException e) {
                      throw new PushNotificationException(e, messages);
                    }
                  });
    } catch (Exception e) {
      throw new PushNotificationException(e, messages);
    }
    return ret;
  }

  public CompletableFuture<List<ExpoPushReceipt>> getPushNotificationReceiptsAsync(
      List<String> _ids) throws PushNotificationReceiptsException {
    CompletableFuture<List<ExpoPushReceipt>> ret = null;
    try {
      ret =
          _postReceiptsAsync(new URL(baseApiUrl + "/push/getReceipts"), _ids)
              .thenApply(
                  (String jsonString) -> {
                    try {
                      ObjectMapper mapper = new ObjectMapper();
                      JsonNode responseJson = mapper.readTree(jsonString);

                      List<ExpoPushReceipt> retList = new ArrayList<>();

                      JsonNode dataNode = responseJson.get("data");
                      if (dataNode != null) {
                        Iterator<Map.Entry<String, JsonNode>> it = dataNode.fields();
                        while (it.hasNext()) {
                          Map.Entry<String, JsonNode> field = it.next();
                          String key = field.getKey();
                          JsonNode expoPushReceiptJsonNode = field.getValue();
                          ExpoPushReceipt epr =
                              mapper.treeToValue(expoPushReceiptJsonNode, ExpoPushReceipt.class);

                          epr.setId(key);
                          retList.add(epr);
                        }
                      }

                      JsonNode errorsNode = responseJson.get("errors");
                      if (errorsNode != null) {
                        List<ExpoPushError> errorsList = new ArrayList<>();
                        for (JsonNode node : errorsNode) {
                          errorsList.add(mapper.convertValue(node, ExpoPushError.class));
                        }
                        throw new PushNotificationReceiptsException(
                            new PushNotificationReceiptsErrorsException(errorsList, retList), _ids);
                      }

                      return retList;
                    } catch (PushNotificationReceiptsException e) {
                      throw e;
                    } catch (Exception e) {
                      throw new PushNotificationReceiptsException(e, _ids);
                    }
                  });
    } catch (Exception e) {
      throw new PushNotificationReceiptsException(e, _ids);
    }
    return ret;
  }

  protected CompletableFuture<String> _postNotificationAsync(
      URL url, List<? extends TPushMessage> messages) throws CompletionException {
    ObjectMapper objectMapper = new ObjectMapper();
    String json = null;

    try {
      json = objectMapper.writeValueAsString(messages);
    } catch (JsonProcessingException e) {
      throw new PushNotificationException(e, messages);
    }
    return pushServerResolver.postAsync(url, json);
  }

  public List<ExpoPushMessageTicketPair<TPushMessage>> zipMessagesTickets(
      List<TPushMessage> messages, List<ExpoPushTicket> tickets) {
    List<ExpoPushMessageTicketPair<TPushMessage>> ret = new ArrayList<>();

    for (int i = 0; i < messages.size(); i++) {
      ret.add(new ExpoPushMessageTicketPair<>(messages.get(i), tickets.get(i)));
    }

    return ret;
  }

  public List<ExpoPushMessageTicketPair<TPushMessage>> filterAllSuccessfulMessages(
      List<ExpoPushMessageTicketPair<TPushMessage>> zippedMessagesTickets) {
    return zippedMessagesTickets.stream()
        .filter(p -> p.ticket.getStatus() == Status.OK)
        .collect(Collectors.toList());
  }

  public List<ExpoPushMessageTicketPair<TPushMessage>> filterAllMessagesWithError(
      List<ExpoPushMessageTicketPair<TPushMessage>> zippedMessagesTickets) {
    return filterAllMessagesWithError(zippedMessagesTickets, null);
  }

  public List<ExpoPushMessageTicketPair<TPushMessage>> filterAllMessagesWithError(
      List<ExpoPushMessageTicketPair<TPushMessage>> zippedMessagesTickets,
      TicketError ticketError) {

    return zippedMessagesTickets.stream()
        .filter(
            p ->
                p.ticket.getStatus() == Status.ERROR
                    && (ticketError == null || p.ticket.getDetails().getError() == ticketError))
        .collect(Collectors.toList());
  }

  public List<String> getTicketIdsFromPairs(
      List<ExpoPushMessageTicketPair<TPushMessage>> okTicketMessagePairs) {
    return getTicketIds(
        okTicketMessagePairs.stream().map(p -> p.ticket).collect(Collectors.toList()));
  }

  public List<String> getTicketIds(List<ExpoPushTicket> okTicketMessages) {
    return okTicketMessages.stream().map(t -> t.getId()).collect(Collectors.toList());
  }

  private class JsonReceiptHelper<T> {
    public List<T> ids;

    public JsonReceiptHelper(List<T> _ids) {
      ids = _ids;
    }
  }

  private <T> CompletableFuture<String> _postReceiptsAsync(URL url, List<T> receipts)
      throws CompletionException {
    JsonReceiptHelper<T> jsonReceiptHelper = new PushClient.JsonReceiptHelper(receipts);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = null;

    try {
      json = objectMapper.writeValueAsString(jsonReceiptHelper);
    } catch (JsonProcessingException e) {
      throw new CompletionException(e);
    }

    return pushServerResolver.postAsync(url, json);
  }

  public static boolean isExponentPushToken(String token) {
    String prefixA = "ExponentPushToken[";
    String prefixB = "ExpoPushToken[";
    String postfix = "]";
    String regex = "[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";

    if (token.matches(regex)) return true;
    if (!token.endsWith(postfix)) return false;
    if (token.startsWith(prefixA)) return true;
    if (token.startsWith(prefixB)) return true;
    return false;
  }

  public long _getActualMessagesCount(List<TPushMessage> messages) {
    return messages.stream().reduce(0, (acc, cur) -> acc + cur.getTo().size(), Integer::sum);
  }

  public List<List<String>> chunkPushNotificationReceiptIds(List<String> recieptIds) {
    return _chunkItems(recieptIds, PUSH_NOTIFICATION_RECEIPT_CHUNK_LIMIT);
  }

  public <T> List<List<T>> _chunkItems(List<T> items, long chunkSize) {
    List<List<T>> chunks = new ArrayList<>();
    List<T> chunk = new ArrayList<>();
    for (T item : items) {
      chunk.add(item);
      if (chunk.size() >= chunkSize) {
        chunks.add(chunk);
        chunk = new ArrayList<>();
      }
    }

    if (chunk.size() > 0) {
      chunks.add(chunk);
    }
    return chunks;
  }

  public List<List<TPushMessage>> chunkPushNotifications(List<TPushMessage> messages) {
    List<List<TPushMessage>> chunks = new ArrayList<>();
    List<TPushMessage> chunk = new ArrayList<>();

    long chunkMessagesCount = 0;
    for (TPushMessage message : messages) {
      List<String> partialTo = new ArrayList<>();
      for (String recipient : message.getTo()) {
        if (recipient.length() <= 0) continue;
        partialTo.add(recipient);
        chunkMessagesCount++;
        if (chunkMessagesCount >= PUSH_NOTIFICATION_CHUNK_LIMIT) {
          // Cap this chunk here if it already exceeds PUSH_NOTIFICATION_CHUNK_LIMIT.
          // Then create a new chunk to continue on the remaining recipients for this message.
          // Because we're using generics, we can't use the constructor. Instead, clone() the
          // message
          TPushMessage tmpCopy = (TPushMessage) message.clone();
          tmpCopy.setTo(partialTo);
          chunk.add(tmpCopy);
          chunks.add(chunk);
          chunk = new ArrayList<>();
          chunkMessagesCount = 0;
          partialTo = new ArrayList<>();
        }
      }

      if (partialTo.size() > 0) {
        TPushMessage tmpCopy = (TPushMessage) message.clone();
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
