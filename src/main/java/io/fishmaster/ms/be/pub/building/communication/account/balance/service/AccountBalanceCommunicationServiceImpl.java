package io.fishmaster.ms.be.pub.building.communication.account.balance.service;

import static org.springframework.http.HttpMethod.POST;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationResDto;
import io.fishmaster.ms.be.pub.building.communication.account.balance.uri.AccountBalanceUriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountBalanceCommunicationServiceImpl implements AccountBalanceCommunicationService {

    private final RestTemplate accountBalanceRestTemplate;
    private final AccountBalanceUriService accountBalanceUriService;

    @Override
    public AccountBalanceOperationResDto handleOperation(AccountBalanceOperationReqDto reqDto) {
        var uri = accountBalanceUriService.getHandleOperationUri();
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = AccountBalanceOperationResDto.class;

        try {
            log.info("Request to handle account balance operation send to account balance ms. {}", reqDto);
            var accountBalanceOperationResDto = accountBalanceRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on handle account balance operation from account balance ms. {}", accountBalanceOperationResDto);
            return accountBalanceOperationResDto;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from account balance ms on handle account balance operation request. %s. Error = %s"
                            .formatted(reqDto, e.getMessage())
            );
        }
    }

}
