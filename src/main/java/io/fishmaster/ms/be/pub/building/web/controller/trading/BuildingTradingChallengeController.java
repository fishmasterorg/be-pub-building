package io.fishmaster.ms.be.pub.building.web.controller.trading;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.fishmaster.ms.be.commons.dto.PageUiDto;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.PubBuildingTradingChallengeService;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.challenge.BuildingTradingChallengeExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.trading.challenge.BuildingTradingChallengeFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.BuildingTradingChallengeHistoryUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.BuildingTradingChallengeUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/building/trading/challenge")
@RestController
public class BuildingTradingChallengeController {

    private final PubBuildingTradingChallengeService pubBuildingTradingChallengeService;

    @PostMapping("/fetch")
    public BuildingTradingChallengeUiDto fetchChallenge(@Valid @NotNull @RequestBody BuildingTradingChallengeFetchedReqDto reqDto) {
        log.info("Request to fetch trading challenge in building received. {}", reqDto);
        var buildingTradingChallengeUiDto = pubBuildingTradingChallengeService.fetchActive(reqDto);
        log.info("Response on fetch trading challenge in building. {}", buildingTradingChallengeUiDto);
        return buildingTradingChallengeUiDto;
    }

    @GetMapping("/history/fetch")
    public PageUiDto<BuildingTradingChallengeHistoryUiDto> fetchHistory(
            @Valid @NotNull @RequestParam(defaultValue = "${pagination.building.pub.challenge.food.page}") Integer page,
            @Valid @NotNull @RequestParam(defaultValue = "${pagination.building.pub.challenge.food.size}") Integer size) {
        log.info("Request to fetch trading challenge history in building received. Page = {}, size = {}", page, size);
        var pageUiDto = pubBuildingTradingChallengeService.fetchHistory(page, size);
        log.info("Response on fetch trading challenge history in building. {}", pageUiDto);
        return pageUiDto;
    }

    @PostMapping("/execute")
    public BuildingTradingChallengeHistoryUiDto execute(@Valid @NotNull @RequestBody BuildingTradingChallengeExecutedReqDto reqDto) {
        log.info("Request to execute trading challenge in building received. {}", reqDto);
        var buildingTradingChallengeHistoryUiDto = pubBuildingTradingChallengeService.executeChallenge(reqDto);
        log.info("Response on execute trading challenge in building. {}", buildingTradingChallengeHistoryUiDto);
        return buildingTradingChallengeHistoryUiDto;
    }

}
