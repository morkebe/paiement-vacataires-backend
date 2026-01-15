package sn.ufr.vacations.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartementResponse {
    private Long id;
    private String nom;
    private String code;
    private String libelle;
    private String description;
    private Integer nombreFilieres;
    private Integer nombreVacataires;
}
