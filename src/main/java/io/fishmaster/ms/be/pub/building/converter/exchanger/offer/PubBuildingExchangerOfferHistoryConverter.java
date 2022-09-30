package io.fishmaster.ms.be.pub.building.converter.exchanger.offer;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.city.BuildingType;
import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import io.fishmaster.ms.be.commons.model.result.Result;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.ExchangerOfferConfigurationDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.exchanger.offer.group.ExchangerOfferGroupConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.ResultConverter;
import io.fishmaster.ms.be.pub.building.db.mongo.document.exchanger.offer.PubBuildingExchangerOfferHistory;
import io.fishmaster.ms.be.pub.building.service.character.utility.CharacterInfluenceUtility;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import io.fishmaster.ms.be.pub.building.web.dto.req.exchanger.offer.BuildingExchangerOfferExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.BuildingExchangerOfferGroupUiDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.exchanger.offer.BuildingExchangerOfferUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingExchangerOfferHistoryConverter {

    private static final BuildingType BUILDING_TYPE = BuildingType.PUB;

    public static PubBuildingExchangerOfferHistory toEntity(
            BuildingExchangerOfferExecutedReqDto reqDto, Status status, Collection<ResultExperience> experiences,
            Collection<AccountCardDto> lockedAccountCardDtos, Collection<AccountCardDto> collectedAccountCardDtos, Clock utcClock) {
        var lockedCardIds = lockedAccountCardDtos.stream()
                .map(AccountCardDto::getId)
                .collect(Collectors.toSet());

        var result = new Result(null, ResultConverter.toResultCard(collectedAccountCardDtos), new ArrayList<>(experiences), null);

        var now = utcClock.millis();
        return PubBuildingExchangerOfferHistory.builder()
                .requestId(MDCUtility.getTraceId())
                .accountId(reqDto.getAccountId())
                .buildingId(reqDto.getBuildingId())
                .characterId(reqDto.getCharacterId())
                .configurationId(reqDto.getConfigurationId())
                .lockedCardIds(lockedCardIds)
                .result(result)
                .status(status)
                .createdDate(now)
                .lastModifiedDate(now)
                .build();
    }

    public static BuildingExchangerOfferGroupUiDto toGroupUiDto(
            ExchangerOfferGroupConfigurationDto groupConfigurationDto, List<BuildingExchangerOfferUiDto> buildingExchangerOfferUiDtos) {
        return new BuildingExchangerOfferGroupUiDto(
                groupConfigurationDto.getDisplayName(), groupConfigurationDto.getDescription(),
                toGroupDateUiDto(groupConfigurationDto.getDate()), buildingExchangerOfferUiDtos);
    }

    private static BuildingExchangerOfferGroupUiDto.Date toGroupDateUiDto(ExchangerOfferGroupConfigurationDto.Date date) {
        return new BuildingExchangerOfferGroupUiDto.Date(date.getStart(), date.getEnd());
    }

    public static BuildingExchangerOfferUiDto toUiDto(
            PubBuildingExchangerOfferHistory pubBuildingExchangerOfferHistory, ExchangerOfferConfigurationDto configurationDto,
            Long offersCount, CharacterDto characterDto) {
        var characterId = pubBuildingExchangerOfferHistory.getCharacterId();
        var result = pubBuildingExchangerOfferHistory.getResult();

        var availableLimit = Math.max(configurationDto.getLimit() - offersCount, 0);
        return new BuildingExchangerOfferUiDto(
                pubBuildingExchangerOfferHistory.getId().toHexString(), pubBuildingExchangerOfferHistory.getBuildingId(),
                characterId, availableLimit, toConfigurationUiDto(configurationDto, characterDto),
                ResultUiDto.of(characterId, result), pubBuildingExchangerOfferHistory.getStatus());
    }

    public static BuildingExchangerOfferUiDto toUiDto(
            Long buildingId, ExchangerOfferConfigurationDto exchangerOfferConfigurationDto,
            Long offersCount, CharacterDto characterDto) {
        var availableLimit = Math.max(exchangerOfferConfigurationDto.getLimit() - offersCount, 0);
        return new BuildingExchangerOfferUiDto(
                null, buildingId, null, availableLimit, toConfigurationUiDto(exchangerOfferConfigurationDto, characterDto),
                ResultUiDto.of(), Status.CREATED);
    }

    private static BuildingExchangerOfferUiDto.Configuration toConfigurationUiDto(
            ExchangerOfferConfigurationDto exchangerOfferConfigurationDto, CharacterDto characterDto) {
        return new BuildingExchangerOfferUiDto.Configuration(
                exchangerOfferConfigurationDto.getId(), exchangerOfferConfigurationDto.getDisplayName(),
                exchangerOfferConfigurationDto.getDescription(), exchangerOfferConfigurationDto.getLevel(),
                exchangerOfferConfigurationDto.getLimit(), toRequiredUiDto(exchangerOfferConfigurationDto.getRequired(), characterDto),
                toResultUiDto(exchangerOfferConfigurationDto.getResult()));
    }

    private static BuildingExchangerOfferUiDto.Required toRequiredUiDto(
            ExchangerOfferConfigurationDto.Required required, CharacterDto characterDto) {
        var energy = CharacterInfluenceUtility.getCharacterEnergy(characterDto, required.getEnergy(), BUILDING_TYPE);
        return new BuildingExchangerOfferUiDto.Required(
                energy, toCostUiDto(required.getCost()), toSpecialityUiDto(required.getSpecialities()),
                toCardUiDto(required.getCards()));
    }

    private static BuildingExchangerOfferUiDto.Result toResultUiDto(ExchangerOfferConfigurationDto.Result result) {
        return new BuildingExchangerOfferUiDto.Result(toCardUiDto(result.getCard()));
    }

    private static BuildingExchangerOfferUiDto.Cost toCostUiDto(ExchangerOfferConfigurationDto.Cost cost) {
        return new BuildingExchangerOfferUiDto.Cost(cost.getCurrency(), cost.getQuantity());
    }

    private static List<BuildingExchangerOfferUiDto.Speciality> toSpecialityUiDto(
            List<ExchangerOfferConfigurationDto.Speciality> specialities) {
        return specialities.stream()
                .map(PubBuildingExchangerOfferHistoryConverter::toSpecialityUiDto)
                .collect(Collectors.toList());
    }

    private static BuildingExchangerOfferUiDto.Speciality toSpecialityUiDto(ExchangerOfferConfigurationDto.Speciality speciality) {
        return new BuildingExchangerOfferUiDto.Speciality(
                speciality.getSequenceNumber(), speciality.getName(), speciality.getLevel());
    }

    private static List<BuildingExchangerOfferUiDto.Card> toCardUiDto(List<ExchangerOfferConfigurationDto.Card> cards) {
        return cards.stream()
                .map(PubBuildingExchangerOfferHistoryConverter::toCardUiDto)
                .collect(Collectors.toList());
    }

    private static BuildingExchangerOfferUiDto.Card toCardUiDto(ExchangerOfferConfigurationDto.Card card) {
        return new BuildingExchangerOfferUiDto.Card(card.getSequenceNumber(), card.getId(), card.getQuantity());
    }

}
