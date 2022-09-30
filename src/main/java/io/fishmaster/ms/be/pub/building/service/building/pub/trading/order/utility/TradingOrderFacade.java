package io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.utility;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.speciality.CharacterSpecialityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.TradingOrderConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.TradingOrderConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.data.TradingOrderDataConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.data.TradingOrderDataConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.PubBuildingTradingOrder;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.model.TradingOrderData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradingOrderFacade {
    TradingOrderConfigurationDto configurationDto;
    TradingOrderDataConfigurationDto dataConfigurationDto;
    Integer cardsQuantity;
    Double baseEnergy;
    Double energyForDeliver;
    Double energyForCancel;
    Double cost;
    Double tax;
    List<ResultExperience> experiences;

    public Long getCardId() {
        return getDataConfigurationDto().getCardId();
    }

    public TradingOrderData getTradingOrderData() {
        var dataConfigurationDto = getDataConfigurationDto();
        return new TradingOrderData(
                dataConfigurationDto.getId(), getCardsQuantity(), getBaseEnergy());
    }

    public static TradingOrderFacade buildWithoutExperiences(
            CharacterDto characterDto, CityDto cityDto, Integer pubBuildingLevel,
            Map<Long, Integer> cardLevelMap, TradingOrderDataConfigurationDtos dataConfigurationDtos,
            TradingOrderConfigurationDto configurationDto, SystemConfigurationDto systemConfigurationDto, BuildingType buildingType) {
        var specialityLevelMap = characterDto.getSpecialities().stream()
                .collect(Collectors.toMap(dto -> dto.getConfiguration().getSpeciality(), CharacterSpecialityDto::getLevel));

        var dataConfigurationDto = TradingOrderFacadeUtility.determineDataConfigurationBySlotNumber(
                configurationDto.getSequenceNumber(), pubBuildingLevel, specialityLevelMap, dataConfigurationDtos);

        var experience = dataConfigurationDto.getExperience();

        var cardsQuantity = TradingOrderFacadeUtility.determineCardsQuantity(
                specialityLevelMap.get(experience.getSpeciality()), pubBuildingLevel,
                cardLevelMap.get(dataConfigurationDto.getCardId()), configurationDto.getCoefficient());

        var baseEnergy = TradingOrderFacadeUtility.determineBaseEnergy(cardsQuantity, configurationDto.getCoefficient());

        return build(
                characterDto, systemConfigurationDto.getTaxRate(), cityDto.getCityTaxRate().getPubOrder(),
                cardsQuantity, baseEnergy, List.of(), dataConfigurationDto, configurationDto, buildingType);
    }

    public static TradingOrderFacade buildWithoutExperiences(
            CharacterDto characterDto, CityDto cityDto, PubBuildingTradingOrder pubBuildingFoodOrder,
            TradingOrderDataConfigurationDtos dataConfigurationDtos, TradingOrderConfigurationDtos configurationDtos,
            SystemConfigurationDto systemConfigurationDto, BuildingType buildingType) {
        var data = pubBuildingFoodOrder.getData();

        var configurationDto = configurationDtos.getById(pubBuildingFoodOrder.getConfigurationId());
        var dataConfigurationDto = dataConfigurationDtos.getById(data.getConfigurationId());

        return build(
                characterDto, systemConfigurationDto.getTaxRate(), cityDto.getCityTaxRate().getPubOrder(),
                data.getCardsQuantity(), data.getBaseEnergy(), List.of(), dataConfigurationDto, configurationDto,
                buildingType);
    }

    public static TradingOrderFacade buildWithExperiences(
            CharacterDto characterDto, CityDto cityDto, PubBuildingTradingOrder pubBuildingFoodOrder,
            TradingOrderDataConfigurationDtos dataConfigurationDtos, TradingOrderConfigurationDtos configurationDtos,
            SystemConfigurationDto systemConfigurationDto, BuildingType buildingType) {
        var data = pubBuildingFoodOrder.getData();

        var configurationDto = configurationDtos.getById(pubBuildingFoodOrder.getConfigurationId());
        var dataConfigurationDto = dataConfigurationDtos.getById(data.getConfigurationId());

        var characterExperiences = TradingOrderFacadeUtility.determineExperiences(
                data.getCardsQuantity(), characterDto, dataConfigurationDto, systemConfigurationDto, buildingType);

        return build(
                characterDto, systemConfigurationDto.getTaxRate(), cityDto.getCityTaxRate().getPubOrder(),
                data.getCardsQuantity(), data.getBaseEnergy(), characterExperiences, dataConfigurationDto, configurationDto,
                buildingType);
    }

    private static TradingOrderFacade build(
            CharacterDto characterDto, Double systemTaxRate, Double cityTaxRate,
            Integer cardsQuantity, Double baseEnergy, List<ResultExperience> experiences,
            TradingOrderDataConfigurationDto dataConfigurationDto, TradingOrderConfigurationDto configurationDto,
            BuildingType buildingType) {
        var energyForDeliver = TradingOrderFacadeUtility.determineEnergyForDeliver(characterDto, baseEnergy, buildingType);
        var energyForCancel = TradingOrderFacadeUtility.determineEnergyForCancel(energyForDeliver);
        var totalCost = TradingOrderFacadeUtility.determineTotalCost(cardsQuantity, dataConfigurationDto.getCost());
        var tax = TradingOrderFacadeUtility.determineTax(totalCost, systemTaxRate, cityTaxRate);
        var cost = TradingOrderFacadeUtility.determineCost(totalCost, tax);

        return new TradingOrderFacade(
                configurationDto, dataConfigurationDto, cardsQuantity, baseEnergy, energyForDeliver, energyForCancel,
                cost, tax, experiences);
    }

}
