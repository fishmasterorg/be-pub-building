package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.offer.model;

import static io.fishmaster.ms.be.commons.constant.card.CardOperationType.CREATE;
import static io.fishmaster.ms.be.commons.constant.card.CardOperationType.LOCK;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.card.CardType;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardRollbackReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.energy.CharacterEnergyOperationResDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterStatusReqDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BuildingExchangerOfferProcessExecuted {
    @Setter(value = AccessLevel.NONE)
    String accountId;
    CharacterStatusReqDto characterStatus;
    List<AccountCardDto> lockedCards = new ArrayList<>();
    List<AccountCardDto> createdCards = new ArrayList<>();
    CharacterEnergyOperationResDto characterEnergyOperationRes;
    AccountBalanceOperationReqDto accountBalanceOperationReq;

    public BuildingExchangerOfferProcessExecuted(String accountId) {
        this.accountId = accountId;
    }

    public void updateCharacterStatus(Status status) {
        if (Objects.nonNull(this.characterStatus)) {
            this.characterStatus.setStatus(status);
        }
    }

    public void updateLockedCards(Status status, CardSource cardSource) {
        this.createdCards.forEach(dto -> {
            dto.setStatus(status);
            if (Status.USED == status) {
                dto.getMeta().setSourceOfUse(cardSource);
            }
        });
    }

    public void updateCreatedCards(Status status, Long cityId) {
        this.createdCards.forEach(dto -> {
            dto.setStatus(status);
            if (CardType.getCityCardTypes().contains(dto.getCard().getType())) {
                dto.getMeta().setCityId(cityId);
            }
        });
    }

    public void complete(Consumer<CharacterStatusReqDto> characterStatusCompleteConsumer,
            Consumer<List<List<AccountCardDto>>> cardsCompleteConsumer) {
        completeCharacterStatus(characterStatusCompleteConsumer);
        completeCards(cardsCompleteConsumer);
    }

    private void completeCharacterStatus(Consumer<CharacterStatusReqDto> consumer) {
        if (Objects.nonNull(this.characterStatus)) {
            consumer.accept(this.characterStatus);
        }
    }

    private void completeCards(Consumer<List<List<AccountCardDto>>> consumer) {
        var toCompleteCards = new ArrayList<List<AccountCardDto>>();
        if (!this.lockedCards.isEmpty()) {
            toCompleteCards.add(this.lockedCards);
        }
        if (!this.createdCards.isEmpty()) {
            toCompleteCards.add(this.createdCards);
        }

        if (!toCompleteCards.isEmpty()) {
            consumer.accept(toCompleteCards);
        }
    }

    public void rollback(Consumer<AccountBalanceOperationReqDto> accountBalanceRollbackConsumer,
            Consumer<CharacterEnergyOperationReqDto> characterEnergyRollbackConsumer,
            Consumer<List<AccountCardRollbackReqDto>> cardsRollbackConsumer,
            Consumer<CharacterStatusReqDto> characterStatusRollbackConsumer) {
        rollbackAccountBalance(accountBalanceRollbackConsumer);
        rollbackCharacterEnergy(characterEnergyRollbackConsumer);
        rollbackCards(cardsRollbackConsumer);
        rollbackCharacterStatus(characterStatusRollbackConsumer);
    }

    private void rollbackAccountBalance(Consumer<AccountBalanceOperationReqDto> consumer) {
        if (Objects.nonNull(this.accountBalanceOperationReq)) {
            consumer.accept(
                    AccountBalanceOperationReqDto.ofNegate(this.accountBalanceOperationReq));
        }
    }

    private void rollbackCharacterEnergy(Consumer<CharacterEnergyOperationReqDto> consumer) {
        if (Objects.nonNull(this.characterEnergyOperationRes)) {
            consumer.accept(
                    CharacterEnergyOperationReqDto.ofNegate(this.characterEnergyOperationRes)
            );
        }
    }

    private void rollbackCards(Consumer<List<AccountCardRollbackReqDto>> consumer) {
        var rollbackCards = new ArrayList<AccountCardRollbackReqDto>();
        if (!this.lockedCards.isEmpty()) {
            rollbackCards.add(
                    AccountCardRollbackReqDto.of(this.accountId, LOCK, this.lockedCards));
        }
        if (!this.createdCards.isEmpty()) {
            rollbackCards.add(
                    AccountCardRollbackReqDto.of(this.accountId, CREATE, this.createdCards));
        }

        if (!rollbackCards.isEmpty()) {
            consumer.accept(rollbackCards);
        }
    }

    private void rollbackCharacterStatus(Consumer<CharacterStatusReqDto> consumer) {
        if (Objects.nonNull(this.characterStatus)) {
            consumer.accept(this.characterStatus);
        }
    }

}
