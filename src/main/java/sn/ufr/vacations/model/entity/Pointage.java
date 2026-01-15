package sn.ufr.vacations.model.entity;

import jakarta.persistence.*;
import lombok.*;
import sn.ufr.vacations.model.enums.TypeCours;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "pointages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pointage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacataire_id", nullable = false)
    private Vacataire vacataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribution_id", nullable = false)
    private Attribution attribution;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_cours", nullable = false)
    private TypeCours typeCours;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    @Column(name = "duree_heures", precision = 5, scale = 2)
    private Double dureeHeures;

    private String remarque;

    @Column(name = "valide")
    private Boolean valide = false;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "valideur")
    private String valideur;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculerDuree();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculerDuree();
    }

    private void calculerDuree() {
        if (heureDebut != null && heureFin != null) {
            long minutes = java.time.Duration.between(heureDebut, heureFin).toMinutes();
            this.dureeMinutes = (int) minutes;
            this.dureeHeures = minutes / 60.0;
        }
    }
}