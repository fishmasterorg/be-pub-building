package io.fishmaster.ms.be.pub.building.db.mongo.repository.exchanger.exchange;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.exchange.PubBuildingExchangerExchangeHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.exchange.aggregation.PubBuildingEquipmentExchangeHistoryGroupedByBuildingId;

public interface PubBuildingExchangerExchangeHistoryRepository extends MongoRepository<PubBuildingExchangerExchangeHistory, ObjectId> {

    @Aggregation(pipeline = {
            "{$sort: {created_date: -1}}",
            "{$match: {account_id: ?0, building_id: ?1}}",
            "{$group: {_id: '$building_id', record: {$first: '$$ROOT'}}}"
    })
    AggregationResults<PubBuildingEquipmentExchangeHistoryGroupedByBuildingId> aggregateByAccountIdAndBuildingId(String accountId, Long buildingId);

    void deleteAllByAccountId(String accountId);

}
