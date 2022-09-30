package io.fishmaster.ms.be.pub.building.service.building.pub;

import java.util.concurrent.CompletableFuture;

import javax.transaction.Transactional;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.commons.utility.CompletableFutureExceptionUtility;
import io.fishmaster.ms.be.pub.building.db.jpa.repository.PubBuildingRepository;
import io.fishmaster.ms.be.pub.building.service.building.pub.barman.offer.PubBuildingBarmanOfferService;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.exchange.PubBuildingExchangerExchangeService;
import io.fishmaster.ms.be.pub.building.service.building.pub.exchanger.offer.PubBuildingExchangerOfferService;
import io.fishmaster.ms.be.pub.building.service.building.pub.task.PubBuildingDailyTaskService;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.PubBuildingTradingChallengeService;
import io.fishmaster.ms.be.pub.building.service.building.pub.trading.order.PubBuildingTradingOrderService;
import io.fishmaster.ms.be.pub.building.utility.MDCThreadPoolExecutor;
import io.fishmaster.ms.be.pub.building.web.dto.req.task.BuildingDailyTaskFetchedReqDto;
import io.fishmaster.ms.be.pub.building.web.dto.ui.PubBuildingUiDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PubBuildingServiceImpl implements PubBuildingService {

    private final PubBuildingDailyTaskService pubBuildingDailyTaskService;
    private final PubBuildingBarmanOfferService pubBuildingBarmanOfferService;
    private final PubBuildingTradingOrderService pubBuildingTradingOrderService;
    private final PubBuildingTradingChallengeService pubBuildingTradingChallengeService;
    private final PubBuildingExchangerExchangeService pubBuildingExchangerExchangeService;
    private final PubBuildingExchangerOfferService pubBuildingExchangerOfferService;

    private final PubBuildingRepository pubBuildingRepository;

    private final ThreadPoolTaskExecutor cachedExecutor = MDCThreadPoolExecutor.getCachedInstance();

    @Transactional
    @Override
    public PubBuildingUiDto getUiDto(String accountId, Long cityId) {
        return CompletableFutureExceptionUtility.getAndHandle(
                getBuildingCompletableFuture(accountId, cityId),
                e -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Error while on get pub building for ui. Account id = %s and city id = %s. Error = %s"
                                .formatted(accountId, cityId, e)));
    }

    private CompletableFuture<PubBuildingUiDto> getBuildingCompletableFuture(String accountId, Long cityId) {
        var pubBuilding = pubBuildingRepository.getByCityId(cityId);

        var builder = PubBuildingUiDto.builder()
                .id(pubBuilding.getId())
                .cityId(pubBuilding.getCityId())
                .level(pubBuilding.getLevel());

        return CompletableFuture.completedFuture(builder)
                .thenCombineAsync(
                        pubBuildingDailyTaskService.fetchAsync(
                                new BuildingDailyTaskFetchedReqDto(accountId, pubBuilding.getId()), cachedExecutor),
                        PubBuildingUiDto.PubBuildingUiDtoBuilder::dailyTasks)
                .thenCombineAsync(
                        pubBuildingBarmanOfferService.getAsyncBarman(accountId, pubBuilding, cachedExecutor),
                        PubBuildingUiDto.PubBuildingUiDtoBuilder::barman)
                .thenCombineAsync(
                        pubBuildingTradingOrderService.getAsyncTradingOrder(accountId, pubBuilding, cachedExecutor),
                        PubBuildingUiDto.PubBuildingUiDtoBuilder::tradingOrders)
                .thenCombineAsync(
                        pubBuildingTradingChallengeService.getAsyncTradingChallenge(accountId, pubBuilding, cachedExecutor),
                        PubBuildingUiDto.PubBuildingUiDtoBuilder::tradingChallenges)
                .thenCombineAsync(
                        pubBuildingExchangerExchangeService.getAsyncExchangerExchange(accountId, pubBuilding, cachedExecutor),
                        PubBuildingUiDto.PubBuildingUiDtoBuilder::exchangerExchange)
                .thenCombineAsync(
                        pubBuildingExchangerOfferService.getAsyncExchangerOffer(accountId, pubBuilding, cachedExecutor),
                        PubBuildingUiDto.PubBuildingUiDtoBuilder::exchangerOffers)
                .thenApplyAsync(PubBuildingUiDto.PubBuildingUiDtoBuilder::build);
    }

}
