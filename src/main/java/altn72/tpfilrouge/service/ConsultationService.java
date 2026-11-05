package altn72.tpfilrouge.service;

import altn72.tpfilrouge.exception.RessourceIntrouvableException;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.repository.EquipementRepository;
import altn72.tpfilrouge.repository.EvaluationRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import altn72.tpfilrouge.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultationService {

    private final StationRepository stationRepository;
    private final EquipementRepository equipementRepository;
    private final PrestataireRepository prestataireRepository;
    private final EvaluationRepository evaluationRepository;

    public ConsultationService(StationRepository stationRepository,
                               EquipementRepository equipementRepository,
                               PrestataireRepository prestataireRepository,
                               EvaluationRepository evaluationRepository) {
        this.stationRepository = stationRepository;
        this.equipementRepository = equipementRepository;
        this.prestataireRepository = prestataireRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public List<Station> listerStations() {
        return stationRepository.findAllByOrderByNomAsc();
    }

    public List<Equipement> listerEquipementsDeLaStation(Integer stationId) {
        return equipementRepository.findByStationIdOrderByLibelleAsc(stationId);
    }

    public Equipement trouverEquipement(Integer equipementId) {
        return equipementRepository.findById(equipementId)
                .orElseThrow(() -> new RessourceIntrouvableException("Équipement", equipementId));
    }

    public List<Prestataire> listerPrestataires() {
        return prestataireRepository.findAllByOrderByNomAsc();
    }

    public List<NotePrestataire> listerNotesMoyennes() {
        return prestataireRepository.findAllByOrderByNomAsc().stream()
                .map(prestataire -> new NotePrestataire(prestataire.getNom(),
                        evaluationRepository.calculerNoteMoyenne(prestataire.getId()).orElse(null)))
                .toList();
    }
}
