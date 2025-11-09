package Codicis_Nocturni.connec_escola.security;

import Codicis_Nocturni.connec_escola.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;
    // O FILTRO NÃO É MAIS INJETADO NO CONSTRUTOR, QUEBRANDO O CICLO
    // private final JwtAuthenticationFilter jwtAuthFilter;

    // O construtor agora só precisa do Repositório
    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BEAN 5: As Regras de Acesso (Com o Filtro JWT)
     * Injetamos o filtro aqui no método, resolvendo a referência circular.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- PÁGINAS PÚBLICAS ---
                        .requestMatchers(
                                "/",
                                "/*.html",
                                "/*.css",
                                "/*.js",
                                "/assets/**",
                                "/api/auth/**",
                                "/h2-console/**"
                        ).permitAll()

                        // --- PÁGINAS PROTEGIDAS ---
                        .anyRequest().authenticated()
                )
                // NOVIDADE: Dizemos ao Spring para NÃO criar sessões no servidor (stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // NOVIDADE: Adicionamos nosso filtro JWT para ser executado
                // ANTES do filtro padrão de autenticação do Spring
                // O filtro 'jwtAuthFilter' é injetado diretamente neste método
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}