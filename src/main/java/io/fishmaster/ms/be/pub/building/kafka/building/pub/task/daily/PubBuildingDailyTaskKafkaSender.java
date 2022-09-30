package io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily;

import static io.fishmaster.ms.be.commons.constant.kafka.KafkaCustomHeaders.SERVICE_TARGET;
import static io.fishmaster.ms.be.commons.constant.service.ServiceName.BE_API_GATEWAY;
import static io.fishmaster.ms.be.commons.constant.service.ServiceName.BE_PUB_BUILDING;
import static io.fishmaster.ms.be.pub.building.utility.KafkaUtility.toRecord;
import static io.fishmaster.ms.be.pub.building.utility.KafkaUtility.toServiceTargetHeaderValue;

import java.util.List;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.google.protobuf.TextFormat;

import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingDailyTaskProgress;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.configuration.kafka.properties.KafkaProperties;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;
import io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily.converter.PubBuildingDailyTaskKafkaConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PubBuildingDailyTaskKafkaSender {

    private final KafkaTemplate<String, byte[]> byteArrayKafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void outcome(PubBuildingDailyTask pubBuildingDailyTask) {
        var kafkaPubBuildingDailyTask = PubBuildingDailyTaskKafkaConverter.toKafkaDto(pubBuildingDailyTask);

        log.info("Send message for pub building daily task outcome: [{}]",
                TextFormat.printer().shortDebugString(kafkaPubBuildingDailyTask));

        byteArrayKafkaTemplate.send(
                toRecord(
                        kafkaProperties.getTopic().getPubBuildingDailyTaskOutcome(),
                        kafkaPubBuildingDailyTask.getAccountId(),
                        kafkaPubBuildingDailyTask.toByteArray(),
                        Map.of(
                                SERVICE_TARGET.getHeaderName(), toServiceTargetHeaderValue(BE_API_GATEWAY))));
    }

    public void refreshCardTaskProgress(TaskGoalType type, String accountId, Long characterId, Long cityId, BuildingType building,
            List<AccountCardDto> accountCardDtos) {
        refreshProgress(
                PubBuildingDailyTaskKafkaConverter.toCardProgressKafkaDto(type, accountId, characterId, cityId, building, accountCardDtos));
    }

    public void refreshEnergyTaskProgress(TaskGoalType type, String accountId, Long characterId, Long cityId, BuildingType building,
            Integer quantity) {
        refreshProgress(
                PubBuildingDailyTaskKafkaConverter.toEnergyProgressKafkaDto(type, accountId, characterId, cityId, building, quantity));
    }

    private void refreshProgress(KafkaPubBuildingDailyTaskProgress kafkaPubBuildingDailyTaskProgress) {
        log.info("Send message for pub building daily task outcome: [{}]",
                TextFormat.printer().shortDebugString(kafkaPubBuildingDailyTaskProgress));

        byteArrayKafkaTemplate.send(
                toRecord(
                        kafkaProperties.getTopic().getPubBuildingDailyTaskProgressRefresh(),
                        kafkaPubBuildingDailyTaskProgress.getAccountId(),
                        kafkaPubBuildingDailyTaskProgress.toByteArray(),
                        Map.of(
                                SERVICE_TARGET.getHeaderName(), toServiceTargetHeaderValue(BE_PUB_BUILDING))));
    }

}
