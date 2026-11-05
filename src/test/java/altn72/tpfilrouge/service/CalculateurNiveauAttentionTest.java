package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.Prestataire;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class CalculateurNiveauAttentionTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 5, 12, 0);

    private final Clock horlogeFixe = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    private final CalculateurNiveauAttention calculateur = new CalculateurNiveauAttention(horlogeFixe);

    @Test
    void incidentNonAffecteOuvertDepuis47HeuresEstVert() {
        assertThat(calculateur.calculer(incidentOuvertIlYa(47))).isEqualTo(NiveauAttention.VERT);
    }

    @Test
    void incidentNonAffecteOuvertDepuis48HeuresPileEstOrange() {
        assertThat(calculateur.calculer(incidentOuvertIlYa(48))).isEqualTo(NiveauAttention.ORANGE);
    }

    @Test
    void incidentNonAffecteOuvertDepuis71HeuresEstOrange() {
        assertThat(calculateur.calculer(incidentOuvertIlYa(71))).isEqualTo(NiveauAttention.ORANGE);
    }

    @Test
    void incidentNonAffecteOuvertDepuis72HeuresPileEstRouge() {
        assertThat(calculateur.calculer(incidentOuvertIlYa(72))).isEqualTo(NiveauAttention.ROUGE);
    }

    @Test
    void incidentAffecteNePorteJamaisUnIndicateurVertOrangeOuRouge() {
        Incident incident = incidentOuvertIlYa(200);
        incident.affecter(new Prestataire("Elevatec", "Karim", "0145000001"));

        assertThat(calculateur.calculer(incident)).isEqualTo(NiveauAttention.PRIS_EN_CHARGE);
    }

    @Test
    void incidentResoluADuneDureeFigeeEtUnNiveauDistinct() {
        Incident incident = incidentOuvertIlYa(120);
        incident.resoudre(MAINTENANT.minusHours(100));

        assertThat(calculateur.calculer(incident)).isEqualTo(NiveauAttention.RESOLU);
        assertThat(calculateur.dureeOuverture(incident)).hasHours(20);
    }

    private Incident incidentOuvertIlYa(long heures) {
        return new Incident(null, "Panne", MAINTENANT.minusHours(heures));
    }
}
