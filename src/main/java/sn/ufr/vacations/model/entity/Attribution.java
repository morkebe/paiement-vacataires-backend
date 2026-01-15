package sn.ufr.vacations.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attributions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacataire_id", nullable = false)
    private Vacataire vacataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    @Column(name = "heures_cm_attribuees")
    private Integer heuresCMAttribuees = 0;

    @Column(name = "heures_td_attribuees")
    private Integer heuresTDAttribuees = 0;

    @Column(name = "heures_tp_attribuees")
    private Integer heuresTPAttribuees = 0;

    @Column(name = "taux_horaire_cm", precision = 10, scale = 2)
    private BigDecimal tauxHoraireCM;

    @Column(name = "taux_horaire_td", precision = 10, scale = 2)
    private BigDecimal tauxHoraireTD;

    @Column(name = "taux_horaire_tp", precision = 10, scale = 2)
    private BigDecimal tauxHoraireTP;

    @OneToMany(mappedBy = "attribution", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pointage> pointages = new ArrayList<>();

    private Boolean soumis = false;

    @Column(name = "annee_academique")
    private String anneeAcademique;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getTotalHeuresAttribuees() {
        return heuresCMAttribuees + heuresTDAttribuees + heuresTPAttribuees;
    }

    public BigDecimal calculerMontantBrut() {
        BigDecimal montantCM = tauxHoraireCM.multiply(new BigDecimal(heuresCMAttribuees));
        BigDecimal montantTD = tauxHoraireTD.multiply(new BigDecimal(heuresTDAttribuees));
        BigDecimal montantTP = tauxHoraireTP.multiply(new BigDecimal(heuresTPAttribuees));
        return montantCM.add(montantTD).add(montantTP);
    }
}