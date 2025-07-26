package io.github.carped99.nsso.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class NetsSsoServerCheckResponse {
    boolean result;
    int errorCode;
    String errorMessage;
    String authStatus;
    String userId;
    String userAttribute;
    String token;
}
