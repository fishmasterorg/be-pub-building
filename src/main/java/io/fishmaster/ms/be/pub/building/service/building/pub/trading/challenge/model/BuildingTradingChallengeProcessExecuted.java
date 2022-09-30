package io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.model;

import static io.fishmaster.ms.be.commons.constant.card.CardOperationType.LOCK;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardRollbackReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterStatusReqDto;
import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationReqDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BuildingTradingChallengeProcessExecuted {
    @Setter(value = AccessLevel.NONE)
    String accountId;
    CharacterStatusReqDto characterStatus;
    List<AccountCardDto> lockedCards = new ArrayList<>();
    AccountBalanceOperationReqDto accountBalanceOperationReq;
    CityBalanceOperationReqDto cityBalanceOperationReq;

    public BuildingTradingChallengeProcessExecuted(String accountId) {
        this.accountId = accountId;
    }

    public void updateCharacterStatus(Status status) {
        if (Objects.nonNull(this.characterStatus)) {
            this.characterStatus.setStatus(status);
        }
    }

    public void updateLockedCards(Status status, CardSource cardSource) {
        this.lockedCards.forEach(dto -> {
            dto.setStatus(status);
            if (Status.USED == status) {
                dto.getMeta().setSourceOfUse(cardSource);
            }
        });
    }

    public void complete(Consumer<List<List<AccountCardDto>>> cardsCompleteConsumer,
                         Consumer<CharacterStatusReqDto> characterStatusCompleteConsumer) {
        completeLockedCards(cardsCompleteConsumer);
        completeCharacterStatus(characterStatusCompleteConsumer);
    }

    private void completeLockedCards(Consumer<List<List<AccountCardDto>>> consumer) {
        if (!this.lockedCards.isEmpty()) {
            consumer.accept(
                    List.of(this.lockedCards)
            );
        }
    }

    private void completeCharacterStatus(Consumer<CharacterStatusReqDto> consumer) {
        if (Objects.nonNull(this.characterStatus)) {
            consumer.accept(this.characterStatus);
        }
    }

    public void rollback(Consumer<CityBalanceOperationReqDto> cityBalanceRollbackConsumer,
                         Consumer<AccountBalanceOperationReqDto> accountBalanceRollbackConsumer,
                         Consumer<List<AccountCardRollbackReqDto>> cardsRollbackConsumer,
                         Consumer<CharacterStatusReqDto> characterStatusRollbackConsumer) {
        rollbackCityBalance(cityBalanceRollbackConsumer);
        rollbackAccountBalance(accountBalanceRollbackConsumer);
        rollbackLockedCards(cardsRollbackConsumer);
        rollbackCharacterStatus(characterStatusRollbackConsumer);
    }

    private void rollbackCharacterStatus(Consumer<CharacterStatusReqDto> consumer) {
        if (Objects.nonNull(this.characterStatus)) {
            consumer.accept(this.characterStatus);
        }
    }

    private void rollbackLockedCards(Consumer<List<AccountCardRollbackReqDto>> consumer) {
        if (!this.lockedCards.isEmpty()) {
            consumer.accept(
                    List.of(AccountCardRollbackReqDto.of(this.accountId, LOCK, this.lockedCards))
            );
        }
    }

    private void rollbackAccountBalance(Consumer<AccountBalanceOperationReqDto> consumer) {
        if (Objects.nonNull(this.accountBalanceOperationReq)) {
            consumer.accept(
                    AccountBalanceOperationReqDto.ofNegate(this.accountBalanceOperationReq)
            );
        }
    }

    private void rollbackCityBalance(Consumer<CityBalanceOperationReqDto> consumer) {
        if (Objects.nonNull(this.cityBalanceOperationReq)) {
            consumer.accept(
                    CityBalanceOperationReqDto.ofNegate(this.cityBalanceOperationReq)
            );
        }
    }


}
