package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.repository.IncidentRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

// Évolution séance finale : détecte les équipements "fragiles" (EF1 à EF6).
// EF5 : seuil et fenêtre définis une seule fois, ici.
// EF6 : la règle vit dans la couche service, jamais dans un contrôleur ou une vue.
@Component
public class DetecteurEquipementFragile {

    public static final int SEUIL_INCIDENTS = 3;
    public static final Duration FENETRE = Duration.ofDays(30);

    private final IncidentRepository incidentRepository;
    private final Clock horloge;

    public DetecteurEquipementFragile(IncidentRepository incidentRepository, Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.horloge = horloge;
    }

    // EF1 à EF4 : nombre d'incidents (tous statuts confondus) ouverts dans la fenêtre glissante des
    // 30 derniers jours, recalculé à chaque appel, jamais stocké en base.
    public boolean estFragile(Equipement equipement) {
        return compterIncidentsRecents(equipement) >= SEUIL_INCIDENTS;
    }

    public long compterIncidentsRecents(Equipement equipement) {
        LocalDateTime depuis = LocalDateTime.now(horloge).minus(FENETRE);
        return incidentRepository.countByEquipementIdAndDateHeureOuvertureGreaterThanEqual(
                equipement.getId(), depuis);
    }
}
