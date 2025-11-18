package Codicis_Nocturni.connec_escola.service;

import Codicis_Nocturni.connec_escola.model.Evento;
import Codicis_Nocturni.connec_escola.model.Local;
import Codicis_Nocturni.connec_escola.model.Role;
import Codicis_Nocturni.connec_escola.model.StatusEvento;
import Codicis_Nocturni.connec_escola.model.Usuario;
import Codicis_Nocturni.connec_escola.repository.EventoRepository;
import Codicis_Nocturni.connec_escola.repository.LocalRepository;
import Codicis_Nocturni.connec_escola.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalRepository localRepository;
    private final EventoRepository eventoRepository;

    public DataLoader(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      LocalRepository localRepository,
                      EventoRepository eventoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.localRepository = localRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // --- 1. CRIAÇÃO DE USUÁRIOS PADRÃO ---
        Usuario admin = null;
        String adminEmail = "eike.admin@escola.com";
        String adminPass = "Eikesg808105&$";

        Optional<Usuario> adminOpt = usuarioRepository.findByEmail(adminEmail);
        if (adminOpt.isEmpty()) {
            admin = new Usuario();
            admin.setNome("Admin Gerente");
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode(adminPass));
            admin.setRole(Role.ADMIN);
            admin = usuarioRepository.save(admin);
            System.out.println(">>> USUÁRIO ADMIN PADRÃO CRIADO: " + adminEmail);
        } else {
            admin = adminOpt.get();
        }

        if (usuarioRepository.findByEmail("aluno@escola.com").isEmpty()) {
            Usuario aluno = new Usuario();
            aluno.setNome("Aluno Comum");
            aluno.setEmail("aluno@escola.com");
            aluno.setSenha(passwordEncoder.encode("Aluno@Connect1"));
            aluno.setRole(Role.ALUNO);
            usuarioRepository.save(aluno);
            System.out.println(">>> USUÁRIO ALUNO PADRÃO CRIADO: aluno@escola.com");
        }

        // --- 2. CRIAÇÃO DE LOCAIS PADRÃO ---
        Local patio = localRepository.findByNome("Pátio Principal")
                .orElseGet(() -> {
                    System.out.println(">>> LOCAL PADRÃO CRIADO: Pátio Principal");
                    return localRepository.save(new Local("Pátio Principal", 500));
                });

        Local auditorio = localRepository.findByNome("Auditório")
                .orElseGet(() -> {
                    System.out.println(">>> LOCAL PADRÃO CRIADO: Auditório");
                    return localRepository.save(new Local("Auditório", 150));
                });

        // --- 3. CRIAÇÃO DE EVENTOS PADRÃO ---
        if (eventoRepository.count() == 0) {

            Evento festaJunina = new Evento();
            festaJunina.setTitulo("Festa Junina da Escola");
            festaJunina.setDescricao("Venha com sua família para nossa tradicional festa com comidas típicas e quadrilha!");
            festaJunina.setDataInicio(LocalDateTime.now().plusWeeks(1).withHour(18).withMinute(0));
            festaJunina.setDataFim(LocalDateTime.now().plusWeeks(1).withHour(22).withMinute(0));
            festaJunina.setLocal(patio);
            festaJunina.setOrganizador(admin);
            festaJunina.setStatus(StatusEvento.AGENDADO);
            // attendees é uma lista agora, não precisa setar

            eventoRepository.save(festaJunina);
            System.out.println(">>> EVENTO PADRÃO CRIADO: Festa Junina");

            Evento reuniao = new Evento();
            reuniao.setTitulo("Reunião de Pais e Mestres");
            reuniao.setDescricao("Reunião para entrega de notas e discussão sobre o desempenho do 3º bimestre.");
            reuniao.setDataInicio(LocalDateTime.now().plusMonths(1).withHour(19).withMinute(0));
            reuniao.setDataFim(LocalDateTime.now().plusMonths(1).withHour(21).withMinute(0));
            reuniao.setLocal(auditorio);
            reuniao.setOrganizador(admin);
            reuniao.setStatus(StatusEvento.AGENDADO);

            eventoRepository.save(reuniao);
            System.out.println(">>> EVENTO PADRÃO CRIADO: Reunião de Pais");
        }
    }
}