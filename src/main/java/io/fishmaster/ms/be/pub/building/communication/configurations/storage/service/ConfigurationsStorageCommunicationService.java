package io.fishmaster.ms.be.pub.building.communication.configurations.storage.service;

import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.PubBuildingConfigurationStateDto;

public interface ConfigurationsStorageCommunicationService {

    PubBuildingConfigurationStateDto getState();

    void clearState();

}
