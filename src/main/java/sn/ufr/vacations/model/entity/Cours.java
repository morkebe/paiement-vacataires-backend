package sn.ufr.vacations.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String libelle;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @Column(name = "volume_horaire_cm")
    private Integer volumeHoraireCM = 0;

    @Column(name = "volume_horaire_td")
    private Integer volumeHoraireTD = 0;

    @Column(name = "volume_horaire_tp")
    private Integer volumeHoraireTP = 0;

    @Column(name = "taux_horaire_cm", precision = 10, scale = 2)
    private BigDecimal tauxHoraireCM;

    @Column(name = "taux_horaire_td", precision = 10, scale = 2)
    private BigDecimal tauxHoraireTD;

    @Column(name = "taux_horaire_tp", precision = 10, scale = 2)
    private BigDecimal tauxHoraireTP;

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attribution> attributions = new ArrayList<>();

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

    public Integer getVolumeHoraireTotal() {
        return volumeHoraireCM + volumeHoraireTD + volumeHoraireTP;
    }
}