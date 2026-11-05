package altn72.tpfilrouge.modele;

public enum NiveauAttention {
    VERT("Vert"),
    ORANGE("Orange"),
    ROUGE("Rouge"),
    PRIS_EN_CHARGE("Pris en charge"),
    RESOLU("Résolu");

    private final String libelle;

    NiveauAttention(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
