package sn.ufr.vacations.model.dto.request;

import sn.ufr.vacations.model.enums.TypeCours;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PointageRequest {
    @NotNull(message = "L'attribution est obligatoire")
    private Long attributionId;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotNull(message = "Le type de cours est obligatoire")
    private TypeCours typeCours;

    private String remarque;
}