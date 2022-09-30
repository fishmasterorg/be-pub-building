package io.fishmaster.ms.be.pub.building.service.character.utility;

import static io.fishmaster.ms.be.commons.constant.Status.ACTIVE;
import static io.fishmaster.ms.be.pub.building.service.character.utility.CharacterUtility.getSkillValue;
import static io.fishmaster.ms.be.pub.building.service.character.utility.CharacterUtility.getTraitValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.character.SkillEffect;
import io.fishmaster.ms.be.commons.constant.character.Speciality;
import io.fishmaster.ms.be.commons.constant.character.TraitEffect;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedForCraftReqDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.speciality.CharacterSpecialityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityTaxRateDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.barman.offer.BarmanOfferConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.ExchangerOfferConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CharacterInfluenceUtility {

    private static final double RANDOM_CHANCE_ORIGIN = 0D;
    private static final double RANDOM_CHANCE_BOUND = 1D;

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int SCALE = 2;


    public static List<AccountCardLockedForCraftReqDto> getLockedCards(
            CharacterDto characterDto, CityDto cityDto, ExchangerExchangeRecipeConfigurationDto recipeConfigurationDto,
            BuildingType buildingType) {
        var toLockRequiredComponentCards = recipeConfigurationDto.getRequired().getCards().stream()
                .map(card -> AccountCardLockedForCraftReqDto.of(characterDto.getAccountId(), cityDto.getId(), ACTIVE, card.getId(),
                        card.getQuantity()))
                .toList();

        return getLockedComponentCards(characterDto, toLockRequiredComponentCards, buildingType);
    }

    public static Double getCraftTax(
            CityTaxRateDto cityTaxRateDto, SystemConfigurationDto systemConfigurationDto,
            ExchangerExchangeRecipeConfigurationDto recipeConfigurationDto) {
        var result = BigDecimal.valueOf(systemConfigurationDto.getTaxRate())
                .add(BigDecimal.valueOf(cityTaxRateDto.getPubExchangerExchange()))
                .multiply(BigDecimal.valueOf(recipeConfigurationDto.getCost()));

        return round(result).doubleValue();
    }

    public static Double getCharacterEnergy(
            CharacterDto characterDto, ExchangerExchangeRecipeConfigurationDto recipeConfigurationDto, BuildingType buildingType) {
        return getCharacterEnergy(characterDto, recipeConfigurationDto.getRequired().getEnergy(), buildingType);
    }

    public static Double getCharacterEnergy(CharacterDto characterDto, Double energy, BuildingType buildingType) {
        var requiredEnergyQuantity = BigDecimal.valueOf(energy);

        var resultEnergy = requiredEnergyQuantity
                .add(getSkillValue(characterDto.getSkillSlots(), SkillEffect.ENERGY_EXPENDED, requiredEnergyQuantity, buildingType))
                .add(getTraitValue(characterDto.getTraitSlots(), TraitEffect.ENERGY_EXPENDED, buildingType));

        return roundAndCompareWithZero(resultEnergy).doubleValue();
    }

    public static Integer getCardRewardsCount(CharacterDto characterDto, Integer level, BuildingType buildingType) {
        return getSkillValue(characterDto.getSkillSlots(), SkillEffect.REWARD, level, buildingType).intValue();
    }

    public static boolean isFailAction(CharacterDto characterDto, BuildingType buildingType) {
        var failActionChance = getTraitValue(characterDto.getTraitSlots(), TraitEffect.FAIL_ACTION, buildingType)
                .doubleValue();

        var randomChance = ThreadLocalRandom.current().nextDouble(RANDOM_CHANCE_ORIGIN, RANDOM_CHANCE_BOUND);

        return failActionChance >= randomChance;
    }

    public static List<ResultExperience> getCharacterExperiences(
            CharacterDto characterDto, ExchangerExchangeRecipeConfigurationDto recipeConfigurationDto,
            SystemConfigurationDto systemConfigurationDto, BuildingType buildingType) {
        var exp = recipeConfigurationDto.getResult().getExp();

        var specialityMap = recipeConfigurationDto.getRequired().getSpecialities().stream()
                .collect(Collectors.toMap(
                        ExchangerExchangeRecipeConfigurationDto.Speciality::getName,
                        ExchangerExchangeRecipeConfigurationDto.Speciality::getSequenceNumber));

        return getCharacterExperiences(characterDto, specialityMap, exp, systemConfigurationDto, buildingType);
    }

    public static List<ResultExperience> getCharacterExperiences(
            CharacterDto characterDto, BarmanOfferConfigurationDto offerConfigurationDto, SystemConfigurationDto systemConfigurationDto,
            BuildingType buildingType) {
        var exp = offerConfigurationDto.getResult().getExp();

        var specialityMap = offerConfigurationDto.getRequired().getSpecialities().stream()
                .collect(Collectors.toMap(
                        BarmanOfferConfigurationDto.Speciality::getName,
                        BarmanOfferConfigurationDto.Speciality::getSequenceNumber));

        return getCharacterExperiences(characterDto, specialityMap, exp, systemConfigurationDto, buildingType);
    }

    public static List<ResultExperience> getCharacterExperiences(
            CharacterDto characterDto, ExchangerOfferConfigurationDto offerConfigurationDto, SystemConfigurationDto systemConfigurationDto,
            BuildingType buildingType) {
        var exp = offerConfigurationDto.getResult().getExp();

        var specialityMap = offerConfigurationDto.getRequired().getSpecialities().stream()
                .collect(Collectors.toMap(
                        ExchangerOfferConfigurationDto.Speciality::getName,
                        ExchangerOfferConfigurationDto.Speciality::getSequenceNumber));

        return getCharacterExperiences(characterDto, specialityMap, exp, systemConfigurationDto, buildingType);
    }

    public static List<ResultExperience> getCharacterExperiences(
            CharacterDto characterDto, Map<Speciality, Integer> specialitiesMap,
            Long exp, SystemConfigurationDto systemConfigurationDto, BuildingType buildingType) {
        var experience = getExperience(characterDto, exp, buildingType);

        return characterDto.getSpecialities().stream()
                .map(CharacterSpecialityDto::getConfiguration)
                .filter(dto -> specialitiesMap.containsKey(dto.getSpeciality()))
                .map(dto -> {
                    var totalExperience = dto.getBuilding() != buildingType
                            ? experience.multiply(BigDecimal.valueOf(systemConfigurationDto.getCraftExperienceSpecialityMultiplier()))
                            : experience;

                    var sequenceNumber = specialitiesMap.get(dto.getSpeciality());
                    return new ResultExperience(sequenceNumber, dto.getSpeciality(), roundToDouble(totalExperience));
                })
                .toList();
    }

    private static List<AccountCardLockedForCraftReqDto> getLockedComponentCards(CharacterDto characterDto,
            List<AccountCardLockedForCraftReqDto> toLockRequiredCards, BuildingType buildingType) {
        var componentCardsCountToBeReturned = getSkillValue(characterDto.getSkillSlots(), SkillEffect.COMPONENT, buildingType).intValue();

        while (componentCardsCountToBeReturned > 0) {
            var randomIndex = ThreadLocalRandom.current().nextInt(0, toLockRequiredCards.size());
            var toLockedRequiredCard = toLockRequiredCards.get(randomIndex);

            var newLimit = toLockedRequiredCard.getLimit() - 1;
            if (newLimit >= 0) {
                toLockedRequiredCard.setLimit(newLimit);
                componentCardsCountToBeReturned--;
            }

            var match = toLockRequiredCards.stream().allMatch(card -> card.getLimit() == 0);
            if (match) {
                break;
            }
        }
        return toLockRequiredCards;
    }

    private static BigDecimal getExperience(CharacterDto characterDto, Long exp, BuildingType buildingType) {
        var requiredExpQuantity = BigDecimal.valueOf(exp);

        return requiredExpQuantity
                .add(getSkillValue(characterDto.getSkillSlots(), SkillEffect.EXPERIENCE, requiredExpQuantity, buildingType))
                .add(getTraitValue(characterDto.getTraitSlots(), TraitEffect.EXPERIENCE, requiredExpQuantity, buildingType));
    }

    private static BigDecimal round(BigDecimal quantity) {
        return quantity.setScale(SCALE, ROUNDING_MODE);
    }

    private static BigDecimal roundAndCompareWithZero(BigDecimal quantity) {
        quantity = round(quantity);
        return quantity.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : quantity;
    }

    private static Double roundToDouble(BigDecimal quantity) {
        return round(quantity).doubleValue();
    }

}
