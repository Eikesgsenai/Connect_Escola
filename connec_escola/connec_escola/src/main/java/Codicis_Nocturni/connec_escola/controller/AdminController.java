package Codicis_Nocturni.connec_escola.controller;

import Codicis_Nocturni.connec_escola.controller.dto.AdminCreateUserRequest;
import Codicis_Nocturni.connec_escola.controller.dto.UserResponse;
import Codicis_Nocturni.connec_escola.controller.dto.UpdateRoleRequest; // Nova Importação
import Codicis_Nocturni.connec_escola.model.Usuario;
import Codicis_Nocturni.connec_escola.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*; // Importação atualizada

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin") // Todos os endpoints aqui começam com /api/admin
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint para o Gerente (ADMIN) listar todos os usuários.
     * Somente usuários com a ROLE_ADMIN podem acessar.
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')") // A MÁGICA DA SEGURANÇA!
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<Usuario> usuarios = usuarioService.findAllUsers();

        // Converte a lista de Usuario para uma lista de UserResponse (sem senha)
        List<UserResponse> response = usuarios.stream()
                .map(UserResponse::new) // O mesmo que (usuario -> new UserResponse(usuario))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para o Gerente (ADMIN) criar um novo usuário (ex: Professor).
     * Somente usuários com a ROLE_ADMIN podem acessar.
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')") // A MÁGICA DA SEGURANÇA!
    public ResponseEntity<?> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        try {
            Usuario novoUsuario = usuarioService.adminCreateUser(request);
            // Devolve o usuário criado (sem a senha)
            return ResponseEntity.ok(new UserResponse(novoUsuario));

        } catch (RuntimeException e) {
            // Se o email já existir
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * NOVO ENDPOINT: Para o Gerente (ADMIN) atualizar a Role de um usuário.
     * Somente usuários com a ROLE_ADMIN podem acessar.
     */
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')") // A MÁGICA DA SEGURANÇA!
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {

        try {
            Usuario usuarioAtualizado = usuarioService.updateUserRole(id, request.getRole());
            return ResponseEntity.ok(new UserResponse(usuarioAtualizado));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}