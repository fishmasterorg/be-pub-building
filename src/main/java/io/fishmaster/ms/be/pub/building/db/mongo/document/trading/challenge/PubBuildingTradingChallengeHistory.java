package io.fishmaster.ms.be.pub.building.db.mongo.document.trading.challenge;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.character.Gender;
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
@Document(collection = "pub_building_trading_challenge_history")
public class PubBuildingTradingChallengeHistory {
    @Id
    private ObjectId id;
    private String requestId;

    private String accountId;
    private String accountNickname;

    private Long characterId;
    private CharacterSkin characterSkin;

    private Long buildingId;
    @Indexed(unique = true)
    private Long buildingTradingChallengeId;
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
    public static class CharacterSkin {
        private Gender gender;
        private Integer hair;
        private Integer face;
        private Integer body;
        private Integer cloth;
        private Integer attribute;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String configurationId;
        private Long cardId;
        private Integer cardsQuantity;
        private Double cost;
        private Double tax;
    }
}
