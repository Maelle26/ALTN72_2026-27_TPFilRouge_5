package altn72.tpfilrouge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class HorlogeConfig {

    // Horloge injectée partout au lieu d'appeler LocalDateTime.now() :
    // les tests peuvent ainsi fixer l'heure courante et vérifier les seuils de 48 h et 72 h
    @Bean
    public Clock horloge() {
        return Clock.systemDefaultZone();
    }
}