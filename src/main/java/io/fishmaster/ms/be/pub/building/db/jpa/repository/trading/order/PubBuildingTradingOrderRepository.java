package io.fishmaster.ms.be.pub.building.db.jpa.repository.trading.order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.order.PubBuildingTradingOrder;

public interface PubBuildingTradingOrderRepository extends JpaRepository<PubBuildingTradingOrder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    Optional<PubBuildingTradingOrder> findWithLockByIdAndAccountId(Long id, String accountId);

    List<PubBuildingTradingOrder> findAllByAccountIdAndPubBuilding_Id(String accountId, Long buildingId);

    void deleteAllByPubBuilding_CityIdIn(Collection<Long> cityIds);

}