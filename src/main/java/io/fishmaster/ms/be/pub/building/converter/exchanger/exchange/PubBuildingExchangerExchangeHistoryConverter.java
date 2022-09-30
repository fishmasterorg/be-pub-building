package io.fishmaster.ms.be.pub.building.converter.exchanger.exchange;

import java.time.Clock;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import io.fishmaster.ms.be.commons.model.result.Result;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.ResultConverter;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.exchange.PubBuildingExchangerExchangeHistory;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.model.BuildingEquipmentExchangeProcessExecuted;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.utility.model.BuildingExchangerExchangeData;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.exchange.BuildingExchangerExchangeUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingExchangerExchangeHistoryConverter {

    public static PubBuildingExchangerExchangeHistory toEntityWithStatusDone(
            BuildingExchangerExchangeExecutedReqDto reqDto, ExchangerExchangeRecipeConfigurationDto recipeConfigurationDto,
            BuildingExchangerExchangeData buildingExchangeData,
            BuildingEquipmentExchangeProcessExecuted buildingEquipmentExchangeProcessExecuted,
            Clock utcClock) {
        var lockedAccountCardIds = buildingEquipmentExchangeProcessExecuted.getLockedCards().stream()
                .map(AccountCardDto::getId)
                .collect(Collectors.toSet());

        var result = new Result(
                null, ResultConverter.toResultCard(buildingEquipmentExchangeProcessExecuted.getCraftedCards()),
                buildingExchangeData.getExperiencesList(), null);

        var now = utcClock.millis();
        return PubBuildingExchangerExchangeHistory.builder()
                .requestId(MDCUtility.getTraceId())
                .accountId(reqDto.getAccountId())
                .characterId(reqDto.getCharacterId())
                .buildingId(reqDto.getBuildingId())
                .recipeConfigurationId(recipeConfigurationDto.getId())
                .lockedCardIds(lockedAccountCardIds)
                .result(result)
                .status(Status.DONE)
                .createdDate(now)
                .lastModifiedDate(now)
                .build();
    }

    public static BuildingExchangerExchangeUiDto toUiDto(PubBuildingExchangerExchangeHistory pubBuildingEquipmentExchangeHistory) {
        var characterId = pubBuildingEquipmentExchangeHistory.getCharacterId();
        var result = pubBuildingEquipmentExchangeHistory.getResult();

        return new BuildingExchangerExchangeUiDto(
                pubBuildingEquipmentExchangeHistory.getId().toHexString(), pubBuildingEquipmentExchangeHistory.getCharacterId(),
                pubBuildingEquipmentExchangeHistory.getBuildingId(), pubBuildingEquipmentExchangeHistory.getRecipeConfigurationId(),
                ResultUiDto.of(characterId, result), pubBuildingEquipmentExchangeHistory.getStatus());
    }

}
