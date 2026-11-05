package altn72.tpfilrouge.api;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.service.CalculateurNiveauAttention;
import altn72.tpfilrouge.service.FiltreIncidents;
import altn72.tpfilrouge.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "Consultation et changement d'état des incidents (F12)")
public class IncidentApiControleur {

    private final IncidentService incidentService;
    private final CalculateurNiveauAttention calculateur;

    public IncidentApiControleur(IncidentService incidentService, CalculateurNiveauAttention calculateur) {
        this.incidentService = incidentService;
        this.calculateur = calculateur;
    }

    @GetMapping
    @Operation(summary = "Lister les incidents actifs (non résolus)")
    public List<IncidentDto> listerActifs() {
        return incidentService.listerActifs(FiltreIncidents.aucun()).stream()
                .map(actif -> IncidentDto.depuis(actif.incident(), actif.niveau()))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un incident ; 404 s'il n'existe pas")
    public IncidentDto consulter(@PathVariable Integer id) {
        return enDto(incidentService.trouver(id));
    }

    @PutMapping("/{id}/prestataire")
    @Operation(summary = "Affecter ou réaffecter un prestataire ; 422 si l'incident est résolu")
    public IncidentDto affecter(@PathVariable Integer id, @Valid @RequestBody AffectationRequete requete) {
        return enDto(incidentService.affecter(id, requete.prestataireId()));
    }

    @PostMapping("/{id}/resolution")
    @Operation(summary = "Déclarer l'incident résolu ; 422 s'il l'est déjà")
    public IncidentDto resoudre(@PathVariable Integer id) {
        return enDto(incidentService.resoudre(id));
    }

    private IncidentDto enDto(Incident incident) {
        return IncidentDto.depuis(incident, calculateur.calculer(incident));
    }
}
