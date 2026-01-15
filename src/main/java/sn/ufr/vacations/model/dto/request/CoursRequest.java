package sn.ufr.vacations.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoursRequest {
    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    private String description;

    @NotNull(message = "La filière est obligatoire")
    private Long filiereId;

    @Min(value = 0, message = "Le volume horaire CM doit être >= 0")
    private Integer volumeHoraireCM = 0;

    @Min(value = 0, message = "Le volume horaire TD doit être >= 0")
    private Integer volumeHoraireTD = 0;

    @Min(value = 0, message = "Le volume horaire TP doit être >= 0")
    private Integer volumeHoraireTP = 0;

    @NotNull(message = "Le taux horaire CM est obligatoire")
    @DecimalMin(value = "0.0", message = "Le taux horaire CM doit être >= 0")
    private BigDecimal tauxHoraireCM;

    @NotNull(message = "Le taux horaire TD est obligatoire")
    @DecimalMin(value = "0.0", message = "Le taux horaire TD doit être >= 0")
    private BigDecimal tauxHoraireTD;

    @NotNull(message = "Le taux horaire TP est obligatoire")
    @DecimalMin(value = "0.0", message = "Le taux horaire TP doit être >= 0")
    private BigDecimal tauxHoraireTP;
}