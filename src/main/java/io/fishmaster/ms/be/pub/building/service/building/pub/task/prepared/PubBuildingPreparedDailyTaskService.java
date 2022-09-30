package io.fishmaster.ms.be.pub.building.service.building.pub.task.prepared;

import io.fishmaster.ms.be.commons.constant.task.WeekDay;

public interface PubBuildingPreparedDailyTaskService {

    void prepareDailyTasksForWeekDay(WeekDay weekDay);

}
