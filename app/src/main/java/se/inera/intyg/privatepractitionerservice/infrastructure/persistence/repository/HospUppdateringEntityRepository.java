package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.HospUppdateringEntity;

/**
 * Created by pebe on 2015-09-03.
 */
@Transactional(transactionManager = "transactionManager")
@Repository
public interface HospUppdateringEntityRepository extends
    JpaRepository<HospUppdateringEntity, Integer> {

  @Query("SELECT h from HospUppdateringEntity h WHERE h.id = 1")
  HospUppdateringEntity findSingle();

  @Query("SELECT h from HospUppdateringEntity h WHERE h.id = 1")
  Optional<HospUppdateringEntity> findHospUppdatering();

}
