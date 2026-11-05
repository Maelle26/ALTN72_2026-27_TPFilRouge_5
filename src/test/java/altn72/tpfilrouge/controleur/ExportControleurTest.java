package altn72.tpfilrouge.controleur;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

// Lot alpha - piste 9 (export CSV) : test d'intégration qui démarre l'application complète
// (comme TpFilRougeApplicationTests) et appelle réellement la route HTTP, au-delà des tests unitaires de service.
// Utilise le client HTTP du JDK (java.net.http) plutôt que TestRestTemplate/MockMvc, dont les classes
// ne se résolvaient pas dans cet environnement (modules de test Spring Boot 4 éclatés différemment).
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportControleurTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    void renvoieLeFichierCsvAvecLesBonsEnTetes() throws IOException, InterruptedException {
        String identifiants = Base64.getEncoder().encodeToString("responsable:password".getBytes(StandardCharsets.UTF_8));
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/historique/export"))
                .header("Authorization", "Basic " + identifiants)
                .GET()
                .build();

        HttpResponse<byte[]> reponse = HttpClient.newHttpClient()
                .send(requete, HttpResponse.BodyHandlers.ofByteArray());

        assertThat(reponse.statusCode()).isEqualTo(200);
        assertThat(reponse.headers().firstValue("Content-Type").orElse("")).contains("text/csv");
        assertThat(reponse.headers().firstValue("Content-Disposition").orElse(""))
                .contains("historique-incidents.csv");
        String contenu = new String(reponse.body(), StandardCharsets.UTF_8);
        assertThat(contenu).contains(
                "Date de résolution;Station;Équipement;Description;Prestataire;Durée (heures);Note;Commentaire");
    }
}