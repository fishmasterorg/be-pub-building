package io.fishmaster.ms.be.pub.building.converter.trading.order;

import java.time.Clock;
import java.util.Objects;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import io.fishmaster.ms.be.commons.model.result.Result;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.TradingOrderConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.ResultConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.PubBuildingTradingOrder;
import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.order.PubBuildingTradingOrderHistory;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.utility.TradingOrderFacade;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingTradingOrderHistoryConverter {

    public static PubBuildingTradingOrderHistory toEntityForCancel(
            Long characterId, PubBuildingTradingOrder pubBuildingTradingOrder, TradingOrderFacade tradingOrderFacade,
            Status status, Clock utcClock) {

        var result = new Result();

        return toEntity(characterId, pubBuildingTradingOrder, tradingOrderFacade, result, status, utcClock);
    }

    public static PubBuildingTradingOrderHistory toEntityForExecute(
            Long characterId, PubBuildingTradingOrder pubBuildingTradingOrder, TradingOrderFacade tradingOrderFacade,
            AccountBalanceOperationReqDto accountBalanceOperationReqDto, Status status, Clock utcClock) {

        var result = new Result(
                null, null, tradingOrderFacade.getExperiences(),
                ResultConverter.toResultMoney(accountBalanceOperationReqDto));

        return toEntity(characterId, pubBuildingTradingOrder, tradingOrderFacade, result, status, utcClock);
    }

    private static PubBuildingTradingOrderHistory toEntity(
            Long characterId, PubBuildingTradingOrder pubBuildingTradingOrder, TradingOrderFacade tradingOrderFacade,
            Result result, Status status, Clock utcClock) {

        var now = utcClock.millis();
        return PubBuildingTradingOrderHistory.builder()
                .requestId(MDCUtility.getTraceId())
                .accountId(pubBuildingTradingOrder.getAccountId())
                .characterId(characterId)

                .buildingId(pubBuildingTradingOrder.getPubBuilding().getId())
                .buildingTradingOrderId(pubBuildingTradingOrder.getId())
                .configurationId(pubBuildingTradingOrder.getConfigurationId())

                .data(toDataEntity(tradingOrderFacade))
                .result(result)
                .status(status)

                .createdDate(now)
                .lastModifiedDate(now)
                .build();
    }

    private static PubBuildingTradingOrderHistory.Data toDataEntity(TradingOrderFacade tradingOrderFacade) {
        var configurationDto = tradingOrderFacade.getConfigurationDto();
        return new PubBuildingTradingOrderHistory.Data(
                configurationDto.getId(), tradingOrderFacade.getCardId(), tradingOrderFacade.getCardsQuantity(),
                tradingOrderFacade.getEnergyForDeliver(), tradingOrderFacade.getEnergyForCancel(),
                tradingOrderFacade.getCost(), tradingOrderFacade.getTax());
    }

    public static BuildingTradingOrderUiDto toUiDto(
            PubBuildingTradingOrderHistory pubBuildingTradingOrderHistory, TradingOrderFacade tradingOrderFacade) {
        var configurationDto = tradingOrderFacade.getConfigurationDto();
        return new BuildingTradingOrderUiDto(
                pubBuildingTradingOrderHistory.getBuildingTradingOrderId(), configurationDto.getSequenceNumber(),
                pubBuildingTradingOrderHistory.getBuildingId(),
                toSpecialityUiDto(configurationDto.getSpeciality()), toDataUiDto(tradingOrderFacade),
                ResultUiDto.of(pubBuildingTradingOrderHistory.getCharacterId(), pubBuildingTradingOrderHistory.getResult()),
                pubBuildingTradingOrderHistory.getStatus());
    }

    private static BuildingTradingOrderUiDto.Speciality toSpecialityUiDto(TradingOrderConfigurationDto.Speciality speciality) {
        return new BuildingTradingOrderUiDto.Speciality(speciality.getName(), speciality.getLevel());
    }

    private static BuildingTradingOrderUiDto.Data toDataUiDto(TradingOrderFacade tradingOrderFacade) {
        if (Objects.isNull(tradingOrderFacade)) {
            return new BuildingTradingOrderUiDto.Data();
        }
        return new BuildingTradingOrderUiDto.Data(
                tradingOrderFacade.getCardId(), tradingOrderFacade.getCardsQuantity(), tradingOrderFacade.getEnergyForDeliver(),
                tradingOrderFacade.getEnergyForCancel(), tradingOrderFacade.getCost());
    }

}
