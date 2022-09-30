package io.fishmaster.ms.be.pub.building.web.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.fishmaster.ms.be.pub.building.service.building.pub.lottery.PubBuildingLotteryService;
import io.fishmaster.ms.be.pub.building.web.dto.req.lottery.BuildingLotteryExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.lottery.BuildingLotteryUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/building/lottery")
@RestController
public class BuildingLotteryController {

    private final PubBuildingLotteryService pubBuildingLotteryService;

    @PostMapping("/execute")
    public BuildingLotteryUiDto execute(@Valid @NotNull @RequestBody BuildingLotteryExecutedReqDto reqDto) {
        log.info("Request to execute lottery in building received. {}", reqDto);
        var buildingLotteryUiDto = pubBuildingLotteryService.executeLottery(reqDto);
        log.info("Response on execute lottery in building. {}", buildingLotteryUiDto);
        return buildingLotteryUiDto;
    }

}
