package io.fishmaster.ms.be.pub.building.communication.city.self.service;

import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.req.CityFetchedReqDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.uri.CityUriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CityCommunicationServiceImpl implements CityCommunicationService {

    private final RestTemplate cityRestTemplate;
    private final CityUriService cityUriService;

    @Override
    public List<CityDto> fetchAllCity(CityFetchedReqDto reqDto) {
        var uri = cityUriService.getCityFetchUri();
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = new ParameterizedTypeReference<List<CityDto>>() {};

        try {
            log.info("Request to fetch all city send to city ms. {}", reqDto);
            var cityStateDtos = cityRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on fetch all city from city ms. Size = {}", cityStateDtos.size());
            return cityStateDtos;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from city ms on fetch all city request. %s. Error = %s"
                            .formatted(reqDto, e.getMessage()));
        }
    }

}
