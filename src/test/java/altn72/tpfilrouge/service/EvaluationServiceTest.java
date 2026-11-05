package altn72.tpfilrouge.service;

import altn72.tpfilrouge.exception.RegleMetierException;
import altn72.tpfilrouge.modele.Evaluation;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.repository.EvaluationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 5, 12, 0);
    private static final Integer ID_INCIDENT = 3;

    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private IncidentService incidentService;

    private EvaluationService service;

    @BeforeEach
    void preparerService() {
        Clock horloge = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new EvaluationService(evaluationRepository, incidentService, horloge);
    }

    @Test
    void unIncidentNonResoluNePeutPasEtreEvalue() {
        Incident incident = new Incident(null, "Panne", MAINTENANT.minusHours(5));
        incident.affecter(unPrestataire());
        when(incidentService.trouver(ID_INCIDENT)).thenReturn(incident);

        assertThatThrownBy(() -> service.evaluer(ID_INCIDENT, 4, "ok"))
                .isInstanceOf(RegleMetierException.class);
    }

    @Test
    void unIncidentDejaEvalueNePeutPasEtreEvalueDeNouveau() {
        Incident incident = mock(Incident.class);
        when(incident.getEvaluation()).thenReturn(mock(Evaluation.class));
        when(incidentService.trouver(ID_INCIDENT)).thenReturn(incident);

        assertThatThrownBy(() -> service.evaluer(ID_INCIDENT, 4, "ok"))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("déjà été évalué");
    }

    @Test
    void uneNoteHorsDeLIntervalleEstRefusee() {
        when(incidentService.trouver(ID_INCIDENT)).thenReturn(incidentResoluEtAffecte());

        assertThatThrownBy(() -> service.evaluer(ID_INCIDENT, 6, "trop"))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("entre 1 et 5");
    }

    @Test
    void uneEvaluationValideEstEnregistreeALaDateDuJour() {
        when(incidentService.trouver(ID_INCIDENT)).thenReturn(incidentResoluEtAffecte());
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(appel -> appel.getArgument(0));

        Evaluation evaluation = service.evaluer(ID_INCIDENT, 4, "Rapide");

        assertThat(evaluation.getNote()).isEqualTo(4);
        assertThat(evaluation.getDateEvaluation()).isEqualTo(LocalDate.of(2026, 11, 5));
    }

    private Incident incidentResoluEtAffecte() {
        Incident incident = new Incident(null, "Panne", MAINTENANT.minusHours(30));
        incident.affecter(unPrestataire());
        incident.resoudre(MAINTENANT.minusHours(2));
        return incident;
    }

    private Prestataire unPrestataire() {
        return new Prestataire("Elevatec", "Karim", "0145000001");
    }
}
