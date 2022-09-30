package io.fishmaster.ms.be.pub.building.configuration.rest.interceptor;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import io.fishmaster.ms.be.pub.building.utility.MDCUtility;

public class MDCRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        prepareRequest(request);
        ClientHttpResponse clientHttpResponse = execution.execute(request, body);
        processResponse(clientHttpResponse);
        return clientHttpResponse;
    }

    private void prepareRequest(HttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        headers.add(MDCUtility.X_TRACE_ID_HEADER, MDCUtility.getTraceId());
    }

    private void processResponse(ClientHttpResponse clientHttpResponse) {
        HttpHeaders headers = clientHttpResponse.getHeaders();
        var traceId = headers.getFirst(MDCUtility.X_TRACE_ID_HEADER);

        MDCUtility.putTraceId(traceId);
    }

}
