package Codicis_Nocturni.connec_escola.controller;

import Codicis_Nocturni.connec_escola.controller.dto.LoginRequest;
import Codicis_Nocturni.connec_escola.controller.dto.LoginResponse;
import Codicis_Nocturni.connec_escola.controller.dto.RegisterRequest;
import Codicis_Nocturni.connec_escola.model.Usuario;
import Codicis_Nocturni.connec_escola.repository.UsuarioRepository;
import Codicis_Nocturni.connec_escola.security.JwtService;
import Codicis_Nocturni.connec_escola.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Nossos "funcionários"
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    // Injetamos os novos serviços
    public AuthController(UsuarioService usuarioService,
                          AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint de REGISTRO
     * Escuta em: POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegisterRequest request) {
        try {
            Usuario usuarioSalvo = usuarioService.registrarNovoUsuario(
                    request.getNome(),
                    request.getEmail(),
                    request.getSenha()
            );
            return ResponseEntity.ok("Usuário registrado com sucesso!");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint de LOGIN
     * Escuta em: POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@Valid @RequestBody LoginRequest request) {
        try {
            // 1. Tenta autenticar o usuário (checar se email e senha batem)
            // É aqui que o Spring Security faz a mágica de comparar as senhas
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()
                    )
            );

            // 2. Se a autenticação deu certo, buscamos o usuário no banco
            // (Precisamos do objeto Usuario para pegar a ROLE)
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação")); // Isso não deve acontecer

            // 3. Geramos o "crachá" (Token JWT) para esse usuário
            String token = jwtService.generateToken(usuario);

            // 4. Enviamos o token e a role de volta para o frontend
            return ResponseEntity.ok(new LoginResponse(token, usuario.getRole().name()));

        } catch (BadCredentialsException e) {
            // 5. Se a senha estiver errada
            return ResponseEntity.status(401).body("Email ou senha inválidos");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor");
        }
    }

    // TODO: Adicionar o endpoint GET /google-success para o OAuth2
}