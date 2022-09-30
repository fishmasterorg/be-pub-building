package io.fishmaster.ms.be.pub.building.converter.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.prepared.PubBuildingPreparedDailyTask;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskStartedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.task.BuildingDailyTaskUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingDailyTaskConverter {

    public static PubBuildingDailyTask toEntityWithStatusInProcess(
            BuildingDailyTaskStartedReqDto reqDto, PubBuilding pubBuilding, DailyTaskConfigurationDto dailyTaskConfigurationDto) {
        var characterIds = new HashSet<>(reqDto.getSelectedCharacters().values());
        return new PubBuildingDailyTask(
                null, reqDto.getAccountId(), pubBuilding, characterIds, dailyTaskConfigurationDto.getId(),
                0, dailyTaskConfigurationDto.getGoal().getQuantity(), Status.IN_PROCESS);
    }

    public static BuildingDailyTaskUiDto toUiDto(PubBuildingPreparedDailyTask pubBuildingPreparedDailyTask,
            DailyTaskConfigurationDto configurationDto) {
        var pubBuilding = pubBuildingPreparedDailyTask.getPubBuilding();
        return new BuildingDailyTaskUiDto(
                null, pubBuilding.getId(), Set.of(), toConfigurationUiDto(configurationDto),
                0, configurationDto.getGoal().getQuantity(), ResultUiDto.of(),
                Status.CREATED, null);
    }

    public static BuildingDailyTaskUiDto toUiDto(PubBuildingDailyTask pubBuildingDailyTask, DailyTaskConfigurationDto configurationDto) {
        var pubBuilding = pubBuildingDailyTask.getPubBuilding();
        return new BuildingDailyTaskUiDto(
                pubBuildingDailyTask.getId(), pubBuilding.getId(), pubBuildingDailyTask.getCharacterIds(),
                toConfigurationUiDto(configurationDto), pubBuildingDailyTask.getCurrentProgress(),
                pubBuildingDailyTask.getFinalProgress(), ResultUiDto.of(), pubBuildingDailyTask.getStatus(),
                pubBuildingDailyTask.getCreatedDate());
    }

    private static BuildingDailyTaskUiDto.Configuration toConfigurationUiDto(DailyTaskConfigurationDto dailyTaskConfigurationDto) {
        return new BuildingDailyTaskUiDto.Configuration(
                dailyTaskConfigurationDto.getId(), dailyTaskConfigurationDto.getDisplayName(),
                dailyTaskConfigurationDto.getLevel(), toGoalUiDto(dailyTaskConfigurationDto.getGoal()),
                toRequiredUiDto(dailyTaskConfigurationDto.getRequired()), toResultUiDto(dailyTaskConfigurationDto.getResult()));
    }

    private static BuildingDailyTaskUiDto.Goal toGoalUiDto(DailyTaskConfigurationDto.Goal goal) {
        return new BuildingDailyTaskUiDto.Goal(goal.getDescription());
    }

    private static BuildingDailyTaskUiDto.Required toRequiredUiDto(DailyTaskConfigurationDto.Required required) {
        return new BuildingDailyTaskUiDto.Required(
                toSpecialityUiDto(required.getSpecialities()));
    }

    private static BuildingDailyTaskUiDto.Result toResultUiDto(DailyTaskConfigurationDto.Result result) {
        return new BuildingDailyTaskUiDto.Result(
                toCardUiDto(result.getCard()));
    }

    private static List<BuildingDailyTaskUiDto.Speciality> toSpecialityUiDto(List<DailyTaskConfigurationDto.Speciality> specialities) {
        return specialities.stream()
                .map(PubBuildingDailyTaskConverter::toSpecialityUiDto)
                .collect(Collectors.toList());
    }

    private static BuildingDailyTaskUiDto.Speciality toSpecialityUiDto(DailyTaskConfigurationDto.Speciality speciality) {
        return new BuildingDailyTaskUiDto.Speciality(
                speciality.getSequenceNumber(), speciality.getName(), speciality.getLevel());
    }

    private static BuildingDailyTaskUiDto.Card toCardUiDto(DailyTaskConfigurationDto.Card card) {
        return new BuildingDailyTaskUiDto.Card(card.getId(), card.getQuantity());
    }

}
