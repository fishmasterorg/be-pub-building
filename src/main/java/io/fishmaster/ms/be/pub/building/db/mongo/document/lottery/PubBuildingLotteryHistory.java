package io.fishmaster.ms.be.pub.building.db.mongo.document.lottery;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pub_building_lottery_history")
public class PubBuildingLotteryHistory {
    @Id
    private ObjectId id;
    private String requestId;
    private String accountId;
    private Long buildingId;
    private String configurationId;
    private Card winnerCard;
    private List<Card> imitateCards;
    private Set<Long> collectedCardIds;
    private Long createdDate;
    private Long lastModifiedDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Card {
        private Long cardId;
        private Integer quantity;
    }
}
