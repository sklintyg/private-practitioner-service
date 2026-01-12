package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.PrivatlakareIdEntity;

@Transactional(transactionManager = "transactionManager")
@Repository
public interface PrivatlakareIdEntityRepository extends
    JpaRepository<PrivatlakareIdEntity, Integer> {

  @Query("SELECT max(pi.id) FROM PrivatlakareIdEntity pi")
  int findLatestGeneratedHsaId();

  @Modifying
  @Query(value = "ALTER TABLE PRIVATLAKARE_ID AUTO_INCREMENT = :id", nativeQuery = true)
  void setIdSequence(@Param("id") Integer id);
}
