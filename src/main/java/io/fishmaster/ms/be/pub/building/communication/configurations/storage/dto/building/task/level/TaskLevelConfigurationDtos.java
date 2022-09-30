package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.task.level;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Iterator;

@RequiredArgsConstructor(staticName = "of")
public class TaskLevelConfigurationDtos implements Streamable<TaskLevelConfigurationDto> {

    private final Streamable<TaskLevelConfigurationDto> streamable;

    @Override
    public Iterator<TaskLevelConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
