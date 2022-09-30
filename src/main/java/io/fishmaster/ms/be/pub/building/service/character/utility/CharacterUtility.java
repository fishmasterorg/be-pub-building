package io.fishmaster.ms.be.pub.building.service.character.utility;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.character.SkillEffect;
import io.fishmaster.ms.be.commons.constant.character.TraitEffect;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.pub.building.communication.character.dto.skill.CharacterSkillSlotDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.speciality.CharacterSpecialityDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.trait.CharacterTraitSlotDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CharacterUtility {

    private static final double RANDOM_CHANCE_ORIGIN = 0D;
    private static final double RANDOM_CHANCE_BOUND = 1D;

    public static BigDecimal getSpecialityCraftTimeValue(
            List<CharacterSpecialityDto> characterSpecialityDtos, BigDecimal requiredQuantity, BuildingType buildingType) {
        return characterSpecialityDtos.stream()
                .map(CharacterSpecialityDto::getConfiguration)
                .filter(dto -> buildingType == dto.getBuilding())
                .map(CharacterSpecialityDto.Configuration::getCraftingSpeed)
                .map(BigDecimal::valueOf)
                .map(requiredQuantity::multiply)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public static BigDecimal getSkillValue(
            List<CharacterSkillSlotDto> characterSkillSlotDtos, SkillEffect skillEffect, BuildingType buildingType) {
        return getSkillValue(characterSkillSlotDtos, skillEffect, BigDecimal.ONE, buildingType);
    }

    public static BigDecimal getSkillValue(
            List<CharacterSkillSlotDto> characterSkillSlotDtos, SkillEffect skillEffect, BigDecimal requiredQuantity,
            BuildingType buildingType) {
        return characterSkillSlotDtos.stream()
                .filter(dto -> dto.getStatus() == Status.BUSY)
                .map(CharacterSkillSlotDto::getSkillConfiguration)
                .filter(Objects::nonNull)
                .filter(configuration -> configuration.getBuildings().contains(buildingType))
                .map(CharacterSkillSlotDto.Configuration::getEffect)
                .filter(effect -> skillEffect == effect.getName())
                .map(effect -> {
                    var randomChance = ThreadLocalRandom.current().nextDouble(RANDOM_CHANCE_ORIGIN, RANDOM_CHANCE_BOUND);
                    return effect.getChance() >= randomChance ? requiredQuantity.multiply(BigDecimal.valueOf(effect.getQuantity()))
                            : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal getSkillValue(
            List<CharacterSkillSlotDto> characterSkillSlotDtos, SkillEffect skillEffect, Integer level,
            BuildingType buildingType) {
        return getSkillValue(characterSkillSlotDtos, skillEffect, level, BigDecimal.ONE, buildingType);
    }

    public static BigDecimal getSkillValue(
            List<CharacterSkillSlotDto> characterSkillSlotDtos, SkillEffect skillEffect, Integer level, BigDecimal requiredQuantity,
            BuildingType buildingType) {
        return characterSkillSlotDtos.stream()
                .filter(dto -> dto.getStatus() == Status.BUSY)
                .map(CharacterSkillSlotDto::getSkillConfiguration)
                .filter(Objects::nonNull)
                .filter(configuration -> configuration.getBuildings().contains(buildingType))
                .map(CharacterSkillSlotDto.Configuration::getEffect)
                .filter(effect -> skillEffect == effect.getName())
                .filter(effect -> effect.getLevels().contains(level))
                .map(effect -> {
                    var randomChance = ThreadLocalRandom.current().nextDouble(RANDOM_CHANCE_ORIGIN, RANDOM_CHANCE_BOUND);
                    return effect.getChance() >= randomChance ? requiredQuantity.multiply(BigDecimal.valueOf(effect.getQuantity()))
                            : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal getTraitValue(
            List<CharacterTraitSlotDto> characterTraitSlotDtos, TraitEffect traitEffect, BuildingType buildingType) {
        return getTraitValue(characterTraitSlotDtos, traitEffect, BigDecimal.ONE, buildingType);
    }

    public static BigDecimal getTraitValue(
        List<CharacterTraitSlotDto> characterTraitSlotDtos, TraitEffect traitEffect,BigDecimal requiredQuantity, BuildingType buildingType) {
        return characterTraitSlotDtos.stream()
                .filter(dto -> dto.getStatus() == Status.BUSY)
                .map(CharacterTraitSlotDto::getTraitConfiguration)
                .filter(Objects::nonNull)
                .filter(configuration -> configuration.getBuildings().contains(buildingType))
                .map(CharacterTraitSlotDto.Configuration::getEffect)
                .filter(effect -> traitEffect == effect.getName())
                .map(effect -> {
                    var randomChance = ThreadLocalRandom.current().nextDouble(RANDOM_CHANCE_ORIGIN, RANDOM_CHANCE_BOUND);
                    return effect.getChance() >= randomChance ? requiredQuantity.multiply(BigDecimal.valueOf(effect.getQuantity()))
                            : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
