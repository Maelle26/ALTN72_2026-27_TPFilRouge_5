package altn72.tpfilrouge.api;

import jakarta.validation.constraints.NotNull;

public record AffectationRequete(@NotNull(message = "Le prestataire est obligatoire.") Integer prestataireId) {
}
