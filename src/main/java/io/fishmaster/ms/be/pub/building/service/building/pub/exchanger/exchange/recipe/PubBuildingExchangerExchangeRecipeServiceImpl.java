package io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.recipe;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fishmaster.ms.be.commons.constant.character.Speciality;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.req.CharacterParamDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.speciality.CharacterSpecialityDto;
import io.fishmaster.ms.be.pub.building.communication.character.service.CharacterCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.self.dto.CityTaxRateDto;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.recipe.ExchangerExchangeRecipeConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.system.SystemConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.exchanger.exchange.PubBuildingExchangerExchangeRecipeConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.Building;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.exchange.BuildingExchangerExchangeRecipeFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.recipe.BuildingExchangerExchangeRecipeUiDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PubBuildingExchangerExchangeRecipeServiceImpl implements PubBuildingExchangerExchangeRecipeService {

    private static final BuildingType BUILDING_TYPE = BuildingType.PUB;

    private final CityCommunicationService cityCommunicationService;
    private final CharacterCommunicationService characterCommunicationService;

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingRepository pubBuildingRepository;

    @Transactional
    @Override
    public List<BuildingExchangerExchangeRecipeUiDto> fetch(BuildingExchangerExchangeRecipeFetchedReqDto reqDto) {
        var building = pubBuildingRepository.getById(reqDto.getBuildingId());

        var cityDto = cityCommunicationService.fetchCity(building.getCityId());

        var systemConfigurationDto = configurationsStorageService.getSystemConfiguration();

        var characterDto = getCharacter(reqDto, cityDto.getId());

        var specialityLevelMap = characterDto.getSpecialities().stream()
                .collect(Collectors.toMap(dto -> dto.getConfiguration().getSpeciality(), CharacterSpecialityDto::getLevel));

        return configurationsStorageService.getExchangerExchangeRecipeConfigurations().stream()
                .sorted(Comparator.comparing(ExchangerExchangeRecipeConfigurationDto::getSequenceNumber))
                .map(getBuildingRecipe(
                        characterDto, specialityLevelMap, cityDto.getCityTaxRate(), building, systemConfigurationDto))
                .toList();
    }

    private CharacterDto getCharacter(BuildingExchangerExchangeRecipeFetchedReqDto reqDto, Long cityId) {
        if (Objects.isNull(reqDto.getCharacterId())) {
            return CharacterDto.ofDefault();
        }
        return characterCommunicationService.fetchCharacter(
                reqDto.getCharacterId(), reqDto.getAccountId(), cityId,
                CharacterParamDto.of(false, true, true, true));
    }

    private Function<ExchangerExchangeRecipeConfigurationDto, BuildingExchangerExchangeRecipeUiDto> getBuildingRecipe(
            CharacterDto characterDto, Map<Speciality, Integer> specialityLevelMap, CityTaxRateDto cityTaxRateDto,
            Building building, SystemConfigurationDto systemConfigurationDto) {
        return dto -> {
            var energy = CharacterInfluenceUtility.getCharacterEnergy(characterDto, dto, BUILDING_TYPE);
            var cost = CharacterInfluenceUtility.getCraftTax(cityTaxRateDto, systemConfigurationDto, dto);

            return BuildingExchangerExchangeRecipeUiDto.builder()
                    .characterId(characterDto.getId())
                    .building(PubBuildingExchangerExchangeRecipeConverter.toBuildingUiDto(building))
                    .detail(PubBuildingExchangerExchangeRecipeConverter.toDetailUiDto(dto))
                    .required(PubBuildingExchangerExchangeRecipeConverter.toRequiredUiDto(
                            dto.getRequired(), specialityLevelMap, energy, cost))
                    .result(PubBuildingExchangerExchangeRecipeConverter.toResultUiDto(dto.getResult()))
                    .build();
        };
    }
}
