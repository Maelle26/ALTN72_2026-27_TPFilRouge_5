package altn72.tpfilrouge.exception;

// Exception personnalisée pour les règles métier
public class RegleMetierException extends RuntimeException {
    public RegleMetierException(String message) {
        super(message);
    }
}