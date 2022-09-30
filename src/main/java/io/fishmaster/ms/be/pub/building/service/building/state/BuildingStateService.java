package io.fishmaster.ms.be.pub.building.service.building.state;

import io.fishmaster.ms.be.pub.building.web.dto.ui.state.PubBuildingStateUiDto;

public interface BuildingStateService {

    PubBuildingStateUiDto getStateForUi(String accountId, Long cityId);

}
