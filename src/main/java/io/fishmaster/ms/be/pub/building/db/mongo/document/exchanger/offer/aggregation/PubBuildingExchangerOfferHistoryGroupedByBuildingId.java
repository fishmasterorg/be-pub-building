package io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.aggregation;

import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.PubBuildingExchangerOfferHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubBuildingExchangerOfferHistoryGroupedByBuildingId {
    private Long id;
    private PubBuildingExchangerOfferHistory record;
}
