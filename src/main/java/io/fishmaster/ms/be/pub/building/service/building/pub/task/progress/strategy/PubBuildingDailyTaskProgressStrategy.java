package io.fishmaster.ms.be.pub.building.service.building.pub.task.progress.strategy;

import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingDailyTaskProgress;

public interface PubBuildingDailyTaskProgressStrategy {

    void refreshTaskProgress(KafkaPubBuildingDailyTaskProgress kafkaPubBuildingDailyTaskProgress);

    TaskGoalType getType();

}
