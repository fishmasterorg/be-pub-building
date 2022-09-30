package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.util.Streamable;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class TradingChallengeConfigurationDtos implements Streamable<TradingChallengeConfigurationDto> {

    private final Streamable<TradingChallengeConfigurationDto> streamable;

    public Map<String, TradingChallengeConfigurationDto> getMapWithKeyById() {
        return streamable.stream()
                .collect(Collectors.toMap(TradingChallengeConfigurationDto::getId, Function.identity()));
    }

    public TradingChallengeConfigurationDto getById(String id) {
        var configurationDtoMap = getMapWithKeyById();
        if (!configurationDtoMap.containsKey(id)) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "Trading challenge slot configuration with id = %s not exists".formatted(id)
            );
        }
        return configurationDtoMap.get(id);
    }

    @Override
    public Iterator<TradingChallengeConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
