package io.fishmaster.ms.be.pub.building.converter.trading.challenge;

import java.util.List;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.trading.challenge.TradingChallengeConfigurationDto;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.challenge.PubBuildingTradingChallenge;
import io.fishmaster.ms.be.pub.building.db.share.TradingChallengeCheckpoint;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.utility.TradingChallengeFacade;
import io.fishmaster.ms.be.pub.building.web.dto.ui.trading.challenge.BuildingTradingChallengeUiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PubBuildingTradingChallengeConverter {

    public static PubBuildingTradingChallenge toEntity(TradingChallengeFacade foodChallengeFacade) {
        var configurationDto = foodChallengeFacade.getConfigurationDto();
        return new PubBuildingTradingChallenge(
                null, configurationDto.getId(), foodChallengeFacade.getFoodChallengeData(),
                foodChallengeFacade.getChallengeEndedTime()
        );
    }

    public static BuildingTradingChallengeUiDto toUiDto(Long pubBuildingFoodChallengeId, TradingChallengeFacade foodChallengeFacade) {
        var configurationDto = foodChallengeFacade.getConfigurationDto();
        return new BuildingTradingChallengeUiDto(
                pubBuildingFoodChallengeId, toSpecialityUiDto(configurationDto.getSpeciality()),
                toDataUiDto(foodChallengeFacade), Status.ACTIVE
        );
    }

    private static BuildingTradingChallengeUiDto.Speciality toSpecialityUiDto(TradingChallengeConfigurationDto.Speciality speciality) {
        return new BuildingTradingChallengeUiDto.Speciality(speciality.getName(), speciality.getLevel());
    }

    private static BuildingTradingChallengeUiDto.Data toDataUiDto(TradingChallengeFacade foodChallengeFacade) {
        var dataConfigurationDto = foodChallengeFacade.getDataConfigurationDto();
        return new BuildingTradingChallengeUiDto.Data(
                dataConfigurationDto.getCardId(), foodChallengeFacade.getCardsQuantity(),
                toCheckpointUiDto(foodChallengeFacade.getCheckpoints())
        );
    }

    private static List<BuildingTradingChallengeUiDto.Checkpoint> toCheckpointUiDto(List<TradingChallengeCheckpoint> checkpoints) {
        return checkpoints.stream()
                .map(PubBuildingTradingChallengeConverter::toCheckpointUiDto)
                .toList();
    }

    private static BuildingTradingChallengeUiDto.Checkpoint toCheckpointUiDto(TradingChallengeCheckpoint checkpoint) {
        return new BuildingTradingChallengeUiDto.Checkpoint(checkpoint.getTime(), checkpoint.getCost(), checkpoint.getTax());
    }

}
