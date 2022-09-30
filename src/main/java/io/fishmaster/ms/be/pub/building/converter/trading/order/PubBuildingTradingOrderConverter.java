package io.fishmaster.ms.be.pub.building.converter.trading.order;

import java.util.Objects;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.TradingOrderConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.PubBuildingTradingOrder;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.utility.TradingOrderFacade;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderDoubleUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.order.BuildingTradingOrderUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingTradingOrderConverter {

    public static PubBuildingTradingOrder toEntity(String accountId, PubBuilding pubBuilding, TradingOrderFacade tradingOrderFacade) {
        var configurationDto = tradingOrderFacade.getConfigurationDto();
        return new PubBuildingTradingOrder(
                null, accountId, pubBuilding, configurationDto.getId(), tradingOrderFacade.getTradingOrderData());
    }

    public static BuildingTradingOrderDoubleUiDto toDoubleUiDto(
            BuildingTradingOrderUiDto previous, BuildingTradingOrderUiDto current) {
        return new BuildingTradingOrderDoubleUiDto(previous, current);
    }

    public static BuildingTradingOrderUiDto toUiDto(
            PubBuildingTradingOrder pubBuildingTradingOrder, TradingOrderFacade tradingOrderFacade) {
        var pubBuilding = pubBuildingTradingOrder.getPubBuilding();
        var configurationDto = tradingOrderFacade.getConfigurationDto();
        return new BuildingTradingOrderUiDto(
                pubBuildingTradingOrder.getId(), configurationDto.getSequenceNumber(), pubBuilding.getId(),
                toSpecialityUiDto(configurationDto.getSpeciality()), toDataUiDto(tradingOrderFacade),
                ResultUiDto.of(), Status.CREATED);
    }

    public static BuildingTradingOrderUiDto toUiDto(PubBuilding pubBuilding, TradingOrderConfigurationDto configurationDto) {
        return new BuildingTradingOrderUiDto(
                null, configurationDto.getSequenceNumber(), pubBuilding.getId(), toSpecialityUiDto(configurationDto.getSpeciality()),
                toDataUiDto(null), ResultUiDto.of(), Status.CREATED);
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
