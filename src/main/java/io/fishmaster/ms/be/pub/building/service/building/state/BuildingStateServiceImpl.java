package io.fishmaster.ms.be.pub.building.service.building.state;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fishmaster.ms.be.pub.building.service.building.pub.PubBuildingService;
import io.fishmaster.ms.be.pub.building.web.dto.ui.state.PubBuildingStateUiDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BuildingStateServiceImpl implements BuildingStateService {

    private final PubBuildingService pubBuildingService;

    @Transactional
    @Override
    public PubBuildingStateUiDto getStateForUi(String accountId, Long cityId) {
        var pubBuildingUiDto = pubBuildingService.getUiDto(accountId, cityId);
        return new PubBuildingStateUiDto(pubBuildingUiDto);
    }
    
}
