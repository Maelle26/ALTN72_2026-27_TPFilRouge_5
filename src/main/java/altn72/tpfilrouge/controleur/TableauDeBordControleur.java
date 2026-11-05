package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.service.ConsultationService;
import altn72.tpfilrouge.service.FiltreIncidents;
import altn72.tpfilrouge.service.IncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TableauDeBordControleur {

    private final IncidentService incidentService;
    private final ConsultationService consultationService;

    public TableauDeBordControleur(IncidentService incidentService, ConsultationService consultationService) {
        this.incidentService = incidentService;
        this.consultationService = consultationService;
    }

    // F11 : incidents actifs, filtrables par niveau d'attention et par station
    @GetMapping("/tableau-de-bord")
    public String tableauDeBord(@RequestParam(required = false) NiveauAttention niveau,
                                @RequestParam(required = false) Integer stationId, Model model) {
        model.addAttribute("incidents", incidentService.listerActifs(new FiltreIncidents(stationId, niveau)));
        model.addAttribute("indicateurs", incidentService.calculerIndicateurs());
        model.addAttribute("stations", consultationService.listerStations());
        model.addAttribute("niveauChoisi", niveau);
        model.addAttribute("stationChoisie", stationId);
        model.addAttribute("niveaux", new NiveauAttention[]{NiveauAttention.ROUGE, NiveauAttention.ORANGE,
                NiveauAttention.VERT, NiveauAttention.PRIS_EN_CHARGE});
        return "tableau-de-bord";
    }
}
