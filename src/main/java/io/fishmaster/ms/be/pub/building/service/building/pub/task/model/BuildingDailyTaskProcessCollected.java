package io.fishmaster.ms.be.pub.building.service.building.pub.task.model;

import static io.fishmaster.ms.be.commons.constant.card.CardOperationType.CREATE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.fishmaster.ms.be.commons.constant.Status;
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
public final class BuildingDailyTaskProcessCollected {
    @Setter(value = AccessLevel.NONE)
    String accountId;
    List<AccountCardDto> cards = new ArrayList<>();

    public BuildingDailyTaskProcessCollected(String accountId) {
        this.accountId = accountId;
    }

    public void updateCards(Status status, Long cityId) {
        this.cards.forEach(dto -> {
            dto.setStatus(status);
            if (CardType.getCityCardTypes().contains(dto.getCard().getType())) {
                dto.getMeta().setCityId(cityId);
            }
        });
    }

    public void complete(Consumer<List<List<AccountCardDto>>> cardsCompleteConsumer) {
        completeCards(cardsCompleteConsumer);
    }

    private void completeCards(Consumer<List<List<AccountCardDto>>> consumer) {
        if (!this.cards.isEmpty()) {
            consumer.accept(
                    List.of(this.cards)
            );
        }
    }

    public void rollback(Consumer<List<AccountCardRollbackReqDto>> cardsRollbackConsumer) {
        rollbackCards(cardsRollbackConsumer);
    }

    private void rollbackCards(Consumer<List<AccountCardRollbackReqDto>> consumer) {
        if (!this.cards.isEmpty()) {
            consumer.accept(
                    List.of(AccountCardRollbackReqDto.of(this.accountId, CREATE, this.cards))
            );
        }
    }

}
