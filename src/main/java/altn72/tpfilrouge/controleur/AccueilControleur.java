package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.Profil;
import altn72.tpfilrouge.securite.UtilisateurConnecte;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilControleur {

    @GetMapping("/connexion")
    public String connexion() {
        return "connexion";
    }

    @GetMapping("/acces-refuse")
    public String accesRefuse() {
        return "acces-refuse";
    }

    @GetMapping("/")
    public String accueil(@AuthenticationPrincipal UtilisateurConnecte utilisateur) {
        return utilisateur.getProfil() == Profil.RESPONSABLE_EXPLOITATION
                ? "redirect:/tableau-de-bord"
                : "redirect:/stations";
    }
}
