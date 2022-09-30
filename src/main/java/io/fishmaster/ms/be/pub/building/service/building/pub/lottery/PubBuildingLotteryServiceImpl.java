package io.fishmaster.ms.be.pub.building.service.building.pub.lottery;

import java.time.Clock;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.commons.constant.card.CardSource;
import io.fishmaster.ms.be.commons.constant.card.CardType;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.AccountCardDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.dto.req.AccountCardLockedReqDto;
import io.fishmaster.ms.be.pub.building.communication.card.inventory.service.CardInventoryCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.city.self.service.CityCommunicationService;
import io.fishmaster.ms.be.pub.building.communication.configurations.storage.dto.building.lottery.LotteryPackConfigurationDto;
import io.fishmaster.ms.be.pub.building.converter.lottery.PubBuildingLotteryHistoryConverter;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.db.mongo.repository.lottery.PubBuildingLotteryHistoryRepository;
import io.fishmaster.ms.be.pub.building.service.building.pub.lottery.model.BuildingLotteryProcessExecuted;
import io.fishmaster.ms.be.pub.building.service.building.pub.lottery.utility.LotteryUtility;
import io.fishmaster.ms.be.pub.building.service.configuration.ConfigurationsStorageService;
import io.fishmaster.ms.be.pub.building.web.dto.req.lottery.BuildingLotteryExecutedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.lottery.BuildingLotteryUiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PubBuildingLotteryServiceImpl implements PubBuildingLotteryService {

    private final CityCommunicationService cityCommunicationService;
    private final CardInventoryCommunicationService cardInventoryCommunicationService;

    private final ConfigurationsStorageService configurationsStorageService;

    private final PubBuildingRepository pubBuildingRepository;

    private final PubBuildingLotteryHistoryRepository pubBuildingLotteryHistoryRepository;

    private final Clock utcClock;

    @Transactional
    @Override
    public BuildingLotteryUiDto executeLottery(BuildingLotteryExecutedReqDto reqDto) {
        var buildingLotteryProcessExecuted = new BuildingLotteryProcessExecuted(reqDto.getAccountId());
        try {
            var pubBuilding = pubBuildingRepository.getById(reqDto.getBuildingId());

            var cityDto = cityCommunicationService.fetchCity(pubBuilding.getCityId());

            var lotteryPackConfigurationDto = configurationsStorageService.getLotteryPackConfigurations()
                    .getById(reqDto.getConfigurationId());

            lockCards(reqDto, lotteryPackConfigurationDto.getRequired(), buildingLotteryProcessExecuted);

            var utility = LotteryUtility.of(lotteryPackConfigurationDto.getResult());
            var winnerCard = utility.getWinnerCard();
            var imitateCards = utility.getImitateCards();

            createCards(reqDto, winnerCard, buildingLotteryProcessExecuted);

            var pubBuildingLotteryHistory = pubBuildingLotteryHistoryRepository.save(
                    PubBuildingLotteryHistoryConverter.toEntity(
                            reqDto, winnerCard, imitateCards, buildingLotteryProcessExecuted.getCreatedCards(), utcClock));

            buildingLotteryProcessExecuted.updateLockedCards(Status.USED, CardSource.PUB_LOTTERY);
            buildingLotteryProcessExecuted.updateCreatedCards(Status.ACTIVE, cityDto.getId());
            buildingLotteryProcessExecuted.complete(cardInventoryCommunicationService::completeAccountCards);

            return PubBuildingLotteryHistoryConverter.toUiDto(pubBuildingLotteryHistory);
        } catch (Exception e) {
            log.error("Error while on execute lottery in pub building. {}. Error = {}", reqDto, e.getMessage());

            buildingLotteryProcessExecuted.rollback(cardInventoryCommunicationService::rollbackAccountCards);

            throw e;
        }
    }

    private void lockCards(
            BuildingLotteryExecutedReqDto reqDto, LotteryPackConfigurationDto.Required required,
            BuildingLotteryProcessExecuted buildingLotteryProcessExecuted) {
        var card = required.getCard();

        cardInventoryCommunicationService.lockAccountCards(
                AccountCardLockedReqDto.of(reqDto.getAccountId(), card.getId(), card.getQuantity(), CardType.TOKEN, Status.ACTIVE),
                card.getQuantity(),
                buildingLotteryProcessExecuted::setLockedCards);
    }

    private void createCards(
            BuildingLotteryExecutedReqDto reqDto, LotteryPackConfigurationDto.Card winnerCard,
            BuildingLotteryProcessExecuted buildingLotteryProcessExecuted) {
        var accountCardDtos = IntStream.range(0, winnerCard.getQuantity())
                .mapToObj(value -> AccountCardDto.of(reqDto.getAccountId(), winnerCard.getId(), CardSource.PUB_LOTTERY))
                .toList();

        cardInventoryCommunicationService.createAccountCards(accountCardDtos, buildingLotteryProcessExecuted::setCreatedCards);
    }
}
