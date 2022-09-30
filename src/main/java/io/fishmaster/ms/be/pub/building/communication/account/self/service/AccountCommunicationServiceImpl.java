package io.fishmaster.ms.be.pub.building.communication.account.self.service;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.account.self.dto.AccountDto;
import io.fishmaster.ms.be.pub.building.communication.account.self.uri.AccountUriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountCommunicationServiceImpl implements AccountCommunicationService {

    private static final String ID_PARAM = "id";

    private final RestTemplate accountRestTemplate;
    private final AccountUriService accountUriService;

    @Override
    public AccountDto getById(String id) {
        return getByParam(ID_PARAM, id);
    }

    private AccountDto getByParam(String key, String value) {
        var uri = accountUriService.getByParamUri(key, value);
        var httpEntity = HttpEntity.EMPTY;
        var responseType = AccountDto.class;

        try {
            log.info("Request to get account send to account ms. Param key, value = {}, {}", key, value);
            var accountDto = accountRestTemplate.exchange(uri, GET, httpEntity, responseType).getBody();
            log.info("Response on get account from account ms. {}", accountDto);
            return accountDto;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from account ms on get account request. Param key, value = %s, %s. Error = %s"
                            .formatted(key, value, e.getMessage())
            );
        }
    }
}
