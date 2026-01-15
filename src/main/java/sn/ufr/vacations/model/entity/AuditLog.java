package sn.ufr.vacations.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entite;

    @Column(name = "entite_id")
    private Long entiteId;

    @Column(name = "utilisateur", nullable = false)
    private String utilisateur;

    @Column(name = "role_utilisateur")
    private String roleUtilisateur;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "valeurs_avant", columnDefinition = "TEXT")
    private String valeursAvant;

    @Column(name = "valeurs_apres", columnDefinition = "TEXT")
    private String valeursApres;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}