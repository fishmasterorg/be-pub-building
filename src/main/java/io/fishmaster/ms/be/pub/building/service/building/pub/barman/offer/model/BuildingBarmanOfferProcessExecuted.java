package io.fishmaster.ms.be.pub.building.service.building.pub.barman.offer.model;

import static io.fishmaster.ms.be.commons.constant.card.CardOperationType.CREATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.card.CardType;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardRollbackReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterStatusReqDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BuildingBarmanOfferProcessExecuted {
    @Setter(value = AccessLevel.NONE)
    String accountId;
    CharacterStatusReqDto characterStatus;
    AccountBalanceOperationReqDto accountBalanceOperationReq;
    List<AccountCardDto> cards = new ArrayList<>();

    public BuildingBarmanOfferProcessExecuted(String accountId) {
        this.accountId = accountId;
    }

    public void updateCharacterStatus(Status status) {
        if (Objects.nonNull(this.characterStatus)) {
            this.characterStatus.setStatus(status);
        }
    }

    public void updateCards(Status status, Long cityId) {
        this.cards.forEach(dto -> {
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
        if (!this.cards.isEmpty()) {
            consumer.accept(
                    List.of(this.cards)
            );
        }
    }

    public void rollback(Consumer<AccountBalanceOperationReqDto> accountBalanceRollbackConsumer,
                         Consumer<List<AccountCardRollbackReqDto>> cardsRollbackConsumer,
                         Consumer<CharacterStatusReqDto> characterStatusRollbackConsumer) {
        rollbackAccountBalance(accountBalanceRollbackConsumer);
        rollbackCards(cardsRollbackConsumer);
        rollbackCharacterStatus(characterStatusRollbackConsumer);
    }

    private void rollbackAccountBalance(Consumer<AccountBalanceOperationReqDto> consumer) {
        if (Objects.nonNull(this.accountBalanceOperationReq)) {
            consumer.accept(
                    AccountBalanceOperationReqDto.ofNegate(this.accountBalanceOperationReq)
            );
        }
    }

    private void rollbackCards(Consumer<List<AccountCardRollbackReqDto>> consumer) {
        if (!this.cards.isEmpty()) {
            consumer.accept(
                    List.of(AccountCardRollbackReqDto.of(this.accountId, CREATE, this.cards))
            );
        }
    }

    private void rollbackCharacterStatus(Consumer<CharacterStatusReqDto> consumer) {
        if (Objects.nonNull(this.characterStatus)) {
            consumer.accept(this.characterStatus);
        }
    }

}
