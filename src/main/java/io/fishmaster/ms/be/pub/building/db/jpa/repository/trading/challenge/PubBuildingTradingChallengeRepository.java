package io.fishmaster.ms.be.pub.building.db.jpa.repository.trading.challenge;

import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import io.fishmaster.ms.be.pub.building.db.jpa.entity.trading.challenge.PubBuildingTradingChallenge;

public interface PubBuildingTradingChallengeRepository extends JpaRepository<PubBuildingTradingChallenge, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    Optional<PubBuildingTradingChallenge> findWithLockById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
    Optional<PubBuildingTradingChallenge> findWithLockByChallengeEndedTimeLessThanEqual(Long now);

    default Optional<PubBuildingTradingChallenge> findFirst() {
        var pubBuildingFoodChallenges = findAll(Pageable.ofSize(1)).getContent();
        if (pubBuildingFoodChallenges.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(pubBuildingFoodChallenges.get(0));
    }

}