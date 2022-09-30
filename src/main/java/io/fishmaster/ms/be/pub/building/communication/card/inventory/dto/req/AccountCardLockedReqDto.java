package io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.card.CardType;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
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
public class AccountCardLockedReqDto {
    String accountId;
    Long cityId;
    BuildingType building;
    Set<Long> ids;
    Set<Long> cardIds;
    Set<CardType> types;
    Set<Status> statuses;
    Integer limit;

    public static AccountCardLockedReqDto of(String accountId, Long cityId, Set<Long> ids,
                                             Set<CardType> types, Set<Status> statuses) {
        return new AccountCardLockedReqDto(
                accountId, cityId, null, ids, Set.of(), types, statuses, null
        );
    }

    public static AccountCardLockedReqDto of(String accountId, BuildingType building, Long cardId, Integer limit,
                                             Set<CardType> types, Set<Status> statuses) {
        return new AccountCardLockedReqDto(
                accountId, null, building, Set.of(), Set.of(cardId), types, statuses, limit
        );
    }

    public static AccountCardLockedReqDto of(String accountId, Long cardId, Integer limit,
                                             CardType type, Status status) {
        return new AccountCardLockedReqDto(
                accountId, null, null, Set.of(), Set.of(cardId), Set.of(type), Set.of(status), limit
        );
    }

    public static AccountCardLockedReqDto of(String accountId, Long cityId, Long cardId, Integer limit,
                                             CardType type, Status status) {
        return new AccountCardLockedReqDto(
                accountId, cityId, null, Set.of(), Set.of(cardId), Set.of(type), Set.of(status), limit
        );
    }
}
