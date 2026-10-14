package altn72.tpfilrouge.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incident", nullable = false)
    private Integer id;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "dateHeure_Ouverture", nullable = false)
    private String dateHeureOuverture;

    @Column(name = "statut", nullable = false, length = 50)
    private Statut statut; // Peut être ENUM

    @Column (name = "dateHeure_Resolution", nullable = true)
    private String dateHeureResolution;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateHeureOuverture() {
        return dateHeureOuverture;
    }

    public void setDateHeureOuverture(String dateHeureOuverture) {
        this.dateHeureOuverture = dateHeureOuverture;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getDateHeureResolution() {
        return dateHeureResolution;
    }

    public void setDateHeureResolution(String dateHeureResolution) {
        this.dateHeureResolution = dateHeureResolution;
    }
}