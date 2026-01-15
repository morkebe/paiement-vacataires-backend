package sn.ufr.vacations.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiliereResponse {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Long departementId;
    private String departementNom;
    private Integer nombreCours;
}
