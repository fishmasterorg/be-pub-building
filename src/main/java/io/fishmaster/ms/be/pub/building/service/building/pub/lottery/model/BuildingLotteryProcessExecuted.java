package io.fishmaster.ms.be.pub.building.service.building.pub.lottery.model;

import static io.fishmaster.ms.be.commons.constant.card.CardOperationType.CREATE;
import static io.fishmaster.ms.be.commons.constant.card.CardOperationType.LOCK;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.card.CardType;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardRollbackReqDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BuildingLotteryProcessExecuted {
    @Setter(value = AccessLevel.NONE)
    String accountId;
    List<AccountCardDto> lockedCards = new ArrayList<>();
    List<AccountCardDto> createdCards = new ArrayList<>();

    public BuildingLotteryProcessExecuted(String accountId) {
        this.accountId = accountId;
    }

    public void updateLockedCards(Status status, CardSource cardSource) {
        this.lockedCards.forEach(dto -> {
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

    public void complete(Consumer<List<List<AccountCardDto>>> createdCardsCompleteConsumer) {
        completeCards(createdCardsCompleteConsumer);
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

    public void rollback(Consumer<List<AccountCardRollbackReqDto>> createdCardsRollbackConsumer) {
        rollbackCards(createdCardsRollbackConsumer);
    }

    private void rollbackCards(Consumer<List<AccountCardRollbackReqDto>> consumer) {
        var rollbackCards = new ArrayList<AccountCardRollbackReqDto>();
        if (!this.lockedCards.isEmpty()) {
            rollbackCards.add(
                    AccountCardRollbackReqDto.of(this.accountId, LOCK, this.lockedCards)
            );
        }
        if (!this.createdCards.isEmpty()) {
            rollbackCards.add(
                    AccountCardRollbackReqDto.of(this.accountId, CREATE, this.createdCards)
            );
        }

        if (!rollbackCards.isEmpty()) {
            consumer.accept(rollbackCards);
        }
    }

}
