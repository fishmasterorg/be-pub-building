package io.fishmaster.ms.be.pub.building.kafka.building.pub.trading.challenge.converter;

import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingTradingChallengeRefreshEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PubBuildingTradingChallengeKafkaConverter {

    public static KafkaPubBuildingTradingChallengeRefreshEvent toRefreshEventKafkaDto() {
        return KafkaPubBuildingTradingChallengeRefreshEvent.newBuilder()
                .setRefresh(true)
                .build();
    }

}
