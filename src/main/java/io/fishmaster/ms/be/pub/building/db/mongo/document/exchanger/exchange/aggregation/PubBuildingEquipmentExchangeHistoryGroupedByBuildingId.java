package io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.exchange.aggregation;

import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.exchange.PubBuildingExchangerExchangeHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubBuildingEquipmentExchangeHistoryGroupedByBuildingId {
    private Long id;
    private PubBuildingExchangerExchangeHistory record;
}
