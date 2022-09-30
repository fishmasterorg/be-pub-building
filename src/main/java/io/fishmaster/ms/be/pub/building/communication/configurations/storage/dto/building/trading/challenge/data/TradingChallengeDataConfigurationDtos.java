package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.data;

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
public class TradingChallengeDataConfigurationDtos implements Streamable<TradingChallengeDataConfigurationDto> {

    private final Streamable<TradingChallengeDataConfigurationDto> streamable;

    public Map<String, TradingChallengeDataConfigurationDto> getMapWithKeyById() {
        return streamable.stream()
                .collect(Collectors.toMap(TradingChallengeDataConfigurationDto::getId, Function.identity()));
    }

    public Set<Long> getSetByCardId() {
        return streamable.stream()
                .map(TradingChallengeDataConfigurationDto::getCardId)
                .collect(Collectors.toSet());
    }

    public TradingChallengeDataConfigurationDto getById(String id) {
        var configurationDtoMap = getMapWithKeyById();
        if (!configurationDtoMap.containsKey(id)) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "Food trading challenge configuration with id = %s not exists".formatted(id)
            );
        }
        return configurationDtoMap.get(id);
    }

    @Override
    public Iterator<TradingChallengeDataConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
