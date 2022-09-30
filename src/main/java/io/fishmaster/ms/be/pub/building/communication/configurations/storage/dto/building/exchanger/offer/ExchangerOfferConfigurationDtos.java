package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.data.util.Streamable;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class ExchangerOfferConfigurationDtos implements Streamable<ExchangerOfferConfigurationDto> {

    private final Streamable<ExchangerOfferConfigurationDto> streamable;

    public ExchangerOfferConfigurationDto getByIdAndLevelLessThanEqual(String id, Integer level) {
        return streamable.stream()
                .filter(dto -> dto.getLevel() <= level)
                .filter(dto -> Objects.equals(dto.getId(), id))
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Exchanger offer configuration with id = %s and level less than equal = %s not exists"
                                .formatted(id, level)
                ));
    }

    public <T> List<T> getListByIdIn(Collection<String> ids, Function<ExchangerOfferConfigurationDto, T> map) {
        return streamable.stream()
                .filter(dto -> ids.contains(dto.getId()))
                .map(map)
                .sorted()
                .toList();
    }

    @Override
    public Iterator<ExchangerOfferConfigurationDto> iterator() {
        return streamable.iterator();
    }

}
