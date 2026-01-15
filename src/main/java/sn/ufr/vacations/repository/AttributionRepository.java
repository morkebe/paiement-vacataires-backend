package sn.ufr.vacations.repository;

import sn.ufr.vacations.model.entity.Attribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributionRepository extends JpaRepository<Attribution, Long> {
    List<Attribution> findByVacataireId(Long vacataireId);
    List<Attribution> findByCoursId(Long coursId);

    @Query("SELECT a FROM Attribution a WHERE a.vacataire.id = :vacataireId AND a.cours.id = :coursId")
    Optional<Attribution> findByVacataireIdAndCoursId(
            @Param("vacataireId") Long vacataireId,
            @Param("coursId") Long coursId
    );

    @Query("SELECT a FROM Attribution a WHERE a.vacataire.id = :vacataireId AND a.soumis = false")
    List<Attribution> findNonSoumisesByVacataireId(@Param("vacataireId") Long vacataireId);

    @Query("SELECT a FROM Attribution a WHERE a.vacataire.id = :vacataireId AND a.anneeAcademique = :annee")
    List<Attribution> findByVacataireIdAndAnneeAcademique(
            @Param("vacataireId") Long vacataireId,
            @Param("annee") String annee
    );

    @Query("SELECT SUM(a.heuresCMAttribuees + a.heuresTDAttribuees + a.heuresTPAttribuees) " +
            "FROM Attribution a WHERE a.cours.id = :coursId")
    Integer getTotalHeuresAttribuees(@Param("coursId") Long coursId);
}