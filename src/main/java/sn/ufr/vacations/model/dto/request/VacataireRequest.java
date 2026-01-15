package sn.ufr.vacations.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VacataireRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le contact est obligatoire")
    private String contact;

    @NotBlank(message = "Le RIB est obligatoire")
    private String coordonneesBancaires;

    @NotNull(message = "Le département est obligatoire")
    private Long departementId;
}