package com.niamedtech.expo.exposerversdk.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class PushNotification {

  public enum Priority {
    @JsonProperty("default")
    OK,
    @JsonProperty("high")
    ERROR,
    @JsonProperty("normal")
    NORMAL;
  }

  @Data
  public static final class Sound {
    private Boolean critical;
    private String name;
    private Long volume;

    public Sound(Sound other) {
      this.critical = other.critical;
      this.name = other.name;
      this.volume = other.volume;
    }
  }

  private List<String> to;

  private Map<String, Object> data;

  private String title;

  private String subtitle;

  private String body;

  private Sound sound;

  private Long ttl;

  private Long expiration;

  private Priority priority;

  private Long badge;

  private String channelId;

  public PushNotification(PushNotification other) {
    this.to = other.to;
    this.title = other.title;
    this.subtitle = other.subtitle;
    this.body = other.body;
    if (other.sound != null) {
      this.sound = new Sound(other.sound);
    }
    this.ttl = other.ttl;
    this.expiration = other.expiration;
    this.priority = other.priority;
    this.badge = other.badge;
    this.channelId = other.channelId;
  }
}
