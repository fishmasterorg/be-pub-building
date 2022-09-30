package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.lottery;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.util.Iterator;
import java.util.Objects;

@RequiredArgsConstructor(staticName = "of")
public class LotteryPackConfigurationDtos implements Streamable<LotteryPackConfigurationDto> {

    private final Streamable<LotteryPackConfigurationDto> streamable;

    public LotteryPackConfigurationDto getById(String id) {
        return streamable.stream()
                .filter(dto -> Objects.equals(dto.getId(), id))
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Lottery pack configuration with id = %s not exists".formatted(id)
                ));
    }

    @Override
    public Iterator<LotteryPackConfigurationDto> iterator() {
        return streamable.iterator();
    }
}
