package altn72.tpfilrouge.securite;

import altn72.tpfilrouge.modele.Profil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ConfigurationSecurite {

    private static final String RESPONSABLE = Profil.RESPONSABLE_EXPLOITATION.name();

    @Bean
    public PasswordEncoder encodeurMotsDePasse() {
        return new BCryptPasswordEncoder();
    }

    // RG11 : les restrictions par profil sont appliquées ici, côté serveur
    @Bean
    public SecurityFilterChain chaineDeSecurite(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(autorisations -> autorisations
                        .requestMatchers("/connexion", "/css/**", "/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/tableau-de-bord/**").hasRole(RESPONSABLE)
                        .requestMatchers("/incidents/*/affectation", "/incidents/*/resolution",
                                "/incidents/*/evaluation").hasRole(RESPONSABLE)
                        .requestMatchers(HttpMethod.PUT, "/api/incidents/*/prestataire").hasRole(RESPONSABLE)
                        .requestMatchers(HttpMethod.POST, "/api/incidents/*/resolution").hasRole(RESPONSABLE)
                        .anyRequest().authenticated())
                .formLogin(formulaire -> formulaire
                        .loginPage("/connexion")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/connexion?erreur"))
                .logout(deconnexion -> deconnexion.logoutSuccessUrl("/connexion?deconnexion"))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        return http.build();
    }
}
