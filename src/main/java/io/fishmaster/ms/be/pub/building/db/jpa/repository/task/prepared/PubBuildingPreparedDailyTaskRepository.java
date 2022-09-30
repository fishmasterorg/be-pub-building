package io.fishmaster.ms.be.pub.building.db.jpa.repository.task.prepared;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.fishmaster.ms.be.commons.constant.task.WeekDay;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.prepared.PubBuildingPreparedDailyTask;

public interface PubBuildingPreparedDailyTaskRepository extends JpaRepository<PubBuildingPreparedDailyTask, Long> {

    List<PubBuildingPreparedDailyTask> findAllByPubBuilding_IdInAndWeekDay(Collection<Long> buildingIds, WeekDay weekDay);

    List<PubBuildingPreparedDailyTask> findAllByPubBuilding_IdAndWeekDay(Long buildingId, WeekDay weekDay);

    boolean existsByPubBuilding_IdAndWeekDayAndConfigurationId(Long buildingId, WeekDay weekDay, String configurationId);

    void deleteAllByPubBuilding_CityIdIn(Collection<Long> cityIds);

}
