package io.fishmaster.ms.be.pub.building.converter.exchanger.exchange;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.fishmaster.ms.be.commons.constant.character.Speciality;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.Building;
import io.fishmaster.ms.be.pub.building.web.dto.ui.recipe.BuildingExchangerExchangeRecipeUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingExchangerExchangeRecipeConverter {

    public static BuildingExchangerExchangeRecipeUiDto.Building toBuildingUiDto(Building building) {
        return new BuildingExchangerExchangeRecipeUiDto.Building(building.getId(), building.getLevel());
    }

    public static BuildingExchangerExchangeRecipeUiDto.Detail toDetailUiDto(ExchangerExchangeRecipeConfigurationDto dto) {
        return new BuildingExchangerExchangeRecipeUiDto.Detail(dto.getId(), dto.getName(), dto.getLevel());
    }

    public static BuildingExchangerExchangeRecipeUiDto.Required toRequiredUiDto(
            ExchangerExchangeRecipeConfigurationDto.Required required, Map<Speciality, Integer> specialityLevelMap,
            Double energy, Double cost) {
        return new BuildingExchangerExchangeRecipeUiDto.Required(
                toSpecialityUiDto(required.getSpecialities(), specialityLevelMap), toCardUiDto(required.getCards()),
                energy, cost);
    }

    public static BuildingExchangerExchangeRecipeUiDto.Result toResultUiDto(ExchangerExchangeRecipeConfigurationDto.Result result) {
        return new BuildingExchangerExchangeRecipeUiDto.Result(toCardUiDto(result.getCard()));
    }

    public static List<BuildingExchangerExchangeRecipeUiDto.Speciality> toSpecialityUiDto(
            List<ExchangerExchangeRecipeConfigurationDto.Speciality> specialities,
            Map<Speciality, Integer> specialityLevelMap) {
        return specialities.stream()
                .sorted(Comparator.comparing(ExchangerExchangeRecipeConfigurationDto.Speciality::getSequenceNumber))
                .map(speciality -> toSpecialityUiDto(speciality, specialityLevelMap))
                .toList();
    }

    public static BuildingExchangerExchangeRecipeUiDto.Speciality toSpecialityUiDto(
            ExchangerExchangeRecipeConfigurationDto.Speciality speciality,
            Map<Speciality, Integer> specialityLevelMap) {
        return new BuildingExchangerExchangeRecipeUiDto.Speciality(
                specialityLevelMap.getOrDefault(speciality.getName(), 0), speciality.getName(),
                speciality.getLevel());
    }

    public static List<BuildingExchangerExchangeRecipeUiDto.Card> toCardUiDto(List<ExchangerExchangeRecipeConfigurationDto.Card> cards) {
        return cards.stream()
                .map(PubBuildingExchangerExchangeRecipeConverter::toCardUiDto)
                .toList();
    }

    public static BuildingExchangerExchangeRecipeUiDto.Card toCardUiDto(ExchangerExchangeRecipeConfigurationDto.Card card) {
        return new BuildingExchangerExchangeRecipeUiDto.Card(card.getSequenceNumber(), card.getId(), card.getQuantity());
    }

}
