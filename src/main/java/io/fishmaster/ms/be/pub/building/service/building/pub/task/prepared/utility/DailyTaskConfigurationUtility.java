package io.fishmaster.ms.be.pub.building.service.building.pub.task.prepared.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.util.concurrent.AtomicDouble;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.PubBuildingConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily.DailyTaskConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.level.TaskLevelConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.level.TaskLevelConfigurationDtos;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DailyTaskConfigurationUtility {

    @Getter
    @AllArgsConstructor
    private static class LevelChance {
        private Integer level;
        private Double chance;
        private Double bound;

        public static LevelChance of(TaskLevelConfigurationDto dto) {
            return new LevelChance(
                    dto.getLevel(), dto.getChance(), null
            );
        }

        public static LevelChance of(LevelChance chance, Double bound) {
            return new LevelChance(chance.getLevel(), chance.getChance(), bound);
        }
    }

    private final Map<Integer, List<DailyTaskConfigurationDto>> dailyTaskConfigurationMap;
    private final List<LevelChance> levelChances;
    private final Integer taskCount;

    public static DailyTaskConfigurationUtility of(DailyTaskConfigurationDtos dailyTaskConfigurationDtos,
                                                   TaskLevelConfigurationDtos taskLevelConfigurationDtos,
                                                   PubBuildingConfigurationDto configurationDto) {
        var taskConfigurationMap = dailyTaskConfigurationDtos.stream()
                .sorted(Comparator.comparing(DailyTaskConfigurationDto::getLevel))
                .filter(dto -> dto.getLevel() <= configurationDto.getTaskLevel())
                .collect(Collectors.groupingBy(DailyTaskConfigurationDto::getLevel));

        var bound = new AtomicDouble(0D);
        var levelChances = taskLevelConfigurationDtos.stream()
                .map(LevelChance::of)
                .sorted(Comparator.comparing(LevelChance::getLevel))
                .map(chance -> LevelChance.of(chance, bound.addAndGet(chance.getChance())))
                .filter(chance -> chance.getLevel() <= configurationDto.getTaskLevel())
                .toList();

        return new DailyTaskConfigurationUtility(taskConfigurationMap, levelChances, configurationDto.getMaxTasks());
    }

    public List<DailyTaskConfigurationDto> determineDailyTaskConfigurations() {
        return IntStream.range(0, taskCount)
                .mapToObj(value -> determineDailyTaskConfiguration())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private DailyTaskConfigurationDto determineDailyTaskConfiguration() {
        var taskLevel = determineTaskLevel();
        while (!dailyTaskConfigurationMap.containsKey(taskLevel)) {
            taskLevel = determineTaskLevel();
        }

        var taskConfigurationDtos = dailyTaskConfigurationMap.get(taskLevel);
        Collections.shuffle(taskConfigurationDtos);

        var randomIndex = ThreadLocalRandom.current().nextInt(0, taskConfigurationDtos.size());

        var taskConfigurationDto = taskConfigurationDtos.get(randomIndex);
        taskConfigurationDtos.remove(taskConfigurationDto);

        if (taskConfigurationDtos.isEmpty()) {
            dailyTaskConfigurationMap.remove(taskLevel);
        }
        return taskConfigurationDto;
    }

    private Integer determineTaskLevel() {
        var lastLevelChance = levelChances.get(levelChances.size() - 1);

        var randomNumber = ThreadLocalRandom.current().nextDouble(0, lastLevelChance.getBound());

        return levelChances.stream()
                .filter(chance -> chance.getBound() > randomNumber)
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Level chance with chance greater than %s not exist".formatted(randomNumber)
                ))
                .getLevel();
    }

}
