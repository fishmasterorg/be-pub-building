package io.fishmaster.ms.be.pub.building.db.jpa.repository.task;

import static io.fishmaster.ms.be.pub.building.db.jpa.field.SetLongAttributeConverter.DELIMITER;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import io.fishmaster.ms.be.commons.constant.Status;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.task.PubBuildingDailyTask;

public interface PubBuildingDailyTaskRepository extends JpaRepository<PubBuildingDailyTask, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    Optional<PubBuildingDailyTask> findWithLockByIdAndAccountIdAndStatusIn(
            Long id, String accountId, Collection<Status> statuses);

    List<PubBuildingDailyTask> findAllByAccountIdAndPubBuilding_Id(
            String accountId, Long buildingId);

    @Query(value = "SELECT pbdt.* FROM (pub_building_daily_task pbdt INNER JOIN pub_building pb ON pbdt.building_id = pb.id) WHERE " +
            "pbdt.account_id = :accountId AND pb.city_id = :cityId AND pbdt.status = :#{#status.name()} AND " +
            "pbdt.configuration_id IN :configurationIds AND " +
            "CAST(string_to_array(pbdt.character_ids, '" + DELIMITER + "') AS bigint[]) && CAST(ARRAY[:characterIds] AS bigint[])",
            nativeQuery = true)
    List<PubBuildingDailyTask> findAllByAccountIdAndPubBuilding_CityIdAndStatusAndConfigurationIdInAndCharacterIdsIn(
            @Param("accountId") String accountId, @Param("cityId") Long cityId, @Param("status") Status status,
            @Param("configurationIds") Collection<String> configurationIds, @Param("characterIds") Collection<Long> characterIds);


    @Query(value = "SELECT pbdt.* FROM (pub_building_daily_task pbdt INNER JOIN pub_building pb ON pbdt.building_id = pb.id) WHERE " +
            "pbdt.account_id = :accountId AND pb.city_id = :cityId AND pbdt.status = :#{#status.name()} AND " +
            "pbdt.configuration_id IN :configurationIds",
            nativeQuery = true)
    List<PubBuildingDailyTask> findAllByAccountIdAndPubBuilding_CityIdAndStatusAndConfigurationIdIn(
            @Param("accountId") String accountId, @Param("cityId") Long cityId, @Param("status") Status status,
            @Param("configurationIds") Collection<String> configurationIds);

    boolean existsByAccountIdAndPubBuilding_IdAndConfigurationId(String accountId, Long buildingId, String configurationId);

    @Query(value = "SELECT EXISTS(" +
            "SELECT * FROM pub_building_daily_task " +
            "WHERE CAST(string_to_array(character_ids, '" + DELIMITER + "') AS bigint[]) && CAST(ARRAY[?1] AS bigint[])" +
            ")", nativeQuery = true)
    boolean existsByCharacterIdsIn(Collection<Long> characterIds);

    void deleteAllByPubBuilding_CityIdIn(Collection<Long> cityIds);

}
