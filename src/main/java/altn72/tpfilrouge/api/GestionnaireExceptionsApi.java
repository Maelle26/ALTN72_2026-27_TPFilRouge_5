package altn72.tpfilrouge.api;

import altn72.tpfilrouge.exception.RegleMetierException;
import altn72.tpfilrouge.exception.RessourceIntrouvableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "altn72.tpfilrouge.api")
public class GestionnaireExceptionsApi {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ProblemDetail ressourceIntrouvable(RessourceIntrouvableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(RegleMetierException.class)
    public ProblemDetail regleMetierViolee(RegleMetierException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail requeteInvalide(MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(erreur -> erreur.getDefaultMessage())
                .findFirst()
                .orElse("La requête est invalide.");
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }
}
