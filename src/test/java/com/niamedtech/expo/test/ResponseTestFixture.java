package com.niamedtech.expo.test;

public final class ResponseTestFixture {

  private ResponseTestFixture() {
    throw new UnsupportedOperationException();
  }

  public static final String RECEIPT_ID_1 = "caa6fe6e-85d8-456c-bf57-e8cdc3fcb137";

  public static final String PUSH_SEND_OK_SINGLE_RESPONSE =
      """
            {
                "data": [
                    {
                        "status": "ok",
                         "id": "caa6fe6e-85d8-456c-bf57-e8cdc3fcb137"
                    }
                ]
            }
            """;

  public static final String GET_RECEIPT_OK_SINGLE_RESPONSE =
      """
            {
                "data": {
                    "caa6fe6e-85d8-456c-bf57-e8cdc3fcb137": {
                        "status": "ok"
                    }
                }
            }
            """;

  public static final String RECEIPT_ID_2 = "0d44e896-1a04-4409-a56e-a7383641cdb7";

  public static final String RECEIPT_ID_3 = "0d44e896-1a04-4409-a56e-a7383641cdb7";

  public static final String PUSH_SEND_OK_MULTIPLE_RESPONSE =
      """
            {
                "data":[
                    {
                        "status":"ok",
                        "id":"0d44e896-1a04-4409-a56e-a7383641cdb7"
                    },
                    {
                        "status":"ok",
                        "id":"d6d93f26-a037-44a4-844d-c5c458844e9b"
                    }
                ]
            }
            """;

  public static final String GET_RECEIPT_OK_MULTIPLE_RESPONSE =
      """
            {
                "data":{
                    "0d44e896-1a04-4409-a56e-a7383641cdb7":{
                        "status":"ok"
                    },
                    "d6d93f26-a037-44a4-844d-c5c458844e9b":{
                        "status":"ok"
                    }
                }
            }
            """;

  public static final String PUSH_SEND_VALIDATION_ERROR_RESPONSE =
      """
            {
                "errors":[
                    {
                        "code":"VALIDATION_ERROR",
                        "message":"[0].data must be of type object.",
                        "isTransient":false,
                        "requestId":"96995d9b-1530-48ad-8501-7410acd9d2c6"
                    }
                ]
            }
            """;

  public static final String PUSH_SEND_FCM_KEY_UNRETRIEVABLE =
      """
        {
        "data":[
            {
            "status":"error",
            "message":"Unable to retrieve the FCM server key for the recipient's app. Make sure you have provided a server key as directed by the Expo FCM documentation.",
            "details": {
                "error":"InvalidCredentials",
                "fault":"developer"
            }
            }
        ]
    """;
}
