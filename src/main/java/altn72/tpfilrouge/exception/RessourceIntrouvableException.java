package altn72.tpfilrouge.exception;

public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String ressource, Integer id) {
        super(ressource + " introuvable (identifiant " + id + ").");
    }
}
