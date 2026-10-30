# Journal de bord
## Projet RailWatch - Supervision d'équipements et suivi d'incidents

**Groupe :** Maëlle BITSINDOU, Alexandre POISSONNEAU

### Modèle de données
Voici la 1ère version du diagramme de classe avec les principales entités et leurs relations :
![Diagramme de classes](docs/diagClasse_v1.png)

Relations principales :
- une **Station** contient plusieurs **Équipements**
- un **Équipement** peut avoir plusieurs **Incidents**
- un **Incident** peut être affecté à un **Prestataire** et avoir au plus une **Évaluation**
- un **Utilisateur** possède un **Profil** (Responsable d'exploitation ou Agent de signalement)

### Schéma d'architecture du projet
*A compléter*

Organisation actuelle des packages :
- `modele` : entités JPA et enums
- `repository` : accès aux données (Spring Data JPA)
- `exception` : exceptions personnalisées

### Répartition des tâches
| Fonctionnalités | Personne en charge | Date de début | Statut |
|-----------------|--------------------|---------------|--------|
| Modèle de données et diagramme de classes | Maëlle | 14/10/2026 | Terminé |
| Relations JPA et règles métier dans les entités | Maëlle | 30/10/2026 | Terminé |
| Repositories et requêtes | Maëlle | 30/10/2026 | En cours |

### Séance n°1 - 12/10/2026
- Création du dépôt GitHub pour le projet
- Ajout du `.gitignore` (dossier target, configuration IntelliJ, fichiers de secrets)

### Séance n°2 - 14/10/2026
- Début de la conception du modèle de données et du diagramme de classes
- Création de la BD (MySQL Workbench) et des tables principales
- Création des entités principales
- Création des données de test
- Suppression de l'exemple « Programmeur » issu du TP

### Séance n°3 - 30/10/2026
- Ajout des relations JPA entre les entités (Station, Équipement, Incident, Prestataire, Évaluation)
- Passage des dates de `String` à `LocalDateTime` pour pouvoir calculer la durée d'ouverture d'un incident
- Création des enums `TypeEquipement` et `EtatFonctionnement`, stockés en texte en base (`@Enumerated(EnumType.STRING)`)
- Règles métier placées dans les entités :
    - RG1 : un incident démarre sans prestataire
    - RG5 : un incident résolu ne peut plus être affecté ni modifié, la résolution ne peut pas précéder l'ouverture
    - RG8 : une seule évaluation par incident, uniquement si l'incident est résolu et affecté, note de 1 à 5
    - RG9 : référence d'équipement unique
- Création de l'exception personnalisée `RegleMetierException`
- Correction de `IncidentRepository` (doublon, type de l'identifiant, noms des champs dans les requêtes)
- Regroupement des repositories dans un package `repository` séparé du modèle