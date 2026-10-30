package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    // 1. Requête dérivée : incidents actifs (non résolus) pour le Dashboard (RG6)
    List<Incident> findByStatutNot(Statut statut);

    // 2. Requête dérivée : historique des incidents résolus, du plus récent au plus ancien (RG7)
    List<Incident> findByStatutOrderByDateHeureResolDesc(Statut statut);

    // 3. Requête JPQL : historique des incidents d'un équipement donné (F3, RG7)
    @Query("SELECT i FROM Incident i WHERE i.equipement.id = :equipementId ORDER BY i.dateHeureOuv DESC")
    List<Incident> findIncidentsByEquipementOrderByDateHeureOuvDesc(@Param("equipementId") Long equipementId);
}
