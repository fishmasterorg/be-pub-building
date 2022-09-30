package io.fishmaster.ms.be.pub.building.db.jpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import io.fishmaster.ms.be.commons.exception.ServiceException;
import io.fishmaster.ms.be.commons.exception.constant.ExceptionCode;
import io.fishmaster.ms.be.pub.building.db.jpa.entity.PubBuilding;

public interface PubBuildingRepository extends JpaRepository<PubBuilding, Long> {

    default PubBuilding getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Pub building with id = %s not exists".formatted(id)
                ));
    }

    Optional<PubBuilding> findByCityId(Long cityId);

    default PubBuilding getByCityId(Long cityId) {
        return findByCityId(cityId)
                .orElseThrow(() -> new ServiceException(
                        ExceptionCode.INNER_SERVICE,
                        "Pub building with city id = %s not exists".formatted(cityId)
                ));
    }

    void deleteAllByCityIdIn(Collection<Long> cityIds);

    default List<PubBuilding> findAllWithPagination(Integer page, Integer size) {
        return findAll(PageRequest.of(page, size)).getContent();
    }

}