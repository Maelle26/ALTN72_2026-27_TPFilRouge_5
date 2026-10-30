package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    // Requête dérivée : incidents actifs (non résolus) pour le tableau de bord (RG6)
    List<Incident> findByStatutNot(Statut statut);

    // Requête dérivée : historique des incidents résolus, du plus récent au plus ancien (RG7)
    List<Incident> findByStatutOrderByDateHeureResolutionDesc(Statut statut);

    // Requête JPQL : incidents d'un équipement, triés par date d'ouverture (F3, RG7)
    @Query("SELECT i FROM Incident i WHERE i.equipement.id = :idEquipement ORDER BY i.dateHeureOuverture DESC")
    List<Incident> findIncidentsDeLEquipement(@Param("idEquipement") Integer idEquipement);
}