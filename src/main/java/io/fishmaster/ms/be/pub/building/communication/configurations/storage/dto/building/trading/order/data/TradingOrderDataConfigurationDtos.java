package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.data;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class TradingOrderDataConfigurationDtos implements Streamable<TradingOrderDataConfigurationDto> {

    private final Streamable<TradingOrderDataConfigurationDto> streamable;

    public Map<String, TradingOrderDataConfigurationDto> getMapWithKeyById() {
        return streamable.stream()
                .collect(Collectors.toMap(TradingOrderDataConfigurationDto::getId, Function.identity()));
    }

    public Set<Long> getSetByCardId() {
        return streamable.stream()
                .map(TradingOrderDataConfigurationDto::getCardId)
                .collect(Collectors.toSet());
    }

    public TradingOrderDataConfigurationDto getById(String id) {
        var configurationDtoMap = getMapWithKeyById();
        if (!configurationDtoMap.containsKey(id)) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "Food trading order data configuration with id = %s not exists".formatted(id)
            );
        }
        return configurationDtoMap.get(id);
    }

    @Override
    public Iterator<TradingOrderDataConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
