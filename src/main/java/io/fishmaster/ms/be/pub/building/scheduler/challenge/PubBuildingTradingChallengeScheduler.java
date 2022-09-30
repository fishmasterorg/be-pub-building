package io.fishmaster.ms.be.pub.building.scheduler.challenge;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.fishmaster.ms.be.pub.building.service.building.pub.trading.challenge.PubBuildingTradingChallengeService;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PubBuildingTradingChallengeScheduler {

    private final PubBuildingTradingChallengeService pubBuildingFoodTradingChallengeService;

    @Scheduled(
            cron = "${scheduler.pub-building.challenge.food.move-failed-to-history.cron}",
            zone = "UTC")
    public void prepareDailyTasks() {
        MDCUtility.putTraceId(null);

        try {
            pubBuildingFoodTradingChallengeService.moveChallengeToHistoryWhenExpire();
        } catch (Exception e) {
            log.error("Error while on move trading challenge to history when expire in pub building. Error = {}", e.getMessage());
        }
    }

}
