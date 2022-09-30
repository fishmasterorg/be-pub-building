package io.fishmaster.ms.be.pub.building.db.mongo.document.trading.order;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.model.result.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pub_building_trading_order_history")
public class PubBuildingTradingOrderHistory {
    @Id
    private ObjectId id;
    private String requestId;
    private String accountId;
    private Long characterId;

    private Long buildingId;
    @Indexed(unique = true)
    private Long buildingTradingOrderId;
    private String configurationId;

    private Data data;
    private Result result;
    private Status status;

    private Long createdDate;
    private Long lastModifiedDate;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String configurationId;
        private Long cardId;
        private Integer cardsQuantity;
        private Double energyForDeliver;
        private Double energyForCancel;
        private Double cost;
        private Double tax;
    }
}
