package io.fishmaster.ms.be.pub.building.service.building.pub.task.progress.strategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.kafka.dto.building.pub.KafkaPubBuildingDailyTaskProgress;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.task.PubBuildingDailyTaskRepository;
import io.fishmaster.ms.be.pub.building.kafka.building.pub.task.daily.PubBuildingDailyTaskKafkaSender;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BasePubBuildingDailyTaskProgressStrategyImpl implements PubBuildingDailyTaskProgressStrategy {

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingDailyTaskKafkaSender pubBuildingDailyTaskKafkaSender;

    private final PubBuildingDailyTaskRepository pubBuildingDailyTaskRepository;

    protected final ObjectMapper mapper;

    abstract void updateTaskProgress(
            String details, List<PubBuildingDailyTask> pubBuildingDailyTasks, Map<String, DailyTaskConfigurationDto> configurationDtoMap);

    @Transactional
    @Override
    public void refreshTaskProgress(KafkaPubBuildingDailyTaskProgress progress) {
        var dailyTaskConfigurations = configurationsStorageService.getDailyTaskConfigurations();

        var pubBuildingDailyTasks =
                pubBuildingDailyTaskRepository.findAllByAccountIdAndPubBuilding_CityIdAndStatusAndConfigurationIdIn(
                        progress.getAccountId(), progress.getCityId(), Status.IN_PROCESS,
                        dailyTaskConfigurations.getSetIdByGoalType(getType()));

        if (pubBuildingDailyTasks.isEmpty()) {
            return;
        }

        var configurationIds = pubBuildingDailyTasks.stream()
                .map(PubBuildingDailyTask::getConfigurationId)
                .collect(Collectors.toSet());

        var configurationDtoMap = dailyTaskConfigurations.getMapWithKeyIdIn(configurationIds);

        updateTaskProgress(progress.getDetails(), pubBuildingDailyTasks, configurationDtoMap);

        pubBuildingDailyTaskRepository.saveAll(pubBuildingDailyTasks)
                .forEach(pubBuildingDailyTaskKafkaSender::outcome);
    }

    protected <T> T readJson(String json, Class<T> tClass, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, tClass);
        } catch (Exception e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "Error while on read details json. %s. Error = %s".formatted(json, e.getMessage()));
        }
    }

}
