package io.fishmaster.ms.be.pub.building.converter.barman.offer;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import io.fishmaster.ms.be.commons.model.result.Result;
import io.fishmaster.ms.be.commons.model.result.ResultExperience;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.barman.offer.BarmanOfferConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.ResultConverter;
import io.fishmaster.ms.be.pub.building.db.mongo.document.barman.offer.PubBuildingBarmanOfferHistory;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import io.fishmaster.ms.be.pub.building.web.dto.req.barman.offer.BuildingBarmanOfferExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.barman.offer.BuildingBarmanOfferUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingBarmanOfferHistoryConverter {

    public static PubBuildingBarmanOfferHistory toEntity(
            BuildingBarmanOfferExecutedReqDto reqDto, Status status, Collection<ResultExperience> experiences,
            Collection<AccountCardDto> accountCardDtos, Clock utcClock) {
        var result = new Result(null, ResultConverter.toResultCard(accountCardDtos), new ArrayList<>(experiences), null);

        var now = utcClock.millis();
        return PubBuildingBarmanOfferHistory.builder()
                .requestId(MDCUtility.getTraceId())
                .accountId(reqDto.getAccountId())
                .buildingId(reqDto.getBuildingId())
                .characterId(reqDto.getCharacterId())
                .configurationId(reqDto.getConfigurationId())
                .result(result)
                .status(status)
                .createdDate(now)
                .lastModifiedDate(now)
                .build();
    }

    public static BuildingBarmanOfferUiDto toUiDto(
            PubBuildingBarmanOfferHistory pubBuildingBarmanOfferHistory, BarmanOfferConfigurationDto barmanOfferConfigurationDto,
            Long weeklyOffersCount) {
        var result = pubBuildingBarmanOfferHistory.getResult();
        var characterId = pubBuildingBarmanOfferHistory.getCharacterId();

        var availableLimit = Math.max(barmanOfferConfigurationDto.getWeeklyLimit() - weeklyOffersCount, 0);
        return new BuildingBarmanOfferUiDto(
                pubBuildingBarmanOfferHistory.getId().toHexString(), pubBuildingBarmanOfferHistory.getBuildingId(),
                characterId, availableLimit, toConfigurationUiDto(barmanOfferConfigurationDto),
                ResultUiDto.of(characterId, result), pubBuildingBarmanOfferHistory.getStatus());
    }

    public static BuildingBarmanOfferUiDto toUiDto(
            Long buildingId, BarmanOfferConfigurationDto barmanOfferConfigurationDto, Long weeklyOffersCount) {
        var availableLimit = Math.max(barmanOfferConfigurationDto.getWeeklyLimit() - weeklyOffersCount, 0);
        return new BuildingBarmanOfferUiDto(
                null, buildingId, null, availableLimit, toConfigurationUiDto(barmanOfferConfigurationDto),
                ResultUiDto.of(), Status.CREATED);
    }

    public static BuildingBarmanOfferUiDto.Configuration toConfigurationUiDto(BarmanOfferConfigurationDto barmanOfferConfigurationDto) {
        return new BuildingBarmanOfferUiDto.Configuration(
                barmanOfferConfigurationDto.getId(), barmanOfferConfigurationDto.getDisplayName(),
                barmanOfferConfigurationDto.getDescription(), barmanOfferConfigurationDto.getLevel(),
                barmanOfferConfigurationDto.getWeeklyLimit(), toRequiredUiDto(barmanOfferConfigurationDto.getRequired()),
                toResultUiDto(barmanOfferConfigurationDto.getResult()));
    }

    public static BuildingBarmanOfferUiDto.Required toRequiredUiDto(BarmanOfferConfigurationDto.Required required) {
        return new BuildingBarmanOfferUiDto.Required(
                toCostUiDto(required.getCost()), toSpecialityUiDto(required.getSpecialities()));
    }

    public static BuildingBarmanOfferUiDto.Result toResultUiDto(BarmanOfferConfigurationDto.Result result) {
        return new BuildingBarmanOfferUiDto.Result(
                toCardUiDto(result.getCard()));
    }

    public static BuildingBarmanOfferUiDto.Cost toCostUiDto(BarmanOfferConfigurationDto.Cost cost) {
        return new BuildingBarmanOfferUiDto.Cost(cost.getCurrency(), cost.getQuantity());
    }

    public static List<BuildingBarmanOfferUiDto.Speciality> toSpecialityUiDto(List<BarmanOfferConfigurationDto.Speciality> specialities) {
        return specialities.stream()
                .map(PubBuildingBarmanOfferHistoryConverter::toSpecialityUiDto)
                .collect(Collectors.toList());
    }

    public static BuildingBarmanOfferUiDto.Speciality toSpecialityUiDto(BarmanOfferConfigurationDto.Speciality speciality) {
        return new BuildingBarmanOfferUiDto.Speciality(
                speciality.getSequenceNumber(), speciality.getName(), speciality.getLevel());
    }

    public static BuildingBarmanOfferUiDto.Card toCardUiDto(BarmanOfferConfigurationDto.Card card) {
        return new BuildingBarmanOfferUiDto.Card(card.getId(), card.getQuantity());
    }

}
