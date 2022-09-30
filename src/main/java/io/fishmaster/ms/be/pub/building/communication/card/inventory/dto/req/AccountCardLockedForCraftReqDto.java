package io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.Status;
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
public class AccountCardLockedForCraftReqDto {
    String accountId;
    Long cityId;
    Status status;
    Long cardId;
    Integer limit;

    public static AccountCardLockedForCraftReqDto of(String accountId, Long cityId, Status status, Long cardId,
                                                     Integer limit) {
        return new AccountCardLockedForCraftReqDto(
                accountId, cityId, status, cardId, limit
        );
    }

    public static AccountCardLockedForCraftReqDto of(String accountId, Status status, Long cardId,
                                                     Integer limit) {
        return of(accountId, null, status, cardId, limit);
    }
}
