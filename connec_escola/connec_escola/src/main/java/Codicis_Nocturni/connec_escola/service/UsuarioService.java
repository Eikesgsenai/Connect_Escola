package Codicis_Nocturni.connec_escola.service;

import Codicis_Nocturni.connec_escola.model.Role;
import Codicis_Nocturni.connec_escola.model.Usuario;
import Codicis_Nocturni.connec_escola.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Camada de serviço para a lógica de negócio de Usuários.
 */
@Service // Marca esta classe como um "Serviço" para o Spring
public class UsuarioService {

    // Nossos "assistentes" - o Spring vai injetá-los para nós
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Construtor para Injeção de Dependência (a forma correta de fazer)
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder; // Pegamos o BCrypt que criamos no SecurityConfig
    }

    /**
     * Registra um novo usuário no sistema.
     * Esta é a nossa principal regra de negócio por enquanto.
     *
     * @param nome O nome do usuário.
     * @param email O email (que será o login).
     * @param senhaAFormatar A senha pura (sem criptografia).
     * @return O usuário salvo.
     */
    @Transactional // Garante que ou tudo dá certo, ou nada é salvo no banco
    public Usuario registrarNovoUsuario(String nome, String email, String senhaAFormatar) {

        // 1. Regra de Negócio: Verificar se o email já existe
        if (usuarioRepository.findByEmail(email).isPresent()) {
            // Se já existe, lançamos um erro.
            // (Vamos criar uma exceção melhor para isso depois)
            throw new RuntimeException("Erro: Este email já está em uso!");
        }

        // 2. Criptografar a Senha
        // NUNCA salve uma senha pura. Usamos o encoder.
        String senhaCriptografada = passwordEncoder.encode(senhaAFormatar);

        // 3. Criar a nova entidade de usuário
        // Por padrão, todo novo registro é um ALUNO
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setRole(Role.ALUNO); // Regra: Padrão é ALUNO

        // 4. Salvar no banco
        // Usamos o repository para salvar a entidade
        return usuarioRepository.save(novoUsuario);
    }

    // TODO: Adicionar métodos para:
    // - Buscar usuário por id
    // - Ligar um Responsavel a um Aluno
    // - Mudar a Role de um usuário (só para ADMINs)
}