package altn72.tpfilrouge.service;

import altn72.tpfilrouge.exception.RegleMetierException;
import altn72.tpfilrouge.modele.Evaluation;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final IncidentService incidentService;
    private final Clock horloge;

    public EvaluationService(EvaluationRepository evaluationRepository,
                             IncidentService incidentService,
                             Clock horloge) {
        this.evaluationRepository = evaluationRepository;
        this.incidentService = incidentService;
        this.horloge = horloge;
    }

    public Optional<Double> noteMoyenne(Integer prestataireId) {
        return evaluationRepository.calculerNoteMoyenne(prestataireId);
    }

    // RG8 : l'entité Evaluation contrôle l'incident (résolu, affecté) et la note (1 à 5)
    @Transactional
    public Evaluation evaluer(Integer incidentId, Integer note, String commentaire) {
        Incident incident = incidentService.trouver(incidentId);
        if (incident.getEvaluation() != null) {
            throw new RegleMetierException("Cet incident a déjà été évalué.");
        }
        Evaluation evaluation = new Evaluation(incident, note, commentaire, LocalDate.now(horloge));
        return evaluationRepository.save(evaluation);
    }
}
