package io.fishmaster.ms.be.pub.building.communication.configurations.storage.service;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.fishmaster.ms.be.commons.constant.service.ServiceName;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.PubBuildingConfigurationStateDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.uri.ConfigurationsStorageUriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConfigurationsStorageCommunicationServiceImpl implements ConfigurationsStorageCommunicationService {

    private static final String CONFIGURATION_STATE_CACHE = "configurationStateCache";

    private final RestTemplate configurationsStorageRestTemplate;
    private final ConfigurationsStorageUriService configurationsStorageUriService;

    @Cacheable(value = CONFIGURATION_STATE_CACHE)
    @Override
    public PubBuildingConfigurationStateDto getState() {
        var uri = configurationsStorageUriService.getConfigurationStateUri(ServiceName.BE_PUB_BUILDING);
        var httpEntity = HttpEntity.EMPTY;
        var responseType = PubBuildingConfigurationStateDto.class;

        try {
            log.info("Request to get configuration state send to configurations storage ms.");
            var configurationStateDto = configurationsStorageRestTemplate.exchange(uri, GET, httpEntity, responseType).getBody();
            log.info("Response on get configuration state from configurations storage ms. {}", configurationStateDto);
            return configurationStateDto;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from configurations storage ms on get configuration state request. Error = %s"
                            .formatted(e.getMessage())
            );
        }
    }

    @CacheEvict(value = CONFIGURATION_STATE_CACHE, allEntries = true)
    @Override
    public void clearState() {
        log.info("[CACHE] Cleared configuration state cache!");
    }

}
