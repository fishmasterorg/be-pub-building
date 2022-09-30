package io.fishmaster.ms.be.pub.building.service.building.pub.task.progress.strategy;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.dto.building.pub.EnergyDailyTaskGoalDetailsDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.task.PubBuildingDailyTaskRepository;
import io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily.PubBuildingDailyTaskKafkaSender;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;

@Component
public class PubBuildingSpentEnergyDailyTaskProgressStrategyImpl extends BasePubBuildingDailyTaskProgressStrategyImpl {

    public PubBuildingSpentEnergyDailyTaskProgressStrategyImpl(
            ConfigurationsStorageService configurationsStorageService,
            PubBuildingDailyTaskKafkaSender pubBuildingDailyTaskKafkaSender,
            PubBuildingDailyTaskRepository pubBuildingDailyTaskRepository,
            ObjectMapper mapper) {
        super(configurationsStorageService, pubBuildingDailyTaskKafkaSender, pubBuildingDailyTaskRepository, mapper);
    }

    @Override
    void updateTaskProgress(
            String details, List<PubBuildingDailyTask> pubBuildingDailyTasks,
            Map<String, DailyTaskConfigurationDto> configurationDtoMap) {
        var detailsDto = readJson(details, EnergyDailyTaskGoalDetailsDto.class, mapper);

        pubBuildingDailyTasks.forEach(pubBuildingDailyTask -> {
            var finalProgress = pubBuildingDailyTask.getFinalProgress();

            var configurationDetails = configurationDtoMap.get(pubBuildingDailyTask.getConfigurationId())
                    .getGoal().getDetails();

            if (configurationDetails.getBuilding() == detailsDto.getBuilding()) {
                var newCurrentProgress = pubBuildingDailyTask.getCurrentProgress() + detailsDto.getQuantity();

                newCurrentProgress = newCurrentProgress > finalProgress ? finalProgress : newCurrentProgress;

                pubBuildingDailyTask.setCurrentProgress(newCurrentProgress);
            }

            if (finalProgress.equals(pubBuildingDailyTask.getCurrentProgress())) {
                pubBuildingDailyTask.setStatus(Status.DONE);
            }
        });
    }

    @Override
    public TaskGoalType getType() {
        return TaskGoalType.SPENT_ENERGY;
    }
}
