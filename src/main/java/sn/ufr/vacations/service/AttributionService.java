package sn.ufr.vacations.service;

import sn.ufr.vacations.exception.BadRequestException;
import sn.ufr.vacations.exception.ResourceNotFoundException;
import sn.ufr.vacations.model.dto.request.AttributionRequest;
import sn.ufr.vacations.model.dto.response.AttributionResponse;
import sn.ufr.vacations.model.entity.Attribution;
import sn.ufr.vacations.model.entity.Cours;
import sn.ufr.vacations.model.entity.Vacataire;
import sn.ufr.vacations.repository.AttributionRepository;
import sn.ufr.vacations.repository.CoursRepository;
import sn.ufr.vacations.repository.VacataireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttributionService {

    private final AttributionRepository attributionRepository;
    private final VacataireRepository vacataireRepository;
    private final CoursRepository coursRepository;
    private final AuditService auditService;

    @Transactional
    public AttributionResponse createAttribution(AttributionRequest request, String createdBy) {
        // Vérifications
        Vacataire vacataire = vacataireRepository.findById(request.getVacataireId())
                .orElseThrow(() -> new ResourceNotFoundException("Vacataire non trouvé"));

        Cours cours = coursRepository.findById(request.getCoursId())
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        // Vérifier si une attribution existe déjà
        attributionRepository.findByVacataireIdAndCoursId(
                request.getVacataireId(), request.getCoursId()
        ).ifPresent(a -> {
            throw new BadRequestException("Ce cours est déjà attribué à ce vacataire");
        });

        // Vérifier les volumes horaires
        Integer totalAttribue = attributionRepository.getTotalHeuresAttribuees(cours.getId());
        if (totalAttribue == null) totalAttribue = 0;

        int nouvelleDemande = request.getHeuresCM() + request.getHeuresTD() + request.getHeuresTP();
        int volumeRestant = cours.getVolumeHoraireTotal() - totalAttribue;

        if (nouvelleDemande > volumeRestant) {
            throw new BadRequestException(
                    String.format("Volume horaire insuffisant. Restant: %d heures, Demandé: %d heures",
                            volumeRestant, nouvelleDemande)
            );
        }

        // Créer l'attribution
        Attribution attribution = Attribution.builder()
                .vacataire(vacataire)
                .cours(cours)
                .heuresCMAttribuees(request.getHeuresCM())
                .heuresTDAttribuees(request.getHeuresTD())
                .heuresTPAttribuees(request.getHeuresTP())
                .tauxHoraireCM(cours.getTauxHoraireCM())
                .tauxHoraireTD(cours.getTauxHoraireTD())
                .tauxHoraireTP(cours.getTauxHoraireTP())
                .anneeAcademique(request.getAnneeAcademique())
                .soumis(false)
                .build();

        attribution = attributionRepository.save(attribution);

        auditService.log("CREATE", "Attribution", attribution.getId(),
                String.format("Attribution de %s à %s", cours.getLibelle(), vacataire.getNomComplet()),
                createdBy);

        return mapToResponse(attribution);
    }

    public List<AttributionResponse> getAttributionsByVacataire(Long vacataireId) {
        return attributionRepository.findByVacataireId(vacataireId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AttributionResponse> getAllAttributions() {
        return attributionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void submitAttribution(Long attributionId, String submittedBy) {
        Attribution attribution = attributionRepository.findById(attributionId)
                .orElseThrow(() -> new ResourceNotFoundException("Attribution non trouvée"));

        if (attribution.getSoumis()) {
            throw new BadRequestException("Cette attribution a déjà été soumise");
        }

        attribution.setSoumis(true);
        attributionRepository.save(attribution);

        auditService.log("SUBMIT", "Attribution", attributionId,
                "Soumission de l'attribution pour paiement", submittedBy);
    }

    public AttributionResponse getAttribution(Long id) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribution non trouvée"));
        return mapToResponse(attribution);
    }

    public List<AttributionResponse> getAttributionsByCours(Long coursId) {
        return attributionRepository.findByCoursId(coursId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttributionResponse updateAttribution(Long id, AttributionRequest request) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribution non trouvée"));

        if (attribution.getSoumis()) {
            throw new BadRequestException("Cette attribution a déjà été soumise et ne peut plus être modifiée");
        }

        // Vérifier les volumes horaires disponibles
        Integer totalAttribue = attributionRepository.getTotalHeuresAttribuees(attribution.getCours().getId());
        if (totalAttribue == null) totalAttribue = 0;

        // Soustraire les heures actuelles de cette attribution
        totalAttribue -= attribution.getTotalHeuresAttribuees();

        int nouvelleDemande = request.getHeuresCM() + request.getHeuresTD() + request.getHeuresTP();
        int volumeRestant = attribution.getCours().getVolumeHoraireTotal() - totalAttribue;

        if (nouvelleDemande > volumeRestant) {
            throw new BadRequestException("Volume horaire insuffisant");
        }

        attribution.setHeuresCMAttribuees(request.getHeuresCM());
        attribution.setHeuresTDAttribuees(request.getHeuresTD());
        attribution.setHeuresTPAttribuees(request.getHeuresTP());
        attribution.setAnneeAcademique(request.getAnneeAcademique());

        attribution = attributionRepository.save(attribution);

        auditService.log("UPDATE", "Attribution", id, "Modification de l'attribution", "system");

        return mapToResponse(attribution);
    }

    @Transactional
    public void deleteAttribution(Long id) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribution non trouvée"));

        if (attribution.getSoumis()) {
            throw new BadRequestException("Cette attribution a déjà été soumise et ne peut plus être supprimée");
        }

        auditService.log("DELETE", "Attribution", id, "Suppression de l'attribution", "system");

        attributionRepository.delete(attribution);
    }

    private AttributionResponse mapToResponse(Attribution attribution) {
        return AttributionResponse.builder()
                .id(attribution.getId())
                .vacataireId(attribution.getVacataire().getId())
                .vacataireNom(attribution.getVacataire().getNomComplet())
                .coursId(attribution.getCours().getId())
                .coursCode(attribution.getCours().getCode())
                .coursLibelle(attribution.getCours().getLibelle())
                .filiereNom(attribution.getCours().getFiliere().getLibelle())
                .heuresCM(attribution.getHeuresCMAttribuees())
                .heuresTD(attribution.getHeuresTDAttribuees())
                .heuresTP(attribution.getHeuresTPAttribuees())
                .totalHeures(attribution.getTotalHeuresAttribuees())
                .tauxHoraireCM(attribution.getTauxHoraireCM())
                .tauxHoraireTD(attribution.getTauxHoraireTD())
                .tauxHoraireTP(attribution.getTauxHoraireTP())
                .montantBrut(attribution.calculerMontantBrut())
                .soumis(attribution.getSoumis())
                .anneeAcademique(attribution.getAnneeAcademique())
                .build();
    }
}