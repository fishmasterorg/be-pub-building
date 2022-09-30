package io.fishmaster.ms.be.pub.building.communication.city.balance.service;

import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationResDto;
import io.fishmaster.ms.be.pub.building.communication.city.balance.uri.CityBalanceUriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CityBalanceCommunicationServiceImpl implements CityBalanceCommunicationService {

    private final RestTemplate cityBalanceRestTemplate;
    private final CityBalanceUriService cityBalanceUriService;

    @Override
    public CityBalanceOperationResDto handleOperation(CityBalanceOperationReqDto reqDto) {
        var uri = cityBalanceUriService.getHandleOperationUri();
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = CityBalanceOperationResDto.class;

        try {
            log.info("Request to handle city balance operation send to city balance ms. {}", reqDto);
            var cityBalanceOperationResDto = cityBalanceRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on handle city balance operation from city balance ms. {}", cityBalanceOperationResDto);
            return cityBalanceOperationResDto;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from city balance ms on handle city balance operation request. %s. Error = %s"
                            .formatted(reqDto, e.getMessage())
            );
        }
    }

    @Override
    public List<CityBalanceOperationResDto> handleOperationBatch(List<CityBalanceOperationReqDto> reqDtos) {
        var uri = cityBalanceUriService.getHandleOperationBatchUri();
        var httpEntity = new HttpEntity<>(reqDtos);
        var responseType = new ParameterizedTypeReference<List<CityBalanceOperationResDto>>() {
        };

        try {
            log.info("Request to handle city balance operation batch send to city balance ms. Batch size = {}", reqDtos.size());
            var resDtos = cityBalanceRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on handle city balance operation batch from city balance ms. Size = {}", resDtos.size());
            return resDtos;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from city balance ms on handle city balance operation batch request. Batch size = %s. Error = %s"
                            .formatted(reqDtos.size(), e.getMessage())
            );
        }
    }
}
