package Codicis_Nocturni.connec_escola.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marca esta classe como um componente para ser injetado
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Injetamos os serviços que o filtro precisa
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Tenta ler o header
        final String jwt;
        final String userEmail;

        // 1. Checa se o token JWT está presente no header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Se não houver token, passa a requisição adiante sem autenticar
        }

        // 2. Extrai o token (remove o "Bearer ")
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt); // Extrai o email do token

        // 3. Se o email existe E o usuário ainda não está autenticado
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Carrega os detalhes do usuário (Role, permissões, etc.)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. Se o token for válido...
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 6. Criamos um objeto de autenticação
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // As permissões (ROLE_ADMIN, etc.)
                );

                // 7. Damos detalhes da requisição
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. E, finalmente, AUTENTICAMOS o usuário dentro do Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continua a cadeia de filtros (deixa a requisição chegar ao Controller)
        filterChain.doFilter(request, response);
    }
}