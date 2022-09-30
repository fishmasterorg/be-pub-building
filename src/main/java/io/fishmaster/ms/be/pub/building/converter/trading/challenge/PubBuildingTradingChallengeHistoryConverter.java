package io.fishmaster.ms.be.pub.building.converter.trading.challenge;

import java.time.Clock;
import java.util.Optional;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.dto.result.ResultUiDto;
import io.fishmaster.ms.be.commons.model.result.Result;
import io.fishmaster.ms.be.pub.building.communication.account.balance.dto.AccountBalanceOperationReqDto;
import io.fishmaster.ms.be.pub.building.communication.account.self.dto.AccountDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterDto;
import io.fishmaster.ms.be.pub.building.communication.character.dto.CharacterSkinDto;
import io.fishmaster.ms.be.pub.building.converter.ResultConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.challenge.PubBuildingTradingChallenge;
import io.fishmaster.ms.be.pub.building.db.mongo.document.trading.challenge.PubBuildingTradingChallengeHistory;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.utility.TradingChallengeFacade;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.BuildingTradingChallengeHistoryUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingTradingChallengeHistoryConverter {

    public static PubBuildingTradingChallengeHistory toEntityWithStatusDone(
            AccountDto accountDto, CharacterDto characterDto, PubBuilding pubBuilding,
            PubBuildingTradingChallenge pubBuildingTradingChallenge, TradingChallengeFacade tradingChallengeFacade,
            AccountBalanceOperationReqDto accountBalanceOperationReqDto, Clock utcClock) {

        var result = new Result(
                null, null, tradingChallengeFacade.getExperiences(),
                ResultConverter.toResultMoney(accountBalanceOperationReqDto));

        var now = utcClock.millis();
        return PubBuildingTradingChallengeHistory.builder()
                .requestId(MDCUtility.getTraceId())

                .accountId(accountDto.getId())
                .accountNickname(accountDto.getNickname())

                .characterId(characterDto.getId())
                .characterSkin(toCharacterSkinEntity(characterDto.getSkin()))

                .buildingId(pubBuilding.getId())
                .buildingTradingChallengeId(pubBuildingTradingChallenge.getId())
                .configurationId(pubBuildingTradingChallenge.getConfigurationId())

                .data(toDataEntityWithStatusDone(tradingChallengeFacade))
                .result(result)
                .status(Status.DONE)

                .createdDate(now)
                .lastModifiedDate(now)
                .build();
    }

    public static PubBuildingTradingChallengeHistory toEntityWithStatusFailed(
            Long pubBuildingTradingChallengeId, TradingChallengeFacade tradingChallengeFacade, Clock utcClock) {
        var configurationDto = tradingChallengeFacade.getConfigurationDto();

        var result = new Result();

        var now = utcClock.millis();
        return PubBuildingTradingChallengeHistory.builder()
                .requestId(MDCUtility.getTraceId())

                .buildingTradingChallengeId(pubBuildingTradingChallengeId)
                .configurationId(configurationDto.getId())

                .data(toDataEntityWithStatusFailed(tradingChallengeFacade))
                .result(result)
                .status(Status.FAILED)

                .createdDate(now)
                .lastModifiedDate(now)
                .build();
    }

    private static PubBuildingTradingChallengeHistory.Data toDataEntityWithStatusDone(TradingChallengeFacade tradingChallengeFacade) {
        var dataConfigurationDto = tradingChallengeFacade.getDataConfigurationDto();
        return new PubBuildingTradingChallengeHistory.Data(
                dataConfigurationDto.getId(), dataConfigurationDto.getCardId(),
                tradingChallengeFacade.getCardsQuantity(), tradingChallengeFacade.getCost(), tradingChallengeFacade.getTax());
    }

    private static PubBuildingTradingChallengeHistory.Data toDataEntityWithStatusFailed(TradingChallengeFacade tradingChallengeFacade) {
        var dataConfigurationDto = tradingChallengeFacade.getDataConfigurationDto();

        var checkpoints = tradingChallengeFacade.getCheckpoints();
        var checkpoint = checkpoints.get(checkpoints.size() - 1);

        return new PubBuildingTradingChallengeHistory.Data(
                dataConfigurationDto.getId(), dataConfigurationDto.getCardId(),
                tradingChallengeFacade.getCardsQuantity(), checkpoint.getCost(), checkpoint.getTax());
    }

    private static PubBuildingTradingChallengeHistory.CharacterSkin toCharacterSkinEntity(CharacterSkinDto characterSkinDto) {
        return new PubBuildingTradingChallengeHistory.CharacterSkin(
                characterSkinDto.getGender(), characterSkinDto.getHair(), characterSkinDto.getFace(),
                characterSkinDto.getBody(), characterSkinDto.getCloth(), characterSkinDto.getAttribute());
    }

    public static BuildingTradingChallengeHistoryUiDto toUiDto(PubBuildingTradingChallengeHistory pubBuildingTradingChallengeHistory) {
        var characterSkinUiDto = Optional.ofNullable(pubBuildingTradingChallengeHistory.getCharacterSkin())
                .map(PubBuildingTradingChallengeHistoryConverter::toCharacterSkinUiDto)
                .orElse(null);

        var result = pubBuildingTradingChallengeHistory.getResult();

        var resultUiDto = Optional.ofNullable(pubBuildingTradingChallengeHistory.getCharacterId())
                .map(characterId -> ResultUiDto.of(characterId, result))
                .orElse(ResultUiDto.of());

        return new BuildingTradingChallengeHistoryUiDto(
                pubBuildingTradingChallengeHistory.getBuildingTradingChallengeId(), pubBuildingTradingChallengeHistory.getAccountNickname(),
                characterSkinUiDto, toDataUiDto(pubBuildingTradingChallengeHistory.getData()), resultUiDto,
                pubBuildingTradingChallengeHistory.getStatus());
    }

    private static BuildingTradingChallengeHistoryUiDto.CharacterSkin toCharacterSkinUiDto(
            PubBuildingTradingChallengeHistory.CharacterSkin characterSkin) {
        return new BuildingTradingChallengeHistoryUiDto.CharacterSkin(
                characterSkin.getGender(), characterSkin.getHair(), characterSkin.getFace(),
                characterSkin.getBody(), characterSkin.getCloth(), characterSkin.getAttribute());
    }

    private static BuildingTradingChallengeHistoryUiDto.Data toDataUiDto(PubBuildingTradingChallengeHistory.Data data) {
        return new BuildingTradingChallengeHistoryUiDto.Data(data.getCardId(), data.getCardsQuantity(), data.getCost());
    }

}
