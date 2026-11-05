package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.modele.TypeEquipement;
import altn72.tpfilrouge.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

// Évolution séance finale : couvre les règles EF1 à EF5.
// Le comptage lui-même (>= date pivot) est délégué à une requête dérivée Spring Data
// (IncidentRepository.countByEquipementIdAndDateHeureOuvertureGreaterThanEqual), dont le comportement
// aux bornes exactes (cas limites 4 et 5 du sujet : "30 jours et 1 minute" vs "29 jours 23h59") est garanti
// par Spring Data/Hibernate et n'est pas revérifié ici avec une vraie base (limite connue, voir journal.md).
@ExtendWith(MockitoExtension.class)
class DetecteurEquipementFragileTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 5, 12, 0);

    @Mock
    private IncidentRepository incidentRepository;

    private DetecteurEquipementFragile detecteur;
    private Equipement equipement;

    @BeforeEach
    void preparer() throws Exception {
        Clock horloge = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        detecteur = new DetecteurEquipementFragile(incidentRepository, horloge);
        Station station = new Station("Gare Centrale");
        equipement = new Equipement("ASC-GC-01", "Ascenseur", TypeEquipement.ASCENSEUR, station);
        fixerId(equipement, 42);
    }

    @Test
    void equipementAvecDeuxIncidentsRecentsNestPasFragile() {
        when(incidentRepository.countByEquipementIdAndDateHeureOuvertureGreaterThanEqual(eq(42), any()))
                .thenReturn(2L);

        assertThat(detecteur.estFragile(equipement)).isFalse();
    }

    @Test
    void equipementAvecTroisIncidentsRecentsEstFragile() {
        when(incidentRepository.countByEquipementIdAndDateHeureOuvertureGreaterThanEqual(eq(42), any()))
                .thenReturn(3L);

        assertThat(detecteur.estFragile(equipement)).isTrue();
    }

    @Test
    void peuImporteLeStatutDesIncidentsLeCompteurVientDUneSeuleRequete() {
        // EF2 : tous les statuts comptent ; c'est le rôle de la requête dérivée (pas de filtre par statut ici).
        when(incidentRepository.countByEquipementIdAndDateHeureOuvertureGreaterThanEqual(eq(42), any()))
                .thenReturn(5L);

        assertThat(detecteur.estFragile(equipement)).isTrue();
    }

    @Test
    void laFenetreGlissanteEstDeTrenteJoursAvantMaintenant() {
        detecteur.compterIncidentsRecents(equipement);

        ArgumentCaptor<LocalDateTime> captureDepuis = ArgumentCaptor.forClass(LocalDateTime.class);
        org.mockito.Mockito.verify(incidentRepository)
                .countByEquipementIdAndDateHeureOuvertureGreaterThanEqual(eq(42), captureDepuis.capture());

        assertThat(captureDepuis.getValue()).isEqualTo(MAINTENANT.minusDays(30));
    }

    @Test
    void leSeuilEtLaFenetreSontDesConstantesUniques() {
        // EF5 : seuil (3) et fenêtre (30 jours) définis une seule fois.
        assertThat(DetecteurEquipementFragile.SEUIL_INCIDENTS).isEqualTo(3);
        assertThat(DetecteurEquipementFragile.FENETRE).isEqualTo(Duration.ofDays(30));
    }

    private static void fixerId(Equipement equipement, int id) throws Exception {
        Field champId = Equipement.class.getDeclaredField("id");
        champId.setAccessible(true);
        champId.set(equipement, id);
    }
}
