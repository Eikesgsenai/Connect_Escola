package Codicis_Nocturni.connec_escola.service;

import Codicis_Nocturni.connec_escola.controller.dto.AdminCreateUserRequest;
import java.util.List;
import Codicis_Nocturni.connec_escola.model.Role; // Importa a Role
import Codicis_Nocturni.connec_escola.model.Usuario;
import Codicis_Nocturni.connec_escola.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Camada de serviço para a lógica de negócio de Usuários.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra um novo usuário (Aluno) a partir da tela pública.
     */
    @Transactional
    public Usuario registrarNovoUsuario(String nome, String email, String senhaAFormatar) {

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Erro: Este email já está em uso!");
        }

        String senhaCriptografada = passwordEncoder.encode(senhaAFormatar);

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setRole(Role.ALUNO);

        return usuarioRepository.save(novoUsuario);
    }

    /**
     * Permite que um Admin crie um novo usuário com qualquer Role.
     */
    @Transactional
    public Usuario adminCreateUser(AdminCreateUserRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Erro: Este email já está em uso!");
        }

        String senhaCriptografada = passwordEncoder.encode(request.getSenha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(request.getNome());
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setRole(request.getRole());

        return usuarioRepository.save(novoUsuario);
    }

    /**
     * Busca todos os usuários no banco.
     */
    public List<Usuario> findAllUsers() {
        return usuarioRepository.findAll();
    }

    /**
     * NOVO MÉTODO: Permite que um Admin (Gerente) mude a Role de outro usuário.
     */
    @Transactional
    public Usuario updateUserRole(Long userId, Role newRole) {
        // 1. Encontra o usuário no banco ou lança um erro
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));

        // 2. Atualiza a Role
        usuario.setRole(newRole);

        // 3. Salva o usuário atualizado
        return usuarioRepository.save(usuario);
    }
}