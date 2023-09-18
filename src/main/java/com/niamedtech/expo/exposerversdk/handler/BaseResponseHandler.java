package com.niamedtech.expo.exposerversdk.handler;

import java.io.IOException;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niamedtech.expo.exposerversdk.exception.ErrorResponseException;
import com.niamedtech.expo.exposerversdk.response.BaseResponse;
import com.niamedtech.expo.exposerversdk.util.ObjectMapperFactory;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles incoming HTTP responses and converts them into a deserialized objects using Jackson.
 */
@Slf4j
@AllArgsConstructor
public class BaseResponseHandler<T> implements HttpClientResponseHandler<T> {

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.getInstance();

    @NonNull
    private final Class<? extends BaseResponse<T>> responseClass;

    private BaseResponse<T> processResponse(ClassicHttpResponse httpResponse)
            throws HttpException, IOException {

        if (httpResponse.getEntity() != null) {
            final String content = EntityUtils.toString(httpResponse.getEntity());
            log.debug("Recived Content: {}", content);
            return OBJECT_MAPPER.readValue(content, responseClass);
        }
        throw new HttpException("Entity is null");
    }

    @Override
    public T handleResponse(ClassicHttpResponse httpResponse)
            throws HttpException, IOException {

        final BaseResponse<T> response = processResponse(httpResponse);
        if (httpResponse.getCode() == 200) {
            return response.getData();
        }

        throw new HttpException("Response with errors",
                new ErrorResponseException(httpResponse.getCode(), httpResponse.getReasonPhrase(),
                        response.getErrors()));
    }

}