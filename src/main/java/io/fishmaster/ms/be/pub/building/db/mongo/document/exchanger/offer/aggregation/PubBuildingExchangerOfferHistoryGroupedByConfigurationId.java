package io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubBuildingExchangerOfferHistoryGroupedByConfigurationId {
    private String id;
    private Long count;
}
