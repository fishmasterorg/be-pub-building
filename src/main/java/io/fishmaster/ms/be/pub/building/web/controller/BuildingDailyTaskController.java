package io.fishmaster.ms.be.pub.building.web.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.fishmaster.ms.be.pub.building.service.building.pub.task.PubBuildingDailyTaskService;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskCanceledReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskCollectedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskExistsReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskStartedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.task.BuildingDailyTaskUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/building/task/daily")
@RestController
public class BuildingDailyTaskController {

    private final PubBuildingDailyTaskService pubBuildingDailyTaskService;

    @PostMapping("/fetch")
    public List<BuildingDailyTaskUiDto> fetch(@Valid @NotNull @RequestBody BuildingDailyTaskFetchedReqDto reqDto) {
        log.info("Request to fetch daily tasks in building received. {}", reqDto);
        var buildingDailyTaskUiDtos = pubBuildingDailyTaskService.fetch(reqDto);
        log.info("Response on fetch daily tasks in building. Size = {}", buildingDailyTaskUiDtos.size());
        return buildingDailyTaskUiDtos;
    }

    @PostMapping("/exists")
    public Boolean exists(@Valid @NotNull @RequestBody BuildingDailyTaskExistsReqDto reqDto) {
        log.info("Request to exists daily tasks in building received. {}", reqDto);
        var exists = pubBuildingDailyTaskService.exists(reqDto);
        log.info("Response on exists daily tasks in building. Exists = {}", exists);
        return exists;
    }

    @PostMapping("/start")
    public BuildingDailyTaskUiDto start(@Valid @NotNull @RequestBody BuildingDailyTaskStartedReqDto reqDto) {
        log.info("Request to start daily task in building received. {}", reqDto);
        var buildingDailyTaskUiDto = pubBuildingDailyTaskService.startTask(reqDto);
        log.info("Response on start daily task in building. {}", buildingDailyTaskUiDto);
        return buildingDailyTaskUiDto;
    }

    @PostMapping("/cancel")
    public BuildingDailyTaskUiDto cancel(@Valid @NotNull @RequestBody BuildingDailyTaskCanceledReqDto reqDto) {
        log.info("Request to cancel daily task in building received. {}", reqDto);
        var buildingDailyTaskUiDto = pubBuildingDailyTaskService.cancelTask(reqDto);
        log.info("Response on cancel daily task in building. {}", buildingDailyTaskUiDto);
        return buildingDailyTaskUiDto;
    }

    @PostMapping("/collect")
    public BuildingDailyTaskUiDto collect(@Valid @NotNull @RequestBody BuildingDailyTaskCollectedReqDto reqDto) {
        log.info("Request to collect daily task in building received. {}", reqDto);
        var buildingDailyTaskUiDto = pubBuildingDailyTaskService.collectTask(reqDto);
        log.info("Response on collect daily task in building. {}", buildingDailyTaskUiDto);
        return buildingDailyTaskUiDto;
    }

}
