package altn72.tpfilrouge.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "equipement")
public class Equipement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // RG9 : la référence d'un équipement est unique
    @Column(name = "reference", nullable = false, unique = true, length = 50)
    private String reference;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private TypeEquipement type;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_fonctionnement", nullable = false, length = 30)
    private EtatFonctionnement etatFonctionnement;

    // RG9 : un équipement est rattaché à une station
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_station", nullable = false)
    private Station station;

    protected Equipement() {
    }

    public Equipement(String reference, String libelle, TypeEquipement type, Station station) {
        this.reference = reference;
        this.libelle = libelle;
        this.type = type;
        this.station = station;
        this.etatFonctionnement = EtatFonctionnement.EN_SERVICE;
    }

    public Integer getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public TypeEquipement getType() {
        return type;
    }

    public EtatFonctionnement getEtatFonctionnement() {
        return etatFonctionnement;
    }

    public void setEtatFonctionnement(EtatFonctionnement etatFonctionnement) {
        this.etatFonctionnement = etatFonctionnement;
    }

    public Station getStation() {
        return station;
    }
}