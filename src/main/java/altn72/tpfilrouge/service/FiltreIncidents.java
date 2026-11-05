package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.NiveauAttention;

public record FiltreIncidents(Integer stationId, NiveauAttention niveau) {

    public static FiltreIncidents aucun() {
        return new FiltreIncidents(null, null);
    }
}
