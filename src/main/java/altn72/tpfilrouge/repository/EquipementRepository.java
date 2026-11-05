package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Integer> {

    List<Equipement> findByStationIdOrderByLibelleAsc(Integer stationId);
}
