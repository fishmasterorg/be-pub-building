package io.fishmaster.ms.be.pub.building.kafka.building.pub.trading.challenge;

import static io.fishmaster.ms.be.commons.constant.kafka.KafkaCustomHeaders.SERVICE_TARGET;
import static io.fishmaster.ms.be.commons.constant.service.ServiceName.BE_API_GATEWAY;
import static io.fishmaster.ms.be.pub.building.kafka.building.pub.trading.challenge.converter.PubBuildingTradingChallengeKafkaConverter.toRefreshEventKafkaDto;
import static io.fishmaster.ms.be.pub.building.utility.KafkaUtility.toRecord;
import static io.fishmaster.ms.be.pub.building.utility.KafkaUtility.toServiceTargetHeaderValue;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.google.protobuf.TextFormat;

import io.fishmaster.ms.be.pub.building.configuration.kafka.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PubBuildingTradingChallengeKafkaSender {

    private final KafkaTemplate<String, byte[]> byteArrayKafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void refresh() {
        var kafkaPubBuildingTradingChallengeRefreshEvent = toRefreshEventKafkaDto();

        log.info("Send message for pub building trading challenge refresh: [{}]",
                TextFormat.printer().shortDebugString(kafkaPubBuildingTradingChallengeRefreshEvent));

        byteArrayKafkaTemplate.send(
                toRecord(
                        kafkaProperties.getTopic().getPubBuildingTradingChallengeRefresh(),
                        kafkaPubBuildingTradingChallengeRefreshEvent.toByteArray(),
                        Map.of(
                                SERVICE_TARGET.getHeaderName(), toServiceTargetHeaderValue(BE_API_GATEWAY)
                        )
                )
        );
    }

}
