package io.fishmaster.ms.be.pub.building.converter.lottery;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.lottery.LotteryPackConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.mongo.document.lottery.PubBuildingLotteryHistory;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import io.fishmaster.ms.be.pub.building.web.dto.req.lottery.BuildingLotteryExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.lottery.BuildingLotteryUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingLotteryHistoryConverter {

    public static PubBuildingLotteryHistory toEntity(
            BuildingLotteryExecutedReqDto reqDto, LotteryPackConfigurationDto.Card winnerCard,
            List<LotteryPackConfigurationDto.Card> imitateCards, Collection<AccountCardDto> accountCardDtos, Clock utcClock) {
        var collectedCardIds = accountCardDtos.stream()
                .map(AccountCardDto::getId)
                .collect(Collectors.toSet());

        var now = utcClock.millis();
        return PubBuildingLotteryHistory.builder()
                .requestId(MDCUtility.getTraceId())
                .accountId(reqDto.getAccountId())
                .buildingId(reqDto.getBuildingId())
                .configurationId(reqDto.getConfigurationId())
                .winnerCard(toCardEntity(winnerCard))
                .imitateCards(toCardEntity(imitateCards))
                .collectedCardIds(collectedCardIds)
                .createdDate(now)
                .lastModifiedDate(now)
                .build();
    }

    public static List<PubBuildingLotteryHistory.Card> toCardEntity(List<LotteryPackConfigurationDto.Card> cards) {
        return cards.stream()
                .map(PubBuildingLotteryHistoryConverter::toCardEntity)
                .toList();
    }

    public static PubBuildingLotteryHistory.Card toCardEntity(LotteryPackConfigurationDto.Card card) {
        return new PubBuildingLotteryHistory.Card(card.getId(), card.getQuantity());
    }

    public static BuildingLotteryUiDto toUiDto(PubBuildingLotteryHistory pubBuildingLotteryHistory) {
        return new BuildingLotteryUiDto(
                pubBuildingLotteryHistory.getId().toHexString(), pubBuildingLotteryHistory.getBuildingId(),
                pubBuildingLotteryHistory.getConfigurationId(), toCardUiDto(pubBuildingLotteryHistory.getWinnerCard()),
                toCardUiDto(pubBuildingLotteryHistory.getImitateCards()), pubBuildingLotteryHistory.getCollectedCardIds());
    }

    public static List<BuildingLotteryUiDto.Card> toCardUiDto(List<PubBuildingLotteryHistory.Card> cards) {
        return cards.stream()
                .map(PubBuildingLotteryHistoryConverter::toCardUiDto)
                .toList();
    }

    public static BuildingLotteryUiDto.Card toCardUiDto(PubBuildingLotteryHistory.Card card) {
        return new BuildingLotteryUiDto.Card(card.getCardId(), card.getQuantity());
    }

}
