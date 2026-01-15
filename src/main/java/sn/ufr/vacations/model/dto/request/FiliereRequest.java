package sn.ufr.vacations.model.dto.request;

import lombok.Data;

@Data
public class FiliereRequest {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Long departementId;
}
