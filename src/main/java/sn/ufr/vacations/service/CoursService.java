package sn.ufr.vacations.service;

import sn.ufr.vacations.exception.BadRequestException;
import sn.ufr.vacations.exception.ResourceNotFoundException;
import sn.ufr.vacations.model.dto.request.CoursRequest;
import sn.ufr.vacations.model.dto.response.CoursResponse;
import sn.ufr.vacations.model.entity.Cours;
import sn.ufr.vacations.model.entity.Filiere;
import sn.ufr.vacations.repository.CoursRepository;
import sn.ufr.vacations.repository.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoursService {

    private final CoursRepository coursRepository;
    private final FiliereRepository filiereRepository;
    private final AuditService auditService;

    @Transactional
    public CoursResponse createCours(CoursRequest request) {
        if (coursRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Ce code de cours existe déjà");
        }

        Filiere filiere = filiereRepository.findById(request.getFiliereId())
                .orElseThrow(() -> new ResourceNotFoundException("Filière non trouvée"));

        Cours cours = Cours.builder()
                .code(request.getCode())
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .filiere(filiere)
                .volumeHoraireCM(request.getVolumeHoraireCM())
                .volumeHoraireTD(request.getVolumeHoraireTD())
                .volumeHoraireTP(request.getVolumeHoraireTP())
                .tauxHoraireCM(request.getTauxHoraireCM())
                .tauxHoraireTD(request.getTauxHoraireTD())
                .tauxHoraireTP(request.getTauxHoraireTP())
                .build();

        cours = coursRepository.save(cours);

        auditService.log("CREATE", "Cours", cours.getId(),
                "Création du cours " + cours.getLibelle(), "system");

        return mapToResponse(cours);
    }

    public CoursResponse getCours(Long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
        return mapToResponse(cours);
    }

    public List<CoursResponse> getAllCours() {
        return coursRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CoursResponse> getCoursByFiliere(Long filiereId) {
        return coursRepository.findByFiliereId(filiereId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CoursResponse> getCoursByDepartement(Long departementId) {
        return coursRepository.findByDepartementId(departementId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CoursResponse updateCours(Long id, CoursRequest request) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        if (!cours.getCode().equals(request.getCode()) &&
                coursRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Ce code de cours existe déjà");
        }

        cours.setCode(request.getCode());
        cours.setLibelle(request.getLibelle());
        cours.setDescription(request.getDescription());
        cours.setVolumeHoraireCM(request.getVolumeHoraireCM());
        cours.setVolumeHoraireTD(request.getVolumeHoraireTD());
        cours.setVolumeHoraireTP(request.getVolumeHoraireTP());
        cours.setTauxHoraireCM(request.getTauxHoraireCM());
        cours.setTauxHoraireTD(request.getTauxHoraireTD());
        cours.setTauxHoraireTP(request.getTauxHoraireTP());

        cours = coursRepository.save(cours);

        auditService.log("UPDATE", "Cours", id,
                "Modification du cours " + cours.getLibelle(), "system");

        return mapToResponse(cours);
    }

    @Transactional
    public void deleteCours(Long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        auditService.log("DELETE", "Cours", id,
                "Suppression du cours " + cours.getLibelle(), "system");

        coursRepository.delete(cours);
    }

    private CoursResponse mapToResponse(Cours cours) {
        return CoursResponse.builder()
                .id(cours.getId())
                .code(cours.getCode())
                .libelle(cours.getLibelle())
                .description(cours.getDescription())
                .filiereId(cours.getFiliere().getId())
                .filiereNom(cours.getFiliere().getLibelle())
                .departementNom(cours.getFiliere().getDepartement().getLibelle())
                .volumeHoraireCM(cours.getVolumeHoraireCM())
                .volumeHoraireTD(cours.getVolumeHoraireTD())
                .volumeHoraireTP(cours.getVolumeHoraireTP())
                .volumeHoraireTotal(cours.getVolumeHoraireTotal())
                .tauxHoraireCM(cours.getTauxHoraireCM())
                .tauxHoraireTD(cours.getTauxHoraireTD())
                .tauxHoraireTP(cours.getTauxHoraireTP())
                .build();
    }
}