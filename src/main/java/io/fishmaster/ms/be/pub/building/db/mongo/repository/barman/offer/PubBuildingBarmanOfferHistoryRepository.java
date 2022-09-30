package io.fishmaster.ms.be.pub.building.db.mongo.repository.barman.offer;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.PubBuildingBarmanOfferHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.aggregation.PubBuildingBarmanOfferHistoryGroupedByBuildingId;
import io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.aggregation.PubBuildingBarmanOfferHistoryGroupedByConfigurationId;

public interface PubBuildingBarmanOfferHistoryRepository extends MongoRepository<PubBuildingBarmanOfferHistory, ObjectId> {

    @Aggregation(pipeline = {
            "{$match: {account_id: ?0, building_id: ?1, status: ?2, created_date: {$gte: ?3, $lt: ?4}}}",
            "{$group : {_id: '$configuration_id', count: {$sum: 1}}}"
    })
    AggregationResults<PubBuildingBarmanOfferHistoryGroupedByConfigurationId> aggregateByAccountIdAndBuildingIdAndStatusAndCreatedDateBetween(
            String accountId, Long buildingId, Status status, Long beforeCreatedDate, Long afterCreatedDate
    );

    @Aggregation(pipeline = {
            "{$sort: {created_date: -1}}",
            "{$match: {account_id: ?0, building_id: ?1}}",
            "{$group: {_id: '$building_id', record: {$first: '$$ROOT'}}}"
    })
    AggregationResults<PubBuildingBarmanOfferHistoryGroupedByBuildingId> aggregateByAccountIdAndBuildingId(String accountId, Long buildingId);

    Long countAllByAccountIdAndBuildingIdAndConfigurationIdAndStatusAndCreatedDateBetween(String accountId, Long buildingId,
                                                                                          String configurationId, Status status,
                                                                                          Long beforeCreatedDate, Long afterCreatedDate);

    void deleteAllByAccountId(String accountId);

}
