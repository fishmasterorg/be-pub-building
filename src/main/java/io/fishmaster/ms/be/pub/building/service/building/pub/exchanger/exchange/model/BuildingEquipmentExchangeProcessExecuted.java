package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.model;

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
import io.fishmaster.ms.be.pub.building.communication.city.balance.dto.CityBalanceOperationReqDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BuildingEquipmentExchangeProcessExecuted {
    @Setter(value = AccessLevel.NONE)
    String accountId;
    CharacterStatusReqDto characterStatus;
    List<AccountCardDto> lockedCards = new ArrayList<>();
    List<AccountCardDto> craftedCards = new ArrayList<>();
    CharacterEnergyOperationResDto characterEnergyOperationRes;
    AccountBalanceOperationReqDto accountBalanceOperationReq;
    CityBalanceOperationReqDto cityBalanceOperationReq;

    public BuildingEquipmentExchangeProcessExecuted(String accountId) {
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

    public void updateCraftedCards(Status status, Long cityId) {
        this.craftedCards.forEach(updateCardConsumer(status, cityId));
    }

    public void complete(Consumer<List<List<AccountCardDto>>> cardsCompleteConsumer,
            Consumer<CharacterStatusReqDto> characterStatusCompleteConsumer) {
        completeCards(cardsCompleteConsumer);
        completeCharacterStatus(characterStatusCompleteConsumer);
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
        if (!this.craftedCards.isEmpty()) {
            toCompleteCards.add(this.craftedCards);
        }

        if (!toCompleteCards.isEmpty()) {
            consumer.accept(toCompleteCards);
        }
    }

    public void rollback(Consumer<CharacterStatusReqDto> characterStatusRollbackConsumer,
            Consumer<List<AccountCardRollbackReqDto>> cardsRollbackConsumer,
            Consumer<CityBalanceOperationReqDto> cityBalanceRollbackConsumer,
            Consumer<AccountBalanceOperationReqDto> accountBalanceRollbackConsumer,
            Consumer<CharacterEnergyOperationReqDto> characterEnergyRollbackConsumer) {
        rollbackCharacterStatus(characterStatusRollbackConsumer);
        rollbackCards(cardsRollbackConsumer);
        rollbackCityBalance(cityBalanceRollbackConsumer);
        rollbackAccountBalance(accountBalanceRollbackConsumer);
        rollbackCharacterEnergy(characterEnergyRollbackConsumer);
    }

    private void rollbackCharacterStatus(Consumer<CharacterStatusReqDto> consumer) {
        if (Objects.nonNull(this.characterStatus)) {
            consumer.accept(this.characterStatus);
        }
    }

    private void rollbackCharacterEnergy(Consumer<CharacterEnergyOperationReqDto> consumer) {
        if (Objects.nonNull(this.characterEnergyOperationRes)) {
            consumer.accept(
                    CharacterEnergyOperationReqDto.ofNegate(this.characterEnergyOperationRes));
        }
    }

    private void rollbackAccountBalance(Consumer<AccountBalanceOperationReqDto> consumer) {
        if (Objects.nonNull(this.accountBalanceOperationReq)) {
            consumer.accept(
                    AccountBalanceOperationReqDto.ofNegate(this.accountBalanceOperationReq));
        }
    }

    private void rollbackCityBalance(Consumer<CityBalanceOperationReqDto> consumer) {
        if (Objects.nonNull(this.cityBalanceOperationReq)) {
            consumer.accept(
                    CityBalanceOperationReqDto.ofNegate(this.cityBalanceOperationReq));
        }
    }

    private void rollbackCards(Consumer<List<AccountCardRollbackReqDto>> consumer) {
        var rollbackCards = new ArrayList<AccountCardRollbackReqDto>();
        if (!this.lockedCards.isEmpty()) {
            rollbackCards.add(
                    AccountCardRollbackReqDto.of(this.accountId, LOCK, this.lockedCards));
        }
        if (!this.craftedCards.isEmpty()) {
            rollbackCards.add(
                    AccountCardRollbackReqDto.of(this.accountId, CREATE, this.craftedCards));
        }

        if (!rollbackCards.isEmpty()) {
            consumer.accept(rollbackCards);
        }
    }

    private Consumer<AccountCardDto> updateCardConsumer(Status status, Long cityId) {
        return dto -> {
            dto.setStatus(status);
            if (CardType.getCityCardTypes().contains(dto.getCard().getType())) {
                dto.getMeta().setCityId(cityId);
            }
        };
    }

}
