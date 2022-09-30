package io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.group;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.util.Streamable;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class ExchangerOfferGroupConfigurationDtos implements Streamable<ExchangerOfferGroupConfigurationDto> {

    private final Streamable<ExchangerOfferGroupConfigurationDto> streamable;

    public List<ExchangerOfferGroupConfigurationDto> getListByDate(Long date) {
        return streamable.stream()
                .filter(dto -> dto.getDate().getStart() < date && date < dto.getDate().getEnd())
                .toList();
    }

    public ExchangerOfferGroupConfigurationDto getByDateAndOfferId(Long date, String offerId) {
        return streamable.stream()
                .filter(dto -> dto.getDate().getStart() < date && date < dto.getDate().getEnd())
                .filter(dto -> dto.getOfferIds().contains(offerId))
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Exchanger offer group configuration with offer id = %s and date = %s not exists"
                                .formatted(offerId, date)));
    }

    @Override
    public Iterator<ExchangerOfferGroupConfigurationDto> iterator() {
        return streamable.iterator();
    }

}
