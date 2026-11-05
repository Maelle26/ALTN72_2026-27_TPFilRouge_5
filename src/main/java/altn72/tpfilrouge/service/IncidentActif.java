package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;

import java.time.Duration;

public record IncidentActif(Incident incident, NiveauAttention niveau, Duration dureeOuverture) {
}
