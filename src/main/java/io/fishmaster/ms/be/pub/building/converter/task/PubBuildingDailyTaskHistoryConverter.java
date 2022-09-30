package io.fishmaster.ms.be.pub.building.converter.task;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import io.fishmaster.ms.be.commons.model.result.Result;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.ResultConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;
import io.fishmaster.ms.be.pub.building.db.mongo.document.task.PubBuildingDailyTaskHistory;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import io.fishmaster.ms.be.pub.building.web.dto.ui.task.BuildingDailyTaskUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingDailyTaskHistoryConverter {

    public static PubBuildingDailyTaskHistory toEntity(PubBuildingDailyTask pubBuildingDailyTask, Clock utcClock) {
        return toEntity(pubBuildingDailyTask, Set.of(), utcClock);
    }

    public static PubBuildingDailyTaskHistory toEntity(
            PubBuildingDailyTask pubBuildingDailyTask, Collection<AccountCardDto> accountCardDtos, Clock utcClock) {
        var pubBuilding = pubBuildingDailyTask.getPubBuilding();

        var result = new Result(null, ResultConverter.toResultCard(accountCardDtos), null, null);

        var now = utcClock.millis();
        return PubBuildingDailyTaskHistory.builder()
                .requestId(MDCUtility.getTraceId())
                .dailyTaskId(pubBuildingDailyTask.getId())
                .accountId(pubBuildingDailyTask.getAccountId())
                .buildingId(pubBuilding.getId())
                .characterIds(pubBuildingDailyTask.getCharacterIds())
                .configurationId(pubBuildingDailyTask.getConfigurationId())
                .currentProgress(pubBuildingDailyTask.getCurrentProgress())
                .finalProgress(pubBuildingDailyTask.getFinalProgress())
                .result(result)
                .status(pubBuildingDailyTask.getStatus())
                .createdDate(pubBuildingDailyTask.getCreatedDate())
                .lastModifiedDate(now)
                .build();
    }

    public static BuildingDailyTaskUiDto toUiDto(
            PubBuildingDailyTaskHistory pubBuildingDailyTaskHistory, DailyTaskConfigurationDto configurationDto) {
        return new BuildingDailyTaskUiDto(
                pubBuildingDailyTaskHistory.getDailyTaskId(), pubBuildingDailyTaskHistory.getBuildingId(),
                pubBuildingDailyTaskHistory.getCharacterIds(), toConfigurationUiDto(configurationDto),
                pubBuildingDailyTaskHistory.getCurrentProgress(), pubBuildingDailyTaskHistory.getFinalProgress(),
                ResultUiDto.of(pubBuildingDailyTaskHistory.getResult()), pubBuildingDailyTaskHistory.getStatus(),
                pubBuildingDailyTaskHistory.getCreatedDate());
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
                .map(PubBuildingDailyTaskHistoryConverter::toSpecialityUiDto)
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
