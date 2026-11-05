package altn72.tpfilrouge.api;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.Statut;

import java.time.LocalDateTime;

public record IncidentDto(
        Integer id,
        String description,
        Statut statut,
        NiveauAttention niveauAttention,
        LocalDateTime dateHeureOuverture,
        LocalDateTime dateHeureResolution,
        String referenceEquipement,
        String station,
        String prestataire) {

    public static IncidentDto depuis(Incident incident, NiveauAttention niveau) {
        return new IncidentDto(
                incident.getId(),
                incident.getDescription(),
                incident.getStatut(),
                niveau,
                incident.getDateHeureOuverture(),
                incident.getDateHeureResolution(),
                incident.getEquipement().getReference(),
                incident.getEquipement().getStation().getNom(),
                incident.getPrestataire() == null ? null : incident.getPrestataire().getNom());
    }
}
