package io.fishmaster.ms.be.pub.building.communication.configurations.storage.uri;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import io.fishmaster.ms.be.commons.constant.service.ServiceName;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.properties.ConfigurationsStorageProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ConfigurationsStorageUriServiceImpl implements ConfigurationsStorageUriService {

    private static final String SERVICE_NAME_PARAM = "serviceName";

    private final ConfigurationsStorageProperties configurationsStorageProperties;

    @Override
    public URI getConfigurationStateUri(ServiceName serviceName) {
        return UriComponentsBuilder.fromUriString(configurationsStorageProperties.getUri())
                .path(configurationsStorageProperties.getPath().getConfigurationState())
                .queryParam(SERVICE_NAME_PARAM, serviceName)
                .build()
                .encode()
                .toUri();
    }

}
