package io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.aggregation;

import io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.PubBuildingBarmanOfferHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubBuildingBarmanOfferHistoryGroupedByBuildingId {
    private Long id;
    private PubBuildingBarmanOfferHistory record;
}
