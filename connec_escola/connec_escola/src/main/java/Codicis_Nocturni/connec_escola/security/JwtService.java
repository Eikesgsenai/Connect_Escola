package Codicis_Nocturni.connec_escola.security;

import Codicis_Nocturni.connec_escola.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // 1. Vamos pegar uma "chave secreta" do application.properties
    // Esta chave é usada para assinar o token e garantir que é válido
    // (Vamos adicionar essa chave no próximo passo)
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // 2. Gera um token para um usuário
    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        // 3. Informação MAIS IMPORTANTE: Colocamos a ROLE (permissão) dentro do token
        // É assim que o frontend saberá se o usuário é ADMIN ou ALUNO
        claims.put("role", usuario.getRole().name());
        claims.put("nome", usuario.getNome());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getUsername()) // O "dono" do token (no nosso caso, o email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Token válido por 24 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Métodos para LER o token ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- Métodos de Validação ---

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // --- Métodos Auxiliares ---

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}