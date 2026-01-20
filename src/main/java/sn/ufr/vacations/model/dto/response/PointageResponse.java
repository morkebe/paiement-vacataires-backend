package sn.ufr.vacations.model.dto.response;

import sn.ufr.vacations.model.enums.TypeCours;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointageResponse {
    private Long id;
    private Long vacataireId;
    private String vacataireNom;
    private Long attributionId;
    private String coursNom;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private TypeCours typeCours;
    private BigDecimal dureeHeures;

    private String remarque;
    private Boolean valide;
    private LocalDateTime dateValidation;
    private String valideur;
}