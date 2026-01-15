package sn.ufr.vacations.repository;

import sn.ufr.vacations.model.entity.Paiement;
import sn.ufr.vacations.model.enums.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByNumeroBordereau(String numeroBordereau);
    List<Paiement> findByVacataireId(Long vacataireId);
    List<Paiement> findByStatut(StatutPaiement statut);

    @Query("SELECT p FROM Paiement p WHERE p.vacataire.id = :vacataireId AND p.statut = :statut")
    List<Paiement> findByVacataireIdAndStatut(
            @Param("vacataireId") Long vacataireId,
            @Param("statut") StatutPaiement statut
    );

    @Query("SELECT p FROM Paiement p WHERE p.dateSoumission BETWEEN :debut AND :fin")
    List<Paiement> findByPeriodeSoumission(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );

    @Query("SELECT p FROM Paiement p WHERE p.anneeAcademique = :annee")
    List<Paiement> findByAnneeAcademique(@Param("annee") String annee);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statut = 'EN_ATTENTE'")
    Long countPaiementsEnAttente();
}