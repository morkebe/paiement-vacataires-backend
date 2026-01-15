package sn.ufr.vacations.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vacataires")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacataire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false, name = "rib")
    private String coordonneesBancaires; // RIB

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @OneToMany(mappedBy = "vacataire", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attribution> attributions = new ArrayList<>();

    @OneToMany(mappedBy = "vacataire", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pointage> pointages = new ArrayList<>();

    @OneToMany(mappedBy = "vacataire", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Paiement> paiements = new ArrayList<>();

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

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}