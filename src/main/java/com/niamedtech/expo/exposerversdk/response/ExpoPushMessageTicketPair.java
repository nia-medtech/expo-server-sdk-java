package com.niamedtech.expo.exposerversdk.response;

public class ExpoPushMessageTicketPair<TPushMessage> {
  public TPushMessage message;
  public ExpoPushTicket ticket;

  ExpoPushMessageTicketPair(TPushMessage message, ExpoPushTicket ticket) {
    this.message = message;
    this.ticket = ticket;
  }
}
