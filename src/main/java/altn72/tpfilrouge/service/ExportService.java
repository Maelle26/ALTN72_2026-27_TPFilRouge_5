package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Lot alpha - piste 9 : export CSV de l'historique des incidents résolus.
@Service
public class ExportService {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] ENTETES = {
            "Date de résolution", "Station", "Équipement", "Description",
            "Prestataire", "Durée (heures)", "Note", "Commentaire"
    };

    private final IncidentService incidentService;

    public ExportService(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // Construit le contenu CSV de l'historique, prêt à être envoyé en téléchargement.
    // Séparateur ';' (plutôt que ',') pour que les colonnes s'alignent correctement
    // à l'ouverture dans Excel en version française, dont le séparateur décimal est la virgule.
    public byte[] genererCsvHistorique() {
        List<Incident> incidents = incidentService.historique();
        StringBuilder csv = new StringBuilder();
        csv.append('﻿'); // BOM UTF-8, pour qu'Excel détecte correctement l'encodage des accents
        csv.append(String.join(";", ENTETES)).append("\r\n");
        for (Incident incident : incidents) {
            csv.append(ligne(incident)).append("\r\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String ligne(Incident incident) {
        String dureeHeures = String.valueOf(java.time.Duration.between(
                incident.getDateHeureOuverture(), incident.getDateHeureResolution()).toHours());
        String prestataire = incident.getPrestataire() != null ? incident.getPrestataire().getNom() : "";
        String note = incident.getEvaluation() != null ? String.valueOf(incident.getEvaluation().getNote()) : "";
        String commentaire = incident.getEvaluation() != null && incident.getEvaluation().getCommentaire() != null
                ? incident.getEvaluation().getCommentaire() : "";
        return String.join(";",
                echapper(incident.getDateHeureResolution().format(FORMAT_DATE)),
                echapper(incident.getEquipement().getStation().getNom()),
                echapper(incident.getEquipement().getReference()),
                echapper(incident.getDescription()),
                echapper(prestataire),
                dureeHeures,
                note,
                echapper(commentaire));
    }

    // Protège les champs texte qui contiendraient le séparateur, des guillemets ou un retour à la ligne.
    private String echapper(String valeur) {
        if (valeur == null) {
            return "";
        }
        if (valeur.contains(";") || valeur.contains("\"") || valeur.contains("\n")) {
            return "\"" + valeur.replace("\"", "\"\"") + "\"";
        }
        return valeur;
    }
}
