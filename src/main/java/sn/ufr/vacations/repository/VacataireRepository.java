package sn.ufr.vacations.repository;

import sn.ufr.vacations.model.entity.Vacataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacataireRepository extends JpaRepository<Vacataire, Long> {
    Optional<Vacataire> findByEmail(String email);
    Optional<Vacataire> findByUserId(Long userId);
    Boolean existsByEmail(String email);
    List<Vacataire> findByDepartementId(Long departementId);

    @Query("SELECT v FROM Vacataire v WHERE " +
            "LOWER(v.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Vacataire> searchVacataires(@Param("keyword") String keyword);
}