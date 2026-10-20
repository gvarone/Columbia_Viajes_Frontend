package com.columbia.viajes.security;

import com.columbia.viajes.client.AuthClient;
import com.columbia.viajes.views.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad del front con la integración oficial de Vaadin:
 * - Las vistas se protegen con @RolesAllowed / @PermitAll / @AnonymousAllowed
 *   (una vista sin anotación queda denegada).
 * - Vaadin maneja CSRF, el login por formulario y el logout.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthClient authClient) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/styles.css").permitAll());

        return http.with(VaadinSecurityConfigurer.vaadin(), configurer -> configurer
                        .loginView(LoginView.class)
                        // Invalida el JWT en el backend al cerrar sesión
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null
                                    && authentication.getPrincipal() instanceof UsuarioAutenticado usuario) {
                                authClient.logout(usuario.token());
                            }
                        }))
                .build();
    }
}
