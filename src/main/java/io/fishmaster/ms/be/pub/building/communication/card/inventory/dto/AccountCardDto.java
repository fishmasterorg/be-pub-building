package io.fishmaster.ms.be.pub.building.communication.card.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
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
public class AccountCardDto {
    Long id;
    String accountId;
    CardDto card;
    Status status;
    Meta meta;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        Long cityId;
        CardSource sourceOfCreation;
        CardSource sourceOfUse;

        public static Meta of(CardSource sourceOfCreation) {
            return new Meta(null, sourceOfCreation, null);
        }
    }

    public static AccountCardDto of(AccountCardDto accountCardDto) {
        return new AccountCardDto(
                accountCardDto.getId(), accountCardDto.getAccountId(), accountCardDto.getCard(),
                accountCardDto.getStatus(), accountCardDto.getMeta()
        );
    }

    public static AccountCardDto of(String accountId, Long cardId, BuildingType buildingType) {
        return of(accountId, cardId, CardSource.ofBuilding(buildingType));
    }

    public static AccountCardDto of(String accountId, Long cardId, CardSource cardSource) {
        return new AccountCardDto(
                null, accountId, CardDto.of(cardId), null,
                Meta.of(cardSource)
        );
    }
}
