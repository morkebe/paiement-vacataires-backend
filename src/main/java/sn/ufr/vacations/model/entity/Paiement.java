package sn.ufr.vacations.model.entity;

import jakarta.persistence.*;
import lombok.*;
import sn.ufr.vacations.model.enums.StatutPaiement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paiements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroBordereau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacataire_id", nullable = false)
    private Vacataire vacataire;

    @ManyToMany
    @JoinTable(
            name = "paiement_attributions",
            joinColumns = @JoinColumn(name = "paiement_id"),
            inverseJoinColumns = @JoinColumn(name = "attribution_id")
    )
    @Builder.Default
    private List<Attribution> attributions = new ArrayList<>();

    @Column(name = "montant_brut", precision = 10, scale = 2, nullable = false)
    private BigDecimal montantBrut;

    @Column(name = "montant_charges", precision = 10, scale = 2)
    private BigDecimal montantCharges;

    @Column(name = "montant_net", precision = 10, scale = 2, nullable = false)
    private BigDecimal montantNet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut;

    @Column(name = "date_soumission")
    private LocalDate dateSoumission;

    @Column(name = "date_validation")
    private LocalDate dateValidation;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(name = "soumis_par")
    private String soumisPar;

    @Column(name = "valide_par")
    private String validePar;

    @Column(name = "motif_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    private String remarque;

    @Column(name = "periode_debut")
    private LocalDate periodeDebut;

    @Column(name = "periode_fin")
    private LocalDate periodeFin;

    @Column(name = "annee_academique")
    private String anneeAcademique;

    @Column(name = "chemin_bordereau")
    private String cheminBordereau;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (statut == null) {
            statut = StatutPaiement.EN_ATTENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}