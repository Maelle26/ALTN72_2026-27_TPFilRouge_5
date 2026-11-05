package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.exception.RegleMetierException;
import altn72.tpfilrouge.exception.RessourceIntrouvableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = "altn72.tpfilrouge.controleur")
public class GestionnaireExceptionsWeb {

    private static final String PAGE_PAR_DEFAUT = "/stations";

    @ExceptionHandler(RessourceIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ressourceIntrouvable(RessourceIntrouvableException exception, Model model) {
        model.addAttribute("explication", exception.getMessage());
        return "error";
    }

    // Une règle de gestion violée renvoie l'utilisateur sur la page d'où il vient, avec un message clair
    @ExceptionHandler(RegleMetierException.class)
    public String regleMetierViolee(RegleMetierException exception, HttpServletRequest requete,
                                    RedirectAttributes redirection) {
        redirection.addFlashAttribute("erreur", exception.getMessage());
        return "redirect:" + pageDOrigine(requete);
    }

    private String pageDOrigine(HttpServletRequest requete) {
        String origine = requete.getHeader("Referer");
        String debutAdresse = requete.getScheme() + "://" + requete.getServerName();
        return origine != null && origine.startsWith(debutAdresse) ? origine : PAGE_PAR_DEFAUT;
    }
}
