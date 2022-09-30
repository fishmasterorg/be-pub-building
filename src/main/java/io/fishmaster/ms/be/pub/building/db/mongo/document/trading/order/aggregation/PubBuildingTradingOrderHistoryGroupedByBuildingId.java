package io.fishmaster.ms.be.pub.building.db.mongo.document.trading.order.aggregation;

import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.order.PubBuildingTradingOrderHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubBuildingTradingOrderHistoryGroupedByBuildingId {
    private Long id;
    private PubBuildingTradingOrderHistory record;
}
