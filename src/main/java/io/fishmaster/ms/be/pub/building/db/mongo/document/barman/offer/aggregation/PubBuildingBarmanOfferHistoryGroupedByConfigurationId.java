package io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubBuildingBarmanOfferHistoryGroupedByConfigurationId {
    private String id;
    private Long count;
}
