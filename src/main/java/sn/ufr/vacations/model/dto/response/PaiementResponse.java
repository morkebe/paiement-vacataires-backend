package sn.ufr.vacations.model.dto.response;

import sn.ufr.vacations.model.enums.StatutPaiement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponse {
    private Long id;
    private String numeroBordereau;
    private Long vacataireId;
    private String vacataireNom;
    private BigDecimal montantBrut;
    private BigDecimal montantNet;
    private StatutPaiement statut;
    private LocalDate dateSoumission;
    private LocalDate dateValidation;
    private LocalDate datePaiement;
    private String soumisPar;
    private String validePar;
    private String motifRejet;
    private String anneeAcademique;
}