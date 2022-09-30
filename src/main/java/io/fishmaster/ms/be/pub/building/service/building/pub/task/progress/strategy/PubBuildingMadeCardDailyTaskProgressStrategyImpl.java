package io.fishmaster.ms.be.pub.building.service.building.pub.task.progress.strategy;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.dto.building.pub.CardDailyTaskGoalDetailsDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.task.PubBuildingDailyTaskRepository;
import io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily.PubBuildingDailyTaskKafkaSender;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;

@Component
public class PubBuildingMadeCardDailyTaskProgressStrategyImpl extends BasePubBuildingDailyTaskProgressStrategyImpl {

    public PubBuildingMadeCardDailyTaskProgressStrategyImpl(
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
        var detailsDto = readJson(details, CardDailyTaskGoalDetailsDto.class, mapper);

        pubBuildingDailyTasks.forEach(pubBuildingDailyTask -> {
            var finalProgress = pubBuildingDailyTask.getFinalProgress();

            var configurationDetails = configurationDtoMap.get(pubBuildingDailyTask.getConfigurationId())
                    .getGoal().getDetails();

            detailsDto.getCards().stream()
                    .filter(card -> configurationDetails.getType() == card.getType())
                    .filter(card -> configurationDetails.getBuilding() == card.getBuilding())
                    .filter(card -> configurationDetails.getLevels().contains(card.getLevel()))
                    .forEach(card -> {
                        var newCurrentProgress = pubBuildingDailyTask.getCurrentProgress() + 1;

                        newCurrentProgress = newCurrentProgress > finalProgress ? finalProgress : newCurrentProgress;

                        pubBuildingDailyTask.setCurrentProgress(newCurrentProgress);
                    });

            if (finalProgress.equals(pubBuildingDailyTask.getCurrentProgress())) {
                pubBuildingDailyTask.setStatus(Status.DONE);
            }
        });
    }

    @Override
    public TaskGoalType getType() {
        return TaskGoalType.MADE_CARD;
    }
}
