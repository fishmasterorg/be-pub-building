package io.fishmaster.ms.be.pub.building.db.mongo.repository.lottery;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.fishmaster.ms.be.pub.building.db.mongo.document.lottery.PubBuildingLotteryHistory;

public interface PubBuildingLotteryHistoryRepository extends MongoRepository<PubBuildingLotteryHistory, ObjectId> {

    void deleteAllByAccountId(String accountId);

}
