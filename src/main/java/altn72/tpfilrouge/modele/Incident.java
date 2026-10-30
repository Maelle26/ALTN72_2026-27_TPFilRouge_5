package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.exception.RegleMetierException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incident", nullable = false)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_equipement", nullable = false)
    private Equipement equipement;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "date_heure_ouverture", nullable = false)
    private LocalDateTime dateHeureOuverture;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "id_prestataire")
    private Prestataire prestataire;

    @Column(name = "date_heure_resolution")
    private LocalDateTime dateHeureResolution;

    @OneToOne(mappedBy = "incident")
    private Evaluation evaluation;

    protected Incident() {
        // Constructeur requis par JPA
    }

    // RG1 : un incident est rattaché à un équipement, a une date d'ouverture et démarre sans prestataire
    public Incident(Equipement equipement, String description, LocalDateTime dateHeureOuverture) {
        this.equipement = equipement;
        this.description = description;
        this.dateHeureOuverture = dateHeureOuverture;
        this.statut = Statut.OUVERT;
    }

    // F7 / RG5 : un incident non résolu peut être affecté puis réaffecté
    public void affecter(Prestataire nouveauPrestataire) {
        verifierNonResolu("Un incident résolu ne peut plus être affecté.");
        this.prestataire = nouveauPrestataire;
        this.statut = Statut.AFFECTE;
    }

    // F8 / RG5 : la résolution ne peut pas précéder l'ouverture
    public void resoudre(LocalDateTime dateHeureResolution) {
        verifierNonResolu("Cet incident est déjà résolu.");
        if (dateHeureResolution.isBefore(dateHeureOuverture)) {
            throw new RegleMetierException("La date de résolution ne peut pas précéder la date d'ouverture.");
        }
        this.dateHeureResolution = dateHeureResolution;
        this.statut = Statut.RESOLU;
    }

    // F6 : seule la description est modifiable, tant que l'incident n'est pas résolu
    public void modifierDescription(String nouvelleDescription) {
        verifierNonResolu("Un incident résolu ne peut plus être modifié.");
        this.description = nouvelleDescription;
    }

    public boolean estResolu() {
        return statut == Statut.RESOLU;
    }

    public boolean estAffecte() {
        return statut == Statut.AFFECTE;
    }

    public boolean estEvaluable() {
        return estResolu() && prestataire != null && evaluation == null;
    }

    private void verifierNonResolu(String message) {
        if (estResolu()) {
            throw new RegleMetierException(message);
        }
    }

    public Integer getId() {
        return id;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateHeureOuverture() {
        return dateHeureOuverture;
    }

    public Statut getStatut() {
        return statut;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public LocalDateTime getDateHeureResolution() {
        return dateHeureResolution;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }
}