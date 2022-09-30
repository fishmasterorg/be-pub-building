package io.fishmaster.ms.be.pub.building.service.building.pub.task;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskCanceledReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskCollectedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskExistsReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskStartedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.task.BuildingDailyTaskUiDto;

public interface PubBuildingDailyTaskService {

    List<BuildingDailyTaskUiDto> fetch(BuildingDailyTaskFetchedReqDto reqDto);

    default CompletableFuture<List<BuildingDailyTaskUiDto>> fetchAsync(BuildingDailyTaskFetchedReqDto reqDto, Executor executor) {
        return CompletableFuture.supplyAsync(() -> fetch(reqDto), executor);
    }

    Boolean exists(BuildingDailyTaskExistsReqDto reqDto);

    BuildingDailyTaskUiDto startTask(BuildingDailyTaskStartedReqDto reqDto);

    BuildingDailyTaskUiDto cancelTask(BuildingDailyTaskCanceledReqDto reqDto);

    BuildingDailyTaskUiDto collectTask(BuildingDailyTaskCollectedReqDto reqDto);

}
