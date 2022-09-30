package io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily.converter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.dto.building.pub.CardDailyTaskGoalDetailsDto;
import io.fishmaster.ms.be.commons.dto.building.pub.EnergyDailyTaskGoalDetailsDto;
import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingDailyTask;
import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingDailyTaskProgress;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingDailyTaskKafkaConverter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static KafkaPubBuildingDailyTask toKafkaDto(PubBuildingDailyTask pubBuildingDailyTask) {
        var pubBuilding = pubBuildingDailyTask.getPubBuilding();
        return KafkaPubBuildingDailyTask.newBuilder()
                .setId(pubBuildingDailyTask.getId())
                .setAccountId(pubBuildingDailyTask.getAccountId())
                .setBuildingId(pubBuilding.getId())
                .addAllCharacterIds(pubBuildingDailyTask.getCharacterIds())
                .setConfigurationId(pubBuildingDailyTask.getConfigurationId())
                .setCurrentProgress(pubBuildingDailyTask.getCurrentProgress())
                .setFinalProgress(pubBuildingDailyTask.getFinalProgress())
                .addAllCollectedCardIds(Set.of())
                .setStatus(pubBuildingDailyTask.getStatus().name())
                .setCreatedDate(pubBuildingDailyTask.getCreatedDate())
                .build();
    }

    public static KafkaPubBuildingDailyTaskProgress toCardProgressKafkaDto(
            TaskGoalType taskGoalType, String accountId, Long characterId, Long cityId, BuildingType buildingType,
            List<AccountCardDto> accountCardDtos) {
        var detailsDto = accountCardDtos.stream()
                .map(accountCardDto -> {
                    var card = accountCardDto.getCard();
                    var level = Objects.isNull(card.getMeta().getLevel()) ? 0 : card.getMeta().getLevel();

                    return new CardDailyTaskGoalDetailsDto.Card(accountCardDto.getId(), card.getType(), buildingType, level);
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), CardDailyTaskGoalDetailsDto::new));

        return toProgressKafkaDto(accountId, characterId, cityId, taskGoalType, writeJson(detailsDto));
    }

    public static KafkaPubBuildingDailyTaskProgress toEnergyProgressKafkaDto(
            TaskGoalType taskGoalType, String accountId, Long characterId, Long cityId, BuildingType buildingType, Integer quantity) {
        var detailsDto = new EnergyDailyTaskGoalDetailsDto(buildingType, quantity);
        return toProgressKafkaDto(accountId, characterId, cityId, taskGoalType, writeJson(detailsDto));
    }

    private static KafkaPubBuildingDailyTaskProgress toProgressKafkaDto(
            String accountId, Long characterId, Long cityId, TaskGoalType taskGoalType, String details) {
        return KafkaPubBuildingDailyTaskProgress.newBuilder()
                .setAccountId(accountId)
                .setCharacterId(characterId)
                .setCityId(cityId)
                .setGoalType(taskGoalType.name())
                .setDetails(details)
                .build();
    }

    private static String writeJson(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            return "";
        }
    }

}
