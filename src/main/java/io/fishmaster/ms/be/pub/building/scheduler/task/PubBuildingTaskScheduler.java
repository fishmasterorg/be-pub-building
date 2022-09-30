package io.fishmaster.ms.be.pub.building.scheduler.task;

import java.time.Clock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.fishmaster.ms.be.commons.utility.DateTimeUtility;
import io.fishmaster.ms.be.pub.building.service.building.pub.task.prepared.PubBuildingPreparedDailyTaskService;
import io.fishmaster.ms.be.pub.building.utility.MDCUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PubBuildingTaskScheduler {

    private final PubBuildingPreparedDailyTaskService pubBuildingPreparedDailyTaskService;

    private final Clock utcClock;

    @Scheduled(
            cron = "${scheduler.pub-building.task.daily.generate.cron}",
            zone = "UTC")
    public void prepareDailyTasks() {
        MDCUtility.putTraceId(null);

        try {
            pubBuildingPreparedDailyTaskService.prepareDailyTasksForWeekDay(
                    DateTimeUtility.getCurrentWeekDay(utcClock));
        } catch (Exception e) {
            log.error("Error while on prepare daily task in pub building. Error = {}", e.getMessage());
        }
    }

}
