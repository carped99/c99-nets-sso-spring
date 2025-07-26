package io.github.carped99.nsso.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class NetsSsoServerLogonResponse {
    boolean result;
    int errorCode;
    String errorMessage;
    String policyVersion;
    String gateUrl;
    String pathESSO;
    String ssoResponse;
    String artifactESSO;
}
