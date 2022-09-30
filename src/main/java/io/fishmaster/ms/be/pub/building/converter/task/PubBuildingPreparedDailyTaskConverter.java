package io.fishmaster.ms.be.pub.building.converter.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.task.WeekDay;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.prepared.PubBuildingPreparedDailyTask;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingPreparedDailyTaskConverter {

    public static List<PubBuildingPreparedDailyTask> toEntity(
            PubBuilding pubBuilding, WeekDay weekDay, List<DailyTaskConfigurationDto> configurationDtos) {
        return configurationDtos.stream()
                .map(dto -> toEntity(pubBuilding, weekDay, dto))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static PubBuildingPreparedDailyTask toEntity(
            PubBuilding pubBuilding, WeekDay weekDay, DailyTaskConfigurationDto configurationDto) {
        return new PubBuildingPreparedDailyTask(null, pubBuilding, weekDay, configurationDto.getId());
    }

}
