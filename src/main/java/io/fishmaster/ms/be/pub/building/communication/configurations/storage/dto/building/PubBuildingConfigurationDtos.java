package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class PubBuildingConfigurationDtos implements Streamable<PubBuildingConfigurationDto> {

    private final Streamable<PubBuildingConfigurationDto> streamable;

    public Map<Integer, PubBuildingConfigurationDto> getMapWithKeyByLevel() {
        return streamable.stream()
                .collect(Collectors.toMap(PubBuildingConfigurationDto::getLevel, Function.identity()));
    }

    @Override
    public Iterator<PubBuildingConfigurationDto> iterator() {
        return stream().iterator();
    }
}
