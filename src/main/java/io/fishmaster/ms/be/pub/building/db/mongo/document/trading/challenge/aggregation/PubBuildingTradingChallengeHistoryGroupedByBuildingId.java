package io.fishmaster.ms.be.pub.building.db.mongo.document.trading.challenge.aggregation;

import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.challenge.PubBuildingTradingChallengeHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubBuildingTradingChallengeHistoryGroupedByBuildingId {
    private Long id;
    private PubBuildingTradingChallengeHistory record;
}
