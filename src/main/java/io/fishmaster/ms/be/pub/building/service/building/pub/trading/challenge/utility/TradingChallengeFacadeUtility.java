package io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.TradingChallengeConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.data.TradingChallengeDataConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.data.TradingChallengeDataConfigurationDtos;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.share.TradingChallengeCheckpoint;
import io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TradingChallengeFacadeUtility {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int SCALE = 2;

    public static TradingChallengeDataConfigurationDto determineDataConfiguration(
            TradingChallengeDataConfigurationDtos dataConfigurationDtos) {
        var dataConfigurationDtoList = dataConfigurationDtos.stream()
                .filter(dto -> dto.getLevel() < 3)
                .collect(Collectors.toCollection(ArrayList::new));

        var randomNumber = ThreadLocalRandom.current().nextInt(0, dataConfigurationDtoList.size());

        Collections.shuffle(dataConfigurationDtoList);

        return dataConfigurationDtoList.get(randomNumber);
    }

    public static Integer determineCardsQuantity(TradingChallengeConfigurationDto configurationDto) {
        return ThreadLocalRandom.current().nextInt(1, configurationDto.getMaxCardsQuantity() + 1);
    }

    public static Long determineChallengeEndedTime(TradingChallengeConfigurationDto configurationDto, Clock utcClock) {
        return utcClock.millis() + configurationDto.getTime().getInterval();
    }

    public static Double determineCost(List<TradingChallengeCheckpoint> checkpoints, Clock utcClock) {
        var now = utcClock.millis();
        return checkpoints.stream()
                .sorted(Comparator.comparing(TradingChallengeCheckpoint::getTime))
                .filter(checkpoint -> checkpoint.getTime() > now)
                .findFirst()
                .orElse(checkpoints.get(checkpoints.size() - 1))
                .getCost();
    }

    public static Double determineTax(List<TradingChallengeCheckpoint> checkpoints, Clock utcClock) {
        var now = utcClock.millis();
        return checkpoints.stream()
                .sorted(Comparator.comparing(TradingChallengeCheckpoint::getTime))
                .filter(checkpoint -> checkpoint.getTime() > now)
                .findFirst()
                .orElse(checkpoints.get(checkpoints.size() - 1))
                .getTax();
    }

    public static List<ResultExperience> determineExperiences(
            Integer cardsQuantity, CharacterDto characterDto, TradingChallengeDataConfigurationDto dataConfigurationDto,
            SystemConfigurationDto systemConfigurationDto, BuildingType buildingType) {
        var experience = dataConfigurationDto.getExperience();

        var characterExperiences = CharacterInfluenceUtility.getCharacterExperiences(
                characterDto, Map.of(experience.getSpeciality(), 0), experience.getQuantity(),
                systemConfigurationDto, buildingType);

        characterExperiences.forEach(characterExperience -> {
            var newQuantity = BigDecimal.valueOf(characterExperience.getQuantity())
                    .multiply(BigDecimal.valueOf(cardsQuantity));

            characterExperience.setQuantity(newQuantity.doubleValue());
        });

        return characterExperiences;
    }

    public static List<TradingChallengeCheckpoint> determineCheckpoints(
            Integer cardsQuantity, Long challengeEndedTime, Double dataConfigurationCost,
            Double systemTaxRate, Double cityTaxRate, TradingChallengeConfigurationDto configurationDto) {
        var time = configurationDto.getTime();

        var challengeStartedTime = challengeEndedTime - time.getInterval();

        var baseCost = determineBaseCost(cardsQuantity, dataConfigurationCost);
        var baseCostRate = determineBaseCostRate(baseCost, configurationDto.getPriceRate());

        var intervalInUnitTo = TimeUnit.valueOf(time.getToUnit()).convert(time.getInterval(), TimeUnit.valueOf(time.getFromUnit()));

        return LongStream.range(0, intervalInUnitTo)
                .mapToObj(value -> {
                    var totalCost = determineTotalCost(value, baseCost, baseCostRate);

                    var tax = determineTax(totalCost, systemTaxRate, cityTaxRate);
                    var cost = determineCost(totalCost, tax);

                    var timeCheckpoint = challengeStartedTime + TimeUnit.valueOf(time.getFromUnit())
                            .convert(value + 1, TimeUnit.valueOf(time.getToUnit()));

                    return new TradingChallengeCheckpoint(timeCheckpoint, cost, tax);
                })
                .sorted(Comparator.comparing(TradingChallengeCheckpoint::getTime))
                .toList();
    }

    private static Double determineBaseCost(Integer cardsQuantity, Double cost) {
        return BigDecimal.valueOf(cost)
                .multiply(BigDecimal.valueOf(cardsQuantity))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    private static Double determineBaseCostRate(Double baseCost, Double priceRate) {
        return BigDecimal.valueOf(baseCost)
                .multiply(BigDecimal.valueOf(priceRate))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    private static Double determineTotalCost(Long value, Double baseCost, Double baseCostRate) {
        return BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(baseCostRate))
                .add(BigDecimal.valueOf(baseCost))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    private static Double determineTax(Double totalCost, Double systemTaxRate, Double cityTaxRate) {
        return BigDecimal.valueOf(systemTaxRate)
                .add(BigDecimal.valueOf(cityTaxRate))
                .multiply(BigDecimal.valueOf(totalCost))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

    private static Double determineCost(Double totalCost, Double tax) {
        return BigDecimal.valueOf(totalCost)
                .subtract(BigDecimal.valueOf(tax))
                .setScale(SCALE, ROUNDING_MODE)
                .doubleValue();
    }

}
