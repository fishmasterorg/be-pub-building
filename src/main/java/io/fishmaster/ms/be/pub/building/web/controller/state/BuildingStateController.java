package io.fishmaster.ms.be.pub.building.web.controller.state;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.fishmaster.ms.be.pub.building.service.building.state.BuildingStateService;
import io.fishmaster.ms.be.pub.building.web.dto.ui.state.PubBuildingStateUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/building/state")
@RestController
public class BuildingStateController {

    private final BuildingStateService pubBuildingStateService;

    @GetMapping("/ui")
    public PubBuildingStateUiDto getBuildingStateForUi(
            @Valid @NotNull @RequestParam Long cityId,
            @Valid @NotNull @RequestParam String accountId) {
        log.info("Request to get building state for ui received. Account id = {}, city id = {}", accountId, cityId);
        var pubBuildingStateUiDto = pubBuildingStateService.getStateForUi(accountId, cityId);
        log.info("Response on get building state for ui. {}", pubBuildingStateUiDto);
        return pubBuildingStateUiDto;
    }

}
