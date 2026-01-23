package sn.ufr.vacations.service;

import sn.ufr.vacations.exception.BadRequestException;
import sn.ufr.vacations.exception.ResourceNotFoundException;
import sn.ufr.vacations.model.dto.request.PointageRequest;
import sn.ufr.vacations.model.dto.response.PointageResponse;
import sn.ufr.vacations.model.entity.Attribution;
import sn.ufr.vacations.model.entity.Pointage;
import sn.ufr.vacations.model.entity.Vacataire;
import sn.ufr.vacations.repository.AttributionRepository;
import sn.ufr.vacations.repository.PointageRepository;
import sn.ufr.vacations.repository.VacataireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointageService {

    private final PointageRepository pointageRepository;
    private final AttributionRepository attributionRepository;
    private final VacataireRepository vacataireRepository;
    private final AuditService auditService;

    @Transactional
    public PointageResponse createPointage(PointageRequest request, Long vacataireId) {
        // Vérifier l'attribution
        Attribution attribution = attributionRepository.findById(request.getAttributionId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribution non trouvée"));

        Vacataire vacataire = vacataireRepository.findById(vacataireId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacataire non trouvé"));

        // Vérifier que l'attribution appartient au vacataire
        if (!attribution.getVacataire().getId().equals(vacataireId)) {
            throw new BadRequestException("Vous n'êtes pas autorisé à pointer ce cours");
        }

        // Vérifier l'heure de fin > heure de début
        if (request.getHeureFin().isBefore(request.getHeureDebut()) ||
                request.getHeureFin().equals(request.getHeureDebut())) {
            throw new BadRequestException("L'heure de fin doit être après l'heure de début");
        }

        // Calculer la durée
        long minutes = Duration.between(request.getHeureDebut(), request.getHeureFin()).toMinutes();
        BigDecimal heuresDuree = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);

        // Vérifier le volume restant
        BigDecimal heuresValidees = pointageRepository.getTotalHeuresValidees(attribution.getId());

        int volumeMax = switch(request.getTypeCours()) {
            case CM -> attribution.getHeuresCMAttribuees();
            case TD -> attribution.getHeuresTDAttribuees();
            case TP -> attribution.getHeuresTPAttribuees();
        };

        if (heuresValidees.add(heuresDuree).compareTo(BigDecimal.valueOf(volumeMax)) > 0) {
            throw new BadRequestException("Le volume horaire maximum pour ce type de cours est dépassé");
        }

        // Créer le pointage
        Pointage pointage = Pointage.builder()
                .vacataire(vacataire)
                .attribution(attribution)
                .date(request.getDate())
                .heureDebut(request.getHeureDebut())
                .heureFin(request.getHeureFin())
                .typeCours(request.getTypeCours())
                .remarque(request.getRemarque())
                .valide(false)
                .build();

        pointage = pointageRepository.save(pointage);

        auditService.log("CREATE", "Pointage", pointage.getId(),
                String.format("Pointage de %s pour %s", vacataire.getNomComplet(),
                        attribution.getCours().getLibelle()),
                vacataire.getUser().getUsername());

        return mapToResponse(pointage);
    }
    @Transactional(readOnly = true)
    public List<PointageResponse> getPointagesByVacataire(Long vacataireId) {
        return pointageRepository.findByVacataireId(vacataireId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PointageResponse> getAllPointages() {
        return pointageRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public PointageResponse getPointage(Long id) {
        return pointageRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Pointage non trouvé"));
    }
    @Transactional(readOnly = true)
    public List<PointageResponse> getPointagesByAttribution(Long attributionId) {
        return pointageRepository.findByAttributionId(attributionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PointageResponse> getPointagesByPeriode(Long vacataireId, LocalDate debut, LocalDate fin) {
        return pointageRepository.findAll().stream()
                .filter(p -> p.getVacataire().getId().equals(vacataireId) &&
                        !p.getDate().isBefore(debut) &&
                        !p.getDate().isAfter(fin))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PointageResponse updatePointage(Long id, PointageRequest request) {
        Pointage pointage = pointageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pointage non trouvé"));

        pointage.setHeureDebut(request.getHeureDebut());
        pointage.setHeureFin(request.getHeureFin());
        pointage.setTypeCours(request.getTypeCours());
        pointage.setRemarque(request.getRemarque());

        pointageRepository.save(pointage);
        return mapToResponse(pointage);
    }

    @Transactional
    public void validatePointagesBatch(List<Long> pointageIds, String validateur) {
        pointageIds.forEach(id -> validatePointage(id, validateur));
    }

    @Transactional
    public void deletePointage(Long id) {
        pointageRepository.deleteById(id);
    }

    @Transactional
    public void validatePointage(Long pointageId, String validateur) {
        Pointage pointage = pointageRepository.findById(pointageId)
                .orElseThrow(() -> new ResourceNotFoundException("Pointage non trouvé"));

        pointage.setValide(true);
        pointage.setDateValidation(LocalDateTime.now());
        pointage.setValideur(validateur);

        pointageRepository.save(pointage);

        auditService.log("VALIDATE", "Pointage", pointageId,
                "Validation du pointage", validateur);
    }

    private PointageResponse mapToResponse(Pointage pointage) {
        return PointageResponse.builder()
                .id(pointage.getId())
                .vacataireId(pointage.getVacataire().getId())
                .vacataireNom(pointage.getVacataire().getNomComplet())
                .attributionId(pointage.getAttribution().getId())
                .coursNom(pointage.getAttribution().getCours().getLibelle())
                .date(pointage.getDate())
                .heureDebut(pointage.getHeureDebut())
                .heureFin(pointage.getHeureFin())
                .typeCours(pointage.getTypeCours())
                .dureeHeures(BigDecimal.valueOf(pointage.getDureeHeures()))
                .remarque(pointage.getRemarque())
                .valide(pointage.getValide())
                .dateValidation(pointage.getDateValidation())
                .valideur(pointage.getValideur())
                .build();
    }
}
