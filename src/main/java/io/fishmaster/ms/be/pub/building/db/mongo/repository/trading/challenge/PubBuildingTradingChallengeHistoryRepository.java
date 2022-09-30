package io.fishmaster.ms.be.pub.building.db.mongo.repository.trading.challenge;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.challenge.PubBuildingTradingChallengeHistory;
import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.challenge.aggregation.PubBuildingTradingChallengeHistoryGroupedByBuildingId;

public interface PubBuildingTradingChallengeHistoryRepository extends MongoRepository<PubBuildingTradingChallengeHistory, ObjectId> {

    @Aggregation(pipeline = {
            "{$sort: {created_date: -1}}",
            "{$match: {account_id: ?0, building_id: ?1}}",
            "{$group: {_id: '$building_id', record: {$first: '$$ROOT'}}}"
    })
    AggregationResults<PubBuildingTradingChallengeHistoryGroupedByBuildingId> aggregateByAccountIdAndBuildingId(
            String accountId, Long buildingId);

}
