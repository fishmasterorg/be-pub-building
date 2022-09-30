package io.fishmaster.ms.be.pub.building.service.building.pub.task.progress;

import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingDailyTaskProgress;

public interface PubBuildingDailyTaskProgressService {

    void refreshTaskProgress(KafkaPubBuildingDailyTaskProgress kafkaPubBuildingDailyTaskProgress);

}
