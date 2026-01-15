package sn.ufr.vacations.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacataireResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String nomComplet;
    private String email;
    private String contact;
    private String coordonneesBancaires;
    private Long departementId;
    private String departementNom;
    private String username;
}