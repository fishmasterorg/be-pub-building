package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class TradingOrderConfigurationDtos implements Streamable<TradingOrderConfigurationDto> {

    private final Streamable<TradingOrderConfigurationDto> streamable;

    public Map<String, TradingOrderConfigurationDto> getMapWithKeyById() {
        return streamable.stream()
                .collect(Collectors.toMap(TradingOrderConfigurationDto::getId, Function.identity()));
    }

    public TradingOrderConfigurationDto getById(String id) {
        var configurationDtoMap = getMapWithKeyById();
        if (!configurationDtoMap.containsKey(id)) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "Food trading order configuration with id = %s not exists".formatted(id)
            );
        }
        return configurationDtoMap.get(id);
    }

    @Override
    public Iterator<TradingOrderConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
