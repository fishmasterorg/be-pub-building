package io.fishmaster.ms.be.pub.building.communication.configurations.storage.uri;


import io.fishmaster.ms.be.commons.constant.service.ServiceName;

import java.net.URI;

public interface ConfigurationsStorageUriService {

    URI getConfigurationStateUri(ServiceName serviceName);

}
