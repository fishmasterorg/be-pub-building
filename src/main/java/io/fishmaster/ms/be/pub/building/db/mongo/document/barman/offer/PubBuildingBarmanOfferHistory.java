package io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.model.result.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pub_building_barman_offer_history")
public class PubBuildingBarmanOfferHistory {
    @Id
    private ObjectId id;
    private String requestId;
    private String accountId;
    private Long buildingId;
    private Long characterId;
    private String configurationId;
    private Result result;
    private Status status;
    private Long createdDate;
    private Long lastModifiedDate;
}
