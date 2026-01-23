package sn.ufr.vacations.service;

import sn.ufr.vacations.exception.BadRequestException;
import sn.ufr.vacations.exception.ResourceNotFoundException;
import sn.ufr.vacations.model.dto.request.DepartementRequest;
import sn.ufr.vacations.model.dto.response.DepartementResponse;
import sn.ufr.vacations.model.entity.Departement;
import sn.ufr.vacations.repository.DepartementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartementService {

    private final DepartementRepository departementRepository;
    private final AuditService auditService;

    @Transactional
    public DepartementResponse createDepartement(DepartementRequest request) {
        if (departementRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Ce code de département existe déjà");
        }

        Departement departement = Departement.builder()
                .code(request.getCode())
                .nom(request.getNom())
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .build();

        departement = departementRepository.save(departement);

        auditService.log("CREATE", "Departement", departement.getId(),
                "Création du département " + departement.getLibelle(), "system");

        return mapToResponse(departement);
    }
    @Transactional(readOnly = true)
    public DepartementResponse getDepartement(Long id) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé"));
        return mapToResponse(departement);
    }
    @Transactional(readOnly = true)
    public List<DepartementResponse> getAllDepartements() {
        return departementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DepartementResponse updateDepartement(Long id, DepartementRequest request) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé"));

        if (!departement.getCode().equals(request.getCode()) &&
                departementRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Ce code de département existe déjà");
        }

        departement.setCode(request.getCode());
        departement.setLibelle(request.getLibelle());
        departement.setDescription(request.getDescription());
        departement.setNom(request.getNom());
        departement = departementRepository.save(departement);

        auditService.log("UPDATE", "Departement", id,
                "Modification du département " + departement.getLibelle(), "system");

        return mapToResponse(departement);
    }

    @Transactional
    public void deleteDepartement(Long id) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé"));

        auditService.log("DELETE", "Departement", id,
                "Suppression du département " + departement.getLibelle(), "system");

        departementRepository.delete(departement);
    }

    private DepartementResponse mapToResponse(Departement departement) {
        return DepartementResponse.builder()
                .id(departement.getId())
                .code(departement.getCode())
                .nom(departement.getNom())
                .libelle(departement.getLibelle())
                .description(departement.getDescription())
                .nombreFilieres(departement.getFilieres().size())
                .nombreVacataires(departement.getVacataires().size())
                .build();
    }
}