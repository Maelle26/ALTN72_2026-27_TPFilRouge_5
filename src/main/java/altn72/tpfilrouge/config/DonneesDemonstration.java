package altn72.tpfilrouge.config;

import altn72.tpfilrouge.modele.*;
import altn72.tpfilrouge.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Remplit la base au démarrage avec des données de démonstration.
 * Les dates sont calculées par rapport à l'instant présent (« il y a 47 heures »),
 * pour que les cas limites des indicateurs (47 h, 48 h, 72 h) restent visibles quel que soit le jour de la démo.
 */
@Component
@ConditionalOnProperty(name = "railwatch.donnees-demo", havingValue = "true", matchIfMissing = true)
public class DonneesDemonstration implements CommandLineRunner {

    // Hash BCrypt aucun mdp en clair
    private static final String HASH_MOT_DE_PASSE_RESPONSABLE = "$2b$10$X8fbVfvFnZaHk4pSZyPcmuGJV67y0DQU0JLrskr3FqEJ8kI9Jy0ZG";
    private static final String HASH_MOT_DE_PASSE_AGENT = "$2b$10$X8fbVfvFnZaHk4pSZyPcmuGJV67y0DQU0JLrskr3FqEJ8kI9Jy0ZG";

    private final StationRepository stationRepository;
    private final EquipementRepository equipementRepository;
    private final PrestataireRepository prestataireRepository;
    private final IncidentRepository incidentRepository;
    private final EvaluationRepository evaluationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final Clock horloge;

    public DonneesDemonstration(StationRepository stationRepository, EquipementRepository equipementRepository,
                                PrestataireRepository prestataireRepository, IncidentRepository incidentRepository,
                                EvaluationRepository evaluationRepository, UtilisateurRepository utilisateurRepository,
                                Clock horloge) {
        this.stationRepository = stationRepository;
        this.equipementRepository = equipementRepository;
        this.prestataireRepository = prestataireRepository;
        this.incidentRepository = incidentRepository;
        this.evaluationRepository = evaluationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.horloge = horloge;
    }

    // Tout ou rien : si une insertion échoue, aucune donnée partielle ne reste en base (RG10)
    @Transactional
    @Override
    public void run(String... args) {
        if (stationRepository.count() > 0) {
            return;
        }
        creerUtilisateurs();

        Station gareCentrale = new Station("Gare Centrale");
        Station republique = new Station("République");
        Station universite = new Station("Université");
        Station hotelDeVille = new Station("Hôtel de Ville");
        stationRepository.saveAll(List.of(gareCentrale, republique, universite, hotelDeVille));

        Equipement ascenseurGare = new Equipement("ASC-GC-01", "Ascenseur quai A", TypeEquipement.ASCENSEUR, gareCentrale);
        Equipement escalierGare = new Equipement("ESC-GC-01", "Escalier mécanique sortie nord", TypeEquipement.ESCALIER_MECANIQUE, gareCentrale);
        Equipement portiqueGare = new Equipement("POR-GC-01", "Portique d'accès hall principal", TypeEquipement.PORTIQUE, gareCentrale);
        Equipement distributeurGare = new Equipement("DIS-GC-01", "Distributeur de titres hall", TypeEquipement.DISTRIBUTEUR_TITRES, gareCentrale);
        Equipement ascenseurRepublique = new Equipement("ASC-RE-01", "Ascenseur accès PMR", TypeEquipement.ASCENSEUR, republique);
        Equipement ecranRepublique = new Equipement("ECR-RE-01", "Écran d'information quai 1", TypeEquipement.ECRAN_INFORMATION, republique);
        Equipement portiqueRepublique = new Equipement("POR-RE-01", "Portique sortie est", TypeEquipement.PORTIQUE, republique);
        Equipement escalierUniversite = new Equipement("ESC-UN-01", "Escalier mécanique quai B", TypeEquipement.ESCALIER_MECANIQUE, universite);
        Equipement distributeurUniversite = new Equipement("DIS-UN-01", "Distributeur de titres entrée", TypeEquipement.DISTRIBUTEUR_TITRES, universite);
        Equipement ecranUniversite = new Equipement("ECR-UN-01", "Écran d'information hall", TypeEquipement.ECRAN_INFORMATION, universite);
        Equipement ascenseurHotelDeVille = new Equipement("ASC-HV-01", "Ascenseur correspondance", TypeEquipement.ASCENSEUR, hotelDeVille);
        Equipement portiqueHotelDeVille = new Equipement("POR-HV-01", "Portique d'accès", TypeEquipement.PORTIQUE, hotelDeVille);
        equipementRepository.saveAll(List.of(ascenseurGare, escalierGare, portiqueGare, distributeurGare,
                ascenseurRepublique, ecranRepublique, portiqueRepublique, escalierUniversite,
                distributeurUniversite, ecranUniversite, ascenseurHotelDeVille, portiqueHotelDeVille));

        Prestataire elevatec = new Prestataire("Elevatec", "Karim", "0145000001");
        Prestataire mecaRail = new Prestataire("MécaRail", "Sophie", "0145000002");
        Prestataire billetiquePlus = new Prestataire("Billettique Plus", "Thomas", "0145000003");
        Prestataire infoVoyageurs = new Prestataire("InfoVoyageurs", "Léa", "0145000004");
        prestataireRepository.saveAll(List.of(elevatec, mecaRail, billetiquePlus, infoVoyageurs));

        // Incidents non affectés : couvrent les cas limites de RG3
        Incident vert2h = incidentOuvertIlYa(2, ecranRepublique, "Écran figé sur l'horaire de 6 h 12");
        Incident vert47h = incidentOuvertIlYa(47, portiqueGare, "Le portique refuse les cartes valides");
        Incident orange48h = incidentOuvertIlYa(48, distributeurGare, "Le distributeur n'accepte plus la carte bancaire");
        Incident orange60h = incidentOuvertIlYa(60, escalierUniversite, "Bruit anormal et arrêts fréquents");
        Incident rouge72h = incidentOuvertIlYa(72, ascenseurRepublique, "Ascenseur bloqué au niveau -1");
        Incident rouge120h = incidentOuvertIlYa(120, portiqueHotelDeVille, "Portique bloqué en position ouverte");

        // Incidents affectés : ne portent pas d'indicateur vert/orange/rouge (RG4), même ouverts depuis longtemps
        Incident affecte30h = incidentOuvertIlYa(30, escalierGare, "Escalier mécanique à l'arrêt");
        affecte30h.affecter(mecaRail);
        Incident affecte90h = incidentOuvertIlYa(90, ascenseurHotelDeVille, "Porte de l'ascenseur ne se ferme plus");
        affecte90h.affecter(elevatec);

        // Incidents résolus : alimentent l'historique (F9) et les évaluations (F10)
        Incident resoluAscenseur = incidentResolu(240, 200, ascenseurGare, "Bouton d'appel hors service", elevatec);
        Incident resoluDistributeur = incidentResolu(180, 170, distributeurUniversite, "Écran tactile inutilisable", billetiquePlus);
        Incident resoluEcran = incidentResolu(150, 100, ecranUniversite, "Écran éteint", infoVoyageurs);
        Incident resoluEscalier = incidentResolu(96, 50, escalierUniversite, "Marche endommagée", mecaRail);
        Incident resoluPortique = incidentResolu(60, 20, portiqueRepublique, "Lecteur de carte défaillant", billetiquePlus);

        incidentRepository.saveAll(List.of(vert2h, vert47h, orange48h, orange60h, rouge72h, rouge120h,
                affecte30h, affecte90h, resoluAscenseur, resoluDistributeur, resoluEcran, resoluEscalier, resoluPortique));

        // resoluPortique reste volontairement sans évaluation, pour pouvoir tester F10 en démo
        LocalDate aujourdHui = LocalDate.now(horloge);
        evaluationRepository.saveAll(List.of(
                new Evaluation(resoluAscenseur, 5, "Intervention rapide et efficace", aujourdHui.minusDays(8)),
                new Evaluation(resoluDistributeur, 3, "Réparation correcte mais délai long", aujourdHui.minusDays(7)),
                new Evaluation(resoluEcran, 2, "Il a fallu relancer le prestataire", aujourdHui.minusDays(4)),
                new Evaluation(resoluEscalier, 4, null, aujourdHui.minusDays(2))));

        mettreHorsServiceLesEquipementsEnPanne(vert47h, orange48h, rouge72h, rouge120h, affecte90h);
        ecranRepublique.setEtatFonctionnement(EtatFonctionnement.DEGRADE);
        escalierUniversite.setEtatFonctionnement(EtatFonctionnement.DEGRADE);
        escalierGare.setEtatFonctionnement(EtatFonctionnement.DEGRADE);
    }

    private void creerUtilisateurs() {
        utilisateurRepository.saveAll(List.of(
                new Utilisateur("responsable", HASH_MOT_DE_PASSE_RESPONSABLE, "Maëlle", Profil.RESPONSABLE_EXPLOITATION),
                new Utilisateur("agent", HASH_MOT_DE_PASSE_AGENT, "Alexandre", Profil.AGENT_SIGNALEMENT)));
    }

    private Incident incidentOuvertIlYa(long heures, Equipement equipement, String description) {
        return new Incident(equipement, description, ilYa(heures));
    }

    private Incident incidentResolu(long heuresDepuisOuverture, long heuresDepuisResolution,
                                    Equipement equipement, String description, Prestataire prestataire) {
        Incident incident = incidentOuvertIlYa(heuresDepuisOuverture, equipement, description);
        incident.affecter(prestataire);
        incident.resoudre(ilYa(heuresDepuisResolution));
        return incident;
    }

    private void mettreHorsServiceLesEquipementsEnPanne(Incident... incidents) {
        for (Incident incident : incidents) {
            incident.getEquipement().setEtatFonctionnement(EtatFonctionnement.HORS_SERVICE);
        }
    }

    private LocalDateTime ilYa(long heures) {
        return LocalDateTime.now(horloge).minusHours(heures);
    }
}