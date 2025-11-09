package Codicis_Nocturni.connec_escola.service;

import Codicis_Nocturni.connec_escola.model.Role;
import Codicis_Nocturni.connec_escola.model.Usuario;
import Codicis_Nocturni.connec_escola.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component // Marca como um componente que deve ser inicializado pelo Spring
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Este método roda automaticamente assim que o Spring Boot liga.
    @Override
    @Transactional // Garante que a operação de salvar aconteça corretamente
    public void run(String... args) throws Exception {

        // Verifica se o admin padrão já existe pelo email antes de criar
        if (usuarioRepository.findByEmail("admin@escola.com").isEmpty()) {

            // --- CRIAÇÃO DO ADMIN GERENTE ---
            Usuario admin = new Usuario();
            admin.setNome("Admin Gerente");
            admin.setEmail("admin@escola.com");
            admin.setSenha(passwordEncoder.encode("Eikesg808105&$")); // Senha fácil para teste
            admin.setRole(Role.ADMIN); // Define a ROLE como ADMIN (o Gerente)

            usuarioRepository.save(admin);

            System.out.println(">>> USUÁRIO ADMIN PADRÃO CRIADO: admin@escola.com / Eikesg808105&$");

            // --- CRIAÇÃO DE UM ALUNO PADRÃO (Para teste de permissão) ---
            Usuario aluno = new Usuario();
            aluno.setNome("Aluno Comum");
            aluno.setEmail("aluno@escola.com");
            aluno.setSenha(passwordEncoder.encode("aluno123"));
            aluno.setRole(Role.ALUNO);
            usuarioRepository.save(aluno);

            System.out.println(">>> USUÁRIO ALUNO PADRÃO CRIADO: aluno@escola.com / aluno123");
        }
    }
}