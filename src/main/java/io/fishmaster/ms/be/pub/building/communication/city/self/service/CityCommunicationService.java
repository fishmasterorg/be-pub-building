package io.fishmaster.ms.be.pub.building.communication.city.self.service;

import java.util.List;
import java.util.Set;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.req.CityFetchedReqDto;

public interface CityCommunicationService {

    List<CityDto> fetchAllCity(CityFetchedReqDto reqDto);

    default CityDto fetchCity(Long cityId) {
        var cityDtos = fetchAllCity(CityFetchedReqDto.of(Set.of(cityId)));
        if (cityDtos.isEmpty()) {
            throw new ServiceException(
                ExceptionCode.INNER_SERVICE,
                "City with id = %s not exists".formatted(cityId)
            );
        }
        return cityDtos.get(0);
    }
    
}
