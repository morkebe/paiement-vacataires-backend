package sn.ufr.vacations.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributionResponse {
    private Long id;
    private Long vacataireId;
    private String vacataireNom;
    private Long coursId;
    private String coursCode;
    private String coursLibelle;
    private String filiereNom;
    private Integer heuresCM;
    private Integer heuresTD;
    private Integer heuresTP;
    private Integer totalHeures;
    private BigDecimal tauxHoraireCM;
    private BigDecimal tauxHoraireTD;
    private BigDecimal tauxHoraireTP;
    private BigDecimal montantBrut;
    private Boolean soumis;
    private String anneeAcademique;
}