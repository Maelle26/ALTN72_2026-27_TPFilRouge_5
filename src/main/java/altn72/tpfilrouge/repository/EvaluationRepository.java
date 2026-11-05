package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    // Seule requête SQL native du projet (voir l'analyse critique dans le journal)
    @Query(value = """
            SELECT AVG(e.note)
            FROM evaluation e
            JOIN incident i ON i.id_incident = e.id_incident
            WHERE i.id_prestataire = :idPrestataire
            """, nativeQuery = true)
    Optional<Double> calculerNoteMoyenne(@Param("idPrestataire") Integer idPrestataire);
}
