package io.fishmaster.ms.be.pub.building.service.building.pub.task.progress;

import java.util.List;

import org.springframework.stereotype.Service;

import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingDailyTaskProgress;
import io.fishmaster.ms.be.pub.building.service.building.pub.task.progress.strategy.PubBuildingDailyTaskProgressStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PubBuildingDailyTaskProgressServiceImpl implements PubBuildingDailyTaskProgressService {

    private final List<PubBuildingDailyTaskProgressStrategy> pubBuildingDailyTaskProgressStrategies;

    @Override
    public void refreshTaskProgress(KafkaPubBuildingDailyTaskProgress kafkaPubBuildingDailyTaskProgress) {
        var taskGoalType = TaskGoalType.valueOf(kafkaPubBuildingDailyTaskProgress.getGoalType());
        pubBuildingDailyTaskProgressStrategies.stream()
                .filter(strategy -> strategy.getType() == taskGoalType)
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Pub building daily task progress strategy with goal type = %s not exists".formatted(taskGoalType)
                ))
                .refreshTaskProgress(kafkaPubBuildingDailyTaskProgress);
    }
}
