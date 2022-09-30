package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.barman.offer;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Iterator;
import java.util.Objects;

@RequiredArgsConstructor(staticName = "of")
public class BarmanOfferConfigurationDtos implements Streamable<BarmanOfferConfigurationDto> {

    private final Streamable<BarmanOfferConfigurationDto> streamable;

    public BarmanOfferConfigurationDto getByIdAndLevelLessThanEqual(String id, Integer level) {
        return streamable.stream()
                .filter(dto -> dto.getLevel() <= level)
                .filter(dto -> Objects.equals(dto.getId(), id))
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Barman offer configuration with id = %s and level less than equal = %s not exists"
                                .formatted(id, level)
                ));
    }

    @Override
    public Iterator<BarmanOfferConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
