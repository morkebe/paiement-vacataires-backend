package sn.ufr.vacations.model.dto.request;

import lombok.Data;

@Data
public class DepartementRequest {
    private Long id;
    private String nom;
    private String code;
    private String libelle;
    private String description;
}
