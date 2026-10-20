package com.columbia.viajes.security;

import com.columbia.viajes.client.AuthClient;
import com.columbia.viajes.dto.request.LoginRequest;
import com.columbia.viajes.dto.response.LoginResponse;
import com.columbia.viajes.security.enums.RolEnum;
import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

/**
 * Valida usuario y contraseña contra POST /api/auth/login del backend.
 * El front no conoce contraseñas: solo guarda el JWT que devuelve el backend.
 */
@Component
public class BackendAuthenticationProvider implements AuthenticationProvider {

    private final AuthClient authClient;

    public BackendAuthenticationProvider(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String nombre = authentication.getName();
        String contrasenia = String.valueOf(authentication.getCredentials());

        LoginResponse respuesta;
        try {
            respuesta = authClient.login(new LoginRequest(nombre, contrasenia));
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.BadRequest e) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        } catch (RestClientException e) {
            throw new AuthenticationServiceException("No se pudo conectar con el servidor", e);
        }

        RolEnum rol = respuesta == null ? null : RolEnum.desdeDb(respuesta.rolNombre());
        if (rol == null || respuesta.token() == null) {
            throw new AuthenticationServiceException("Respuesta de login inválida");
        }

        UsuarioAutenticado usuario = new UsuarioAutenticado(respuesta.nombre(), rol, respuesta.token());
        return UsernamePasswordAuthenticationToken.authenticated(
                usuario, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol.name())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
