package se.inera.intyg.privatepractitionerservice.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.inera.intyg.privatepractitionerservice.infrastructure.persistence.entity.EpostEntity;

@Repository
public interface EpostEntityRepository extends JpaRepository<EpostEntity, Long> {

  List<EpostEntity> findByPrivatlakareId(String privatlakareId);
}

