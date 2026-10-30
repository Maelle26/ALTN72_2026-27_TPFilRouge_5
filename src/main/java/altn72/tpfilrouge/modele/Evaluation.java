package altn72.tpfilrouge.modele;

import altn72.tpfilrouge.exception.RegleMetierException;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "evaluation")
public class Evaluation {
    public static final int NOTE_MINIMALE = 1;
    public static final int NOTE_MAXIMALE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluation", nullable = false)
    private Integer id;

    // RG8 : une seule évaluation par incident (unique = true)
    @OneToOne(optional = false)
    @JoinColumn(name = "id_incident", nullable = false, unique = true)
    private Incident incident;

    @Column(name = "note", nullable = false)
    private Integer note;

    @Column(name = "commentaire", length = 500)
    private String commentaire;

    @Column(name = "date_evaluation", nullable = false)
    private LocalDate dateEvaluation;

    protected Evaluation() {
        // Constructeur requis par JPA
    }

    // RG8 : l'incident doit être résolu et affecté, la note est de 1 à 5
    public Evaluation(Incident incident, Integer note, String commentaire, LocalDate dateEvaluation) {
        if (!incident.estResolu() || incident.getPrestataire() == null) {
            throw new RegleMetierException("Seul un incident résolu et affecté à un prestataire peut être évalué.");
        }
        if (note == null || note < NOTE_MINIMALE || note > NOTE_MAXIMALE) {
            throw new RegleMetierException("La note doit être un entier compris entre "
                    + NOTE_MINIMALE + " et " + NOTE_MAXIMALE + ".");
        }
        this.incident = incident;
        this.note = note;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
    }

    public Integer getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public Integer getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public LocalDate getDateEvaluation() {
        return dateEvaluation;
    }
}