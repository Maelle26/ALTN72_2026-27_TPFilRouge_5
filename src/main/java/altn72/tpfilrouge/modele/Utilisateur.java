package altn72.tpfilrouge.modele;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur", nullable = false)
    private Integer id;

    @Column(name = "login", nullable = false, unique = true, length = 50)
    private String login;

    // Hash BCrypt (60 caractères) : le mot de passe n'est jamais stocké en clair
    @Column(name = "mot_de_passe_hache", nullable = false, length = 100)
    private String motDePasseHache;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(name = "profil", nullable = false, length = 30)
    private Profil profil;

    protected Utilisateur() {
        // Constructeur requis par JPA
    }

    public Utilisateur(String login, String motDePasseHache, String prenom, Profil profil) {
        this.login = login;
        this.motDePasseHache = motDePasseHache;
        this.prenom = prenom;
        this.profil = profil;
    }

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getMotDePasseHache() {
        return motDePasseHache;
    }

    public String getPrenom() {
        return prenom;
    }

    public Profil getProfil() {
        return profil;
    }
}