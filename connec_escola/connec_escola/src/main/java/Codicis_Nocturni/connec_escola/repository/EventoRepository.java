package Codicis_Nocturni.connec_escola.repository;

import Codicis_Nocturni.connec_escola.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    // Padrão por enquanto.
    // No futuro, poderíamos adicionar:
    // List<Evento> findAllByLocal(Local local);
    // List<Evento> findAllByDataInicioBetween(LocalDateTime start, LocalDateTime end);
}