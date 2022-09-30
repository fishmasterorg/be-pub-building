package io.fishmaster.ms.be.pub.building.db.mongo.repository.exchanger.offer;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.PubBuildingExchangerOfferHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.aggregation.PubBuildingExchangerOfferHistoryGroupedByBuildingId;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.aggregation.PubBuildingExchangerOfferHistoryGroupedByConfigurationId;

public interface PubBuildingExchangerOfferHistoryRepository extends MongoRepository<PubBuildingExchangerOfferHistory, ObjectId> {

    @Aggregation(pipeline = {
            "{$match: {account_id: ?0, building_id: ?1, status: ?2, created_date: {$gte: ?3, $lt: ?4}}}",
            "{$group : {_id: '$configuration_id', count: {$sum: 1}}}"
    })
    AggregationResults<PubBuildingExchangerOfferHistoryGroupedByConfigurationId> aggregateByAccountIdAndBuildingIdAndStatusAndCreatedDateBetween(
            String accountId, Long buildingId, Status status, Long beforeCreatedDate, Long afterCreatedDate
    );

    @Aggregation(pipeline = {
            "{$sort: {created_date: -1}}",
            "{$match: {account_id: ?0, building_id: ?1}}",
            "{$group: {_id: '$building_id', record: {$first: '$$ROOT'}}}"
    })
    AggregationResults<PubBuildingExchangerOfferHistoryGroupedByBuildingId> aggregateByAccountIdAndBuildingId(String accountId, Long buildingId);

    Long countAllByAccountIdAndBuildingIdAndConfigurationIdAndStatusAndCreatedDateBetween(String accountId, Long buildingId,
                                                                                          String configurationId, Status status,
                                                                                          Long beforeCreatedDate, Long afterCreatedDate);

    void deleteAllByAccountId(String accountId);

}
