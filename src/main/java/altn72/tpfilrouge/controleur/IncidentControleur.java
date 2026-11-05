package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.service.CalculateurNiveauAttention;
import altn72.tpfilrouge.service.ConsultationService;
import altn72.tpfilrouge.service.EvaluationService;
import altn72.tpfilrouge.service.IncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IncidentControleur {

    private final IncidentService incidentService;
    private final EvaluationService evaluationService;
    private final ConsultationService consultationService;
    private final CalculateurNiveauAttention calculateur;

    public IncidentControleur(IncidentService incidentService,
                              EvaluationService evaluationService,
                              ConsultationService consultationService,
                              CalculateurNiveauAttention calculateur) {
        this.incidentService = incidentService;
        this.evaluationService = evaluationService;
        this.consultationService = consultationService;
        this.calculateur = calculateur;
    }

    // F4
    @GetMapping("/equipements/{equipementId}/incidents/nouveau")
    public String formulaireDeclaration(@PathVariable Integer equipementId, Model model) {
        model.addAttribute("equipement", consultationService.trouverEquipement(equipementId));
        return "incident-nouveau";
    }

    @PostMapping("/equipements/{equipementId}/incidents")
    public String declarer(@PathVariable Integer equipementId, @RequestParam String description,
                           RedirectAttributes redirection) {
        incidentService.declarer(equipementId, description);
        redirection.addFlashAttribute("message", "L'incident a bien été déclaré.");
        return "redirect:/equipements/" + equipementId;
    }

    // F5
    @GetMapping("/incidents/{incidentId}")
    public String detail(@PathVariable Integer incidentId, Model model) {
        Incident incident = incidentService.trouver(incidentId);
        model.addAttribute("incident", incident);
        model.addAttribute("niveau", calculateur.calculer(incident));
        model.addAttribute("dureeHeures", calculateur.dureeOuverture(incident).toHours());
        model.addAttribute("prestataires", consultationService.listerPrestataires());
        return "incident-detail";
    }

    // F6
    @PostMapping("/incidents/{incidentId}/modification")
    public String modifier(@PathVariable Integer incidentId, @RequestParam String description,
                           RedirectAttributes redirection) {
        incidentService.modifierDescription(incidentId, description);
        redirection.addFlashAttribute("message", "La description a été modifiée.");
        return versLeDetail(incidentId);
    }

    // F7
    @PostMapping("/incidents/{incidentId}/affectation")
    public String affecter(@PathVariable Integer incidentId, @RequestParam Integer prestataireId,
                           RedirectAttributes redirection) {
        incidentService.affecter(incidentId, prestataireId);
        redirection.addFlashAttribute("message", "Le prestataire a été affecté à l'incident.");
        return versLeDetail(incidentId);
    }

    // F8
    @PostMapping("/incidents/{incidentId}/resolution")
    public String resoudre(@PathVariable Integer incidentId, RedirectAttributes redirection) {
        incidentService.resoudre(incidentId);
        redirection.addFlashAttribute("message", "L'incident est déclaré résolu.");
        return versLeDetail(incidentId);
    }

    // F10
    @PostMapping("/incidents/{incidentId}/evaluation")
    public String evaluer(@PathVariable Integer incidentId, @RequestParam Integer note,
                          @RequestParam(required = false) String commentaire, RedirectAttributes redirection) {
        evaluationService.evaluer(incidentId, note, commentaire);
        redirection.addFlashAttribute("message", "Merci, l'évaluation est enregistrée.");
        return versLeDetail(incidentId);
    }

    // F9
    @GetMapping("/historique")
    public String historique(Model model) {
        model.addAttribute("incidents", incidentService.historique());
        model.addAttribute("notes", consultationService.listerNotesMoyennes());
        return "historique";
    }

    private String versLeDetail(Integer incidentId) {
        return "redirect:/incidents/" + incidentId;
    }
}
