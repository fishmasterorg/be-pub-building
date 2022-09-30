package io.fishmaster.ms.be.pub.building.service.building.pub.task.prepared;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fishmaster.ms.be.commons.constant.task.WeekDay;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.PubBuildingConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.level.TaskLevelConfigurationDtos;
import io.fishmaster.ms.be.pub.building.converter.task.PubBuildingPreparedDailyTaskConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.prepared.PubBuildingPreparedDailyTask;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.task.prepared.PubBuildingPreparedDailyTaskRepository;
import io.fishmaster.ms.be.pub.building.service.building.pub.task.prepared.utility.DailyTaskConfigurationUtility;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PubBuildingPreparedDailyTaskServiceImpl implements PubBuildingPreparedDailyTaskService {

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingRepository pubBuildingRepository;
    private final PubBuildingPreparedDailyTaskRepository pubBuildingPreparedDailyTaskRepository;

    @Value("${pagination.building.pub.limit}")
    private Integer pubBuildingLimit;

    @Transactional
    @Override
    public void prepareDailyTasksForWeekDay(WeekDay weekDay) {
        var page = new AtomicInteger(0);

        var taskLevelConfigurationDtos = configurationsStorageService.getTaskLevelConfigurations();
        var dailyTaskConfigurationDtos = configurationsStorageService.getDailyTaskConfigurations();

        var pubBuildingConfigurationDtoMap = configurationsStorageService.getPubBuildingConfigurations()
                .getMapWithKeyByLevel();

        var pubBuildings = pubBuildingRepository.findAllWithPagination(page.getAndIncrement(), pubBuildingLimit);

        while (!pubBuildings.isEmpty()) {
            var pubBuildingIds = pubBuildings.stream()
                    .map(PubBuilding::getId)
                    .collect(Collectors.toSet());

            var pubBuildingTasksMap = pubBuildingPreparedDailyTaskRepository.findAllByPubBuilding_IdInAndWeekDay(
                    pubBuildingIds, weekDay).stream().collect(Collectors.groupingBy(task -> task.getPubBuilding().getId()));

            var pubBuildingTasks = pubBuildings.stream()
                    .map(prepareDailyTasksForBuilding(
                            weekDay, pubBuildingTasksMap, taskLevelConfigurationDtos,
                            dailyTaskConfigurationDtos, pubBuildingConfigurationDtoMap))
                    .flatMap(List::stream)
                    .toList();

            pubBuildingPreparedDailyTaskRepository.saveAll(pubBuildingTasks);

            pubBuildings = pubBuildingRepository.findAllWithPagination(page.getAndIncrement(), pubBuildingLimit);
        }
    }

    private Function<PubBuilding, List<PubBuildingPreparedDailyTask>> prepareDailyTasksForBuilding(
            WeekDay weekDay, Map<Long, List<PubBuildingPreparedDailyTask>> pubBuildingTasksMap,
            TaskLevelConfigurationDtos taskLevelConfigurationDtos, DailyTaskConfigurationDtos dailyTaskConfigurationDtos,
            Map<Integer, PubBuildingConfigurationDto> configurationDtoMap) {
        return building -> {
            var pubBuildingTasks = pubBuildingTasksMap.getOrDefault(building.getId(), List.of());
            var configurationDto = configurationDtoMap.get(building.getLevel());
            if (Objects.isNull(configurationDto)) {
                throw new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Pub building configuration with level = %s not exists".formatted(building.getLevel()));
            }
            var determinedDailyTaskConfigurationDtos = DailyTaskConfigurationUtility.of(
                    dailyTaskConfigurationDtos, taskLevelConfigurationDtos, configurationDto).determineDailyTaskConfigurations();

            return getPubBuildingPreparedDailyTasks(weekDay, building, pubBuildingTasks, determinedDailyTaskConfigurationDtos);
        };
    }

    private List<PubBuildingPreparedDailyTask> getPubBuildingPreparedDailyTasks(WeekDay weekDay, PubBuilding building,
            List<PubBuildingPreparedDailyTask> pubBuildingPreparedDailyTasks,
            List<DailyTaskConfigurationDto> dailyTaskConfigurationDtos) {
        if (pubBuildingPreparedDailyTasks.isEmpty()) {
            return PubBuildingPreparedDailyTaskConverter.toEntity(building, weekDay, dailyTaskConfigurationDtos);
        }

        pubBuildingPreparedDailyTasks.forEach(pubBuildingPreparedDailyTask -> {
            var dailyTaskConfigurationDto = dailyTaskConfigurationDtos.get(dailyTaskConfigurationDtos.size() - 1);
            dailyTaskConfigurationDtos.remove(dailyTaskConfigurationDto);

            pubBuildingPreparedDailyTask.setConfigurationId(dailyTaskConfigurationDto.getId());
        });

        if (!dailyTaskConfigurationDtos.isEmpty()) {
            pubBuildingPreparedDailyTasks.addAll(
                    PubBuildingPreparedDailyTaskConverter.toEntity(building, weekDay, dailyTaskConfigurationDtos));
        }
        return pubBuildingPreparedDailyTasks;
    }

}
