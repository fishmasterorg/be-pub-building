package io.fishmaster.ms.be.pub.building.communication.card.inventory.service;

import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.CardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedForCraftReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardRollbackReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.CardFetchedReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.uri.CardInventoryUriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardInventoryCommunicationServiceImpl implements CardInventoryCommunicationService {

    private final RestTemplate cardInventoryRestTemplate;
    private final CardInventoryUriService cardInventoryUriService;

    @Override
    public List<CardDto> fetchCards(CardFetchedReqDto reqDto) {
        var uri = cardInventoryUriService.getCardFetchUri();
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = new ParameterizedTypeReference<List<CardDto>>() {
        };

        try {
            log.info("Request to fetch cards send to card inventory ms. {}", reqDto);
            var cardDtos = cardInventoryRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on fetch cards from card inventory ms. Size = {}", cardDtos.size());
            return cardDtos;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from card inventory ms on fetch cards request. %s. Error = %s"
                            .formatted(reqDto, e.getMessage())
            );
        }
    }

    @Override
    public List<AccountCardDto> createAccountCards(List<AccountCardDto> accountCardDtos) {
        var uri = cardInventoryUriService.getAccountCardCreateUri();
        var httpEntity = new HttpEntity<>(accountCardDtos);
        var responseType = new ParameterizedTypeReference<List<AccountCardDto>>() {
        };

        try {
            log.info("Request to create account cards send to card inventory ms. Size = {}", accountCardDtos.size());
            accountCardDtos = cardInventoryRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on create account cards from card inventory ms. Size = {}", accountCardDtos.size());
            return accountCardDtos;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from card inventory ms on create account cards request. Size = %s. Error = %s"
                            .formatted(accountCardDtos.size(), e.getMessage())
            );
        }
    }

    @Override
    public List<AccountCardDto> lockAccountCards(AccountCardLockedReqDto reqDto) {
        var uri = cardInventoryUriService.getAccountCardLockUri();
        var httpEntity = new HttpEntity<>(reqDto);
        var responseType = new ParameterizedTypeReference<List<AccountCardDto>>() {
        };

        try {
            log.info("Request to lock account cards send to card inventory ms. {}", reqDto);
            var accountCardDtos = cardInventoryRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on lock account cards from card inventory ms. Size = {}", accountCardDtos.size());
            return accountCardDtos;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from card inventory ms on lock account cards request. %s. Error = %s"
                            .formatted(reqDto, e.getMessage())
            );
        }
    }

    @Override
    public List<AccountCardDto> lockForCraftAccountCards(List<AccountCardLockedForCraftReqDto> reqDtoBatch) {
        var uri = cardInventoryUriService.getAccountCardLockForCraftUri();
        var httpEntity = new HttpEntity<>(reqDtoBatch);
        var responseType = new ParameterizedTypeReference<List<AccountCardDto>>() {
        };

        try {
            log.info("Request to lock account cards for craft send to card inventory ms. Batch size = {}", reqDtoBatch);
            var accountCardDtos = cardInventoryRestTemplate.exchange(uri, POST, httpEntity, responseType).getBody();
            log.info("Response on lock account cards for craft from card inventory ms. Size = {}", accountCardDtos.size());
            return accountCardDtos;
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from card inventory ms on lock account cards for craft request. Batch size = %s. Error = %s"
                            .formatted(reqDtoBatch, e.getMessage())
            );
        }
    }

    @Override
    public void completeAccountCards(List<List<AccountCardDto>> accountCardDtoBatch) {
        var uri = cardInventoryUriService.getAccountCardCompleteUri();
        var httpEntity = new HttpEntity<>(accountCardDtoBatch);
        var responseType = String.class;

        try {
            log.info("Request to complete locked account cards send to card inventory ms. Size = {}", accountCardDtoBatch.size());
            var httpStatus = cardInventoryRestTemplate.exchange(uri, POST, httpEntity, responseType).getStatusCode();
            log.info("Response on complete locked account cards from card inventory ms. Http status = {}", httpStatus);
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from card inventory ms on complete locked account cards request. Size = %s. Error = %s"
                            .formatted(accountCardDtoBatch.size(), e.getMessage())
            );
        }
    }

    @Override
    public void rollbackAccountCards(List<AccountCardRollbackReqDto> reqDtoBatch) {
        var uri = cardInventoryUriService.getAccountCardRollbackUri();
        var httpEntity = new HttpEntity<>(reqDtoBatch);
        var responseType = String.class;

        try {
            log.info("Request to rollback locked account cards send to card inventory ms. Size = {}", reqDtoBatch.size());
            var httpStatus = cardInventoryRestTemplate.exchange(uri, POST, httpEntity, responseType).getStatusCode();
            log.info("Response on rollback locked account cards from card inventory ms. Http status = {}", httpStatus);
        } catch (ResourceAccessException e) {
            throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "No response from card inventory ms on rollback locked account cards request. Size = %s. Error = %s"
                            .formatted(reqDtoBatch.size(), e.getMessage())
            );
        }
    }
}
