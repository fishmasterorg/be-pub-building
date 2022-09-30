package io.fishmaster.ms.be.pub.building.service.building.pub;

import io.fishmaster.ms.be.pub.building.web.dto.ui.PubBuildingUiDto;

public interface PubBuildingService {
    
    PubBuildingUiDto getUiDto(String accountId, Long cityId);

}