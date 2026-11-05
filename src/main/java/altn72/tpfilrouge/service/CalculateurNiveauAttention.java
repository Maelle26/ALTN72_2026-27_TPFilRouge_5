package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CalculateurNiveauAttention {

    public static final Duration SEUIL_ORANGE = Duration.ofHours(48);
    public static final Duration SEUIL_ROUGE = Duration.ofHours(72);

    private final Clock horloge;

    public CalculateurNiveauAttention(Clock horloge) {
        this.horloge = horloge;
    }

    // RG3 et RG4 : le niveau est calculé à la volée, jamais stocké en base
    public NiveauAttention calculer(Incident incident) {
        if (incident.estResolu()) {
            return NiveauAttention.RESOLU;
        }
        if (incident.estAffecte()) {
            return NiveauAttention.PRIS_EN_CHARGE;
        }
        return niveauSelonDuree(dureeOuverture(incident));
    }

    public Duration dureeOuverture(Incident incident) {
        LocalDateTime fin = incident.estResolu()
                ? incident.getDateHeureResolution()
                : LocalDateTime.now(horloge);
        return Duration.between(incident.getDateHeureOuverture(), fin);
    }

    private NiveauAttention niveauSelonDuree(Duration duree) {
        if (duree.compareTo(SEUIL_ROUGE) >= 0) {
            return NiveauAttention.ROUGE;
        }
        if (duree.compareTo(SEUIL_ORANGE) >= 0) {
            return NiveauAttention.ORANGE;
        }
        return NiveauAttention.VERT;
    }
}
