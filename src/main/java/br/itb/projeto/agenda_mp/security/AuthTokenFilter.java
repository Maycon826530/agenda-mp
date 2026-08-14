package br.itb.projeto.agenda_mp.security;

import java.io.IOException;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final AuthTokenService authTokenService;
    private final UsuarioRepository usuarioRepository;

    public AuthTokenFilter(AuthTokenService authTokenService, UsuarioRepository usuarioRepository) {
        this.authTokenService = authTokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            Long usuarioId = authTokenService.validateAndGetUsuarioId(authorization.substring(7));
            if (usuarioId != null) {
                usuarioRepository.findById(usuarioId).ifPresent(usuario -> autenticar(usuario));
            }
        }
        filterChain.doFilter(request, response);
    }

    private void autenticar(Usuario usuario) {
        var authorities = java.util.List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario.getId(), null, authorities));
    }
}
