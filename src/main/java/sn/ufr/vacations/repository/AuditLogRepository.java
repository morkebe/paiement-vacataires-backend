package sn.ufr.vacations.repository;

import sn.ufr.vacations.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUtilisateur(String utilisateur);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByEntite(String entite);

    @Query("SELECT a FROM AuditLog a WHERE a.entite = :entite AND a.entiteId = :entiteId ORDER BY a.timestamp DESC")
    List<AuditLog> findByEntiteAndEntiteId(
            @Param("entite") String entite,
            @Param("entiteId") Long entiteId
    );

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :debut AND :fin ORDER BY a.timestamp DESC")
    List<AuditLog> findByPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );

    @Query("SELECT a FROM AuditLog a WHERE a.utilisateur = :utilisateur AND a.timestamp >= :depuis ORDER BY a.timestamp DESC")
    List<AuditLog> findRecentByUtilisateur(
            @Param("utilisateur") String utilisateur,
            @Param("depuis") LocalDateTime depuis
    );
}