package sn.ufr.vacations.service;

import org.springframework.beans.factory.annotation.Autowired;
import sn.ufr.vacations.exception.BadRequestException;
import sn.ufr.vacations.exception.ResourceNotFoundException;
import sn.ufr.vacations.model.dto.request.VacataireRequest;
import sn.ufr.vacations.model.dto.response.VacataireResponse;
import sn.ufr.vacations.model.entity.Departement;
import sn.ufr.vacations.model.entity.User;
import sn.ufr.vacations.model.entity.Vacataire;
import sn.ufr.vacations.model.enums.Role;
import sn.ufr.vacations.repository.DepartementRepository;
import sn.ufr.vacations.repository.UserRepository;
import sn.ufr.vacations.repository.VacataireRepository;
import sn.ufr.vacations.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacataireService {
    @Autowired(required = false)
    private final VacataireRepository vacataireRepository;
    private final UserRepository userRepository;
    private final DepartementRepository departementRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditService auditService;


    @Transactional

    public VacataireResponse createVacataire(VacataireRequest request, String createdBy) {
        // Vérifier si l'email existe déjà
        if (vacataireRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }

        // Récupérer le département
        Departement departement = departementRepository.findById(request.getDepartementId())
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé"));

        // Générer identifiant et mot de passe
        String username = generateUsername(request.getNom(), request.getPrenom());
        String password = PasswordGenerator.generate();

        // Créer l'utilisateur
        User user = User.builder()
                .username(username)
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .role(Role.VACATAIRE)
                .enabled(true)
                .firstLogin(true)
                .departement(departement)
                .build();

        user = userRepository.save(user);

        // Créer le vacataire
        Vacataire vacataire = Vacataire.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .contact(request.getContact())
                .coordonneesBancaires(request.getCoordonneesBancaires())
                .user(user)
                .departement(departement)
                .build();

        vacataire = vacataireRepository.save(vacataire);

        // Envoyer email avec identifiants
        emailService.sendCredentials(vacataire.getEmail(), username, password);

        // Audit
        auditService.log("CREATE", "Vacataire", vacataire.getId(),
                "Création du vacataire " + vacataire.getNomComplet(), createdBy);

        return mapToResponse(vacataire);
    }

    public VacataireResponse getVacataire(Long id) {
        Vacataire vacataire = vacataireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacataire non trouvé"));
        return mapToResponse(vacataire);
    }

    public List<VacataireResponse> getAllVacataires() {
        return vacataireRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<VacataireResponse> getVacatairesByDepartement(Long departementId) {
        return vacataireRepository.findByDepartementId(departementId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VacataireResponse updateVacataire(Long id, VacataireRequest request) {
        Vacataire vacataire = vacataireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacataire non trouvé"));

        vacataire.setNom(request.getNom());
        vacataire.setPrenom(request.getPrenom());
        vacataire.setContact(request.getContact());
        vacataire.setCoordonneesBancaires(request.getCoordonneesBancaires());

        vacataire = vacataireRepository.save(vacataire);

        auditService.log("UPDATE", "Vacataire", id,
                "Modification du vacataire " + vacataire.getNomComplet(), "system");

        return mapToResponse(vacataire);
    }

    private String generateUsername(String nom, String prenom) {
        String base = (prenom.substring(0, 1) + nom).toLowerCase()
                .replaceAll("[^a-z]", "");
        String username = base;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = base + counter++;
        }

        return username;
    }

    public VacataireResponse getVacataireByUserId(Long userId) {
        Vacataire vacataire = vacataireRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vacataire non trouvé pour cet utilisateur"));
        return mapToResponse(vacataire);
    }

    @Transactional
    public void deleteVacataire(Long id) {
        Vacataire vacataire = vacataireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacataire non trouvé"));

        auditService.log("DELETE", "Vacataire", id,
                "Suppression du vacataire " + vacataire.getNomComplet(), "system");

        vacataireRepository.delete(vacataire);
    }

    private VacataireResponse mapToResponse(Vacataire vacataire) {
        return VacataireResponse.builder()
                .id(vacataire.getId())
                .nom(vacataire.getNom())
                .prenom(vacataire.getPrenom())
                .nomComplet(vacataire.getNomComplet())
                .email(vacataire.getEmail())
                .contact(vacataire.getContact())
                .coordonneesBancaires(vacataire.getCoordonneesBancaires())
                .departementId(vacataire.getDepartement().getId())
                .departementNom(vacataire.getDepartement().getLibelle())
                .username(vacataire.getUser().getUsername())
                .build();
    }
}
