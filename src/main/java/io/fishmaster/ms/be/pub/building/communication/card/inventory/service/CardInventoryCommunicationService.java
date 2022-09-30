package io.fishmaster.ms.be.pub.building.communication.card.inventory.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.CardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedForCraftReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardRollbackReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.CardFetchedReqDto;

public interface CardInventoryCommunicationService {

    List<CardDto> fetchCards(CardFetchedReqDto reqDto);

    default List<CardDto> fetchCards(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return fetchCards(CardFetchedReqDto.of(ids));
    }

    List<AccountCardDto> createAccountCards(List<AccountCardDto> accountCardDtos);

    default void createAccountCards(List<AccountCardDto> accountCardDtos, Consumer<List<AccountCardDto>> consumer) {
        if (accountCardDtos.isEmpty()) {
            return;
        }
        consumer.accept(createAccountCards(accountCardDtos));
    }

    List<AccountCardDto> lockAccountCards(AccountCardLockedReqDto reqDto);

    default void lockAccountCards(AccountCardLockedReqDto reqDto, Integer totalCount,
            Consumer<List<AccountCardDto>> consumer) {
        var accountCardDtos = lockAccountCards(reqDto);
        consumer.accept(accountCardDtos);

        if (totalCount > accountCardDtos.size()) {
            throw new ServiceException(
                    ExceptionCode.NOT_ENOUGH_CARDS_IN_INVENTORY,
                    "There are not enough cards. Total count = %s, current count = %s".formatted(totalCount, accountCardDtos.size()));
        }
    }

    List<AccountCardDto> lockForCraftAccountCards(List<AccountCardLockedForCraftReqDto> reqDtoBatch);

    default void lockForCraftAccountCards(List<AccountCardLockedForCraftReqDto> reqDtoBatch, Integer totalCount,
            Consumer<List<AccountCardDto>> consumer) {
        var accountCardDtos = lockForCraftAccountCards(reqDtoBatch);
        consumer.accept(accountCardDtos);

        if (totalCount > accountCardDtos.size()) {
            throw new ServiceException(
                    ExceptionCode.NOT_ENOUGH_CARDS_IN_INVENTORY,
                    "There are not enough cards. Total count = %s, current count = %s".formatted(totalCount, accountCardDtos.size()));
        }
    }

    void completeAccountCards(List<List<AccountCardDto>> accountCardDtoBatch);

    void rollbackAccountCards(List<AccountCardRollbackReqDto> reqDtoBatch);

}
