package sn.ufr.vacations.repository;

import sn.ufr.vacations.model.entity.Pointage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {
    List<Pointage> findByVacataireId(Long vacataireId);
    List<Pointage> findByAttributionId(Long attributionId);

    @Query("SELECT p FROM Pointage p WHERE p.vacataire.id = :vacataireId AND p.date BETWEEN :debut AND :fin")
    List<Pointage> findByVacataireIdAndPeriode(
            @Param("vacataireId") Long vacataireId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );

    @Query("SELECT p FROM Pointage p WHERE p.attribution.id = :attributionId AND p.valide = false")
    List<Pointage> findNonValidesByAttribution(@Param("attributionId") Long attributionId);

    // CORRECTION: Changer Double en BigDecimal
    @Query("SELECT COALESCE(SUM(p.dureeHeures), 0) FROM Pointage p WHERE p.attribution.id = :attributionId AND p.valide = true")
    BigDecimal getTotalHeuresValidees(@Param("attributionId") Long attributionId);
}
