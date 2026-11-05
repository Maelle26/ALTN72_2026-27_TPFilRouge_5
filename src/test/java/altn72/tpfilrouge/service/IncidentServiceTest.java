package altn72.tpfilrouge.service;

import altn72.tpfilrouge.exception.RegleMetierException;
import altn72.tpfilrouge.exception.RessourceIntrouvableException;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.Statut;
import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.modele.TypeEquipement;
import altn72.tpfilrouge.repository.EquipementRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 5, 12, 0);
    private static final Integer ID_INCIDENT = 7;

    @Mock
    private IncidentRepository incidentRepository;
    @Mock
    private EquipementRepository equipementRepository;
    @Mock
    private PrestataireRepository prestataireRepository;

    private IncidentService service;

    @BeforeEach
    void preparerService() {
        Clock horloge = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new IncidentService(incidentRepository, equipementRepository, prestataireRepository,
                new CalculateurNiveauAttention(horloge), horloge);
    }

    @Test
    void incidentInexistantProduitUneExceptionDeRessourceIntrouvable() {
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.trouver(ID_INCIDENT))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessageContaining("Incident introuvable");
    }

    @Test
    void resoudreEnregistreLaDateDeResolutionEtLeStatut() {
        Incident incident = incidentOuvertIlYa(10);
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));

        Incident resolu = service.resoudre(ID_INCIDENT);

        assertThat(resolu.getStatut()).isEqualTo(Statut.RESOLU);
        assertThat(resolu.getDateHeureResolution()).isEqualTo(MAINTENANT);
    }

    @Test
    void unIncidentResoluNePeutPlusEtreAffecte() {
        Incident incident = incidentOuvertIlYa(10);
        incident.resoudre(MAINTENANT);
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(1)).thenReturn(Optional.of(unPrestataire()));

        assertThatThrownBy(() -> service.affecter(ID_INCIDENT, 1))
                .isInstanceOf(RegleMetierException.class);
    }

    @Test
    void affecterPasseLIncidentAuStatutAffecteAvecSonPrestataire() {
        Incident incident = incidentOuvertIlYa(10);
        Prestataire prestataire = unPrestataire();
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(1)).thenReturn(Optional.of(prestataire));

        Incident affecte = service.affecter(ID_INCIDENT, 1);

        assertThat(affecte.getStatut()).isEqualTo(Statut.AFFECTE);
        assertThat(affecte.getPrestataire()).isSameAs(prestataire);
    }

    @Test
    void declarerRefuseUneDescriptionVide() {
        assertThatThrownBy(() -> service.declarer(1, "   "))
                .isInstanceOf(RegleMetierException.class);
    }

    @Test
    void declarerCreeUnIncidentOuvertSansPrestataireALHeureCourante() {
        Equipement equipement = new Equipement("ASC-01", "Ascenseur", TypeEquipement.ASCENSEUR, new Station("Gare"));
        when(equipementRepository.findById(1)).thenReturn(Optional.of(equipement));
        when(incidentRepository.save(org.mockito.ArgumentMatchers.any(Incident.class)))
                .thenAnswer(appel -> appel.getArgument(0));

        Incident incident = service.declarer(1, "  Bloqué  ");

        assertThat(incident.getStatut()).isEqualTo(Statut.OUVERT);
        assertThat(incident.getPrestataire()).isNull();
        assertThat(incident.getDescription()).isEqualTo("Bloqué");
        assertThat(incident.getDateHeureOuverture()).isEqualTo(MAINTENANT);
    }

    @Test
    void leTableauDeBordFiltreSurLeNiveauDAttention() {
        Incident rouge = incidentOuvertIlYa(80);
        Incident vert = incidentOuvertIlYa(5);
        when(incidentRepository.findByStatutNot(Statut.RESOLU)).thenReturn(List.of(vert, rouge));

        List<IncidentActif> actifs = service.listerActifs(new FiltreIncidents(null, NiveauAttention.ROUGE));

        assertThat(actifs).hasSize(1);
        assertThat(actifs.get(0).incident()).isSameAs(rouge);
    }

    private Incident incidentOuvertIlYa(long heures) {
        Station station = new Station("Gare Centrale");
        Equipement equipement = new Equipement("ASC-01", "Ascenseur", TypeEquipement.ASCENSEUR, station);
        return new Incident(equipement, "Panne", MAINTENANT.minusHours(heures));
    }

    private Prestataire unPrestataire() {
        return new Prestataire("Elevatec", "Karim", "0145000001");
    }
}
