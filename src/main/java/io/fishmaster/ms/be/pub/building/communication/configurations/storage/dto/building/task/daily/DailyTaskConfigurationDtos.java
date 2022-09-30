package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.daily;

import io.fishmaster.ms.be.commons.constant.task.TaskGoalType;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class DailyTaskConfigurationDtos implements Streamable<DailyTaskConfigurationDto> {

    private final Streamable<DailyTaskConfigurationDto> streamable;

    public Map<String, DailyTaskConfigurationDto> getMapWithKeyIdIn(Collection<String> ids) {
        return streamable.stream()
                .filter(dto -> ids.contains(dto.getId()))
                .collect(Collectors.toMap(DailyTaskConfigurationDto::getId, Function.identity()));
    }

    public Map<String, DailyTaskConfigurationDto> getMapWithKeyId() {
        return streamable.stream()
                .collect(Collectors.toMap(DailyTaskConfigurationDto::getId, Function.identity()));
    }

    public Set<String> getSetIdByGoalType(TaskGoalType type) {
        return streamable.stream()
                .filter(dto -> dto.getGoal().getType() == type)
                .map(DailyTaskConfigurationDto::getId)
                .collect(Collectors.toSet());
    }

    public DailyTaskConfigurationDto getById(String id) {
        return streamable.stream()
                .filter(dto -> Objects.equals(dto.getId(), id))
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Daily task configuration with id = %s not exists".formatted(id)
                ));
    }

    @Override
    public Iterator<DailyTaskConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
