package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareEntity;

/**
 * Created by pebe on 2015-06-24.
 */
@Repository
public interface PrivatlakareEntityRepository extends JpaRepository<PrivatlakareEntity, String> {

  @Query("SELECT p from PrivatlakareEntity p WHERE p.hsaId = :hsaId")
  Optional<PrivatlakareEntity> findByHsaId(@Param("hsaId") String hsaId);

  @Query("SELECT p from PrivatlakareEntity p WHERE p.personId = :personId")
  Optional<PrivatlakareEntity> findByPersonId(@Param("personId") String personId);

  @Query("SELECT p FROM PrivatlakareEntity p WHERE "
      + "p.privatlakareId NOT IN (SELECT p2.privatlakareId FROM PrivatlakareEntity p2 JOIN p2.legitimeradeYrkesgrupper ly WHERE ly.namn = 'Läkare')")
  List<PrivatlakareEntity> findWithoutLakarBehorighet();

  @Query("SELECT p FROM PrivatlakareEntity p WHERE "
      + "p.privatlakareId NOT IN (SELECT p2.privatlakareId FROM PrivatlakareEntity p2 JOIN p2.legitimeradeYrkesgrupper ly WHERE ly.namn = 'Läkare') "
      + "AND p.enhetStartdatum IS NULL")
  List<PrivatlakareEntity> findNeverHadLakarBehorighet();

  @Query("SELECT p FROM PrivatlakareEntity p WHERE "
      + "p.privatlakareId NOT IN (SELECT p2.privatlakareId FROM PrivatlakareEntity p2 JOIN p2.legitimeradeYrkesgrupper ly WHERE ly.namn = 'Läkare') "
      + "AND p.enhetStartdatum IS NULL "
      + "AND p.registreringsdatum <= :beforeDate")
  List<PrivatlakareEntity> findNeverHadLakarBehorighetAndRegisteredBefore(
      @Param("beforeDate") LocalDateTime beforeDate);

}
