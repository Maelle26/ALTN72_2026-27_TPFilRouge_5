package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;

import java.time.Duration;

// equipementFragile : évolution séance finale (EF3), pour afficher le badge "Équipement fragile"
// sur le tableau de bord sans recalculer la règle dans la vue.
public record IncidentActif(Incident incident, NiveauAttention niveau, Duration dureeOuverture,
                            boolean equipementFragile) {
}
