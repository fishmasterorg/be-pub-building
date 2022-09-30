package io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.model;

import java.util.Objects;
import java.util.function.Consumer;

import io.fishmaster.ms.be.commons.constant.Status;
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
public final class BuildingTradingOrderProcessCanceled {
    @Setter(value = AccessLevel.NONE)
    String accountId;
    CharacterStatusReqDto characterStatus;
    CharacterEnergyOperationResDto characterEnergyOperationRes;

    public BuildingTradingOrderProcessCanceled(String accountId) {
        this.accountId = accountId;
    }

    public void updateCharacterStatus(Status status) {
        if (Objects.nonNull(this.characterStatus)) {
            this.characterStatus.setStatus(status);
        }
    }

    public void complete(Consumer<CharacterStatusReqDto> characterStatusCompleteConsumer) {
        completeCharacterStatus(characterStatusCompleteConsumer);
    }

    private void completeCharacterStatus(Consumer<CharacterStatusReqDto> consumer) {
        if (Objects.nonNull(this.characterStatus)) {
            consumer.accept(this.characterStatus);
        }
    }

    public void rollback(
            Consumer<CharacterEnergyOperationReqDto> characterEnergyRollbackConsumer,
            Consumer<CharacterStatusReqDto> characterStatusRollbackConsumer) {
        rollbackCharacterEnergy(characterEnergyRollbackConsumer);
        rollbackCharacterStatus(characterStatusRollbackConsumer);
    }

    private void rollbackCharacterStatus(Consumer<CharacterStatusReqDto> consumer) {
        if (Objects.nonNull(this.characterStatus)) {
            consumer.accept(this.characterStatus);
        }
    }

    private void rollbackCharacterEnergy(Consumer<CharacterEnergyOperationReqDto> consumer) {
        if (Objects.nonNull(this.characterEnergyOperationRes)) {
            consumer.accept(CharacterEnergyOperationReqDto.ofNegate(this.characterEnergyOperationRes));
        }
    }

}
