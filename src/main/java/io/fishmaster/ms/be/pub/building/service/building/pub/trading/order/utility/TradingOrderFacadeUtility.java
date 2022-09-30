package io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.character.Speciality;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.data.TradingOrderDataConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.order.data.TradingOrderDataConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TradingOrderFacadeUtility {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int SCALE = 2;

    public static Integer determineCardsQuantity(
            Integer specialityLevel, Integer buildingLevel, Integer cardLevel, Double slotCoefficient) {
        var cardsQuantity = BigDecimal.valueOf(specialityLevel)
                .add(BigDecimal.valueOf(buildingLevel))
                .divide(BigDecimal.valueOf(2), ROUNDING_MODE)
                .multiply(BigDecimal.valueOf(slotCoefficient))
                .subtract(BigDecimal.valueOf(cardLevel))
                .setScale(SCALE, ROUNDING_MODE)
                .intValue();

        return cardsQuantity <= 0 ? 1 : cardsQuantity;
    }

    public static Double determineBaseEnergy(Integer cardsQuantity, Double slotCoefficient) {
        return BigDecimal.valueOf(cardsQuantity)
                .multiply(BigDecimal.valueOf(slotCoefficient))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    public static Double determineEnergyForDeliver(CharacterDto characterDto, Double baseEnergy, BuildingType buildingType) {
        return CharacterInfluenceUtility.getCharacterEnergy(characterDto, baseEnergy, buildingType);
    }

    public static Double determineEnergyForCancel(Double energyForDeliver) {
        return BigDecimal.valueOf(energyForDeliver)
                .multiply(BigDecimal.valueOf(2))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    public static Double determineTax(Double totalCost, Double systemTaxRate, Double cityTaxRate) {
        return BigDecimal.valueOf(systemTaxRate)
                .add(BigDecimal.valueOf(cityTaxRate))
                .multiply(BigDecimal.valueOf(totalCost))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    public static Double determineCost(Double totalCost, Double tax) {
        return BigDecimal.valueOf(totalCost)
                .subtract(BigDecimal.valueOf(tax))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    public static Double determineTotalCost(Integer cardsQuantity, Double cost) {
        return BigDecimal.valueOf(cost)
                .multiply(BigDecimal.valueOf(cardsQuantity))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    public static List<ResultExperience> determineExperiences(
            Integer cardsQuantity, CharacterDto characterDto, TradingOrderDataConfigurationDto dataConfigurationDto,
            SystemConfigurationDto systemConfigurationDto, BuildingType buildingType) {
        var experience = dataConfigurationDto.getExperience();
        var specialityMap = Map.of(experience.getSpeciality(), 0);

        var characterExperiences = CharacterInfluenceUtility.getCharacterExperiences(
                characterDto, specialityMap, experience.getQuantity(), systemConfigurationDto, buildingType);

        characterExperiences.forEach(characterExperience -> {
            var newQuantity = BigDecimal.valueOf(characterExperience.getQuantity())
                    .multiply(BigDecimal.valueOf(cardsQuantity));

            characterExperience.setQuantity(newQuantity.doubleValue());
        });

        return characterExperiences;
    }

    public static TradingOrderDataConfigurationDto determineDataConfigurationBySlotNumber(Integer slotNumber, Integer buildingLevel,
            Map<Speciality, Integer> specialityLevelMap,
            TradingOrderDataConfigurationDtos dataConfigurationDtos) {
        var dataConfigurationDtoList = dataConfigurationDtos.stream().collect(Collectors.toCollection(ArrayList::new));
        return switch (slotNumber) {
            case 0 -> dataConfigurationDtoList.stream()
                    .filter(dto -> {
                        var speciality = dto.getSpeciality();
                        return speciality.getLevel() <= specialityLevelMap.get(speciality.getName());
                    })
                    .collect(Collectors.collectingAndThen(Collectors.toList(), TradingOrderFacadeUtility::determineConfiguration));
            case 1 -> dataConfigurationDtoList.stream()
                    .filter(dto -> dto.getLevel() <= buildingLevel)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), TradingOrderFacadeUtility::determineConfiguration));
            case 2 -> determineConfiguration(dataConfigurationDtoList);
            default -> throw new ServiceException(
                    ExceptionCode.INNER_SERVICE,
                    "Cannot determine food order configuration by slot number = %s".formatted(slotNumber));
        };
    }

    private static TradingOrderDataConfigurationDto determineConfiguration(
            List<TradingOrderDataConfigurationDto> foodOrderDataConfigurationDtos) {
        var randomNumber = ThreadLocalRandom.current().nextInt(0, foodOrderDataConfigurationDtos.size());

        Collections.shuffle(foodOrderDataConfigurationDtos);

        return foodOrderDataConfigurationDtos.get(randomNumber);
    }

}
