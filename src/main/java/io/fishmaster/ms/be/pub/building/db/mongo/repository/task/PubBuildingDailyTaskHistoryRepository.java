package io.fishmaster.ms.be.pub.building.db.mongo.repository.task;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.pub.building.db.mongo.document.task.PubBuildingDailyTaskHistory;

public interface PubBuildingDailyTaskHistoryRepository extends MongoRepository<PubBuildingDailyTaskHistory, ObjectId> {

    List<PubBuildingDailyTaskHistory> findAllByAccountIdAndBuildingIdAndStatusAndCreatedDateBetween(
            String accountId, Long buildingId, Status status, Long beforeCreatedDate, Long afterCreatedDate);

    boolean existsByAccountIdAndBuildingIdAndConfigurationIdAndStatusAndCreatedDateBetween(
            String accountId, Long buildingId, String configurationId, Status status, Long beforeCreatedDate, Long afterCreatedDate);

    void deleteAllByAccountId(String accountId);

}
