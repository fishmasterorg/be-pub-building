package io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.card.CardOperationType;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountCardRollbackReqDto {
    @NotNull Set<Long> ids;
    @NotBlank String accountId;
    @NotNull CardOperationType operationType;

    public static AccountCardRollbackReqDto of(String accountId, CardOperationType operationType,
                                               Collection<AccountCardDto> accountCardDtos) {
        var accountCardIds = accountCardDtos.stream()
                .map(AccountCardDto::getId)
                .collect(Collectors.toSet());
        return new AccountCardRollbackReqDto(accountCardIds, accountId, operationType);
    }
}
