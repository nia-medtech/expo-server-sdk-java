package com.niamedtech.expo.exposerversdk.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public final class ReceiptRequest {

  @NonNull
  private final List<String> ids;
}
