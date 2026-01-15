package sn.ufr.vacations.repository;

import sn.ufr.vacations.model.entity.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    Optional<Filiere> findByCode(String code);
    Boolean existsByCode(String code);
    List<Filiere> findByDepartementId(Long departementId);
}