package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.Profil;
import altn72.tpfilrouge.securite.UtilisateurConnecte;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Rend l'utilisateur connecté disponible dans toutes les pages (bandeau « Bonjour … »)
@ControllerAdvice(basePackages = "altn72.tpfilrouge.controleur")
public class VueCommune {

    @ModelAttribute("utilisateur")
    public UtilisateurConnecte utilisateur(@AuthenticationPrincipal UtilisateurConnecte utilisateur) {
        return utilisateur;
    }

    // Sert uniquement à masquer les boutons : la vraie protection est dans ConfigurationSecurite (RG11)
    @ModelAttribute("estResponsable")
    public boolean estResponsable(@AuthenticationPrincipal UtilisateurConnecte utilisateur) {
        return utilisateur != null && utilisateur.getProfil() == Profil.RESPONSABLE_EXPLOITATION;
    }
}
