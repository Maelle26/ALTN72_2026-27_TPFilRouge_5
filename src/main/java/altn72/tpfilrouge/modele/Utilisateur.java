package altn72.tpfilrouge.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur", nullable = false)
    private Integer id;

    @Column(name = "mdp", nullable = false, length = 100)
    private String mdp;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name= "profile", length = 255)
    private Profil profile;
}