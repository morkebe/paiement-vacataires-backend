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
public class CoursResponse {
    private Long id;
    private String intitule;
    private String code;
    private String libelle;
    private String description;
    private String typeCours;
    private Long filiereId;
    private String filiereNom;
    private String departementNom;
    private Integer volumeHoraireCM;
    private Integer volumeHoraireTD;
    private Integer volumeHoraireTP;
    private Integer volumeHoraireTotal;
    private BigDecimal tauxHoraireCM;
    private BigDecimal tauxHoraireTD;
    private BigDecimal tauxHoraireTP;
}
