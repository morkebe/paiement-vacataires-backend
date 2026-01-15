package sn.ufr.vacations.repository;

import sn.ufr.vacations.model.entity.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {
    Optional<Cours> findByCode(String code);
    Boolean existsByCode(String code);
    List<Cours> findByFiliereId(Long filiereId);

    @Query("SELECT c FROM Cours c WHERE c.filiere.departement.id = :departementId")
    List<Cours> findByDepartementId(@Param("departementId") Long departementId);
}