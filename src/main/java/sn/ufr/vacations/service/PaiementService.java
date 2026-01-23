package sn.ufr.vacations.service;

import sn.ufr.vacations.exception.BadRequestException;
import sn.ufr.vacations.exception.ResourceNotFoundException;
import sn.ufr.vacations.model.dto.response.PaiementResponse;
import sn.ufr.vacations.model.entity.Attribution;
import sn.ufr.vacations.model.entity.Paiement;
import sn.ufr.vacations.model.entity.Vacataire;
import sn.ufr.vacations.model.enums.StatutPaiement;
import sn.ufr.vacations.repository.AttributionRepository;
import sn.ufr.vacations.repository.PaiementRepository;
import sn.ufr.vacations.repository.VacataireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final VacataireRepository vacataireRepository;
    private final AttributionRepository attributionRepository;
    private final AuditService auditService;

    @Transactional
    public PaiementResponse createPaiement(Long vacataireId, List<Long> attributionIds, String createdBy) {
        Vacataire vacataire = vacataireRepository.findById(vacataireId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacataire non trouvé"));

        List<Attribution> attributions = attributionRepository.findAllById(attributionIds);

        if (attributions.isEmpty()) {
            throw new BadRequestException("Aucune attribution trouvée");
        }

        // Vérifier que toutes les attributions sont soumises
        boolean allSubmitted = attributions.stream().allMatch(Attribution::getSoumis);
        if (!allSubmitted) {
            throw new BadRequestException("Toutes les attributions doivent être soumises");
        }

        // Calculer le montant total
        BigDecimal montantBrut = attributions.stream()
                .map(Attribution::calculerMontantBrut)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Pour l'instant, montant net = montant brut (à ajuster selon les charges)
        BigDecimal montantNet = montantBrut;

        // Générer le numéro de bordereau
        String numeroBordereau = generateNumeroBordereau();

        Paiement paiement = Paiement.builder()
                .numeroBordereau(numeroBordereau)
                .vacataire(vacataire)
                .attributions(attributions)
                .montantBrut(montantBrut)
                .montantNet(montantNet)
                .statut(StatutPaiement.EN_ATTENTE)
                .dateSoumission(LocalDate.now())
                .soumisPar(createdBy)
                .build();

        paiement = paiementRepository.save(paiement);

        auditService.log("CREATE", "Paiement", paiement.getId(),
                String.format("Création du paiement pour %s - Montant: %s",
                        vacataire.getNomComplet(), montantBrut), createdBy);

        return mapToResponse(paiement);
    }

    @Transactional
    public void validatePaiement(Long paiementId, String validateur) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));

        if (paiement.getStatut() != StatutPaiement.EN_ATTENTE) {
            throw new BadRequestException("Ce paiement ne peut plus être validé");
        }

        paiement.setStatut(StatutPaiement.VALIDE);
        paiement.setDateValidation(LocalDate.now());
        paiement.setValidePar(validateur);

        paiementRepository.save(paiement);

        auditService.log("VALIDATE", "Paiement", paiementId,
                "Validation du paiement", validateur);
    }

    @Transactional
    public void rejectPaiement(Long paiementId, String motif, String rejetePar) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));

        if (paiement.getStatut() != StatutPaiement.EN_ATTENTE) {
            throw new BadRequestException("Ce paiement ne peut plus être rejeté");
        }

        paiement.setStatut(StatutPaiement.REJETE);
        paiement.setMotifRejet(motif);

        paiementRepository.save(paiement);

        auditService.log("REJECT", "Paiement", paiementId,
                "Rejet du paiement: " + motif, rejetePar);
    }
    @Transactional(readOnly = true)
    public List<PaiementResponse> getPaiementsByVacataire(Long vacataireId) {
        return paiementRepository.findByVacataireId(vacataireId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PaiementResponse> getPaiementsByStatut(StatutPaiement statut) {
        return paiementRepository.findByStatut(statut).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<PaiementResponse> getAllPaiements() {
        return paiementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public PaiementResponse getPaiement(Long id) {
        return paiementRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
    }
    @Transactional(readOnly = true)
    public void marquerCommePaye(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        paiement.setStatut(StatutPaiement.PAYE);
        paiement.setDatePaiement(LocalDate.now());
        paiementRepository.save(paiement);
    }
    @Transactional(readOnly = true)
    public Resource generateBordereau(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé"));
        byte[] data = new byte[0]; // Placeholder for PDF generation
        return new ByteArrayResource(data);
    }

    public Map<String, Object> getStatistiques() {
        return new HashMap<>();
    }

    private String generateNumeroBordereau() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = paiementRepository.count() + 1;
        return String.format("BRD-%s-%04d", date, count);
    }

    private PaiementResponse mapToResponse(Paiement paiement) {
        return PaiementResponse.builder()
                .id(paiement.getId())
                .numeroBordereau(paiement.getNumeroBordereau())
                .vacataireId(paiement.getVacataire().getId())
                .vacataireNom(paiement.getVacataire().getNomComplet())
                .montantBrut(paiement.getMontantBrut())
                .montantNet(paiement.getMontantNet())
                .statut(paiement.getStatut())
                .dateSoumission(paiement.getDateSoumission())
                .dateValidation(paiement.getDateValidation())
                .datePaiement(paiement.getDatePaiement())
                .soumisPar(paiement.getSoumisPar())
                .validePar(paiement.getValidePar())
                .motifRejet(paiement.getMotifRejet())
                .anneeAcademique(paiement.getAnneeAcademique())
                .build();
    }
}
