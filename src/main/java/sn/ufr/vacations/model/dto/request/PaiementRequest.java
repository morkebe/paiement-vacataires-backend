package sn.ufr.vacations.model.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PaiementRequest {
    private Long id;
    private Long attributionId;
    private Long vacataireId;
    private List<Long> attributionIds;
    private Double montant;
}
