package io.fishmaster.ms.be.pub.building.exception.communication;

import io.fishmaster.ms.be.commons.constant.service.ServiceName;
import io.fishmaster.ms.be.commons.exception.RemoteServicePassThroughException;
import io.fishmaster.ms.be.commons.exception.model.ServiceErrorDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestTemplateErrorHandler extends BaseResponseErrorHandler {

    private final ServiceName serviceName;

    @Override
    String getServiceName() {
        return serviceName.getProviderName();
    }

    @Override
    void handleError(ServiceErrorDto serviceErrorDto) {
        throw new RemoteServicePassThroughException(serviceErrorDto, serviceName);
    }

}
