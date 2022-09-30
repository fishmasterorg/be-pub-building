package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe;

import java.util.Iterator;
import java.util.Objects;

import org.springframework.data.util.Streamable;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class ExchangerExchangeRecipeConfigurationDtos implements Streamable<ExchangerExchangeRecipeConfigurationDto> {

    private final Streamable<ExchangerExchangeRecipeConfigurationDto> streamable;

    public ExchangerExchangeRecipeConfigurationDto getByIdAndLevelLessThanEqual(String id, Integer level) {
        return streamable.stream()
                .filter(dto -> dto.getLevel() <= level)
                .filter(dto -> Objects.equals(dto.getId(), id))
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Exchanger exchange recipe configuration with id = %s and level less than equal = %s not exists"
                                .formatted(id, level)));
    }

    @Override
    public Iterator<ExchangerExchangeRecipeConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
