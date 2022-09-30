package io.fishmaster.ms.be.pub.building.exception.communication;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.exception.model.ServiceErrorDto;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseResponseErrorHandler extends DefaultResponseErrorHandler {

    private static final String PROVIDER_ERROR_MESSAGE_TEMPLATE = "Server error [%s]: %s ";

    private final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

    abstract String getServiceName();

    abstract void handleError(ServiceErrorDto serviceErrorDto);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        fetchError(response).ifPresent(this::handleError);

        throwProviderException(response, response.getStatusCode());
    }

    private void throwProviderException(ClientHttpResponse response, HttpStatus status) throws IOException {
        throw new ServiceException(
                ExceptionCode.INNER_SERVICE,
                String.format(PROVIDER_ERROR_MESSAGE_TEMPLATE, getServiceName(), getErrorMessage(response, status))
        );
    }

    private String getErrorMessage(ClientHttpResponse response, HttpStatus status) throws IOException {
        return status + " " + IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);
    }

    private Optional<ServiceErrorDto> fetchError(ClientHttpResponse response) {
        try {
            return Optional.of((ServiceErrorDto) converter.read(ServiceErrorDto.class, response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
