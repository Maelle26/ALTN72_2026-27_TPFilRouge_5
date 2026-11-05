package altn72.tpfilrouge.service;

import altn72.tpfilrouge.exception.RegleMetierException;
import altn72.tpfilrouge.exception.RessourceIntrouvableException;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.Statut;
import altn72.tpfilrouge.repository.EquipementRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EquipementRepository equipementRepository;
    private final PrestataireRepository prestataireRepository;
    private final CalculateurNiveauAttention calculateur;
    private final Clock horloge;

    public IncidentService(IncidentRepository incidentRepository,
                           EquipementRepository equipementRepository,
                           PrestataireRepository prestataireRepository,
                           CalculateurNiveauAttention calculateur,
                           Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.equipementRepository = equipementRepository;
        this.prestataireRepository = prestataireRepository;
        this.calculateur = calculateur;
        this.horloge = horloge;
    }

    public Incident trouver(Integer incidentId) {
        return incidentRepository.findById(incidentId)
                .orElseThrow(() -> new RessourceIntrouvableException("Incident", incidentId));
    }

    public List<Incident> listerParEquipement(Integer equipementId) {
        return incidentRepository.findIncidentsDeLEquipement(equipementId);
    }

    public List<Incident> historique() {
        return incidentRepository.findByStatutOrderByDateHeureResolutionDesc(Statut.RESOLU);
    }

    public List<IncidentActif> listerActifs(FiltreIncidents filtre) {
        return incidentRepository.findByStatutNot(Statut.RESOLU).stream()
                .sorted(Comparator.comparing(Incident::getDateHeureOuverture))
                .map(this::enActif)
                .filter(actif -> correspond(actif, filtre))
                .toList();
    }

    public IndicateursTableauDeBord calculerIndicateurs() {
        List<IncidentActif> actifs = listerActifs(FiltreIncidents.aucun());
        return new IndicateursTableauDeBord(
                actifs.size(),
                compter(actifs, NiveauAttention.ROUGE),
                compter(actifs, NiveauAttention.ORANGE),
                compter(actifs, NiveauAttention.VERT),
                compter(actifs, NiveauAttention.PRIS_EN_CHARGE));
    }

    @Transactional
    public Incident declarer(Integer equipementId, String description) {
        validerDescription(description);
        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement", equipementId));
        Incident incident = new Incident(equipement, description.trim(), LocalDateTime.now(horloge));
        return incidentRepository.save(incident);
    }

    @Transactional
    public Incident modifierDescription(Integer incidentId, String description) {
        validerDescription(description);
        Incident incident = trouver(incidentId);
        incident.modifierDescription(description.trim());
        return incident;
    }

    @Transactional
    public Incident affecter(Integer incidentId, Integer prestataireId) {
        Incident incident = trouver(incidentId);
        Prestataire prestataire = prestataireRepository.findById(prestataireId)
                .orElseThrow(() -> new RessourceIntrouvableException("Prestataire", prestataireId));
        incident.affecter(prestataire);
        return incident;
    }

    @Transactional
    public Incident resoudre(Integer incidentId) {
        Incident incident = trouver(incidentId);
        incident.resoudre(LocalDateTime.now(horloge));
        return incident;
    }

    private IncidentActif enActif(Incident incident) {
        return new IncidentActif(incident, calculateur.calculer(incident), calculateur.dureeOuverture(incident));
    }

    private boolean correspond(IncidentActif actif, FiltreIncidents filtre) {
        boolean stationOk = filtre.stationId() == null
                || filtre.stationId().equals(actif.incident().getEquipement().getStation().getId());
        boolean niveauOk = filtre.niveau() == null || filtre.niveau() == actif.niveau();
        return stationOk && niveauOk;
    }

    private long compter(List<IncidentActif> actifs, NiveauAttention niveau) {
        return actifs.stream().filter(actif -> actif.niveau() == niveau).count();
    }

    private void validerDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new RegleMetierException("La description de l'incident est obligatoire.");
        }
    }
}
