package sn.ufr.vacations.service;

import sn.ufr.vacations.exception.BadRequestException;
import sn.ufr.vacations.exception.ResourceNotFoundException;
import sn.ufr.vacations.model.dto.request.FiliereRequest;
import sn.ufr.vacations.model.dto.response.FiliereResponse;
import sn.ufr.vacations.model.entity.Departement;
import sn.ufr.vacations.model.entity.Filiere;
import sn.ufr.vacations.repository.DepartementRepository;
import sn.ufr.vacations.repository.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FiliereService {

    private final FiliereRepository filiereRepository;
    private final DepartementRepository departementRepository;
    private final AuditService auditService;

    @Transactional
    public FiliereResponse createFiliere(FiliereRequest request) {
        if (filiereRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Ce code de filière existe déjà");
        }

        Departement departement = departementRepository.findById(request.getDepartementId())
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé"));

        Filiere filiere = Filiere.builder()
                .code(request.getCode())
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .departement(departement)
                .build();

        filiere = filiereRepository.save(filiere);

        auditService.log("CREATE", "Filiere", filiere.getId(),
                "Création de la filière " + filiere.getLibelle(), "system");

        return mapToResponse(filiere);
    }
    @Transactional(readOnly = true)
    public FiliereResponse getFiliere(Long id) {
        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filière non trouvée"));
        return mapToResponse(filiere);
    }
    @Transactional(readOnly = true)
    public List<FiliereResponse> getAllFilieres() {
        return filiereRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<FiliereResponse> getFilieresByDepartement(Long departementId) {
        return filiereRepository.findByDepartementId(departementId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FiliereResponse updateFiliere(Long id, FiliereRequest request) {
        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filière non trouvée"));

        if (!filiere.getCode().equals(request.getCode()) &&
                filiereRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Ce code de filière existe déjà");
        }

        filiere.setCode(request.getCode());
        filiere.setLibelle(request.getLibelle());
        filiere.setDescription(request.getDescription());

        filiere = filiereRepository.save(filiere);

        auditService.log("UPDATE", "Filiere", id,
                "Modification de la filière " + filiere.getLibelle(), "system");

        return mapToResponse(filiere);
    }

    @Transactional
    public void deleteFiliere(Long id) {
        Filiere filiere = filiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filière non trouvée"));

        auditService.log("DELETE", "Filiere", id,
                "Suppression de la filière " + filiere.getLibelle(), "system");

        filiereRepository.delete(filiere);
    }

    private FiliereResponse mapToResponse(Filiere filiere) {
        return FiliereResponse.builder()
                .id(filiere.getId())
                .code(filiere.getCode())
                .libelle(filiere.getLibelle())
                .description(filiere.getDescription())
                .departementId(filiere.getDepartement().getId())
                .departementNom(filiere.getDepartement().getLibelle())
                .nombreCours(filiere.getCours().size())
                .build();
    }
}
