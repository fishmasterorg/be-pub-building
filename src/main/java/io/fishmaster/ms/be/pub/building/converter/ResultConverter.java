package io.fishmaster.ms.be.pub.building.converter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.model.result.ResultCard;
import io.fishmaster.ms.be.commons.model.result.ResultMoney;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResultConverter {

    public static List<ResultCard> toResultCard(AccountCardDto accountCardDto) {
        if (Objects.isNull(accountCardDto)) {
            return toResultCard(List.of());
        }
        return toResultCard(List.of(accountCardDto));
    }

    public static List<ResultCard> toResultCard(Collection<AccountCardDto> accountCardDtos) {
        return accountCardDtos.stream()
                .collect(Collectors.groupingBy(accountCardDto -> accountCardDto.getCard().getId()))
                .entrySet().stream()
                .map(entry -> {
                    var accountCardIds = entry.getValue().stream()
                            .map(AccountCardDto::getId)
                            .collect(Collectors.toSet());
                    return new ResultCard(entry.getKey(), accountCardIds);
                })
                .toList();
    }

    public static List<ResultMoney> toResultMoney(AccountBalanceOperationReqDto reqDto) {
        if (Objects.isNull(reqDto)) {
            return toResultMoney(List.of());
        }
        return toResultMoney(List.of(reqDto));
    }

    public static List<ResultMoney> toResultMoney(Collection<AccountBalanceOperationReqDto> reqDtos) {
        return reqDtos.stream()
                .collect(Collectors.groupingBy(reqDto -> reqDto.getCurrency()))
                .entrySet().stream()
                .map(entry -> {
                    var quantity = entry.getValue().stream()
                            .map(AccountBalanceOperationReqDto::getQuantity)
                            .reduce(0D, Double::sum);

                    return new ResultMoney(entry.getKey(), quantity);
                })
                .toList();
    }

}
