package sn.ufr.vacations.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttributionRequest {
    @NotNull(message = "Le vacataire est obligatoire")
    private Long vacataireId;

    @NotNull(message = "Le cours est obligatoire")
    private Long coursId;

    @Min(value = 0, message = "Les heures CM doivent être >= 0")
    private Integer heuresCM = 0;

    @Min(value = 0, message = "Les heures TD doivent être >= 0")
    private Integer heuresTD = 0;

    @Min(value = 0, message = "Les heures TP doivent être >= 0")
    private Integer heuresTP = 0;

    private String anneeAcademique;
}