package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.service.ConsultationService;
import altn72.tpfilrouge.service.DetecteurEquipementFragile;
import altn72.tpfilrouge.service.IncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StationControleur {

    private final ConsultationService consultationService;
    private final IncidentService incidentService;
    private final DetecteurEquipementFragile detecteurFragile;

    public StationControleur(ConsultationService consultationService, IncidentService incidentService,
                             DetecteurEquipementFragile detecteurFragile) {
        this.consultationService = consultationService;
        this.incidentService = incidentService;
        this.detecteurFragile = detecteurFragile;
    }

    // F2 : équipements d'une station (la première station est affichée par défaut)
    @GetMapping("/stations")
    public String stations(@RequestParam(required = false) Integer stationId, Model model) {
        List<Station> stations = consultationService.listerStations();
        Integer stationChoisie = stationId != null || stations.isEmpty() ? stationId : stations.get(0).getId();
        model.addAttribute("stations", stations);
        model.addAttribute("stationChoisie", stationChoisie);
        if (stationChoisie != null) {
            model.addAttribute("equipements", consultationService.listerEquipementsDeLaStation(stationChoisie));
        }
        return "stations";
    }

    // F3 : fiche d'un équipement et liste de ses incidents, du plus récent au plus ancien
    @GetMapping("/equipements/{equipementId}")
    public String ficheEquipement(@PathVariable Integer equipementId, Model model) {
        Equipement equipement = consultationService.trouverEquipement(equipementId);
        model.addAttribute("equipement", equipement);
        model.addAttribute("incidents", incidentService.listerParEquipementAvecNiveau(equipementId));
        // Évolution séance finale (EF1-EF4) : badge "Équipement fragile" sur la fiche
        model.addAttribute("fragile", detecteurFragile.estFragile(equipement));
        return "equipement";
    }
}
